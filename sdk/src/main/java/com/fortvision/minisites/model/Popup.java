package com.fortvision.minisites.model;

import androidx.annotation.Nullable;

/**
 * Represent Popup related data of an {@link FVButton}.
 */

public class Popup {

    @Nullable
    private final String content;

    private final boolean preload;

    private final int startMargin;

    private final int endMargin;

    private final int topMargin;

    private final int bottomMargin;

    /**
     * Construct a new {@code Popup}
     *
     * @param content      - the url of the content
     * @param preload      - determine whether to preload the popup content
     * @param startMargin  - the start margin to apply to this popup
     * @param endMargin    - the end margin to apply to this popup
     * @param topMargin    - the top margin to apply to this popup
     * @param bottomMargin - the bottom margin to apply to this popup
     */
    public Popup(@Nullable String content, boolean preload, int startMargin, int endMargin, int topMargin, int bottomMargin) {
        this.content = content;
        this.preload = preload;
        this.startMargin = startMargin;
        this.endMargin = endMargin;
        this.topMargin = topMargin;
        this.bottomMargin = bottomMargin;
    }

    /**
     * Construct a new {@code Popup}
     * see {@link Popup#Popup(String, boolean, int, int, int, int)}
     */
    public Popup(@Nullable String content, boolean preload/*, int width, int height*/) {
        this(content, preload, 0, 0, 0, 0);
    }

    @Nullable
    public String getContent() {
        return content;
    }

    public boolean isPreload() {
        return preload;
    }

    public int getStartMargin() {
        return startMargin;
    }

    public int getEndMargin() {
        return endMargin;
    }

    public int getTopMargin() {
        return topMargin;
    }

    public int getBottomMargin() {
        return bottomMargin;
    }
}
