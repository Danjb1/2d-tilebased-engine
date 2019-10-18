package engine.game.physics;

import java.util.ArrayList;
import java.util.List;

import engine.game.tiles.Slope;

/**
 * Class designed to hold a number of collisions during physics processing.
 *
 * @author Dan Bryce
 */
public class CollisionResult {

    /**
     * All Collisions that have occurred in the x-axis.
     */
    private List<Collision> collisionsX = new ArrayList<>();

    /**
     * All Collisions that have occurred in the y-axis.
     */
    private List<Collision> collisionsY = new ArrayList<>();

    /**
     * Nearest Collisions detected by this CollisionResult.
     */
    private Collision nearestCollisionX, nearestCollisionY;

    /**
     * Hitbox involved in this Collision.
     */
    private Hitbox hitbox;

    /**
     * Attempted movement distance.
     */
    private float attempted_dx, attempted_dy;

    /**
     * New x-position of the Hitbox after this CollisionResult is applied.
     */
    private float newX;

    /**
     * New y-position of the Hitbox after this CollisionResult is applied.
     */
    private float newY;

    /**
     * Flag used for a special case involving ceiling slopes.
     */
    private boolean maintainSpeedY;

    /**
     * Creates a CollisionResult by attempting to move the given Hitbox by the
     * given distances.
     *
     * @param hitbox
     * @param dx
     * @param dy
     */
    public CollisionResult(Hitbox hitbox, float dx, float dy) {
        this.hitbox = hitbox;
        attempted_dx = dx;
        attempted_dy = dy;
        newX = hitbox.left() + dx;
        newY = hitbox.top() + dy;
    }

    /**
     * Returns the original hitbox used in the collision detection.
     *
     * @return
     */
    public Hitbox getHitbox() {
        return hitbox;
    }

    /**
     * Getter for the new Hitbox left.
     *
     * <p>Before resolveCollisions_X() is called, this will return the new left
     * position of the Hitbox assuming no collisions; afterwards, it will
     * return the new position with the nearest collision applied.
     *
     * @return
     */
    public float getLeft() {
        return newX;
    }

    /**
     * Getter for the new Hitbox right.
     *
     * See {@link #getLeft}.
     *
     * @return
     */
    public float getRight() {
        return newX + hitbox.width - Physics.SMALLEST_DISTANCE;
    }

    /**
     * Getter for the new Hitbox centre.
     *
     * See {@link #getLeft}.
     *
     * @return
     */
    public float getCentreX() {
        return newX + hitbox.width / 2;
    }

    /**
     * Getter for the new Hitbox centre.
     *
     * See {@link #getLeft}.
     *
     * @return
     */
    public float getCentreY() {
        return newY + hitbox.height / 2;
    }

    /**
     * Getter for the new Hitbox top.
     *
     * See {@link #getLeft}.
     *
     * @return
     */
    public float getTop() {
        return newY;
    }

    /**
     * Getter for the new Hitbox bottom.
     *
     * See {@link #getLeft}.
     *
     * @return
     */
    public float getBottom() {
        return newY + hitbox.height - Physics.SMALLEST_DISTANCE;
    }

    /**
     * Returns the nearest Collision in the x-axis.
     *
     * <p>This must be called after resolveCollisions_X().
     *
     * @return
     */
    public Collision getNearestCollisionX() {
        return nearestCollisionX;
    }

    /**
     * Returns the nearest Collision in the y-axis.
     *
     * <p>This must be called after resolveCollisions_Y().
     *
     * @return
     */
    public Collision getNearestCollisionY() {
        return nearestCollisionY;
    }

    /**
     * Adds a Collision in the x-axis.
     *
     * @param collision
     */
    public void addCollision_X(Collision collision){
        collisionsX.add(collision);
    }

    /**
     * Adds a Collision in the y-axis.
     *
     * @param collision
     */
    public void addCollision_Y(Collision collision){
        collisionsY.add(collision);
    }

    /**
     * Adds the given Collision in the x-axis, overriding all other Collisions.
     *
     * @param collision
     */
    public void setCollision_X(Collision collision) {
        collisionsX.clear();
        collisionsX.add(collision);
    }

    /**
     * Adds the given Collision in the y-axis, overriding all other Collisions.
     *
     * @param collision
     */
    public void setCollision_Y(Collision collision) {
        collisionsY.clear();
        collisionsY.add(collision);
    }

    /**
     * Getter for the attempted movement distance in the x-axis.
     *
     * @return
     */
    public float getAttemptedDx() {
        return attempted_dx;
    }

    /**
     * Getter for the attempted movement distance in the y-axis.
     *
     * @return
     */
    public float getAttemptedDy() {
        return attempted_dy;
    }

    /**
     * Returns the position of the colliding edge of the Hitbox.
     *
     * <p>When moving left, the colliding edge is the left of the Hitbox, and
     * when moving right it is the right of the Hitbox.
     *
     * @return
     */
    public float getCollisionEdgeX() {
        return wasCollisionWithLeftEdge() ?
                hitbox.left() :
                    hitbox.right();
    }

    /**
     * Returns the position of the colliding edge of the Hitbox.
     *
     * <p>When moving up, the colliding edge is the top of the Hitbox, and when
     * moving down it is the bottom of the Hitbox.
     *
     * @return
     */
    public float getCollisionEdgeY() {
        return wasCollisionWithTopEdge() ?
                hitbox.top() :
                    hitbox.bottom();
    }

    /**
     * Determines if the nearest y-Collision was with a Slope.
     *
     * @return
     */
    public boolean isCollisionWithSlope() {
        return nearestCollisionY != null &&
                nearestCollisionY.getTile() instanceof Slope;
    }

    /**
     * Determines if the nearest y-Collision was with a floor Slope.
     *
     * @return
     */
    private boolean isCollisionWithFloorSlope() {
        return isCollisionWithSlope() &&
                ((Slope) nearestCollisionY.getTile()).isFloorSlope();
    }

    /**
     * Determines if the nearest y-Collision was with a ceiling Slope.
     *
     * @return
     */
    private boolean isCollisionWithCeilingSlope() {
        return isCollisionWithSlope() &&
                ((Slope) nearestCollisionY.getTile()).isCeilingSlope();
    }

    /**
     * Determines if a Collision occurred in the x-axis.
     *
     * @return
     */
    public boolean hasCollisionOccurredX() {
        return nearestCollisionX != null;
    }

    /**
     * Determines if a Collision occurred in the y-axis.
     *
     * @return
     */
    public boolean hasCollisionOccurredY() {
        return nearestCollisionY != null;
    }

    /**
     * Determines the nearest Collision in the x-axis.
     *
     * <p>Must be called after all Collisions are added. After this is called,
     * no more collisions can be added.
     */
    public void resolveCollisions_X() {

        if (collisionsX.isEmpty()){
            return;
        }

        collisionsX.sort(null);
        nearestCollisionX = collisionsX.get(0);
        collisionsX.clear(); // No longer needed

        if (wasCollisionWithLeftEdge()){
            // We add a small distance because we want the Hitbox to be placed
            // NEXT to the colliding Tile, not inside it.
            newX = nearestCollisionX.getCollisionPos() +
                    Physics.SMALLEST_DISTANCE;
        } else if (wasCollisionWithRightEdge()){
            newX = nearestCollisionX.getCollisionPos() - hitbox.width;
        }
    }

    /**
     * Determines the nearest Collision in the y-axis.
     *
     * See {@link #resolveCollisions_X}.
     */
    public void resolveCollisions_Y() {

        if (collisionsY.isEmpty()){
            return;
        }

        collisionsY.sort(null);
        nearestCollisionY = collisionsY.get(0);
        collisionsY.clear(); // No longer needed

        if (wasCollisionWithTopEdge()){
            // We add a small distance because we want the Hitbox to be placed
            // NEXT to the colliding Tile, not inside it.
            newY = nearestCollisionY.getCollisionPos() +
                    Physics.SMALLEST_DISTANCE;
            if (isCollisionWithCeilingSlope() && attempted_dy > 0){
                /*
                 * Special case: Entities pressing into a ceiling slope while
                 * falling need to snap to the slope, but should maintain their
                 * vertical velocity. It doesn't make sense for Entities to
                 * suddenly stop falling just because they bump into a ceiling
                 * slope.
                 */
                maintainSpeedY = true;
            }
        } else if (wasCollisionWithBottomEdge()){
            newY = nearestCollisionY.getCollisionPos() - hitbox.height;
        }
    }

    /**
     * Determines if this collision was with the left edge of the Hitbox.
     *
     * @return
     */
    private boolean wasCollisionWithLeftEdge() {
        // If the Entity was moving left, the left edge must have collided
        return attempted_dx < 0;
    }

    /**
     * Determines if this collision was with the right edge of the Hitbox.
     *
     * @return
     */
    private boolean wasCollisionWithRightEdge() {
        // If the Entity was moving right, the right edge must have collided
        return attempted_dx > 0;
    }

    /**
     * Determines if this collision was with the top edge of the Hitbox.
     *
     * @return
     */
    private boolean wasCollisionWithTopEdge() {

        /*
         * We have to include these special cases for floor and ceiling slopes,
         * because we cannot rely on the direction of travel alone; it is
         * possible to collide with either slope while moving up or down.
         */
        if (isCollisionWithCeilingSlope()){
            // Collisions with ceiling slopes always involve the top edge of the
            // Hitbox.
            return true;
        }

        if (isCollisionWithFloorSlope()){
            // Collisions with floor slopes always involve the bottom edge of
            // the Hitbox.
            return false;
        }

        // Otherwise...
        // If the Entity was moving up, the top edge must have collided
        return attempted_dy < 0;
    }

    /**
     * Determines if this collision was with the bottom edge of the Hitbox.
     *
     * @return
     */
    private boolean wasCollisionWithBottomEdge() {

        /*
         * We have to include these special cases for floor and ceiling slopes,
         * because we cannot rely on the direction of travel alone; it is
         * possible to collide with either slope while moving up or down.
         */
        if (isCollisionWithFloorSlope()){
            // Collisions with floor slopes always involve the bottom edge of
            // the Hitbox.
            return true;
        }

        if (isCollisionWithCeilingSlope()){
            // Collisions with ceiling slopes always involve the top edge of the
            // Hitbox.
            return false;
        }

        // Otherwise...
        // If the Entity was moving down, the bottom edge must have collided
        return attempted_dy > 0;
    }

    /**
     * Determines if the Hitbox's y-speed should be maintained, even if a
     * collision has occurred.
     *
     * @return
     */
    public boolean shouldMaintainSpeedY() {
        return maintainSpeedY;
    }

    /**
     * Determines if the Hitbox should "stick" to a floor slope.
     *
     * <p>If Hitboxes didn't "stick" to slopes, then with enough forward
     * momentum an Entity running down a slope would fly off the slope and then
     * fall down onto it in an arc (the "stairs effect").
     *
     * <p>To counter this, we ensure that a Hitbox that is on the ground remains
     * "stuck" to a slope unless some upward momentum (e.g. a jump) is applied.
     *
     * @return
     */
    public boolean shouldHitboxStickToSlope() {
        return hitbox.isGrounded() && attempted_dy > 0;
    }

}
