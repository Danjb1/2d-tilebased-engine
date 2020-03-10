package engine.game.tiles;

import engine.game.physics.Collision;
import engine.game.physics.CollisionResult;
import engine.game.physics.Hitbox.CollisionNode;
import engine.game.physics.PostProcessCollision;

/**
 * Left Ceiling Slope.
 *
 * <pre>
 *  #####
 *  ###
 *  #
 * </pre>
 */
public class LeftCeilingSlope extends Slope {

    public LeftCeilingSlope(int id) {
        super(id);
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
        if (getSlopeNodeX(result) > slopeCollision.getTileRight()
                && collision.node.x > result.hitbox.width / 2) {
            return true;
        }

        // Allow y-collisions with the ceiling at the bottom of the Slope
        // e.g. when the slope node has left the slope,
        // and a corner of the Hitbox is intersecting the ceiling
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
            // (for example, if this Slope leads up to a vertical wall, and the
            //  Hitbox collided with the solid tile above the slope)
            return false;
        }

        return super.shouldBeOnSlope(result, collision);
    }

    @Override
    protected float getMaxCollisionY() {
        // See comments in RightCeilingSlope
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
        return x <= Tile.HEIGHT - y;
    }

    @Override
    protected boolean isPointInsideSummit(float xInSlope, float yInSlope) {
        return xInSlope < 0 && yInSlope < Tile.HEIGHT;
    }

    @Override
    protected boolean isPointInsideBase(float xInSlope, float yInSlope) {
        return xInSlope > Tile.HEIGHT && yInSlope < 0;
    }

    @Override
    protected float calculateY(float distIntoTileX) {
        /*
         *       _____ B
         *      |   ./
         *      | ./
         *      |/
         *      '
         *      A
         *
         * (A) x = 0, y = 1
         * (B) x = 1, y = 0
         */
        return Tile.HEIGHT - distIntoTileX;
    }

    @Override
    protected float getBounceMultiplierX() {
        return -1;
    }

    @Override
    protected float getBounceMultiplierY() {
        return -1;
    }

    @Override
    protected boolean shouldRemoveSpeedOnCollision(CollisionResult result) {
        // Remove y-speed if the Hitbox was moving up
        // (but not when hitting the slope while falling)
        return result.getAttemptedDy() < 0;
    }

}
