package com.danjb.engine.game.tiles;

import com.danjb.engine.game.physics.Collision;
import com.danjb.engine.game.physics.CollisionResult;
import com.danjb.engine.game.physics.PostProcessCollision;
import com.danjb.engine.game.physics.Hitbox.CollisionNode;

/**
 * Right Ceiling Slope.
 *
 * <pre>
 *  #####
 *    ###
 *      #
 * </pre>
 */
public class RightCeilingSlope extends Slope {

    public RightCeilingSlope(int id) {
        super(id);
    }

    @Override
    protected boolean isNodeValidForSlope(CollisionNode node) {
        return node.isOnTopEdge();
    }

    @Override
    protected boolean isCollisionValid_Y(
            CollisionResult result,
            PostProcessCollision slopeCollision,
            Collision collision) {

        // Allow y-collisions below the Slope
        // e.g. if the Hitbox touches the floor while against the slope
        //  (this does not stop the hitbox clipping into the floor as a result
        //   of becoming "wedged")
        if (collision.collisionPos > slopeCollision.getTileBottom()) {
            return true;
        }

        // Allow y-collisions with the ceiling at the top of the Slope
        // e.g. when the slope node has left the slope,
        // and a corner of the Hitbox has hit the ceiling
        if (getSlopeNodeX(result) < slopeCollision.getTileLeft()
                && collision.node.x < result.hitbox.width / 2) {
            return true;
        }

        // Allow y-collisions with the ceiling at the bottom of the Slope
        // e.g. when the slope node has left the slope,
        // and a corner of the Hitbox is intersecting the ceiling
        if (getSlopeNodeX(result) > slopeCollision.getTileRight()
                && collision.node.x > result.hitbox.width / 2) {
            return true;
        }

        // Disable other y-collisions while on the Slope
        return false;
    }

    @Override
    protected boolean shouldBeOnSlope(
            CollisionResult result, PostProcessCollision collision) {

        if (result.hasCollisionOccurredX()
                && getSlopeNodeX(result) < collision.getTileLeft()) {
            // The Hitbox has collided with a wall
            // (for example, if this Slope leads up to a vertical wall, and the
            //  Hitbox collided with the solid tile above the slope)
            return false;
        }

        return super.shouldBeOnSlope(result, collision);
    }

    @Override
    protected float getMaxCollisionY() {
        // For ceiling slopes, we have to ensure that when a collision occurs
        // at the bottom of the slope, the Hitbox will end up strictly below
        // the slope tile when the collision resolves, otherwise the Hitbox
        // may become embedded in the ceiling.
        //
        // Technically it is more correct to say that the collision occurs at
        // (Tile.HEIGHT - Physics.SMALLEST_DISTANCE), as that signifies the far
        // edge of the tile. We add Physics.SMALLEST_DISTANCE back on later in
        // `Hitbox.resolveCollisions_Y()`, which should result in the Hitbox
        // being placed below the tile.
        //
        // However, rounding errors can occur when working with such precise
        // values, leading to the Hitbox becoming embedded in the ceiling, so
        // we err on the side of caution instead.
        //
        // Example (buggy):
        //  - Slope tile is at y=3
        //  - Physics.SMALLEST_DISTANCE is 0.0001f
        //  - getMaxCollisionY() returns 0.9999f
        //  - collisionY = 3.0f + 0.9999f = 3.9998999f (rounding error)
        //  - Hitbox is placed at 3.9998999f + 0.0001f = 3.9999998f (bug)
        //
        // Example (fixed):
        //  - Slope tile is at y=3
        //  - Physics.SMALLEST_DISTANCE is 0.0001f
        //  - getMaxCollisionY() returns 1.0f
        //  - collisionY = 3.0f + 1.0f = 4.0f
        //  - Hitbox is placed at 4.0f + 0.0001f = 4.0001f (ok!)
        return Tile.HEIGHT;
    }

    @Override
    protected float calculateNodeYAfterCollision(
            CollisionResult result, CollisionNode node, float collisionY) {
        return collisionY + node.y;
    }

    @Override
    protected float getSlopeNodeY(CollisionResult result) {
        return result.top();
    }

    @Override
    protected boolean isPointInSlopeRegion(float x, float y) {
        return x >= y;
    }

    @Override
    protected boolean isPointInsideSummit(float xInSlope, float yInSlope) {
        return xInSlope > Tile.WIDTH && yInSlope < Tile.HEIGHT;
    }

    @Override
    protected boolean isPointInsideBase(float xInSlope, float yInSlope) {
        return xInSlope < 0 && yInSlope < 0;
    }

    @Override
    protected float calculateY(float distIntoTileX) {
        /*
         *     A _____
         *       \.   |
         *         \. |
         *           \|
         *            '
         *            B
         *
         * (A) x = 0, y = 0
         * (B) x = 1, y = 1
         */
        return distIntoTileX;
    }

    @Override
    protected float getBounceMultiplierX() {
        return 1;
    }

    @Override
    protected float getBounceMultiplierY() {
        return 1;
    }

    @Override
    protected boolean shouldRemoveSpeedOnCollision(CollisionResult result) {
        // Remove y-speed if the Hitbox was moving up
        // (but not when hitting the slope while falling)
        return result.getAttemptedDy() < 0;
    }

}
