package com.fortvision.minisites.model;

import android.support.annotation.NonNull;
import android.text.TextUtils;

/**
 * A POJO representing a FortVision simple image button.
 */

public class VideoButton extends FVButton {

    @NonNull
    private final String videoURL;

    //private final boolean bigVideoAlignmentBottom;

    private final boolean hasContent;

    private final float bigAspectRatio;

    public VideoButton(boolean dismissible, int dismissSize, int width, int height, @NonNull Anchor anchor,
                       @NonNull String campaignId, int designId, float opacity, int opacityTimeout,
                       @NonNull Popup popup, @NonNull String videoURL, /*boolean bigVideoAlignmentBottom,*/ int bigWidth, int bigHeight) {
        super(dismissible, dismissSize, width, height, anchor, campaignId, designId, opacity, opacityTimeout, popup);
        this.videoURL = videoURL;
        //this.bigVideoAlignmentBottom = bigVideoAlignmentBottom;
        hasContent = !TextUtils.isEmpty(getPopup().getContent());
        bigAspectRatio = (float) bigHeight / bigWidth;
    }

    @Override
    public FVButtonType getButtonType() {
        return hasContent ? FVButtonType.VIDEO_WITH_CONTENT : FVButtonType.VIDEO_WITHOUT_CONTENT;
    }

    @NonNull
    public String getVideoURL() {
        return videoURL;
    }

   /* public boolean isBigVideoAlignmentBottom() {
        return bigVideoAlignmentBottom;
    }*/

    public float getBigAspectRatio() {
        return bigAspectRatio;
    }

}
