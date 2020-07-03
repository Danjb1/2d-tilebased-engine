package com.danjb.engine.game.physics;

import java.util.Set;

import com.danjb.engine.application.Logger;
import com.danjb.engine.game.level.Level;
import com.danjb.engine.game.level.TileProvider;
import com.danjb.engine.game.physics.Hitbox.CollisionNode;
import com.danjb.engine.game.tiles.PhysicsTile;
import com.danjb.engine.game.tiles.PostProcessingTile;
import com.danjb.engine.game.tiles.Tile;
import com.danjb.engine.util.GameUtils;

/**
 * Class containing static methods and fields pertaining to the game's physics.
 *
 * @author Dan Bryce
 */
public abstract class Physics {

    /**
     * Minimum speed at which Hitboxes are considered to be moving.
     *
     * <p>Speeds lower than this are negligible, so it is simpler to consider
     * Hitboxes stationary.
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
     * <p>This is important for a number of reasons. For example, a Hitbox the
     * height of a tile should be able to fit inside a single tile without its
     * hitbox overlapping the tile below. However, as soon as any gravity is
     * applied whatsoever, a collision should be registered with the tile below.
     *
     * <p>This value ensures that such a collision will happen every frame, and
     * the Hitbox will be continually repositioned in the correct position atop
     * the ground tile.
     */
    public static final float SMALLEST_DISTANCE = GameUtils.worldUnits(0.0001f);

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
     * @param level
     * @param tileProvider
     * @param hitbox
     * @param dx Attempted distance travelled in x-direction.
     * @param dy Attempted distance travelled in y-direction.
     * @return
     */
    public static CollisionResult getCollisionResult(
            Level level,
            TileProvider tileProvider,
            Hitbox hitbox,
            float dx,
            float dy) {

        if (Math.abs(dx) > MAX_MOVE_DISTANCE) {
            /*
             * Hitbox has attempted to move further than a single Tile, which
             * can be problematic for collision detection. This can happen if
             * the game is lagging.
             *
             * Currently our solution is just to move as far as possible while
             * preserving the x:y ratio of the movement.
             */
            Logger.get().log("Trying to move too far in the x-axis: %f", dx);
            if (dx > dy) {
                float ratio = dy / dx;
                dx = Math.copySign(Tile.WIDTH - Physics.SMALLEST_DISTANCE, dx);
                dy = ratio * dx;
            } // If dy > dx, it will be handled in the block below
        }

        if (Math.abs(dy) > MAX_MOVE_DISTANCE) {
            // See comment above
            Logger.get().log("Trying to move too far in the y-axis: %f", dy);
            float ratio = dx / dy;
            dy = Math.copySign(Tile.HEIGHT - Physics.SMALLEST_DISTANCE, dy);
            dx = ratio * dy;
        }

        CollisionResult result = new CollisionResult(hitbox, dx, dy);

        if (hitbox.isSolid()) {

            /*
             * Move in each axis independently and resolve collisions along the
             * way.
             *
             * If the Hitbox intersects a PostProcessingTile at any point during
             * the movement, a PostProcessingCollision will be registered. After
             * the movement is finished, all PostProcessingCollisions will be
             * resolved.
             */

            // Move in the x-axis
            if (dx < 0) {
                detectCollisionsX(result, level, tileProvider, hitbox.getLeftNodes());
            } else if (dx > 0) {
                detectCollisionsX(result, level, tileProvider, hitbox.getRightNodes());
            }

            // Move in the y-axis
            if (dy < 0) {
                detectCollisionsY(result, level, tileProvider, hitbox.getTopNodes());
            } else if (dy > 0) {
                detectCollisionsY(result, level, tileProvider, hitbox.getBottomNodes());
            }

            /*
             * POST-PROCESSING COLLISION DETECTION.
             *
             * STAGE 1:
             * Check for PostProcessingCollisions at the initial Hitbox
             * position.
             *
             * It may be that the Hitbox is already intersecting a
             * PostProcessingTile, which affects which collisions are permitted.
             *
             *  EXAMPLE:
             *   - Hitbox is on a right slope, moving right.
             *   - After the x-movement is applied, the Hitbox is no longer
             *      intersecting the slope but is intersecting the floor tile at
             *      the top of the slope.
             *   - An x-collision is registered, but no PostProcessingCollision
             *      is registered, so the x-collision never gets invalidated.
             */
            detectPostProcessCollisions(
                    result, level, tileProvider, hitbox.getAllNodes(), 0, 0);


            /*
             * STAGE 2:
             * Check for PostProcessingCollisions after the x-movement is
             * applied.
             *
             * It is possible that the x-movement could move the Hitbox into a
             * PostProcessingTile, but the y-movement could move the Hitbox out
             * of it, so we have to check for collisions before the y-movement
             * is applied.
             *
             *  EXAMPLE:
             *   - Hitbox is in the empty tile "between" 2 right slopes (above
             *      one, and left of the other), moving right.
             *   - After the x-movement is applied, the Hitbox intersects the
             *      slope immediately to the right.
             *   - Were we to apply the y-movement, the bottom node of the
             *      Hitbox would fall into the solid block BELOW the slope,
             *      therefore it would never be inside the slope.
             */
            if (dx != 0) {
                detectPostProcessCollisions(
                        result, level, tileProvider, hitbox.getAllNodes(), dx, 0);
            }

            /*
             * STAGE 3:
             * Check for PostProcessingCollisions at the final Hitbox position.
             */
            if (dy != 0) {
                detectPostProcessCollisions(
                        result, level, tileProvider, hitbox.getAllNodes(), dx, dy);
            }

            result.finish();
        }

        return result;
    }

    /**
     * Detects collisions in the x-direction.
     *
     * @param result CollisionResult to update after detecting collisions.
     * @param level
     * @param tileProvider
     * @param nodesY
     */
    private static void detectCollisionsX(
            CollisionResult result,
            Level level,
            TileProvider tileProvider,
            CollisionNode[] nodesY) {

        // Determine the position of the Hitbox to use in collision detection;
        // we store this now because this function may return different values
        // as more collisions are added!
        float left = result.left();

        // Get collision results for each node along the Hitbox's edge
        for (CollisionNode node : nodesY) {

            // Find the new position of this CollisionNode
            float nodeX = left + node.x;
            float nodeY = result.initialNodeY(node);

            // Find the tile which this node will intersect
            int tileX = Tile.getTileX(nodeX);
            int tileY = Tile.getTileY(nodeY);
            int tileId = level.getDefaultLayer().getTile(tileX, tileY);
            PhysicsTile tile = (PhysicsTile) tileProvider.getTile(
                    level.getDefaultLayer().getLayerId(), tileId);

            // Let the tile handle this collision
            tile.checkForCollision_X(result, nodeX, node);
        }
    }

    /**
     * Detects collisions in the y-direction.
     *
     * @param result CollisionResult to update after detecting collisions.
     * @param level
     * @param tileProvider
     * @param nodesX
     */
    private static void detectCollisionsY(
            CollisionResult result,
            Level level,
            TileProvider tileProvider,
            CollisionNode[] nodesX) {

        // Determine the position of the Hitbox to use in collision detection;
        // we store this now because these functions may return different values
        // as more collisions are added!
        float left = result.left();
        float top = result.top();

        // Get collision results for each node along the Hitbox's edge
        for (CollisionNode node : nodesX) {

            // Find the new position of this CollisionNode,
            // using the already-calculated x-collision result
            float nodeX = left + node.x;
            float nodeY = top + node.y;

            // Find the tile which this node will intersect
            int tileX = Tile.getTileX(nodeX);
            int tileY = Tile.getTileY(nodeY);
            int tileId = level.getDefaultLayer().getTile(tileX, tileY);
            PhysicsTile tile = (PhysicsTile) tileProvider.getTile(
                    level.getDefaultLayer().getLayerId(), tileId);

            // Let the tile handle this collision
            tile.checkForCollision_Y(result, nodeY, node);
        }
    }

    /**
     * Detects collisions with PostProcessingTiles at each Node of the Hitbox.
     *
     * <p>If any part of the Hitbox intersects a PostProcessingTiles, the tile
     * should be informed of the collision.
     *
     * @param result CollisionResult to update after detecting collisions.
     * @param level
     * @param tileProvider
     * @param nodes
     * @param dx
     * @param dy
     */
    private static void detectPostProcessCollisions(
            CollisionResult result,
            Level level,
            TileProvider tileProvider,
            Set<CollisionNode> nodes,
            float dx,
            float dy) {

        for (CollisionNode node : nodes) {

            // Find the desired position of this CollisionNode
            // (this ignores any previously-detected collisions, since they may
            //  be overridden by a PostProcessingCollision)
            float nodeX = result.initialNodeX(node) + dx;
            float nodeY = result.initialNodeY(node) + dy;

            // Find the tile which this node will intersect
            int tileX = Tile.getTileX(nodeX);
            int tileY = Tile.getTileY(nodeY);
            int tileId = level.getDefaultLayer().getTile(tileX, tileY);
            PhysicsTile tile = (PhysicsTile) tileProvider.getTile(
                    level.getDefaultLayer().getLayerId(), tileId);

            // If it is a PostProcessingTile, add a PostProcessCollision
            if (tile instanceof PostProcessingTile) {
                PostProcessCollision collision = new PostProcessCollision(
                        (PostProcessingTile) tile, tileX, tileY, node);
                result.addPostProcessCollision(collision);
            }
        }
    }

}
