package engine.game.tiles;

import engine.game.GameUtils;
import engine.game.physics.Collision;
import engine.game.physics.CollisionResult;
import engine.game.physics.Hitbox;
import engine.game.physics.Hitbox.CollisionNode;
import engine.game.physics.Physics;
import engine.game.physics.PostProcessCollision;

/**
 * Base class for Slopes.
 *
 * <p>There are a lot of problems with implementing slopes in a tile-based game:
 *
 * 1) Hitboxes may at times intersect the tile behind or below a slope.
 *     Collisions with such tiles must be disabled.
 *
 * 2) Regular collisions must still be respected outside the slope, for example,
 *     if a slope leads into a wall.
 *
 * 3) It looks strange if a Hitbox is positioned such that only its corner sits
 *     on the slope, as the rest of the Hitbox will be floating. Thus, when a
 *     Hitbox collides with a slope, it should be positioned such that its
 *     horizontal centre sits atop the slope.
 *
 * 4) Unlike regular collisions which affect the speed of a Hitbox, collisions
 *     with ceiling slopes should not slow a falling Hitbox.
 *
 * 5) Hitboxes that do not support slope traversal (for example, projectiles)
 *     should take into account the angle of the slope when bouncing off the
 *     slope.
 *
 * 6) By default, fast-moving Hitboxes will fly off the slope, instead of
 *     sliding down it.
 *
 * These problems are addressed herein.
 *
 * @author Dan Bryce
 */
public abstract class Slope extends ForegroundTile
        implements PostProcessingTile {

    /**
     * Constructs a Slope with the given Slope flags.
     *
     * @param id
     */
    protected Slope(int id) {
        super(id);
    }

    @Override
    public boolean hasSpecialCollisionProperties() {
        return true;
    }

    /**
     * Resolves a PostProcessCollision with this Slope.
     */
    @Override
    public void postProcessing(
            CollisionResult result, PostProcessCollision slopeCollision) {

        // Filter out invalid x-collisions
        for (Collision collision : result.getCollisionsX()) {
            if (!isCollisionValid_X(result, slopeCollision, collision)) {
                result.invalidateCollision(collision);
            }
        }

        // Filter out invalid y-collisions
        for (Collision collision : result.getCollisionsY()) {
            if (!isCollisionValid_Y(result, slopeCollision, collision)) {
                result.invalidateCollision(collision);
            }
        }

        // If the desired destination is in the Slope, add a Collision
        if (shouldCollide(result, slopeCollision)) {
            collideWithSlope(result, slopeCollision);
        }
    }

    /**
     * Determines if the given x-Collision is valid, in light of a collision
     * with this Slope.
     *
     * @param result
     * @param slopeCollision
     * @param collision
     * @return
     */
    protected boolean isCollisionValid_X(
            CollisionResult result,
            PostProcessCollision slopeCollision,
            Collision collision) {

        int tileXBefore = Tile.getTileX(result.initialNodeX(collision.node));
        int tileXAfter = Tile.getTileX(result.desiredNodeX(collision.node));
        if (tileXBefore == tileXAfter) {
            /*
             * Disable x-collisions if the CollisionNode was already
             * intersecting the Tile in question.
             *
             * This can happen when the Hitbox moves from 1 slope to another.
             *
             *  EXAMPLE:
             *   - Hitbox is on a right slope, moving right. The slope node is
             *      inside the slope, but the bottom-right node is intersecting
             *      the solid tile "behind" the slope.
             *   - After the attempted movement is applied, the Hitbox is no
             *      longer intersecting the same slope. It is intersecting the
             *      next slope tile (right and up), but the slope node is now
             *      inside the solid block below the new slope.
             *   - An x-collision is generated for the bottom-right node.
             *      This collision would normally be valid, since the slope node
             *      is not yet inside the new slope.
             *   - Because the node was ALREADY intersecting that solid block,
             *      the collision is invalidated - problem solved.
             */
            return false;
        }

        if (result.initialNodeY(collision.node) < slopeCollision.getTileTop()) {
            /*
             * Allow x-collisions triggered by CollisionNodes above the Slope
             *
             * This can happen if a Slope connects directly to a wall.
             *
             *  EXAMPLE:
             *   - Hitbox is on a right slope, moving right.
             *   - The Hitbox collides with a wall at the top of the slope.
             *   - This collision is valid, because it is above the slope tile.
             */
            return true;
        }

        if (result.initialNodeY(collision.node) > slopeCollision.getTileBottom()) {
            /*
             * Allow x-collisions triggered by CollisionNodes below the Slope
             *
             * This can happen if a Slope leads to a vertical drop.
             *
             *  EXAMPLE:
             *   - Hitbox moves towards a right slope. There is a solid block
             *      tile directly below the slope.
             *   - The Hitbox collides in such a way that it intersects the
             *      slope, but also the solid block. An x-collision is
             *      generated.
             *   - This collision is valid, because it is below the slope tile.
             */
            return true;
        }

        // Disable other x-collisions while on the Slope
        return false;
    }

    /**
     * Determines if the given x-Collision is valid, in light of a collision
     * with this Slope.
     *
     * @param result
     * @param slopeCollision
     * @param collision
     * @return
     */
    protected abstract boolean isCollisionValid_Y(
            CollisionResult result,
            PostProcessCollision slopeCollision,
            Collision collision);

    /**
     * Determines if a CollisionResult should collide with this Slope.
     *
     * @param result
     * @param collision
     * @return
     */
    protected boolean shouldCollide(
            CollisionResult result, PostProcessCollision collision) {

        // Determine the position of the slope node relative to this Slope tile
        float xInSlope = getSlopeNodeX(result) - collision.getTileLeft();
        float yInSlope = getSlopeNodeY(result) - collision.getTileTop();

        // A Hitbox is only considered to be intersecting the Slope if its slope
        // node is inside the *solid* part of the Slope
        return isPointInSlope(xInSlope, yInSlope);
    }

    /**
     * Determines the absolute x-position of the "slope node", that is, the
     * point on the Hitbox which should sit atop the slope.
     *
     * @param result
     * @return
     */
    protected float getSlopeNodeX(CollisionResult result) {
        return result.centreX();
    }

    /**
     * Determines the absolute y-position of the "slope node", that is, the
     * point on the Hitbox which should sit atop the slope.
     *
     * @param result
     * @return
     */
    protected abstract float getSlopeNodeY(CollisionResult result);

    /**
     * Causes the CollisionResult to collide with this Slope.
     *
     * @param result
     * @param slopeCollision
     */
    protected void collideWithSlope(
            CollisionResult result, PostProcessCollision slopeCollision) {
        Collision collision = createCollision(result, slopeCollision);
        result.addCollision_Y(collision);
    }

    /**
     * Creates a y-collision based on a PostProcessCollision with this Slope.
     *
     * @param result
     * @param slopeCollision
     * @return
     */
    protected Collision createCollision(
            CollisionResult result, PostProcessCollision slopeCollision) {

        // Determine the "correct" y-position of the slope node on the Slope
        float xInSlope = getSlopeNodeX(result) - slopeCollision.getTileLeft();
        float yInSlopeCorrect = calculateY(xInSlope);

        // Keep this position within acceptable bounds
        yInSlopeCorrect = GameUtils.clamp(yInSlopeCorrect,
                0,
                Tile.HEIGHT - Physics.SMALLEST_DISTANCE);

        // Find the absolute y-position of this point
        float collisionY = slopeCollision.getTileTop() + yInSlopeCorrect;

        // Calculate the initial and corrected position of the CollisionNode
        float yBefore = result.initialNodeY(slopeCollision.node);
        float yAfter = calculateNodeYAfterCollision(
                result, slopeCollision.node, collisionY);

        // Create the Collision
        return Collision.create(
                yBefore,
                yAfter,
                slopeCollision.node,
                this);
    }

    /**
     * Given the point of a collision, calculates the new position of the
     * CollisionNode that triggered this collision.
     *
     * @param result
     * @param node
     * @param collisionY
     * @return
     */
    protected abstract float calculateNodeYAfterCollision(
            CollisionResult result, CollisionNode node, float collisionY);

    /**
     * Determines if a point is inside the solid part of this Slope.
     *
     * <p>The point is relative to the top-left of the Tile.
     *
     * @param x Position from 0 - Tile.WIDTH.
     * @param y Position from 0 - Tile.HEIGHT.
     * @return
     */
    protected abstract boolean isPointInSlope(float x, float y);

    /**
     * Gets the y-position of the Slope at the given x-position.
     *
     * <p>Positions range from 0 - Tile.SIZE.
     *
     * @param distIntoTileX x-position relative to the left of the Tile.
     * @return y-position relative to the top of the Tile.
     */
    protected abstract float calculateY(float distIntoTileX);

    /**
     * Causes a Hitbox to bounce after a collision with this Slope.
     *
     * <p>If you imagine 2 lines protruding from a Slope, one horizontal and one
     * vertical, those lines create 3 different sectors. These correspond to the
     * 3 different ways each Slope can be hit, e.g.
     *
     * <pre>
     *  #\ 1 |
     *  ##\  | 2
     *  ###\ |____
     *  ####\
     *  #####\  3
     * </pre>
     *
     * <p>1: Glancing blow from the left.
     * <br>2: Direct hit.
     * <br>3. Glancing blow from the right.
     *
     * <p>Fortunately, after some experimentation, it would appear that these
     * different cases can all be handled in the same way - just swap the x- and
     * y-speeds, and possibly invert them (depending on the slope).
     */
    @Override
    public void hitboxCollidedY(CollisionResult result) {

        // Collisions with Slopes are always in the y-axis,
        // but they affect the Hitbox speed in BOTH axes

        Hitbox hitbox = result.hitbox;
        float prevSpeedX = hitbox.getSpeedX();
        float prevSpeedY = hitbox.getSpeedY();

        // The new y-speed is the old x-speed
        float newSpeedY = prevSpeedX
                * hitbox.bounceCoefficient
                * getBouceMultiplierY();
        hitbox.setSpeedY(newSpeedY);

        // The x-speed is only affected if a Hitbox supports slope traversal
        if (!hitbox.getCollisionFlag(Hitbox.SUPPORTS_SLOPE_TRAVERSAL)) {

            // The new x-speed is the old y-speed
            float newSpeedX = prevSpeedY
                    * hitbox.bounceCoefficient
                    * getBouceMultiplierX();
            hitbox.setSpeedX(newSpeedX);
        }
    }

    protected abstract float getBouceMultiplierX();

    protected abstract float getBouceMultiplierY();

}
