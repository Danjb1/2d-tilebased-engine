package engine.game.tiles;

import engine.game.physics.Collision;
import engine.game.physics.CollisionResult;
import engine.game.physics.Physics;
import engine.game.physics.PostProcessCollision;
import engine.game.physics.Hitbox.CollisionNode;

/**
 * Left Slope.
 *
 * <pre>
 *  #
 *  ###
 *  #####
 * </pre>
 */
public class LeftSlope extends Slope {

    public LeftSlope(int id) {
        super(id);
    }

    @Override
    protected boolean isNodeValidForSlope(CollisionNode node) {
        return node.isOnBottomEdge();
    }

    @Override
    protected boolean isCollisionValid_Y(
            CollisionResult result,
            PostProcessCollision slopeCollision,
            Collision collision) {

        // Allow y-collisions above the Slope
        // e.g. if the Hitbox collides with a low ceiling while on the slope
        //  (note that such a collision will only be generated if the Hitbox has
        //   some upward velocity, and this does not stop the hitbox clipping
        //   into the ceiling as a result of becoming "wedged")
        if (collision.collisionPos < slopeCollision.getTileTop()) {
            return true;
        }

        // Allow y-collisions with the floor at the bottom of the Slope
        // e.g. when the slope node has left the slope,
        // and a corner of the Hitbox has hit the floor
        if (getSlopeNodeX(result) > slopeCollision.getTileRight()
                && collision.node.x > result.hitbox.width / 2) {
            return true;
        }

        // Allow y-collisions with the floor at the top of the Slope
        // e.g. when the slope node has left the slope,
        // and a corner of the Hitbox is intersecting the floor
        if (getSlopeNodeX(result) < slopeCollision.getTileLeft()
                && collision.node.x < result.hitbox.width / 2) {
            return true;
        }

        // Disable other y-collisions while on the Slope
        return false;
    }

    @Override
    protected boolean shouldBeOnSlope(
            CollisionResult result, PostProcessCollision collision) {

        if (result.hasCollisionOccurredX()
                && getSlopeNodeX(result) > collision.getTileRight()) {
            // The Hitbox has collided with a wall
            // (for example, if this Slope leads down to a vertical drop, and
            // the Hitbox collided with the solid tile under the slope)
            return false;
        }

        if (result.hitbox.isGrounded()
                && result.getAttemptedDx() > 0
                && result.getAttemptedDy() > 0
                && result.bottom() <= collision.getTileBottom()) {
            // The Hitbox was just grounded, so even if it's not intersecting
            // the solid part of the slope, we should "pull" it onto the slope.
            // This prevents fast-moving Hitboxes from flying off slopes.
            return true;
        }

        return super.shouldBeOnSlope(result, collision);
    }

    @Override
    protected float getMaxCollisionY() {
        // For floor slopes, we have to be careful that the collision does not
        // occur outside the bounds of the tile, otherwise the Hitbox can
        // become embedded in the floor
        return Tile.HEIGHT - Physics.SMALLEST_DISTANCE;
    }

    @Override
    protected float calculateNodeYAfterCollision(
            CollisionResult result, CollisionNode node, float collisionY) {
        return (collisionY - result.hitbox.height) + node.y;
    }

    @Override
    protected float getSlopeNodeY(CollisionResult result) {
        return result.bottom();
    }

    @Override
    protected boolean isPointInSlopeRegion(float x, float y) {
        return x <= y;
    }

    @Override
    protected boolean isPointInsideSummit(float xInSlope, float yInSlope) {
        return xInSlope < 0 && yInSlope > 0;
    }

    @Override
    protected boolean isPointInsideBase(float xInSlope, float yInSlope) {
        return xInSlope > Tile.WIDTH && yInSlope > Tile.HEIGHT;
    }

    @Override
    protected float calculateY(float distIntoTileX) {
        /*
         *      A
         *      .
         *      |\.
         *      |  \.
         *      |____\
         *             B
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
        // Remove y-speed if the Hitbox was moving down
        // (but not when hitting the slope on the ascent of a jump)
        return result.getAttemptedDy() > 0;
    }

}
