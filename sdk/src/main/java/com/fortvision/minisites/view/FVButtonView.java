package com.fortvision.minisites.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import androidx.annotation.AttrRes;
import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.fortvision.minisites.R;
import com.fortvision.minisites.model.Anchor;
import com.fortvision.minisites.model.FVButton;
import com.fortvision.minisites.utils.Assets;
import com.fortvision.minisites.utils.CancelableRunnable;
import com.fortvision.minisites.utils.Utils;

import java.util.concurrent.TimeUnit;

/**
 * A base class for view that can display an {@link FVButton}
 */

public abstract class FVButtonView extends FrameLayout {

    protected ImageView closeBtn;

    protected FVButton button;

    protected FVButtonActionListener listener;

    private Anchor currentAnchor;

    private final CancelableRunnable dimButtonView = new CancelableRunnable() {
        ObjectAnimator animator;

        @Override
        public void run() {
            animator = ObjectAnimator.ofFloat(FVButtonView.this, View.ALPHA, getButton().getOpacity());
            animator.setDuration(2000).start();
        }

        @Override
        public void cancel(boolean interrupt) {
            if (animator != null)
                animator.cancel();
        }
    };

    public FVButtonView(@NonNull Context context) {
        this(context, null);
    }

    public FVButtonView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FVButtonView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClipChildren(false);
        setMeasureAllChildren(true);
        LayoutInflater.from(context).inflate(R.layout.fv_minisites_button, this);
        closeBtn = (ImageView) findViewById(R.id.fv_minisites_close_btn);
        Assets.loadButtonCloseImage(closeBtn);
    }

    @CallSuper
    public void accept(@NonNull final FVButton button) {
        this.button = button;
        Context context = getContext();
        closeBtn.setVisibility(button.isDismissible() ? VISIBLE : GONE);
        if (button.isDismissible()) {
            closeBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onButtonDismissed(button, getCloseDismissTypeEvent());
                }
            });
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) closeBtn.getLayoutParams();
            lp.height = lp.width = Utils.dpToPx(context, button.getDismissSize());
            //lp.setMargins(0, -200, 0, 0);
            closeBtn.setLayoutParams(lp);
            updateButtonAnchor(button.getAnchor());
        }
        applyButtonDimensionToView(getContentView());
    }

    @FVButtonActionListener.DismissType
    public int getCloseDismissTypeEvent() {
        return FVButtonActionListener.DismissType.PRESSED_CLOSE;
    }

    @FVButtonActionListener.DismissType
    public int getSwipedDismissTypeEvent() {
        return FVButtonActionListener.DismissType.SWIPED_OUT;
    }

    @FVButtonActionListener.DismissType
    public int getTrashTypeEvent() {
        return FVButtonActionListener.DismissType.DRAGGED_TO_TRASH;
    }

    public boolean canBeTrashed() {
        return true;
    }

    public abstract View getContentView();


    private void applyButtonDimensionToView(View view) {
        Context context = getContext();
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = Utils.dpToPx(context, button.getWidth());
        layoutParams.height = Utils.dpToPx(context, button.getHeight());
        view.setLayoutParams(layoutParams);
    }

    public void setCurrentAnchor(@NonNull Anchor anchor) {
        currentAnchor = anchor;
    }

    public void updateButtonAnchor(@NonNull Anchor anchor) {
        currentAnchor = anchor;
        View contentView = getChildAt(1);
        ViewGroup.MarginLayoutParams contentLP = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
        FrameLayout.LayoutParams closeLP = (FrameLayout.LayoutParams) closeBtn.getLayoutParams();
        contentLP.topMargin = closeLP.height;
        if (anchor.isAlignedRight()) {
            contentLP.setMarginEnd(0);
            contentLP.setMarginStart(closeLP.width);
            closeLP.gravity = Gravity.TOP | Gravity.START;
        } else {
            contentLP.setMarginEnd(closeLP.width);
            contentLP.setMarginStart(0);
            closeLP.gravity = Gravity.TOP | Gravity.END;
        }
        closeBtn.setLayoutParams(closeLP);
        contentView.setLayoutParams(contentLP);
    }

    public void setActionsListener(@NonNull FVButtonActionListener listener) {
        this.listener = listener;
    }

    public FVButton getButton() {
        return button;
    }

    public void hideClose() {
        if (!button.isDismissible())
            return;

        closeBtn.setVisibility(GONE);
        final ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) getChildAt(1).getLayoutParams();
        setY(getY() + lp.topMargin);
        if (currentAnchor.isAlignedRight()) {
            setX(getX() + lp.getMarginStart());
        }
        lp.setMargins(0, 0, 0, 0);
        lp.setMarginEnd(0);
        lp.setMarginStart(0);
        getChildAt(1).setLayoutParams(lp);
    }

    public void restoreClose() {
        if (!button.isDismissible())
            return;

        closeBtn.setVisibility(VISIBLE);
        View contentView = getChildAt(1);
        ViewGroup.MarginLayoutParams contentLP = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
        contentLP.topMargin = closeBtn.getHeight();
        if (getCurrentAnchor().isAlignedRight()) {
            contentLP.setMarginEnd(0);
            contentLP.setMarginStart(closeBtn.getWidth());
            setX(getX() - contentLP.getMarginStart());
        } else {
            contentLP.setMarginEnd(closeBtn.getWidth());
            contentLP.setMarginStart(0);
        }
        contentView.setLayoutParams(contentLP);
        setY(getY() - contentLP.topMargin);
    }

    public void onButtonDragged(int dx, int dy) {
    }

    public Anchor getCurrentAnchor() {
        return currentAnchor != null ? currentAnchor : button.getAnchor();
    }

    public void startDimTimer() {
        postDelayed(dimButtonView, TimeUnit.SECONDS.toMillis(getButton().getOpacityTimeout()));
    }

    public void cancelDim() {
        removeCallbacks(dimButtonView);
        dimButtonView.cancel(true);
        setAlpha(1);
    }

}
