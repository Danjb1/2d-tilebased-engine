package com.danjb.engine.game.tiles;

import java.util.List;
import java.util.stream.Collectors;

import com.danjb.engine.game.physics.Collision;
import com.danjb.engine.game.physics.CollisionResult;
import com.danjb.engine.game.physics.Hitbox;
import com.danjb.engine.game.physics.Hitbox.CollisionNode;
import com.danjb.engine.game.physics.PostProcessCollision;
import com.danjb.engine.util.MathUtils;

/**
 * Base class for Slopes.
 *
 * <!-------------------------------------------------------------------------->
 * <h1>Problems Faced</h1>
 * <!-------------------------------------------------------------------------->
 *
 * <p>There are a lot of problems with implementing slopes in a tile-based game:
 *
 * <ol>
 * <li>
 *     Hitboxes may at times intersect the tile behind or below a slope.
 *     Collisions with such tiles must be disabled.
 * </li>
 *
 * <li>
 *     Regular collisions must still be respected outside the slope, for example,
 *     if a slope leads into a wall.
 * </li>
 *
 * <li>
 *     It looks strange if a Hitbox is positioned such that only its corner sits
 *     on the slope, as the rest of the Hitbox will be floating. Thus, when a
 *     Hitbox collides with a slope, it should be positioned such that its
 *     horizontal centre sits atop the slope.
 * </li>
 *
 * <li>
 *     Unlike regular collisions which affect the speed of a Hitbox, collisions
 *     with ceiling slopes should not slow a falling Hitbox.
 * </li>
 *
 * <li>
 *     Hitboxes that do not support slope traversal (for example, projectiles)
 *     should take into account the angle of the slope when bouncing off the
 *     slope.
 * </li>
 *
 * <li>
 *     By default, fast-moving Hitboxes will fly off the slope, instead of
 *     sliding down it.
 * </li>
 * </ol>
 *
 * These problems, and others, are addressed herein.
 *
 * <!-------------------------------------------------------------------------->
 * <h1>Approach</h1>
 * <!-------------------------------------------------------------------------->
 *
 * <p>The approach used is described here:<br>
 * http://www.danjb.com/game_dev/tilebased_platformer_slopes_2
 *
 * <p>It sounds simple in theory, but the task quickly becomes very complex as
 * more corner cases are discovered. The solution laid out here works, for the
 * most part, but it is difficult to understand and covers a lot of special
 * cases.
 *
 * <p>A simpler and more robust solution would be preferable.
 *
 * <h2>Caveat</h2>
 *
 * <p>There is one extra case (at least) covered here which that article naively
 * omits...
 *
 * <p>The article suggests that each slope can effectively ignore collisions
 * that occur when the slope node is outside of its "region", or column. This is
 * true when slopes are connected to other slopes and floor tiles, but consider
 * the case where a slope tile leads to a vertical drop; in this case, if a
 * hitbox is right on the edge of the slope, its slope node will not be in the
 * region of ANY slope, and will therefore fall right through the tile:
 *
 * <pre>
 *    \   _____
 *     \ |     |
 *      \|__.__|
 *       \
 *        |
 *        |
 * </pre>
 *
 * <p>To fix this, we allow slopes to generate collisions if the slope node is
 * outside of its column, but we define a notion of priority; if a Hitbox
 * collides with multiple slope tiles, the one whose column contains the slope
 * node takes priority.
 *
 * <!-------------------------------------------------------------------------->
 * <h1>Known Issues</h1>
 * <!-------------------------------------------------------------------------->
 *
 * <h2>Conflicting Priorities</h2>
 *
 * <p>If a Hitbox collides with multiple slope tiles that all have priority
 * (i.e. multiple slope tiles in the same column), the last tile processed will
 * take precedence. This causes noticeably strange behaviour in thin sloped
 * tunnels.
 *
 * <p>To fix this, we should allow all such tiles to generate collisions, and
 * the nearest collision should be preferred.
 *
 * <h2>Wide Hitboxes</h2>
 *
 * <p>Wide Hitboxes can clip into walls, if atop a slope that leads to a wall.
 *
 * <p>This happens because the edge of the Hitbox is already inside the wall
 * by the time it is high enough to collide with it.
 *
 * <pre>
 *         |  <-- Hitbox is too short to collide with this wall.
 *        _\___
 *    A  |__.__|
 *           \
 *       <-->
 *        B
 * </pre>
 *
 * <p>This is the case when length B is greater than length A
 *      (hitbox.width / 2 > hitbox.height).
 *
 * <p>To fix this, we would have to add additional CollisionNodes above the
 * Hitbox, that can generate collisions only when the Hitbox is on a slope.
 *
 * <h2>Wedged Hitboxes</h2>
 *
 * <p>A Hitbox wedged between a slope and a solid floor / ceiling tile will be
 * forced into the solid block.
 *
 * <h2>Back of Slopes</h2>
 *
 * <p>A Hitbox can travel straight through the back of a slope. Ideally these
 * should be treated as solid edges.
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

        // Ignore CollisionNodes that are not relevant to this Slope
        if (!isNodeValidForSlope(slopeCollision.node)) {
            return;
        }

        // Prevent slopes from interfering with each other;
        // the slope containing the slope node takes precedence over others
        boolean hasPriority = doesSlopeHavePriority(result, slopeCollision);

        // If another slope collision has priority over this one,
        // we shouldn't generate any collisions
        if (!hasPriority) {
            return;
        }

        // Filter out invalid x-collisions
        for (Collision collision : result.getCollisionsX()) {
            if (!isCollisionValid_X(result, slopeCollision, collision)) {
                result.invalidateCollision(collision);
            }
        }

        // Filter out invalid y-collisions
        for (Collision collision : result.getCollisionsY()) {

            if (collision.tile instanceof Slope && hasPriority) {
                // Disable collisions generated by slopes with a lower priority
                result.invalidateCollision(collision);

            } else if (!isCollisionValid_Y(result, slopeCollision, collision)) {
                result.invalidateCollision(collision);
            }
        }

        // If the Hitbox should be atop the Slope, add a collision
        if (shouldBeOnSlope(result, slopeCollision)) {
            addSlopeCollision(result, slopeCollision);
        }
    }

    /**
     * Determines if a CollisionNode can generate a collision for this slope.
     *
     * <p>The collision generated is always relative to the node involved in
     * the collision, so considering nodes on the wrong side of the Hitbox (or
     * in the middle of a tall Hitbox) can result in strange behaviour.
     *
     * @param node
     * @return
     */
    protected abstract boolean isNodeValidForSlope(CollisionNode node);

    /**
     * Determines if a slope collision should take priority over collisions
     * generated by other slopes.
     *
     * @param result
     * @param slopeCollision
     * @return
     */
    private boolean doesSlopeHavePriority(CollisionResult result,
            PostProcessCollision slopeCollision) {

        // A slope containing the slope node always has priority
        if (isSlopeNodeInColumn(result, slopeCollision)) {
            return true;
        }

        // If no other slope collisions have been generated (yet), then this
        // slope has priority, at least for now
        List<Collision> otherSlopeCollisions = getOtherSlopeCollisions(result);
        if (otherSlopeCollisions.isEmpty()) {
            return true;
        }

        return false;
    }

    /**
     * Gets the collisions generated by other slope tiles.
     *
     * @param result
     * @return
     */
    private List<Collision> getOtherSlopeCollisions(CollisionResult result) {
        return result.getCollisionsY()
                .stream()
                .filter(c -> c.tile instanceof Slope)
                .collect(Collectors.toList());
    }

    /**
     * Determines if the slope node is in line with a slope collision.
     *
     * @param result
     * @param slopeCollision
     * @return
     */
    private boolean isSlopeNodeInColumn(
            CollisionResult result, PostProcessCollision slopeCollision) {
        float xInSlope = getSlopeNodeX(result) - slopeCollision.getTileLeft();
        return xInSlope >= 0 && xInSlope < Tile.WIDTH;
    }

    /**
     * Determines if the Hitbox should sit atop the Slope.
     *
     * @param result
     * @param collision
     * @return
     */
    protected boolean shouldBeOnSlope(
            CollisionResult result, PostProcessCollision collision) {

        // Determine the position of the slope node relative to this Slope tile
        float xInSlope = getSlopeNodeX(result) - collision.getTileLeft();
        float yInSlope = getSlopeNodeY(result) - collision.getTileTop();

        /*
         * There are 2 situations in which we place a Hitbox on a slope:
         *
         *   1) The slope node of the Hitbox is inside the slope region.
         *
         *      This case is straightforward; the Hitbox has moved such that it
         *      is intersecting the slope, and should be moved onto it.
         *
         *   2) The slope node is inside the summit of a slope.
         *
         *      This case is a little more complicated. When the Hitbox reaches
         *      the top of a slope (or the bottom, for a ceiling slope), the
         *      slope node may intersect the solid block at the summit. This
         *      would normally result in an x-collision being generated for the
         *      solid block.
         *
         *      Instead, we recognise this case, and treat the Hitbox as if it
         *      is still ascending the slope. This ensures that the Hitbox will
         *      clear the summit, so that it doesn't collide with the solid
         *      block in the x-axis.
         *
         *   3) The slope node is inside the base of a slope.
         *
         *      This is another corner case. Imagine a slope leading to a
         *      vertical drop, and a Hitbox standing at the very edge of the
         *      slope. The last collision generated by the slope will not be
         *      lower than the bottom of the tile, due to the clamping enforced
         *      during slope collision generation.
         *
         *      At this point, the slope node is not inside the slope region,
         *      because it lies in the tile adjacent to the slope, but it is in
         *      higher than the extrapolated line of the slope:
         *
         *          #\     ___
         *          ###\  |   |
         *          #####\|_._|
         *          #######|
         *          #######|
         *
         *      If we do not handle this case, then the hitbox will fall until
         *      it *is* inside the slope region, then its position will be reset
         *      to the clamped position, and it will fall again, and so on.
         */
        return isPointInSlopeRegion(xInSlope, yInSlope)
                || isPointInsideSummit(xInSlope, yInSlope)
                || isPointInsideBase(xInSlope, yInSlope);
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
     * Determines if the given y-Collision is valid, in light of a collision
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
     * Determines if a point is inside this Slope's "region".
     *
     * <p>For floor slopes, the region is defined as the solid part of the
     * slope, and all tiles below it.
     *
     * <p>For ceiling slopes, the region is defined as the solid part of the
     * slope, and all tiles above it.
     *
     * <p>The point is relative to the top-left of the Tile.
     *
     * @param x Position from 0 - Tile.WIDTH.
     * @param y Position from 0 - Tile.HEIGHT.
     * @return
     */
    protected abstract boolean isPointInSlopeRegion(float x, float y);

    /**
     * Determines if a point is inside the summit of this Slope.
     *
     * @param xInSlope
     * @param yInSlope
     * @return
     */
    protected abstract boolean isPointInsideSummit(
            float xInSlope, float yInSlope);

    /**
     * Determines if a point is inside the base of this Slope.
     *
     * @param xInSlope
     * @param yInSlope
     * @return
     */
    protected abstract boolean isPointInsideBase(
            float xInSlope, float yInSlope);

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
    protected void addSlopeCollision(
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

        // Keep this position within acceptable bounds; without this, a hitbox
        // standing on the edge of a slope might get moved into the tile below,
        // bypassing the collision properties of that tile
        yInSlopeCorrect = MathUtils.clampf(yInSlopeCorrect,
                0,
                getMaxCollisionY());

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
     * Gets the maximum permitted y-distance of a collision, measured from the
     * top of the slope tile.
     *
     * @return
     */
    protected abstract float getMaxCollisionY();

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

        Hitbox hitbox = result.hitbox;

        if (shouldBounceOffSlope(hitbox)) {
            rebound(hitbox);

        } else if (shouldRemoveSpeedOnCollision(result)) {
            hitbox.setSpeedY(0);
        }
    }

    /**
     * Determines if a Hitbox should bounce off this slope, as opposed to
     * sliding up or down it.
     *
     * @param hitbox
     * @return
     */
    private boolean shouldBounceOffSlope(Hitbox hitbox) {
        return !hitbox.getCollisionFlag(Hitbox.SUPPORTS_SLOPE_TRAVERSAL);
    }

    /**
     * Causes a Hitbox to rebound off this Slope.
     *
     * @param hitbox
     */
    private void rebound(Hitbox hitbox) {

        // Collisions with Slopes are always in the y-axis,
        // but they affect the Hitbox speed in BOTH axes
        float prevSpeedX = hitbox.getSpeedX();
        float prevSpeedY = hitbox.getSpeedY();

        // The new y-speed is the old x-speed
        float newSpeedY = prevSpeedX
                * hitbox.bounceCoefficient
                * getBounceMultiplierY();
        hitbox.setSpeedY(newSpeedY);

        // The new x-speed is the old y-speed
        float newSpeedX = prevSpeedY
                * hitbox.bounceCoefficient
                * getBounceMultiplierX();
        hitbox.setSpeedX(newSpeedX);
    }

    /**
     * Gets the multiplier applied to a Hitbox's x-speed after a bounce.
     *
     * @return
     */
    protected abstract float getBounceMultiplierX();

    /**
     * Gets the multiplier applied to a Hitbox's y-speed after a bounce.
     *
     * @return
     */
    protected abstract float getBounceMultiplierY();

    /**
     * Determines if a Hitbox should have its y-speed removed after a collision.
     *
     * @param result
     * @return
     */
    protected abstract boolean shouldRemoveSpeedOnCollision(
            CollisionResult result);

}
