package game;

import java.util.Random;

import game.physics.Hitbox;
import game.tiles.Tile;

/**
 * Utility functions.
 * 
 * @author Dan Bryce
 */
public class GameUtils {

    /**
     * Width of a Tile's image, in pixels.
     */
    public static final int TILE_IMAGE_WIDTH = 16;

    /**
     * Ratio of world units to pixels.
     */
    private static final float UNITS_PER_PIXEL =
            (float) Tile.WIDTH / TILE_IMAGE_WIDTH;

    ////////////////////////////////////////////////////////////////////////////
    // Directions
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Valid directions in the x-axis.
     */
    public enum DirectionX {

        LEFT (-1),
        NONE  (0),
        RIGHT (1);

        private int multiplier;

        private DirectionX(int multiplier) {
            this.multiplier = multiplier;
        }

        public int getMultiplier(){
            return multiplier;
        }
    }

    /**
     * Valid directions in the y-axis.
     */
    public enum DirectionY {

        UP   (-1),
        NONE  (0),
        DOWN  (1);

        private int multiplier;

        private DirectionY(int multiplier) {
            this.multiplier = multiplier;
        }

        public int getMultiplier(){
            return multiplier;
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    // Angles
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Gets the angle of the line from one Hitbox to another.
     * 
     * @param h1
     * @param h2
     * @return
     */
    public static float getAngle(Hitbox h1, Hitbox h2) {
        float dx = h1.getCentreX() - h2.getCentreX();
        float dy = h1.getCentreY() - h2.getCentreY();
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
        if (dx == 0){
            // Moving up / down
            return dy < 0 ? 0 : 180;
        }
        if (dy == 0){
            // Moving left / right
            return dx < 0 ? 270 : 90;
        }

        double angle = Math.toDegrees(Math.atan(Math.abs(dx) / Math.abs(dy)));

        // Adjust angle according to quadrant
        if (dx > 0 && dy < 0){
            // no change
        } else if (dx > 0 && dy > 0){
            angle = 180 - angle;
        } else if (dx < 0 && dy > 0){
            angle = 180 + angle;
        } else {
            angle = 360 - angle;
        }

        return (float) angle;
    }

    /**
     * Gets the x-component of the given angle.
     * 
     * @param val
     * @param angle
     * @return
     */
    public static float getAngleXComponent(float val, double angle) {
        double angleRad = Math.toRadians(angle);
        return (float) (val * Math.sin(angleRad));
    }

    /**
     * Gets the y-component of the given angle.
     * 
     * @param val
     * @param angle
     * @return
     */
    public static float getAngleYComponent(float val, double angle) {
        double angleRad = Math.toRadians(angle);
        // We need to multiply by -1
        // (something to do with trigonometry quadrants?)
        return (float) (val * Math.cos(angleRad) * (-1));
    }

    /**
     * Ensures the given angle is in the range 0-360.
     * 
     * @param angle
     * @return
     */
    public static float normaliseAngle(float angle) {
        while (angle < 0){
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
     * @param angle1
     * @param angle2
     * @return Smallest positive angle between the 2 given angles.
     */
    public static float getAngleDifference(float angle1, float angle2) {
        float angleDiff = Math.abs(angle1 - angle2);
        return Math.min(angleDiff, 360 - angleDiff);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Unit conversions
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Produces a length in world units.
     * 
     * @param val Length relative to one Tile.
     * @return
     */
    public static float worldUnits(float val) {
        /*
         * In reality, this does nothing, since a Tile is defined to be 1 unit.
         * However, using this method gives us flexibility if the definition of
         * world units ever changes. It also makes the caller's intent very
         * clear.
         */
        return val * Tile.SIZE;
    }

    /**
     * Convert from world units to pixels.
     * 
     * <p>Note that a pixel in this context is not necessarily the same as a
     * pixel of the screen. The size of a pixel is actually determined by the
     * camera; a camera with a higher zoom level will "see" all pixels at a
     * larger scale.
     * 
     * @param worldUnits
     * @return
     */
    public static float worldToPx(float worldUnits){
        return worldUnits / UNITS_PER_PIXEL;
    }

    /**
     * Convert from pixels to world units.
     * 
     * @param px
     * @return
     */
    public static float pxToWorld(int px){
        return px * UNITS_PER_PIXEL;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Random Numbers
    ////////////////////////////////////////////////////////////////////////////
    
    /**
     * Returns either -1 or 1 at random.
     * 
     * @param i
     * @return
     */
    public static int randomSign(int i) {
        return i * (Math.random() < 0.5 ? -1 : 1);
    }

    /**
     * Returns a random double between the 2 limits.
     * 
     * @param min
     * @param max
     * @return
     */
    public static double randBetween(double min, double max){
        return min + (Math.random() * (max - min));
    }

    /**
     * Returns a random double between the 2 limits, using the given random
     * number generator.
     * 
     * @param min
     * @param max
     * @return
     */
    public static double randBetween(double min, double max, Random random) {
        return min + (random.nextDouble() * (max - min));
    }

    /**
     * Returns a random int between the 2 limits.
     * 
     * @param min
     * @param max
     * @return
     */
    public static int randBetween(int min, int max){
        return min + (int)(Math.random() * ((max - min) + 1));
    }

    /**
     * Returns a random int between the 2 limits, using the given random
     * number generator.
     * 
     * @param min
     * @param max
     * @return
     */
    public static int randBetween(int min, int max, Random random) {
        return min + (int)(random.nextDouble() * ((max - min) + 1));
    }

}
