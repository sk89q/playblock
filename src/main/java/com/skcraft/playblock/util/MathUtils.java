package com.skcraft.playblock.util;

/**
 * Math utility functions.
 */
public final class MathUtils {

    private MathUtils() {
    }

    /**
     * Clamp a value between two bounds (inclusive).
     * 
     * @param value the value
     * @param min the minimum
     * @param max the maximum
     * @return the clamped value
     */
    public static float clamp(float value, float min, float max) {
        return value < min ? min : (value > max ? max : value);
    }

}
