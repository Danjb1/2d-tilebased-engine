package engine.game.tiles;

import engine.game.physics.CollisionResult;
import engine.game.physics.Hitbox;
import engine.game.physics.Hitbox.CollisionNode;

/**
 * Class representing a Tile within the foreground layer of the Level.
 *
 * <p>ForegroundTiles support collision, whereas Tiles exist at a purely
 * abstract level.
 *
 * @author Dan Bryce
 */
public abstract class ForegroundTile extends Tile {

    /*
     * Tile types.
     */
    public static final int TYPE_AIR               = 0;
    public static final int TYPE_SOLID_BLOCK       = 1;
    public static final int TYPE_SLOPE_RIGHT       = 2;
    public static final int TYPE_SLOPE_LEFT        = 3;
    public static final int TYPE_SLOPE_CEIL_RIGHT  = 4;
    public static final int TYPE_SLOPE_CEIL_LEFT   = 5;
    public static final int TYPE_SEMISOLID         = 6;

    /**
     * ID that is always mapped to an air tile.
     *
     * <p>This ensures that we can always retrieve an air tile for when entities
     * are falling out of the world, or to use in place of tiles that can't be
     * loaded.
     */
    public static final int ID_AIR = 0;

    /**
     * ID that is always mapped to a solid block.
     *
     * <p>This ensures that we can always retrieve a solid tile for collision
     * with the level edges.
     */
    public static final int ID_SOLID_BLOCK = 1;

    /**
     * Creates a ForegroundTile with the given ID.
     *
     * @param id
     */
    public ForegroundTile(int id) {
        super(id);
    }

    /**
     * Checks for an x-collision and adds it to the CollisionResult.
     *
     * @param result CollisionResult to which Collisions should be added.
     * @param dstX
     * The absolute x-position of the collision node after the attempted
     * movement.
     * @param node Node that has collided.
     */
    public void checkForCollision_X(
            CollisionResult result, float dstX, CollisionNode node) {
        // No collision by default
    }

    /**
     * Checks for a y-collision and adds it to the CollisionResult.
     *
     * @param result
     * @param dstY
     * The absolute y-position of the collision node after the attempted
     * movement.
     * @param node Node that has collided.
     */
    public void checkForCollision_Y(
            CollisionResult result, float dstY, CollisionNode node) {
        // No collision by default
    }

    /**
     * Determines whether or not this Tile is completely solid.
     *
     * @return
     */
    public boolean isSolid() {
        return false;
    }

    /**
     * Determines whether or not this Tile has special collision properties.
     *
     * @return
     */
    public boolean hasSpecialCollisionProperties() {
        return false;
    }

    /**
     * Called when a Hitbox collides with this Tile in the x-axis.
     *
     * <p>For tiles that have collision, this will cause the Hitbox to bounce
     * off the tile according to its bounce coefficient. A bounce coefficient of
     * zero (the default) will cause the Hitbox to stop.
     *
     * @param result
     */
    public void hitboxCollidedX(CollisionResult result) {
        Hitbox hitbox = result.hitbox;
        float newSpeedX = -hitbox.getSpeedX() * hitbox.bounceCoefficient;
        hitbox.setSpeedX(newSpeedX);
    }

    /**
     * Called when a Hitbox collides with this Tile in the y-axis.
     *
     * @see ForegroundTile#hitboxCollidedX(CollisionResult)
     * @param result
     */
    public void hitboxCollidedY(CollisionResult result) {
        Hitbox hitbox = result.hitbox;
        float newSpeedY = -hitbox.getSpeedY() * hitbox.bounceCoefficient;
        hitbox.setSpeedY(newSpeedY);
    }

}
