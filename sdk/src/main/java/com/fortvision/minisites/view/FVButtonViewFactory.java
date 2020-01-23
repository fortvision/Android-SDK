package com.fortvision.minisites.view;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fortvision.minisites.FVButtonContext;
import com.fortvision.minisites.model.FVButton;

/**
 * A factory class that creates FVButtonView from an {@link FVButton}
 */

public class FVButtonViewFactory {


    @Nullable
    public static FVButtonView create(@NonNull FVButtonContext fvContext, @NonNull final FVButton button) {
        Activity activity = fvContext.getActivity();
        if (activity == null)
            return null;

        final FVButtonView buttonView;
        switch (button.getButtonType()) {
            case SIMPLE_IMAGE:
                buttonView = new FVButtonImageView(activity);
                break;
            case IFRAME:
                buttonView = new FVButtonIframeView(activity);
                break;
            case VIDEO_WITH_CONTENT:
            case VIDEO_WITHOUT_CONTENT:
                buttonView = new FVButtonVideoView(activity);
                break;
            default:
                throw new RuntimeException("unreachable");
        }
        return buttonView;
    }
}
