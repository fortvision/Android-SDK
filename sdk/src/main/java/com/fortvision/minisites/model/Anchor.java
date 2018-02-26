package com.fortvision.minisites.model;

/**
 * Represent a relative anchoring position on the UI.
 */

public class Anchor {

    private final float xPos;

    private final float yPos;

    private final boolean alignedRight;

    /**
     * Construct a new {@code Anchor}
     *
     * @param xPos         - The position in percentage on the X axes
     * @param yPos         - The position in percentage on the Y axes
     * @param alignedRight - Determine the side of the anchoring
     */
    public Anchor(float xPos, float yPos, boolean alignedRight) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.alignedRight = alignedRight;
    }

    /**
     * @return The position in percentage on the X axes
     */
    public float getxPos() {
        return xPos;
    }

    /**
     * @return The position in percentage on the Y axes
     */
    public float getyPos() {
        return yPos;
    }

    public boolean isAlignedRight() {
        return alignedRight;
    }
}
