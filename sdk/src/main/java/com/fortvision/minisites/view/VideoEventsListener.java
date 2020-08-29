package com.fortvision.minisites.view;

import androidx.annotation.NonNull;
import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.fortvision.minisites.view.VideoEventsListener.VideoEvent.*;

/**
 * An interface for various types of video events.
 */

public interface VideoEventsListener {
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({AdLoaded, AdStarted, AdStopped, AdVideoFirstQuartile, AdVideoMidpoint, AdVideoThirdQuartile,
            AdVideoComplete, AdPlaying, AdPaused, AdError, AdMute, AdUnmute, VideoRemoved, AdMinimize})
    @interface VideoEvent {
        String AdLoaded = "AdLoaded";
        String AdStarted = "AdStarted";
        String AdStopped = "AdStopped";
        String AdVideoFirstQuartile = "0.25";
        String AdVideoMidpoint = "0.5";
        String AdVideoThirdQuartile = "0.75";
        String AdVideoComplete = "1";
        String AdPaused = "AdPaused";
        String AdPlaying = "AdPlaying";
        String AdError = "AdError";
        String AdMute = "AdMute";
        String AdUnmute = "AdUnmute";
        String VideoRemoved = "VideoRemoved";
        String AdMinimize = "AdMinimize";
    }

    void onVideoEvent(@NonNull FVButtonVideoView view, @NonNull @VideoEvent String eventType);

    void onMinimizeVideo(@NonNull FVButtonVideoView view);
}
