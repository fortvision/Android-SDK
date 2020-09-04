package com.fortvision.minisites.model;

import androidx.annotation.NonNull;

import android.text.TextUtils;

import lombok.Getter;

/**
 * A POJO representing a FortVision simple image button.
 */
@Getter
public class VideoButton extends FVButton {

    @NonNull
    private final String videoURL;

    //private final boolean bigVideoAlignmentBottom;

    private final boolean hasContent;

    private final float bigAspectRatio;

    public VideoButton(boolean dismissible, int dismissSize, DimensionedSize width, DimensionedSize height, @NonNull Anchor anchor,
                       @NonNull String campaignId, int designId, float opacity, int opacityTimeout,
                       @NonNull Popup popup, @NonNull String videoURL, int bigWidth, int bigHeight) {
        super(dismissible, dismissSize, width, height, anchor, campaignId, designId, opacity, opacityTimeout, popup);
        this.videoURL = videoURL;
        hasContent = !TextUtils.isEmpty(getPopup().getContent());
        bigAspectRatio = (float) bigHeight / bigWidth;
    }

    @Override
    public FVButtonType getButtonType() {
        return hasContent ? FVButtonType.VIDEO_WITH_CONTENT : FVButtonType.VIDEO_WITHOUT_CONTENT;
    }

}
