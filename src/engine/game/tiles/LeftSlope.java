package engine.game.tiles;

import engine.game.physics.Collision;
import engine.game.physics.CollisionResult;
import engine.game.physics.Hitbox.CollisionNode;
import engine.game.physics.PostProcessCollision;

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
    protected boolean isCollisionValid_Y(
            CollisionResult result,
            PostProcessCollision slopeCollision,
            Collision collision) {

        // Allow y-collisions with the floor at the bottom of the Slope
        if (getSlopeNodeX(result) > slopeCollision.getTileRight()) {
            return true;
        }

        // Disable y-collisions while on the Slope
        return false;
    }

    @Override
    protected boolean shouldCollide(
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

        return super.shouldCollide(result, collision);
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
    protected boolean isPointInSlope(float x, float y) {
        return x <= y;
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
    protected float getBouceMultiplierX() {
        return 1;
    }

    @Override
    protected float getBouceMultiplierY() {
        return 1;
    }

}
