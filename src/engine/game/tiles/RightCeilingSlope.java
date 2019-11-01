package engine.game.tiles;

import engine.game.physics.Collision;
import engine.game.physics.CollisionResult;
import engine.game.physics.Hitbox.CollisionNode;
import engine.game.physics.PostProcessCollision;

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
    protected boolean isCollisionValid_Y(
            CollisionResult result,
            PostProcessCollision slopeCollision,
            Collision collision) {

        // Allow y-collisions with the ceiling at the top of the Slope
        if (getSlopeNodeX(result) < slopeCollision.getTileLeft()) {
            return true;
        }

        // Disable other y-collisions while on the Slope
        return false;
    }

    @Override
    protected boolean shouldCollide(
            CollisionResult result, PostProcessCollision collision) {

        if (result.hasCollisionOccurredX()
                && getSlopeNodeX(result) < collision.getTileLeft()) {
            // The Hitbox has collided with a wall
            // (for example, if this Slope leads up to a vertical wall, and the
            //  Hitbox collided with the solid tile above the slope)
            return false;
        }

        return super.shouldCollide(result, collision);
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
    protected boolean isPointInSlope(float x, float y) {
        return x >= y;
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
    protected float getBouceMultiplierX() {
        return 1;
    }

    @Override
    protected float getBouceMultiplierY() {
        return 1;
    }

}
