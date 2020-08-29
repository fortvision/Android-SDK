package com.fortvision.minisites.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

/**
 * A POJO representing a FortVision simple image button.
 */

public class ImageButton extends FVButton {

    @Nullable
    private final String imageLeftURL;

    @Nullable
    private final String imageRightURL;

    @NonNull
    private final String imageDragURL;

    public ImageButton(boolean dismissible, int dismissSize, int width, int height, @NonNull Anchor anchor,
                       @NonNull String campaignId, int designId, float opacity, int opacityTimeout,
                       @NonNull Popup popup, @Nullable String imageLeftURL,
                       @Nullable String imageRightURL, @NonNull String imageDragURL) {
        super(dismissible, dismissSize, width, height, anchor, campaignId, designId, opacity, opacityTimeout, popup);
        this.imageLeftURL = imageLeftURL;
        this.imageRightURL = imageRightURL;
        this.imageDragURL = imageDragURL;
    }

    @Override
    public FVButtonType getButtonType() {
        return FVButtonType.SIMPLE_IMAGE;
    }

    @NonNull
    public String getAnchorImageURL(@NonNull Anchor anchor) {
        if (anchor.isAlignedRight() && !TextUtils.isEmpty(imageRightURL))
            return imageRightURL;

        if (!anchor.isAlignedRight() && !TextUtils.isEmpty(imageLeftURL))
            return imageLeftURL;

        return imageDragURL;
    }

    @NonNull
    public String getDefaultImageURL() {
        return imageDragURL;
    }


    public String getRightImageURL() {
        return imageRightURL;
    }

    public String getLeftImageURL() {
        return imageLeftURL;
    }
}
