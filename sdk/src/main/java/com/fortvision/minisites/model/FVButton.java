package com.fortvision.minisites.model;

import android.support.annotation.NonNull;

/**
 * An abstract base class for all FV button types.
 */

public abstract class FVButton {

    private final boolean dismissible;

    private final int dismissSize;

    private final int width;

    private final int height;

    @NonNull
    private final Anchor anchor;

    @NonNull
    private final String campaignId;

    private final int designId;

    private final float opacity;

    private final int opacityTimeout;

    @NonNull
    private final Popup popup;

    private final float aspectRatio;


    protected FVButton(boolean dismissible, int dismissSize, int width, int height, @NonNull Anchor anchor, @NonNull String campaignId,
                       int designId, float opacity, int opacityTimeout, @NonNull Popup popup) {
        this.dismissible = dismissible;
        this.dismissSize = dismissSize;
        this.width = width;
        this.height = height;
        this.anchor = anchor;
        this.campaignId = campaignId;
        this.designId = designId;
        this.opacity = opacity;
        this.opacityTimeout = opacityTimeout;
        this.popup = popup;
        aspectRatio = (float) height / width;
    }

    public boolean isDismissible() {
        return dismissible;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @NonNull
    public Anchor getAnchor() {
        return anchor;
    }

    @NonNull
    public String getCampaignId() {
        return campaignId;
    }

    public int getDesignId() {
        return designId;
    }

    public float getOpacity() {
        return opacity;
    }

    public int getOpacityTimeout() {
        return opacityTimeout;
    }

    @NonNull
    public Popup getPopup() {
        return popup;
    }

    public abstract FVButtonType getButtonType();

    public int getDismissSize() {
        return dismissSize;
    }

    public float getAspectRatio() {
        return aspectRatio;
    }
}
