package com.fortvision.minisites.model;

import androidx.annotation.NonNull;

import lombok.Getter;

/**
 * A POJO representing a FortVision simple image button.
 */
@Getter
public class AutoClickButton extends FVButton {

    public AutoClickButton(boolean dismissible, int dismissSize, DimensionedSize width, DimensionedSize height, @NonNull Anchor anchor,
                           @NonNull String campaignId, int designId, float opacity, int opacityTimeout,
                           @NonNull Popup popup) {
        super(dismissible, dismissSize, width, height, anchor, campaignId, designId, opacity, opacityTimeout, popup);
    }

    @Override
    public FVButtonType getButtonType() {
        return FVButtonType.AUTO_CLICK;
    }
}
