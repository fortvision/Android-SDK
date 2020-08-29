package com.fortvision.minisites.view;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.AttrRes;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;

import com.devbrackets.android.exomedia.core.video.scale.ScaleType;
import com.devbrackets.android.exomedia.listener.OnBufferUpdateListener;
import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnErrorListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.listener.VideoControlsSeekListener;
import com.devbrackets.android.exomedia.ui.widget.EMVideoView;
import com.fortvision.minisites.R;
import com.fortvision.minisites.model.FVButton;
import com.fortvision.minisites.model.FVButtonType;
import com.fortvision.minisites.model.ImageButton;
import com.fortvision.minisites.model.VideoButton;
import com.fortvision.minisites.utils.Utils;

import java.util.concurrent.TimeUnit;

/**
 * A view that designed to display a simple {@link ImageButton}
 */

public class FVButtonVideoView extends FVButtonView implements OnPreparedListener, OnBufferUpdateListener, OnCompletionListener, OnErrorListener, FVVideoControlsButtonListener, VideoControlsSeekListener {

    private EMVideoView videoBtn;

    private VideoEventsListener videoListener;

    private FVVideoControls controls;

    private boolean soundOn;

    private boolean isBig;

    private long videoStartedTime;

    private int positionSecInVideo;

    private boolean webViewInteractedWith;

    private boolean sentQuarterEvent;

    private boolean sentHalfEvent;

    private boolean sentThirdQuartileEvent;

    private boolean isCompleted;

    private boolean isCompletedTemp;

    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;

    private OnTouchListener webViewInteractedListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            webViewInteractedWith = true;
            return false;
        }
    };

    public FVButtonVideoView(@NonNull Context context) {
        this(context, null);
    }

    public FVButtonVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FVButtonVideoView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.fv_minisites_video_content, this);
        videoBtn = (EMVideoView) findViewById(R.id.fv_minisites_video_btn);
        videoBtn.setOnPreparedListener(this);
        videoBtn.setOnBufferUpdateListener(this);
        videoBtn.setOnCompletionListener(this);
        videoBtn.setOnErrorListener(this);
        videoBtn.setScaleType(ScaleType.FIT_CENTER);
        videoBtn.setMeasureBasedOnAspectRatioEnabled(true);
        controls = new FVVideoControls(context);
        controls.setSeekListener(this);
        controls.setButtonListener(this);
        controls.setHideEmptyTextContainer(false);
        videoBtn.setControls(controls);
        controls.setVisibility(INVISIBLE);
    }

    @Override
    public View getContentView() {
        return videoBtn;
    }

    @Override
    public void accept(@NonNull final FVButton button) {
        super.accept(button);
        if (button.getButtonType() != FVButtonType.VIDEO_WITH_CONTENT && button.getButtonType() != FVButtonType.VIDEO_WITHOUT_CONTENT)
            return;
        VideoButton vb = (VideoButton) button;
        videoBtn.setVideoURI(Uri.parse(vb.getVideoURL()));
        videoBtn.setVolume(0);
        controls.setSoundOn(false);
        isCompleted = false;
        soundOn = false;
        isBig = false;
        webViewInteractedWith = false;
        sentQuarterEvent = false;
        sentHalfEvent = false;
        sentThirdQuartileEvent = false;
        videoBtn.start();
        reportVideoEvent(VideoEventsListener.VideoEvent.AdLoaded);
    }

    public boolean isBigMode() {
        return isBig;
    }

    public int getSecSinceStart() {
        return (int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - videoStartedTime);
    }

    public int getCurrentPosInVideo() {
        return positionSecInVideo;
    }

    @Override
    public void onPrepared() {
        if (isCompleted())
            return;

        Log.d("VIDEO", "onPrepared");
        videoStartedTime = System.currentTimeMillis();
        if (listener != null)
            listener.onFinishedLoadingData(this);
        reportVideoEvent(VideoEventsListener.VideoEvent.AdStarted);
    }

    @Override
    public void onBufferingUpdate(@IntRange(from = 0L, to = 100L) int percent) {
        Log.d("VIDEO", "onBufferingUpdate:" + percent);
    }

    @Override
    public void onCompletion() {
        Log.d("VIDEO", "onCompletion");
        reportVideoEvent(VideoEventsListener.VideoEvent.AdVideoComplete);
        isCompleted = true;
        isCompletedTemp = true;
    }

    @Override
    public boolean onError() {
        Log.d("VIDEO", "onError");
        reportVideoEvent(VideoEventsListener.VideoEvent.AdError);
        return false;
    }

    public void setVideoListener(@NonNull VideoEventsListener videoListener) {
        this.videoListener = videoListener;
    }

    @Override
    public boolean onPlayPauseClicked() {
        reportVideoEvent(videoBtn.isPlaying() ? VideoEventsListener.VideoEvent.AdPaused : VideoEventsListener.VideoEvent.AdPlaying);
        if (isCompletedTemp) {
            isCompletedTemp = false;
            videoBtn.restart();
            controls.updatePlayPause();
            return true;
        }
        return false;
    }

    @FVButtonActionListener.DismissType
    public int getCloseDismissTypeEvent() {
        return FVButtonActionListener.DismissType.VIDEO_PRESSED_CLOSE;
    }

    @FVButtonActionListener.DismissType
    public int getSwipedDismissTypeEvent() {
        return isBig ? FVButtonActionListener.DismissType.VIDEO_SWIPED_BIG_OUT : FVButtonActionListener.DismissType.VIDEO_SWIPED_SMALL_OUT;
    }

    @FVButtonActionListener.DismissType
    public int getTrashTypeEvent() {
        throw new IllegalStateException("Video button view does not support trash event");
    }

    public boolean canBeTrashed() {
        return false;
    }

    @Override
    public boolean onPreviousClicked() {
        return false;
    }

    @Override
    public boolean onNextClicked() {
        return false;
    }

    @Override
    public boolean onRewindClicked() {
        return false;
    }

    @Override
    public boolean onFastForwardClicked() {
        return false;
    }

    @Override
    public boolean onSeekStarted() {
        return false;
    }

    @Override
    public boolean onSeekEnded(int seekTime) {
        return false;
    }

    public void setShowBanner(boolean showBanner) {
        WebView webView;
        View progressBar;
        View poweredByView;
        final View container;
        /*if (!((VideoButton) button).isBigVideoAlignmentBottom()) {
            webView = (WebView) findViewById(R.id.fv_minisites_webview_bottom);
            progressBar = findViewById(R.id.fv_minisites_progress_bottom);
            poweredByView = findViewById(R.id.fv_minisites_powered_by_bottom);
            container = findViewById(R.id.fv_minisites_bottom_content);
        } else {*/
            webView = (WebView) findViewById(R.id.fv_minisites_webview_top);
            progressBar = findViewById(R.id.fv_minisites_progress_top);
            poweredByView = findViewById(R.id.fv_minisites_powered_by_top);
            container = findViewById(R.id.fv_minisites_top_content);
            if (onGlobalLayoutListener != null)
                container.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
            onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (container.getVisibility() == VISIBLE)
                        setY(getY() - container.getHeight());
                    container.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    onGlobalLayoutListener = null;
                }
            };
            container.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
        //}
        if (showBanner) {
            controls.setVisibility(VISIBLE);
            showControls();
            videoBtn.setVolume(1);
            controls.setSoundOn(true);
            soundOn = true;
            container.setVisibility(View.VISIBLE);
            Utils.configureWebView(webView, progressBar);
            ViewGroup.LayoutParams lp = webView.getLayoutParams();
            //lp.height = Utils.dpToPx(getContext(), button.getPopup().getHeight());
            webView.setLayoutParams(lp);
            webView.setOnTouchListener(webViewInteractedListener);
            Utils.configuredPoweredByView(poweredByView);
            String contentUrl = button.getPopup().getContent();
            if (!TextUtils.isEmpty(contentUrl) && TextUtils.isEmpty(webView.getUrl()))
                webView.loadUrl(contentUrl);
        } else {
            videoBtn.setVolume(0);
            controls.setSoundOn(false);
            soundOn = false;
            controls.setVisibility(INVISIBLE);
            //if (container.getVisibility() == VISIBLE && ((VideoButton) button).isBigVideoAlignmentBottom())
                setY(getY() + container.getHeight());
            container.setVisibility(View.GONE);
        }
    }

    public void setMode(boolean isBig) {
        this.isBig = isBig;
    }

    public void showControls() {
        controls.show();
        controls.hideDelayed(2000);
    }

    @Override
    public boolean onMinimizedClicked() {
        if (videoListener != null)
            videoListener.onMinimizeVideo(this);
        return true;
    }

    @Override
    public boolean onToggleSoundClicked() {
        videoBtn.setVolume(soundOn ? 0 : 1);
        reportVideoEvent(soundOn ? VideoEventsListener.VideoEvent.AdMute
                    : VideoEventsListener.VideoEvent.AdUnmute);
        soundOn = !soundOn;
        controls.setSoundOn(soundOn);
        return true;
    }

    @Override
    public void onPositionUpdated(int positionSeconds, int durationSeconds) {
        positionSecInVideo = positionSeconds;
        @VideoEventsListener.VideoEvent String progressEvent = null;
        if ((float) positionSeconds / durationSeconds >= 0.75 && !sentThirdQuartileEvent) {
            progressEvent = VideoEventsListener.VideoEvent.AdVideoThirdQuartile;
            sentThirdQuartileEvent = true;
        } else if ((float) positionSeconds / durationSeconds >= 0.5 && !sentHalfEvent) {
            progressEvent = VideoEventsListener.VideoEvent.AdVideoMidpoint;
            sentHalfEvent = true;
        } else if ((float) positionSeconds / durationSeconds >= 0.25 && !sentQuarterEvent) {
            progressEvent = VideoEventsListener.VideoEvent.AdVideoFirstQuartile;
            sentQuarterEvent = true;
        }
        reportVideoEvent(progressEvent);
    }

    public boolean isWebViewInteractedWith() {
        return webViewInteractedWith;
    }

    public boolean isCompleted(){
        return isCompleted;
    }

    private void reportVideoEvent(@VideoEventsListener.VideoEvent String event){
        if (event != null & videoListener != null && !isCompleted())
            videoListener.onVideoEvent(this, event);
    }
}
