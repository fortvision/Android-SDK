package com.fortvision.minisites.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class DimensionedSize {

    @NonNull
    private double size;

    @NonNull
    private String dimension;

    public int toInt() {
        return (int) size;
    }

    public boolean isPx() {
        return dimension.equals("px");
    }

    public boolean isPercent() {
        return dimension.equals("%");
    }
}
