package com.fortvision.minisites.view;

import com.devbrackets.android.exomedia.listener.VideoControlsButtonListener;


interface FVVideoControlsButtonListener extends VideoControlsButtonListener {

    /**
     * Occurs when the Minimize button on the {@link FVVideoControls}
     * is clicked.
     *
     * @return True if the event has been handled
     */
    boolean onMinimizedClicked();

    /**
     * Occurs when the sound toggle button on the {@link FVVideoControls}
     * is clicked.
     *
     * @return True if the event has been handled
     */
    boolean onToggleSoundClicked();

    /**
     * Called when the position of the video is updated.
     */
    void onPositionUpdated(int positionSeconds, int durationSeconds);

}
