package engine.game.tiles;

import engine.game.physics.Hitbox;

/**
 * Abstract Slope class, to be subclassed by specific Slope types.
 *
 * @author Dan Bryce
 */
public abstract class Slope extends ForegroundTile {

    /*
     * Slope flag constants.
     *
     * These can be used (with the '&' operator) to create conditions that apply
     * only to certain Slope tiles, e.g.
     *
     *     if (slope.getSlopeFlags() & SLOPE_LEFT > 0)
     */
    public static final int SLOPE_LEFT          = 0xff000000;
    public static final int SLOPE_RIGHT         = 0x00ff0000;
    public static final int SLOPE_CEILING_LEFT  = 0x0000ff00;
    public static final int SLOPE_CEILING_RIGHT = 0x000000ff;
    public static final int SLOPE_LEFT_ANY      = SLOPE_LEFT | SLOPE_CEILING_LEFT;
    public static final int SLOPE_RIGHT_ANY     = SLOPE_RIGHT | SLOPE_CEILING_RIGHT;
    public static final int SLOPE_FLOOR_ANY     = SLOPE_LEFT | SLOPE_RIGHT;
    public static final int SLOPE_CEILING_ANY   = SLOPE_CEILING_LEFT | SLOPE_CEILING_RIGHT;
    public static final int NOT_ON_SLOPE        = 0;

    /**
     * Slope flags of this Slope tile.
     */
    protected int slopeFlags;

    /**
     * Constructs a Slope with the given Slope flags.
     *
     * @param id
     * @param slopeFlags
     */
    protected Slope(int id, int slopeFlags) {
        super(id);

        this.slopeFlags = slopeFlags;
    }

    public boolean isFloorSlope() {
        return (slopeFlags & SLOPE_FLOOR_ANY) != 0;
    }

    public boolean isCeilingSlope() {
        return (slopeFlags & SLOPE_CEILING_ANY) != 0;
    }

    public boolean isLeftSlope() {
        return (slopeFlags & SLOPE_LEFT_ANY) != 0;
    }

    public boolean isRightSlope() {
        return (slopeFlags & SLOPE_RIGHT_ANY) != 0;
    }

    /**
     * Gets the y-position of the Slope at the given x-position.
     *
     * <p>Positions range from 0 - Tile.SIZE.
     *
     * @param distIntoTileX
     * @return
     */
    public abstract float getSlopeY_At_X(float distIntoTileX);

    /**
     * Determines if a point is inside the solid part of this Slope.
     *
     * <p>The point is relative to the top-left of the Tile.
     *
     * @param x Position from 0 - Tile.WIDTH.
     * @param y Position from 0 - Tile.HEIGHT.
     * @return
     */
    public abstract boolean isPointInSlope(float x, float y);

    /**
     * Changes the given Hitbox's speed to cause it to bounce off this Slope.
     *
     * <p>If you imagine 2 lines protruding from a Slope, one horizontal and one
     * vertical, those lines create 3 different sectors. These correspond to the
     * 3 different ways each Slope can be hit, e.g.
     *
     * <pre>
     *  #\ 1 |
     *  ##\  | 2
     *  ###\ |____
     *  ####\
     *  #####\  3
     * </pre>
     *
     * <p>1: Glancing blow from the left.
     * <br>2: Direct hit.
     * <br>3. Glancing blow from the right.
     *
     * <p>Fortunately, after some experimentation, it would appear that these
     * different cases can all be handled in the same way - just swap the x- and
     * y-speeds, and possibly invert them (depending on the slope).
     *
     * @param hitbox
     * @param bounceCoefficient
     */
    public abstract void collide(Hitbox hitbox, float bounceCoefficient);

    ////////////////////////////////////////////////////////////////////////////
    // Slope Subclasses
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Left Slope.
     *
     * <pre>
     *  #
     *  ###
     *  #####
     * </pre>
     */
    public static class Left extends Slope {

        public Left(int id) {
            super(id, SLOPE_LEFT);
        }

        @Override
        public boolean isPointInSlope(float x, float y) {
            return y >= x;
        }

        @Override
        public float getSlopeY_At_X(float distIntoTileX) {
            return Tile.HEIGHT - distIntoTileX;
        }

        @Override
        public void collide(Hitbox hitbox, float bounceCoefficient) {
            hitbox.setSpeedX(hitbox.getSpeedY() * bounceCoefficient);
            hitbox.setSpeedY(hitbox.getSpeedX() * bounceCoefficient);
        }

    }

    /**
     * Right Slope.
     *
     * <pre>
     *      #
     *    ###
     *  #####
     * </pre>
     */
    public static class Right extends Slope {

        public Right(int id) {
            super(id, SLOPE_RIGHT);
        }

        @Override
        public boolean isPointInSlope(float x, float y) {
            return x + y >= Tile.HEIGHT;
        }

        @Override
        public float getSlopeY_At_X(float distIntoTileX) {
            return distIntoTileX;
        }

        @Override
        public void collide(Hitbox hitbox, float bounceCoefficient) {
            hitbox.setSpeedX(-hitbox.getSpeedY() * bounceCoefficient);
            hitbox.setSpeedY(-hitbox.getSpeedX() * bounceCoefficient);
        }

    }

    /**
     * Left Ceiling Slope.
     *
     * <pre>
     *  #####
     *  ###
     *  #
     * </pre>
     */
    public static class LeftCeiling extends Slope {

        public LeftCeiling(int id) {
            super(id, SLOPE_CEILING_LEFT);
        }

        @Override
        public boolean isPointInSlope(float x, float y) {
            return x + y <= Tile.HEIGHT;
        }

        @Override
        public float getSlopeY_At_X(float distIntoTileX) {
            return Tile.HEIGHT - distIntoTileX;
        }

        @Override
        public void collide(Hitbox hitbox, float bounceCoefficient) {
            hitbox.setSpeedX(-hitbox.getSpeedY() * bounceCoefficient);
            hitbox.setSpeedY(-hitbox.getSpeedX() * bounceCoefficient);
        }

    }

    /**
     * Right Ceiling Slope.
     *
     * <pre>
     *  #####
     *    ###
     *      #
     * </pre>
     */
    public static class RightCeiling extends Slope {

        public RightCeiling(int id) {
            super(id, SLOPE_CEILING_RIGHT);
        }

        @Override
        public boolean isPointInSlope(float x, float y) {
            return y <= x;
        }

        @Override
        public float getSlopeY_At_X(float distIntoTileX) {
            return distIntoTileX;
        }

        @Override
        public void collide(Hitbox hitbox, float bounceCoefficient) {
            hitbox.setSpeedX(hitbox.getSpeedY() * bounceCoefficient);
            hitbox.setSpeedY(hitbox.getSpeedX() * bounceCoefficient);
        }

    }

}
