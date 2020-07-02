package com.danjb.engine.util;

/**
 * Mathematical utility functions.
 *
 * @author Dan Bryce
 */
public class MathUtils {

    /**
     * Clamps an integer between 2 limits.
     *
     * @param val
     * @param min
     * @param max
     * @return
     */
    public static int clampi(int val, int min, int max) {
        if (val < min) {
            return min;
        } else if (val > max) {
            return max;
        } else {
            return val;
        }
    }

    /**
     * Clamps a float between 2 limits.
     *
     * @param val
     * @param min
     * @param max
     * @return
     */
    public static float clampf(float val, float min, float max) {
        if (val < min) {
            return min;
        } else if (val > max) {
            return max;
        } else {
            return val;
        }
    }

    /**
     * Clamps a double between 2 limits.
     *
     * @param val
     * @param min
     * @param max
     * @return
     */
    public static double clampd(double val, double min, double max) {
        if (val < min) {
            return min;
        } else if (val > max) {
            return max;
        } else {
            return val;
        }
    }

    /**
     * Determines if 2 values have the same sign.
     *
     * @param a
     * @param b
     * @return
     */
    public static boolean sameSign(int a, float b) {
        return a == b || (a > 0) == (b > 0);
    }

}
