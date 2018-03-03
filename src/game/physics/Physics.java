package game.physics;

import game.GameUtils;
import game.Level;
import game.Logic;
import game.tiles.ForegroundTile;
import game.tiles.Tile;
import launcher.Logger;

/**
 * Class containing static methods and fields pertaining to the game's physics.
 *
 * @author Dan Bryce
 */
public abstract class Physics {

    /**
     * Strength of gravity, in world units per second per second.
     */
    public static final float GRAVITY = GameUtils.worldUnits(40);

    /**
     * Strength of ground friction, as a multiplier.
     */
    public static final float GROUND_FRICTION = 0.9925f;

    /**
     * Strength of air friction, as a multiplier.
     */
    public static final float AIR_FRICTION = 0.9995f;

    /**
     * Minimum speed at which Entities are considered to be moving.
     *
     * <p>Measured in world units per second.
     */
    public static final float MOVING_SPEED = GameUtils.worldUnits(0.25f);

    /**
     * The smallest possible unit of distance in the game world.
     *
     * <p>In fact, the game engine permits smaller distances than this, but it
     * is highly unlikely (if not impossible) that we will ever have to contend
     * with such distances.
     *
     * <p>This is necessary where hitboxes and tiles are concerned; the game
     * world is a continuous space, but is divided into discrete blocks (tiles).
     * Each tile should fill all available space between its top / left edge and
     * the top / left edge of the next tile.
     *
     * <p>That is, we have the following requirement:
     *
     * <ul>
     *     <li>0 * Tile.WIDTH  <=  tile[0]  <  1 * Tile.WIDTH</li>
     *     <li>1 * Tile.WIDTH  <=  tile[1]  <  2 * Tile.WIDTH</li>
     *     <li>2 * Tile.WIDTH  <=  tile[2]  <  3 * Tile.WIDTH, etc.</li>
     * </ul>
     *
     * <p>So how do we determine the right edge of a tile? We need the greatest
     * possible value that is *strictly less than* the left edge of the next
     * tile along.
     *
     * <p>That is where this constant comes in; subtracting this distance from
     * (tile left + tile width) gives us the position of the right edge of the
     * tile.
     *
     * <p>This is important for a number of reasons. For example, a player
     * should be able to fit inside a single tile without his hitbox overlapping
     * into the tile below. However, as soon as gravity changes his position
     * even slightly, he should collide with the ground beneath him. This
     * ensures that such a collision will happen every frame, and the player
     * will be continually repositioned in the correct position atop the ground
     * tile.
     */
    public static final float SMALLEST_DISTANCE =
            GameUtils.worldUnits(0.0001f);

    /**
     * Apply friction to the given x-speed and return the result.
     * @param speedX
     * @param delta
     * @return
     */
    public static float applyFriction(float speedX, int delta) {
        return applyDeceleration(speedX, delta, GROUND_FRICTION);
    }

    /**
     * Apply deceleration to the given x-speed and return the result.
     *
     * <p>See here for the origin of this variable-timestep implementation:
     * http://gamedev.stackexchange.com/a/20962
     *
     * @param speedX
     * @param delta
     * @param deceleration
     * @return
     */
    public static float applyDeceleration(float speedX, int delta,
            double deceleration) {
        return (float) (speedX * Math.pow(deceleration, delta));
    }

    /**
     * Apply some acceleration to the given speed and return the result.
     *
     * @param speed
     * @param delta
     * @param acceleration
     * @return
     */
    public static float applyAcceleration(float speed, int delta,
            float acceleration) {
        return speed + (acceleration * delta / 1000);
    }

    /**
     * Apply gravity to the given y-speed and return the result.
     *
     * @param speedY
     * @param delta
     * @param coefficient Gravity multiplier.
     * @return
     */
    public static float applyGravity(float speedY, int delta,
            float coefficient) {
        return speedY + (coefficient * GRAVITY * delta / 1000);
    }

    /**
     * Gets the CollisionResult of attempting to moving the given Hitbox the
     * given distance.
     *
     * @param logic
     * @param hitbox
     * @param dx Attempted distance travelled in x-direction.
     * @param dy Attempted distance travelled in y-direction.
     * @return
     */
    public static CollisionResult getCollisionResult(Logic logic,
            Hitbox hitbox, float dx, float dy) {

        if (Math.abs(dx) >= Tile.WIDTH){
            /*
             * Entity has attempted to move further than a single Tile, which
             * can be problematic for collision detection. This can happen if
             * the game is lagging.
             *
             * Currently our solution is just to move as far as possible while
             * preserving the x:y ratio of the movement.
             */
            Logger.log("Trying to move too far in the x-axis: %f", dx);
            if (dx > dy){
                float ratio = dy / dx;
                dx = Tile.WIDTH - Physics.SMALLEST_DISTANCE;
                dy = ratio * dx;
            } // If dy > dx, it will be handled in the block below
        }

        if (Math.abs(dy) >= Tile.HEIGHT){
            // See comment above
            Logger.log("Trying to move too far in the y-axis: %f", dy);
            float ratio = dx / dy;
            dy = Tile.HEIGHT - Physics.SMALLEST_DISTANCE;
            dx = ratio * dy;
        }

        CollisionResult result = new CollisionResult(hitbox, dx, dy);

        // Move in each axis separately
        if (dx != 0) {
            detectCollisionsX(result, logic,
                    hitbox.getCollisionNodesY());
        }
        if (dy != 0) {
            detectCollisionsY(result, logic,
                    hitbox.getCollisionNodesX());
        }

        // Adjust the CollisionResult for Slopes
        SlopeUtils.doSlopePostProcessing(result, logic);

        return result;
    }

    /**
     * Detects collisions in the x-direction.
     *
     * @param result CollisionResult to update after detecting collisions.
     * @param logic
     * @param collisionNodesY
     */
    private static void detectCollisionsX(CollisionResult result, Logic logic,
            float[] collisionNodesY) {

        Level level = logic.getLevel();

        // Get collision results for each node along the Entity's edge
        for (float node : collisionNodesY){

            float y = result.getHitbox().getTop() + node;
            float xBefore = result.getCollisionEdgeX();
            float xAfter = xBefore + result.getAttemptedDx();

            int tileX = Tile.getTileX(xAfter);
            int tileY = Tile.getTileY(y);
            int tileId = level.getForeground().getTile(tileX, tileY);
            ForegroundTile tile = (ForegroundTile) logic.getTile(tileId);

            if (SlopeUtils.isTileBehindSlope(result, tileX, tileY, logic) ||
                    SlopeUtils.isTileAtBottomOfFloorSlope(result, tileX, tileY, logic) ||
                    SlopeUtils.isTileAtTopOfCeilingSlope(result, tileX, tileY, logic)){
                // Don't collide with Tiles behind or at the bottom of slopes,
                // lest they interfere with the Slope physics.
                continue;
            } else if (tile.hasCollisionX(result, logic, tileX, tileY)){
                tile.collisionOccurredX(result);
            }
        }

        result.resolveCollisions_X();
    }

    /**
     * Detects collisions in the y-direction.
     * @param result CollisionResult to update after detecting collisions.
     * @param logic
     * @param collisionNodesX
     */
    private static void detectCollisionsY(CollisionResult result, Logic logic,
            float[] collisionNodesX) {

        Level level = logic.getLevel();

        // Get collision results for each node along the Entity's edge
        for (float node : collisionNodesX){

            // Use the already-calculated x-collision result
            float x = result.getLeft() + node;
            float yBefore = result.getCollisionEdgeY();
            float yAfter = yBefore + result.getAttemptedDy();

            int tileX = Tile.getTileX(x);
            int tileY = Tile.getTileY(yAfter);
            int tileId = level.getForeground().getTile(tileX, tileY);
            ForegroundTile tile = (ForegroundTile) logic.getTile(tileId);

            if (SlopeUtils.isTileBelowFloorSlope(result, tileX, tileY, logic)){
                // Don't collide with Tiles underneath slopes, lest they
                // interfere with the Slope physics.
                continue;
            } else if (tile.hasCollisionY(result, logic, tileX, tileY)){
                tile.collisionOccurredY(result);
            }
        }

        result.resolveCollisions_Y();
    }

}
