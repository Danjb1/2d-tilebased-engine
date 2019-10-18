package engine.game.physics;

import engine.game.GameUtils;
import engine.game.Level;
import engine.game.Logic;
import engine.game.tiles.ForegroundTile;
import engine.game.tiles.Tile;
import engine.launcher.Logger;

/**
 * Class containing static methods and fields pertaining to the game's physics.
 *
 * @author Dan Bryce
 */
public abstract class Physics {

    /**
     * Minimum speed at which Entities are considered to be moving.
     *
     * <p>Speeds lower than this are negligible, so it is simpler to consider
     * Entities stationary.
     *
     * <p>Measured in world units per second.
     */
    public static final float MOVING_SPEED = GameUtils.worldUnits(0.1f);

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
     * <p>This is important for a number of reasons. For example, an Entity the
     * height of a tile should be able to fit inside a single tile without its
     * hitbox overlapping the tile below. However, as soon as any gravity is
     * applied whatsoever, a collision should be registered with the tile below.
     *
     * <p>This value ensures that such a collision will happen every frame, and
     * the entity will be continually repositioned in the correct position atop
     * the ground tile.
     */
    public static final float SMALLEST_DISTANCE =
            GameUtils.worldUnits(0.0001f);

    /**
     * Strength of gravity, in world units per second per second.
     */
    public static float gravity = GameUtils.worldUnits(40);

    /**
     * Strength of ground friction, as a multiplier.
     */
    public static float groundFriction = 0.0075f;

    /**
     * Strength of air friction, as a multiplier.
     */
    public static float airFriction = 0.0005f;

    /**
     * Maximum movement distance the Physics can handle in one frame.
     */
    public static final float MAX_MOVE_DISTANCE =
            GameUtils.worldUnits(1) - SMALLEST_DISTANCE;

    /**
     * Applies deceleration to the given speed and returns the result.
     *
     * <p>See here for the origin of this variable-timestep implementation:
     * http://gamedev.stackexchange.com/a/20962
     *
     * @param speed
     * @param delta
     * @param deceleration
     * @return
     */
    public static float applyDeceleration(
            float speed, int delta, double deceleration) {
        if (deceleration == 1) {
            return speed;
        }
        return (float) (speed * Math.pow(deceleration, delta));
    }

    /**
     * Applies some acceleration to the given speed and returns the result.
     *
     * @param speed
     * @param delta
     * @param acceleration
     * @return
     */
    public static float applyAcceleration(
            float speed, int delta, float acceleration) {
        return speed + (acceleration * delta / 1000);
    }

    /**
     * Applies gravity to the given y-speed and returns the result.
     *
     * @param speedY
     * @param delta
     * @param coefficient Gravity multiplier.
     * @return
     */
    public static float applyGravity(
            float speedY, int delta, float coefficient) {
        return speedY + (coefficient * gravity * delta / 1000);
    }

    /**
     * Applies ground friction to the given x-speed and returns the result.
     *
     * @param speedX
     * @param delta
     * @param coefficient
     * @return
     */
    public static float applyGroundFriction(
            float speedX, int delta, float coefficient) {
        float multiplier = 1 - (groundFriction * coefficient);
        return applyDeceleration(speedX, delta, multiplier);
    }

    /**
     * Applies air friction to the given x-speed and returns the result.
     *
     * @param speed
     * @param delta
     * @param coefficient
     * @return
     */
    public static float applyAirFriction(
            float speed, int delta, float coefficient) {
        float multiplier = 1 - (airFriction * coefficient);
        return applyDeceleration(speed, delta, multiplier);
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

        if (Math.abs(dx) > MAX_MOVE_DISTANCE){
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
                dx = Math.copySign(Tile.WIDTH - Physics.SMALLEST_DISTANCE, dx);
                dy = ratio * dx;
            } // If dy > dx, it will be handled in the block below
        }

        if (Math.abs(dy) > MAX_MOVE_DISTANCE){
            // See comment above
            Logger.log("Trying to move too far in the y-axis: %f", dy);
            float ratio = dx / dy;
            dy = Math.copySign(Tile.HEIGHT - Physics.SMALLEST_DISTANCE, dy);
            dx = ratio * dy;
        }

        CollisionResult result = new CollisionResult(hitbox, dx, dy);

        if (hitbox.isSolid()) {

            // Move in each axis separately
            if (dx != 0) {
                detectCollisionsX(result, logic,
                        hitbox.getVerticalCollisionNodes());
            }
            if (dy != 0) {
                detectCollisionsY(result, logic,
                        hitbox.getHorizontalCollisionNodes());
            }

            // Adjust the CollisionResult for Slopes
            SlopeUtils.doSlopePostProcessing(result, logic);

        }

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

            float y = result.getHitbox().top() + node;
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
     *
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
