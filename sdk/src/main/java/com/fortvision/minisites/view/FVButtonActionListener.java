package com.fortvision.minisites.view;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import com.fortvision.minisites.model.FVButton;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * An interface for a listener to the different actions taken by an FV Button view.
 */

public interface FVButtonActionListener {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({DismissType.PRESSED_CLOSE, DismissType.DRAGGED_TO_TRASH, DismissType.SWIPED_OUT,
            DismissType.VIDEO_PRESSED_CLOSE, DismissType.VIDEO_SWIPED_BIG_OUT, DismissType.VIDEO_SWIPED_SMALL_OUT})
    @interface DismissType {
        int PRESSED_CLOSE = 3;
        int DRAGGED_TO_TRASH = 1;
        int SWIPED_OUT = 2;
        int VIDEO_PRESSED_CLOSE = 6;
        int VIDEO_SWIPED_BIG_OUT = 5;
        int VIDEO_SWIPED_SMALL_OUT = 4;
    }

    void onFinishedLoadingData(@NonNull FVButtonView buttonView);

    void onButtonClick(@NonNull FVButton button);

    void onButtonDismissed(@NonNull FVButton button, @DismissType int dismissType);

    void onButtonVisible(@NonNull FVButton button);
}
