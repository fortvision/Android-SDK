package com.fortvision.minisites.model;

import androidx.annotation.Nullable;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represent Popup related data of an {@link FVButton}.
 */
@Getter
@AllArgsConstructor
public class Popup {

    /**
     * the url of the content
     */
    @Nullable
    private final String content;

    /**
     * determine whether to preload the popup content
     */
    private final boolean preload;

    /**
     * the start margin to apply to this popup
     */
    private final int startMargin;

    /**
     * the end margin to apply to this popup
     */
    private final int endMargin;

    /**
     * the top margin to apply to this popup
     */
    private final int topMargin;

    /**
     * the bottom margin to apply to this popup
     */
    private final int bottomMargin;

    /**
     * Construct a new {@code Popup}
     * see {@link Popup#Popup(String, boolean, int, int, int, int)}
     */
    public Popup(@Nullable String content, boolean preload/*, int width, int height*/) {
        this(content, preload, 0, 0, 0, 0);
    }
}
