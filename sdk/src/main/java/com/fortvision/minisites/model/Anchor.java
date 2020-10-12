package com.fortvision.minisites.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Represent a relative anchoring position on the UI.
 */
@Getter
@AllArgsConstructor
public class Anchor {

    /**
     * The position in percentage on the X axes
     */
    private final DimensionedSize xPos;

    /**
     * The position in percentage on the Y axes
     */
    private final DimensionedSize yPos;

    private final boolean alignedRight;
}
