package com.fortvision.minisites.model;

import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class FVButton {

    private final boolean dismissible;

    private final int dismissSize;

    private final DimensionedSize width;

    private final DimensionedSize height;

    @NonNull
    private final Anchor anchor;

    @NonNull
    private final String campaignId;

    private final int designId;

    private final float opacity;

    private final int opacityTimeout;

    @NonNull
    private final Popup popup;

    public double getAspectRation() {
        return height.getSize() / width.getSize();
    }

    public abstract FVButtonType getButtonType();
}
