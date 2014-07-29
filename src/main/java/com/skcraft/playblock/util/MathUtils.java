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
     * @param value
     *            the value
     * @param min
     *            the minimum
     * @param max
     *            the maximum
     * @return the clamped value
     */
    public static float clamp(float value, float min, float max) {
        return value < min ? min : (value > max ? max : value);
    }

    /**
     * Quadratic ease-in function.
     * 
     * @param t
     *            time
     * @param b
     *            initial value
     * @param c
     *            change in value
     * @param d
     *            duration
     * @return the value
     */
    public static double easeInQuad(double t, double b, double c, double d) {
        if (t <= 0)
            return b;
        if (t > d)
            return b + c;
        t /= d;
        return c * t * t + b;
    }

    /**
     * Cubic ease-in/out function.
     * 
     * @param t
     *            time
     * @param b
     *            initial value
     * @param c
     *            change in value
     * @param d
     *            duration
     * @return the value
     */
    public static double easeInOutCubic(double t, double b, double c, double d) {
        if (t <= 0)
            return b;
        if (t > d)
            return b + c;
        t /= d / 2;
        if (t < 1)
            return c / 2 * t * t * t + b;
        t -= 2;
        return c / 2 * (t * t * t + 2) + b;
    }

}
