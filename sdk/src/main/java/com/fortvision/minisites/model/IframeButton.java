package com.fortvision.minisites.model;

import android.support.annotation.NonNull;

/**
 * A POJO representing a FortVision button that shows a specific url.
 */

public class IframeButton extends FVButton {

    @NonNull
    private final String buttonContentUrl;

    public IframeButton(boolean dismissible, int dismissSize, int width, int height, @NonNull Anchor anchor,
                        @NonNull String campaignId, int designId, float opacity, int opacityTimeout,
                        @NonNull Popup popup, @NonNull String buttonContentUrl) {
        super(dismissible, dismissSize, width, height, anchor, campaignId, designId, opacity, opacityTimeout, popup);
        this.buttonContentUrl = buttonContentUrl;
    }

    @Override
    public FVButtonType getButtonType() {
        return FVButtonType.IFRAME;
    }


    public String getButtonContentUrl() {
        return buttonContentUrl;
    }
}
