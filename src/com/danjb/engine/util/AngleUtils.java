package com.danjb.engine.util;

import com.danjb.engine.game.physics.Hitbox;

/**
 * Utility functions relating to angles.
 *
 * @author Dan Bryce
 */
public class AngleUtils {

    /**
     * Gets the angle of the line from one Hitbox to another.
     *
     * @param h1
     * @param h2
     * @return
     */
    public static float getAngle(Hitbox h1, Hitbox h2) {
        float dx = h1.centreX() - h2.centreX();
        float dy = h1.centreY() - h2.centreY();
        return getAngle(dx, dy);
    }

    /**
     * Given 2 trajectories, gets the corresponding angle in degrees.
     *
     * @param dx
     * @param dy
     * @return
     */
    public static float getAngle(float dx, float dy) {

        // Edge cases when moving in only one axis
        if (dx == 0) {
            // Moving up / down
            return dy < 0 ? 0 : 180;
        }
        if (dy == 0) {
            // Moving left / right
            return dx < 0 ? 270 : 90;
        }

        double angle = Math.toDegrees(Math.atan(Math.abs(dx) / Math.abs(dy)));

        // Adjust angle according to quadrant
        if (dx > 0 && dy < 0) {
            // no change
        } else if (dx > 0 && dy > 0) {
            angle = 180 - angle;
        } else if (dx < 0 && dy > 0) {
            angle = 180 + angle;
        } else {
            angle = 360 - angle;
        }

        return (float) angle;
    }

    /**
     * Gets the x-component of the given angle.
     *
     * @param angle Angle in degrees, where 0 = up, 90 = right, etc.
     * @return 0-1 (0 when angle is vertical, 1 when angle is horizontal).
     */
    public static float getAngleXComponent(double angle) {
        double angleRad = Math.toRadians(angle);
        return (float) Math.sin(angleRad);
    }

    /**
     * Gets the y-component of the given angle.
     *
     * @param angle Angle in degrees, where 0 = up, 90 = right, etc.
     * @return 0-1 (0 when angle is horizontal, 1 when angle is vertical).
     */
    public static float getAngleYComponent(double angle) {
        double angleRad = Math.toRadians(angle);
        return (float) -Math.cos(angleRad);
    }

    /**
     * Ensures the given angle is in the range 0-360.
     *
     * @param angle Angle in degrees.
     * @return
     */
    public static float normaliseAngle(float angle) {
        while (angle < 0) {
            angle += 360;
        }
        return angle % 360;
    }

    /**
     * Calculates the difference between the 2 given angles.
     *
     * <p>This also handles the "wrap-around" case where one angle is close to
     * 360 and the other is close to 0, but the angle between them is actually
     * small.
     *
     * @param angle1 Angle in degrees.
     * @param angle2 Angle in degrees.
     * @return Smallest positive angle between the 2 given angles.
     */
    public static float getAngleDifference(float angle1, float angle2) {
        float angleDiff = Math.abs(angle1 - angle2);
        return Math.min(angleDiff, 360 - angleDiff);
    }

}
