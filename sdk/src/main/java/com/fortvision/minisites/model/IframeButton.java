package com.fortvision.minisites.model;

import androidx.annotation.NonNull;

import lombok.Getter;

/**
 * A POJO representing a FortVision button that shows a specific url.
 */
@Getter
public class IframeButton extends FVButton {

    @NonNull
    private final String buttonContentUrl;

    public IframeButton(boolean dismissible, int dismissSize, DimensionedSize width, DimensionedSize height, @NonNull Anchor anchor,
                        @NonNull String campaignId, int designId, float opacity, int opacityTimeout,
                        @NonNull Popup popup, @NonNull String buttonContentUrl) {
        super(dismissible, dismissSize, width, height, anchor, campaignId, designId, opacity, opacityTimeout, popup);
        this.buttonContentUrl = buttonContentUrl;
    }

    @Override
    public FVButtonType getButtonType() {
        return FVButtonType.IFRAME;
    }
}
