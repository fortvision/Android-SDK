package com.fortvision.minisites.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.devbrackets.android.exomedia.ui.widget.VideoControlsMobile;
import com.fortvision.minisites.R;
import com.fortvision.minisites.utils.Assets;

import java.util.concurrent.TimeUnit;

/**
 *
 */

public class FVVideoControls extends VideoControlsMobile {

    private Drawable playDrawable, pauseDrawable;

    private Drawable muteDrawable, unmuteDrawable;

    @Nullable
    protected FVVideoControlsButtonListener buttonsListener;

    private ImageButton soundToggle;

    public FVVideoControls(Context context) {
        this(context, null);
    }

    public FVVideoControls(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FVVideoControls(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        findViewById(R.id.exomedia_controls_video_seek).setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        soundToggle = (ImageButton) findViewById(R.id.fv_minisites_sound_toggle);
        soundToggle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonsListener != null)
                    buttonsListener.onToggleSoundClicked();
            }
        });
        findViewById(R.id.fv_minisites_video_close).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonsListener != null)
                    buttonsListener.onMinimizedClicked();
            }
        });
        muteDrawable = ContextCompat.getDrawable(context, R.drawable.fv_minisites_mute_button);
        unmuteDrawable = ContextCompat.getDrawable(context, R.drawable.fv_minisites_unmute_button);
        Assets.loadVideoControlPlay(getContext(), new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                playDrawable = resource;
                updatePlayPause();
            }
        });

        Assets.loadVideoControlPause(getContext(), new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                pauseDrawable = resource;
                updatePlayPause();
            }
        });

    }

    protected void updatePlayPause() {
        if (playDrawable != null && pauseDrawable != null) {
            setPlayPauseDrawables(playDrawable, pauseDrawable);
        }
    }

    /**
     * Specifies the callback to inform of button click events
     *
     * @param callback The callback
     */
    public void setButtonListener(@Nullable FVVideoControlsButtonListener callback) {
        super.setButtonListener(callback);
        this.buttonsListener = callback;
    }

    /*@Override
    public void setPosition(@IntRange(from = 0L) long position) {

    }

    @Override
    public void setDuration(@IntRange(from = 0L) long duration) {

    }
*/
    @Override
    public void updateProgress(@IntRange(from = 0L) long position, @IntRange(from = 0L) long duration, @IntRange(from = 0L, to = 100L) int bufferPercent) {
        super.updateProgress(position, duration, bufferPercent);
        if (buttonsListener != null)
            buttonsListener.onPositionUpdated((int) TimeUnit.MILLISECONDS.toSeconds(position), (int) TimeUnit.MILLISECONDS.toSeconds(duration));
        //Log.d("TAGGGG", position + " " + duration + " " + bufferPercent);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fv_minisites_video_controls_2;
    }

    /*@Override
    protected void animateVisibility(boolean toVisible) {

    }

    @Override
    protected void updateTextContainerVisibility() {

    }*/

    @Override
    public void showLoading(boolean initialLoad) {
    }

    @Override
    public void finishLoading() {
    }


    public void setSoundOn(boolean soundOn) {
        soundToggle.setImageDrawable(soundOn ? muteDrawable : unmuteDrawable);
    }
}
