package engine.game.tiles;

import engine.game.Logic;
import engine.game.physics.CollisionResult;

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
     * are falling out of the world.
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
     * Determines whether or not Entities can collide with this Tile when
     * attempting to move according to the given CollisionResult.
     *
     * @param result
     * @param logic
     * @param tileX
     * @param tileY
     * @return
     */
    public boolean hasCollisionX(CollisionResult result, Logic logic, int tileX,
            int tileY){
        return false;
    }

    /**
     * Determines whether or not Entities can collide with this Tile when
     * attempting to move according to the given CollisionResult.
     *
     * @param result
     * @param logic
     * @param tileX
     * @param tileY
     * @return
     */
    public boolean hasCollisionY(CollisionResult result, Logic logic, int tileX,
            int tileY){
        return false;
    }

    /**
     * Modifies the given CollisionResult after a collision with this Tile.
     *
     * @param collision
     */
    public void collisionOccurredX(CollisionResult collision){
        // Solid tiles should override this
    }

    /**
     * Modifies the given CollisionResult after a collision with this Tile.
     *
     * @param collision
     */
    public void collisionOccurredY(CollisionResult collision){
        // Solid tiles should override this
    }

}
