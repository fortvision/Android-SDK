package com.fortvision.minisites;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.amazonaws.mobileconnectors.kinesis.kinesisrecorder.KinesisFirehoseRecorder;
import com.amazonaws.mobileconnectors.kinesis.kinesisrecorder.KinesisRecorder;
import com.amazonaws.regions.Regions;
import com.fortvision.minisites.model.Anchor;
import com.fortvision.minisites.model.FVButton;
import com.fortvision.minisites.network.BaseCallback;
import com.fortvision.minisites.network.FVServerAPI;
import com.fortvision.minisites.network.FVServerApiFactory;
import com.fortvision.minisites.utils.Assets;
import com.fortvision.minisites.utils.BaseAnimatorListener;
import com.fortvision.minisites.utils.Utils;
import com.fortvision.minisites.view.FVButtonActionListener;
import com.fortvision.minisites.view.FVButtonVideoView;
import com.fortvision.minisites.view.FVButtonView;
import com.fortvision.minisites.view.VideoEventsListener;

import java.text.SimpleDateFormat;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * A controller for the {@link com.fortvision.minisites.view.FVButtonView}
 */

class ButtonViewController implements FVButtonActionListener, VideoEventsListener {

    //Parameters configured in res/values/application_settings.xml
    private String cognitoIdentityPoolId;
    private Regions region;
    private String kinesisStreamName;
    private String firehoseStreamName;
    private String androidId;

    protected static final String APPLICATION_NAME = "android-mobile-streams";
    protected static final double RAD2DEG = 180 / Math.PI;
    protected KinesisRecorder kinesisRecorder;
    protected KinesisFirehoseRecorder firehoseRecorder;
    protected SensorManager sensorManager;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ");


    @SuppressLint("StaticFieldLeak")
    private static ButtonViewController INSTANCE;

    private final Runnable showTrash = new Runnable() {

        @Override
        public void run() {
            trashView.setVisibility(View.VISIBLE);
        }
    };

    private final Runnable dismissVideoCompleted = new Runnable() {

        @Override
        public void run() {
            removeCurrentButton(true);
        }
    };

    private Runnable animateButtonOut = null;

    private Handler handler = new Handler(Looper.getMainLooper());

    private FVButtonContext fvContext;

    private FVButtonView buttonView;

    private FrameLayout buttonContainer;

    private ImageView trashView;

    private static final int PARENT_LAID_OUT = 1;
    private static final int BUTTON_LAID_OUT = 1 << 1;
    private static final int BUTTON_FINISHED_LOAD = 1 << 2;

    private int buttonStatus;

    private FVButtonActionListener listener;

    private VideoEventsListener videoEventsListener;

    private long startLoadedTime;

    private FVServerAPI serverAPI;

    private ButtonViewController() {
        serverAPI = FVServerApiFactory.create();
    }

    static ButtonViewController get() {
        if (INSTANCE == null)
            INSTANCE = new ButtonViewController();
        return INSTANCE;
    }

    @SuppressLint({"InflateParams", "StaticFieldLeak"})
    void startManageButton(@NonNull FVButtonContext fvContext, @NonNull final FVButtonView buttonView, @NonNull FVButton button) {
        buttonStatus = 0;
        this.fvContext = fvContext;
        this.buttonView = buttonView;
        Activity activity = fvContext.getActivity();
        if (activity == null)
            return;

        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        buttonContainer = (FrameLayout) layoutInflater.inflate(R.layout.fv_minisites_button_container, null, false);
        trashView = (ImageView) buttonContainer.findViewById(R.id.fv_minisites_trashView);
        Assets.loadTrashImage(trashView);
        activity.getWindow().addContentView(buttonContainer, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        buttonView.setVisibility(View.INVISIBLE);
        buttonView.setActionsListener(this);
        if (buttonView instanceof FVButtonVideoView)
            ((FVButtonVideoView) buttonView).setVideoListener(this);
        buttonView.accept(button);
        buttonView.setVisibility(View.INVISIBLE);
        buttonView.getContentView().setOnTouchListener(new ButtonTouchListener(buttonView));
        buttonView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        buttonContainer.addView(buttonView);
        final ViewTreeObserver containerTreeObserver = buttonContainer.getViewTreeObserver();
        containerTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                containerTreeObserver.removeOnGlobalLayoutListener(this);
                addFlagToStatus(PARENT_LAID_OUT);
                showButton();
            }
        });
        final ViewTreeObserver buttonTreeObserver = buttonView.getViewTreeObserver();
        buttonTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                buttonTreeObserver.removeOnGlobalLayoutListener(this);
                addFlagToStatus(BUTTON_LAID_OUT);
                showButton();
            }
        });
        //if (buttonView.getButton().getPopup().isPreload())
        //    popupWebView.loadUrl(buttonView.getButton().getPopup().getContent());
    }

    private void addFlagToStatus(int flag) {
        buttonStatus |= flag;
    }

    private boolean testStatus(int flagBits) {
        return (buttonStatus & flagBits) == flagBits;
    }

    private void showButton() {
        if (!testStatus(PARENT_LAID_OUT | BUTTON_FINISHED_LOAD | BUTTON_LAID_OUT))
            return;

        buttonView.setVisibility(View.VISIBLE);
        Anchor anchor = buttonView.getButton().getAnchor();
        if (anchor.isAlignedRight())
            buttonView.setX(buttonContainer.getWidth());
        else
            buttonView.setX(-buttonView.getWidth());
        float anchorY = clampAnchorY(anchor.getyPos());
        buttonView.setY(anchorY * buttonContainer.getHeight());
        animateButtonToAnchor(new Anchor(anchor.getxPos(), anchorY, anchor.isAlignedRight()));
        buttonView.startDimTimer();
        onButtonVisible(buttonView.getButton());
    }

    private void animateButtonToAnchor(final Anchor anchor) {
        buttonView.setCurrentAnchor(anchor);
        float targetX;
        if (anchor.isAlignedRight())
            targetX = buttonContainer.getWidth() - anchor.getxPos() * buttonContainer.getWidth() - buttonView.getWidth();
        else
            targetX = anchor.getxPos() * buttonContainer.getWidth();
        float targetY = anchor.getyPos() * buttonContainer.getHeight();
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(buttonView, View.X, targetX).setDuration(200);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(buttonView, View.Y, targetY).setDuration(200);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(animatorX, animatorY);
        set.start();
    }

    private void startShowTrashTimer() {
        handler.postDelayed(showTrash, 500);
    }

    private void removeTrash() {
        handler.removeCallbacks(showTrash);
        trashView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onFinishedLoadingData(@NonNull FVButtonView buttonView) {
        addFlagToStatus(BUTTON_FINISHED_LOAD);
        showButton();
    }

    private void animateButtonOut(@Nullable final Runnable animationFinishedCallback) {
        int targetX;
        if (buttonView.getX() + buttonView.getWidth() / 2 >= buttonContainer.getWidth() / 2)
            targetX = buttonContainer.getWidth();
        else
            targetX = -buttonView.getWidth();
        ObjectAnimator animator = ObjectAnimator.ofFloat(buttonView, View.X, targetX);
        animator.addListener(new BaseAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (animationFinishedCallback != null)
                    animationFinishedCallback.run();
            }
        });
        animator.setDuration(200).start();
    }

    @Override
    public void onButtonClick(@NonNull FVButton button) {
        Activity activity = fvContext.getActivity();
        if (activity == null)
            return;
        startLoadedTime = System.currentTimeMillis();
        animateButtonOut(null);

        PopupDialogFragment dialogFragment = new PopupDialogFragment(activity, button.getPopup(), INSTANCE);
        dialogFragment.show(activity.getFragmentManager(), "popup");

        if (listener != null)
            listener.onButtonClick(button);
    }

    public void onPopupDismissed() {
        if (buttonView != null) {
            animateButtonToAnchor(buttonView.getCurrentAnchor());
            buttonView.startDimTimer();
        }
        onClosePopup();
    }

    public long getSecSinceStart() {
        return System.currentTimeMillis() - startLoadedTime;
    }

    public void onLoadPopup() {
        if (buttonView != null) {
            FVButton button = buttonView.getButton();
            Call<ResponseBody> call = serverAPI.reportLoadPopup(button.getCampaignId(), String.valueOf(button.getDesignId()), MiniSites.INSTANCE.getCachedUserId(),
                    Utils.getDeviceIpAsStr(), MiniSites.INSTANCE.getCachedUserAgent(), getSecSinceStart());
            call.enqueue(new BaseCallback<ResponseBody>());

            //HERE KINESIS
        }
    }

    public void onClosePopup() {
        if (buttonView != null) {
            FVButton button = buttonView.getButton();
            Call<ResponseBody> call = serverAPI.reportClosePopup(button.getCampaignId(), String.valueOf(button.getDesignId()), MiniSites.INSTANCE.getCachedUserId(),
                    Utils.getDeviceIpAsStr(), MiniSites.INSTANCE.getCachedUserAgent(), getSecSinceStart());
            call.enqueue(new BaseCallback<ResponseBody>());

            //HERE KINESIS
        }
    }

    void removeCurrentButton(boolean animate) {
        if (buttonView == null || animateButtonOut != null)
            return;

        animateButtonOut = new Runnable() {
            private FVButtonView buttonView = ButtonViewController.this.buttonView;
            private View buttonContainer = ButtonViewController.this.buttonContainer;

            @Override
            public void run() {

                if (buttonView instanceof FVButtonVideoView) {
                    if (videoEventsListener != null)
                        videoEventsListener.onVideoEvent((FVButtonVideoView) buttonView, VideoEvent.VideoRemoved);
                }
                if (buttonContainer != null) {
                    buttonView.cancelDim();
                    removeTrash();
                    ((ViewGroup) buttonContainer.getParent()).removeView(buttonContainer);
                }
                animateButtonOut = null;
            }
        };

        if (animate)
            animateButtonOut(animateButtonOut);
        else
            animateButtonOut.run();

        handler.removeCallbacks(dismissVideoCompleted);
        buttonView.getContentView().setOnTouchListener(null);
        buttonView = null;
        buttonContainer = null;
        fvContext = null;
    }

    private float clampAnchorY(float yPos) {
        return (float) Math.min(Math.max(yPos, 0.2), 0.8 - (float) buttonView.getHeight() / buttonContainer.getHeight());
    }

    @Override
    public void onButtonDismissed(@NonNull FVButton button, @DismissType int dismissType) {
        removeCurrentButton(dismissType != DismissType.DRAGGED_TO_TRASH);
        if (listener != null)
            listener.onButtonDismissed(button, dismissType);
    }

    @Override
    public void onButtonVisible(@NonNull FVButton button) {
        if (listener != null)
            listener.onButtonVisible(button);
    }

    void setListener(FVButtonActionListener listener) {
        this.listener = listener;
    }

    void setVideoEventsListener(MiniSites videoEventsListener) {
        this.videoEventsListener = videoEventsListener;
    }

    @Override
    public void onVideoEvent(@NonNull FVButtonVideoView view, @NonNull @VideoEvent String eventType) {
        switch (eventType) {
            case VideoEvent.AdVideoComplete:
                if (!view.isBigMode() || !view.isWebViewInteractedWith())
                    handler.postDelayed(dismissVideoCompleted, 3000);
                break;
        }
        if (videoEventsListener != null)
            videoEventsListener.onVideoEvent(view, eventType);
    }

    @Override
    public void onMinimizeVideo(@NonNull final FVButtonVideoView buttonView) {

        buttonView.setMode(false);
        buttonView.setShowBanner(false);
        final Context context = buttonView.getContext();
        final FVButton button = buttonView.getButton();
        final View animatedView = buttonView.getContentView();
        final int initialContentHeight = buttonView.getHeight();
        final float startX = buttonView.getX();
        final float startY = buttonView.getY();
        final float targetY = buttonView.getCurrentAnchor().getyPos() * buttonContainer.getHeight()
                + (button.isDismissible() ? Utils.dpToPx(context, button.getDismissSize())
                + context.getResources().getDimensionPixelSize(R.dimen.fv_minisites_video_padding) : 0);
        final float targetX;
        final int targetContentViewWidth = Utils.dpToPx(context, button.getWidth());
        final int targetContentViewHeight = Utils.dpToPx(context, button.getHeight());
        if (buttonView.getCurrentAnchor().isAlignedRight())
            targetX = buttonContainer.getWidth() - buttonView.getCurrentAnchor().getxPos() * buttonContainer.getWidth() - targetContentViewWidth - context.getResources().getDimensionPixelSize(R.dimen.fv_minisites_video_padding) * 2;
        else
            targetX = buttonView.getCurrentAnchor().getxPos() * buttonContainer.getWidth();
        final ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) animatedView.getLayoutParams();
        animatedView.setLayoutParams(lp);
        ValueAnimator animator = new ValueAnimator();
        animator.setDuration(200);
        animator.setIntValues(animatedView.getWidth(), targetContentViewWidth);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedFraction = animation.getAnimatedFraction();
                lp.width = (Integer) animation.getAnimatedValue();
                lp.height = (int) (initialContentHeight - animatedFraction * (initialContentHeight - targetContentViewHeight));
                animatedView.setLayoutParams(lp);
                buttonView.setX(startX + animatedFraction * (targetX - startX));
                buttonView.setY(startY + animatedFraction * (targetY - startY));
            }
        });
        animator.addListener(new BaseAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                buttonContainer.setBackground(null);
                buttonContainer.setOnClickListener(null);
                buttonContainer.setClickable(false);
                animatedView.setOnTouchListener(new ButtonTouchListener(buttonView));
                buttonView.startDimTimer();
                buttonView.restoreClose();
                if (buttonView.isCompleted())
                    handler.postDelayed(dismissVideoCompleted, 3000);
                if (videoEventsListener != null)
                    videoEventsListener.onVideoEvent(buttonView, VideoEvent.AdMinimize);
            }
        });
        animator.start();
    }

    private class ButtonTouchListener extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {
        private final GestureDetector gestureDetector;
        private final FVButtonView buttonView;
        private final View buttonContentView;
        private int dx = 0, dy = 0;
        private boolean isNearTrash = false;
        private Vibrator vibrator;
        private Rect trashRect = new Rect();
        private boolean reactToClickGesture;
        private long actionDownTime = 0;
        private Boolean lockedXAxes;
        private final FVButton button;
        private float origX, origY, origWidth, origHeight;
        private float prevX, prevY, prevWidth, prevHeight;
        private float videoStartX = 0;

        ButtonTouchListener(FVButtonView buttonView) {
            Context context = buttonView.getContext();
            this.gestureDetector = new GestureDetector(context, this);
            this.buttonView = buttonView;
            this.buttonContentView = buttonView.getContentView();
            this.button = buttonView.getButton();
            if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(context, Manifest.permission.VIBRATE))
                vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.d("event", (int) buttonView.getX() + "," + (int) buttonView.getY() + " " + (int) event.getRawX() + "," + (int) event.getRawY());
            int buttonWidth = buttonView.getWidth();
            int buttonHeight = buttonView.getHeight();
            int buttonContentWidth = buttonContentView.getWidth();
            //int buttonContentHeight = buttonContentView.getHeight();
            //int maxVideoWidth = buttonContainer.getWidth() - buttonView.getContext().getResources().getDimensionPixelSize(R.dimen.fv_minisites_video_padding) * 2;
            //int maxVideoHeight = (int) (maxVideoWidth * button.getAspectRatio());

            if (gestureDetector.onTouchEvent(event) && reactToClickGesture)
                return true;

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lockedXAxes = null;
                    actionDownTime = System.currentTimeMillis();
                    reactToClickGesture = true;
                    buttonView.cancelDim();
                    buttonView.hideClose();
                    if (buttonView.canBeTrashed()) {
                        isNearTrash = false;
                        startShowTrashTimer();
                    }
                    videoStartX = origX = buttonView.getX();
                    origY = buttonView.getY();
                    origWidth = buttonContentView.getWidth();
                    origHeight = buttonContentView.getHeight();

                    dx = (int) (buttonView.getX() - event.getRawX());
                    dy = (int) (buttonView.getY() - event.getRawY());
                    break;
                case MotionEvent.ACTION_UP:
                    if (buttonView.canBeTrashed()) {
                        if (isNearTrash) {
                            onButtonDismissed(buttonView.getButton(), buttonView.getTrashTypeEvent());
                            return true;
                        }
                        removeTrash();
                    }

                    if (buttonView.getAlpha() <= 0.25) {
                        onButtonDismissed(buttonView.getButton(), buttonView.getSwipedDismissTypeEvent());
                        return true;
                    }

                    buttonView.restoreClose();
                    buttonView.setAlpha(1);
                    buttonView.startDimTimer();
                    float xPos = buttonView.getButton().getAnchor().getxPos();
                    int containerHeight = buttonContainer.getHeight();
                    float yPos = clampAnchorY(buttonView.getY() / containerHeight);
                    boolean alignedRight = ((buttonView.getX() + buttonWidth / 2) / buttonContainer.getWidth()) > 0.5;
                    final Anchor anchor = new Anchor(xPos, yPos, alignedRight);
                    buttonView.updateButtonAnchor(anchor);
                    buttonView.post(new Runnable() {
                        @Override
                        public void run() {
                            animateButtonToAnchor(anchor);
                        }
                    });

                    break;
                case MotionEvent.ACTION_MOVE:
                    if (buttonView.canBeTrashed()) {
                        trashView.getHitRect(trashRect);
                        Rect buttonRect = new Rect((int) buttonView.getX(), (int) buttonView.getY(), (int) buttonView.getX() + buttonView.getWidth(), (int) buttonView.getY() + buttonView.getHeight());

                        //if (!isNearTrash && trashRect.contains((int) (event.getRawX() - buttonContainer.getX()), (int) (event.getRawY() - buttonContainer.getY()))) {
                        if (!isNearTrash && Rect.intersects(trashRect, buttonRect)) {
                            isNearTrash = true;
                            buttonView.setAlpha(buttonView.getButton().getOpacity());
                            if (vibrator != null)
                                vibrator.vibrate(500);
                            //} else if (isNearTrash && !trashRect.contains((int) (event.getRawX() - buttonContainer.getX()), (int) (event.getRawY() - buttonContainer.getY()))) {
                        } else if (isNearTrash && !Rect.intersects(trashRect, buttonRect)) {
                            isNearTrash = false;
                            buttonView.setAlpha(1);
                            if (vibrator != null)
                                vibrator.cancel();
                        }
                    }
                    if (System.currentTimeMillis() - actionDownTime >= 130)
                        reactToClickGesture = false;
                    float newPosX = event.getRawX() + dx;
                    float newPosY = Math.min(Math.max(0, event.getRawY() + dy), buttonContainer.getHeight() - buttonHeight);
                    //float newPosY = event.getRawY() + dy;
                    buttonView.setX(newPosX);
                    buttonView.setY(newPosY);

                    if (buttonView.getX() < 0 || buttonView.getX() + buttonContentWidth > buttonContainer.getWidth()) {
                        float alpha = buttonView.getX() < 0 ? 1 + buttonView.getX() / (buttonContentWidth / 2) :
                                1 - (buttonView.getX() + buttonContentWidth - buttonContainer.getWidth()) / (buttonContentWidth / 2);
                        if (buttonView instanceof FVButtonVideoView)
                            alpha = Math.min(alpha, 0.5f);
                        buttonView.setAlpha(alpha);
                    }
                    break;
            }
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (reactToClickGesture) {
                if (buttonView instanceof FVButtonVideoView && ((FVButtonVideoView) buttonView).isBigMode()) {
                    ((FVButtonVideoView) buttonView).showControls();
                } else {
                    buttonView.restoreClose();
                    removeTrash();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onButtonClick(buttonView.getButton());
                        }
                    });

                }
            }
            return true;
        }
    }
}
