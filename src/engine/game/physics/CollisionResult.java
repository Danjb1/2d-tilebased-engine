package engine.game.physics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import engine.game.physics.Hitbox.CollisionNode;

/**
 * Class designed to hold a number of collisions during physics processing.
 *
 * @author Dan Bryce
 */
public class CollisionResult {

    /**
     * Hitbox involved in this collision.
     *
     * <p>Until the CollisionResult is applied, this can be used to get the
     * initial position of the Hitbox, before any movement takes place.
     */
    public Hitbox hitbox;

    /**
     * All Collisions that have occurred in the x-axis.
     */
    private List<Collision> collisionsX = new ArrayList<>();

    /**
     * All Collisions that have occurred in the y-axis.
     */
    private List<Collision> collisionsY = new ArrayList<>();

    /**
     * All PostProcessCollision that have occurred.
     */
    private Set<PostProcessCollision> postProcessCollisions = new HashSet<>();

    /**
     * Nearest x-Collision detected by this CollisionResult.
     */
    private Collision nearestCollisionX;

    /**
     * Nearest y-Collision detected by this CollisionResult.
     */
    private Collision nearestCollisionY;

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
     * Whether Collisions still need to be resolved.
     */
    private boolean needsResolve = true;

    ////////////////////////////////////////////////////////////////////////////
    // Lifecycle
    ////////////////////////////////////////////////////////////////////////////

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
    }

    /**
     * Must be called at the end of collision processing.
     *
     * <p>After this, no more collisions should be added.
     */
    public void finish() {
        resolvePostProcessCollisions();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Movement
    ////////////////////////////////////////////////////////////////////////////

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

    ////////////////////////////////////////////////////////////////////////////
    // Adding / Invalidating / Getting Collisions
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the nearest Collision in the x-axis.
     *
     * @return
     */
    public Collision getNearestCollisionX() {
        resolve();
        return nearestCollisionX;
    }

    /**
     * Returns the nearest Collision in the y-axis.
     *
     * @return
     */
    public Collision getNearestCollisionY() {
        resolve();
        return nearestCollisionY;
    }

    /**
     * Gets all Collisions that have occurred in the x-axis.
     *
     * <p>Changes to the resulting list will have no effect.
     *
     * @return
     */
    public List<Collision> getCollisionsX() {
        return new ArrayList<Collision>(collisionsX);
    }

    /**
     * Gets all Collisions that have occurred in the y-axis.
     *
     * <p>Changes to the resulting list will have no effect.
     *
     * @return
     */
    public List<Collision> getCollisionsY() {
        return new ArrayList<Collision>(collisionsY);
    }

    /**
     * Records a Collision in the x-axis.
     *
     * @param collision
     */
    public void addCollision_X(Collision collision) {
        collisionsX.add(collision);
        needsResolve = true;
    }

    /**
     * Records a Collision in the y-axis.
     *
     * @param collision
     */
    public void addCollision_Y(Collision collision) {
        collisionsY.add(collision);
        needsResolve = true;
    }

    /**
     * Records a PostProcessCollision.
     *
     * @param collision
     */
    public void addPostProcessCollision(PostProcessCollision collision) {
        postProcessCollisions.add(collision);
    }

    /**
     * Renders a Collision invalid.
     *
     * <p>It is important to note that if an x-collision is invalidated during
     * or after the post-processing stage of collision detection, the results
     * may not be entirely as expected, due to the fact that y-collisions will
     * have already been calculated based on x-collisions.
     *
     * @param collision
     */
    public void invalidateCollision(Collision collision) {
        collision.valid = false;
        needsResolve = true;
    }

    /**
     * Determines if a Collision occurred in the x-axis.
     *
     * @return
     */
    public boolean hasCollisionOccurredX() {
        resolve();
        return nearestCollisionX != null;
    }

    /**
     * Determines if a Collision occurred in the y-axis.
     *
     * @return
     */
    public boolean hasCollisionOccurredY() {
        resolve();
        return nearestCollisionY != null;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Collision Resolution
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Resolves all registered Collisions.
     */
    private void resolve() {
        if (needsResolve) {
            needsResolve = false;
            resolveCollisions_X();
            resolveCollisions_Y();
        }
    }

    /**
     * Determines the nearest Collision in the x-axis.
     *
     * <p>Must be called whenever a Collision is added or invalidated.
     */
    private void resolveCollisions_X() {

        // Find the nearest valid collision
        nearestCollisionX = collisionsX
                .stream()
                .filter(c -> c.valid)
                .sorted()
                .findFirst()
                .orElse(null);

        if (nearestCollisionX == null) {
            newX = hitbox.x + attempted_dx;
            return;
        }

        if (nearestCollisionX.node.isOnLeftEdge()) {
            // We add a small distance because we want the Hitbox to be placed
            // NEXT to the colliding Tile, not inside it.
            newX = nearestCollisionX.collisionPos + Physics.SMALLEST_DISTANCE;
        } else {
            newX = nearestCollisionX.collisionPos - hitbox.width;
        }
    }

    /**
     * Determines the nearest Collision in the y-axis.
     *
     * See {@link #resolveCollisions_X}.
     */
    private void resolveCollisions_Y() {

        // Find the nearest valid collision
        nearestCollisionY = collisionsY
                .stream()
                .filter(c -> c.valid)
                .sorted()
                .findFirst()
                .orElse(null);

        if (nearestCollisionY == null) {
            newY = hitbox.y + attempted_dy;
            return;
        }

        if (nearestCollisionY.node.isOnTopEdge()) {
            // We add a small distance because we want the Hitbox to be placed
            // NEXT to the colliding Tile, not inside it.
            newY = nearestCollisionY.collisionPos + Physics.SMALLEST_DISTANCE;
        } else {
            newY = nearestCollisionY.collisionPos - hitbox.height;
        }
    }

    /**
     * Resolves all PostProcessCollisions.
     *
     * <p>Must be called after all collisions have been added.
     */
    private void resolvePostProcessCollisions() {

        // Defer to the PostProcessingTiles to resolve their own collisions
        for (PostProcessCollision collision : postProcessCollisions) {
            collision.tile.postProcessing(this, collision);
        }

        // New collisions may have been added, which need to be resolved
        needsResolve = true;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Node Position Calculations
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Calculates the initial absolute x-position of a CollisionNode.
     *
     * @param node
     * @return
     */
    public float initialNodeX(CollisionNode node) {
        return hitbox.x + node.x;
    }

    /**
     * Calculates the initial absolute y-position of a CollisionNode.
     *
     * <p>This should be used to find the y-position used in an x-collision,
     * since x-collisions are generated before any y-movement is applied.
     *
     * @param node
     * @return
     */
    public float initialNodeY(CollisionNode node) {
        return hitbox.y + node.y;
    }

    /**
     * Calculates the desired absolute x-position of a CollisionNode, that is,
     * the position of this node if no x-collisions occurred.
     *
     * @param node
     * @return
     */
    public float desiredNodeX(CollisionNode node) {
        return hitbox.x + node.x + attempted_dx;
    }

    /**
     * Calculates the desired absolute y-position of a CollisionNode, that is,
     * the position of this node if no y-collisions occurred.
     *
     * @param node
     * @return
     */
    public float desiredNodeY(CollisionNode node) {
        return hitbox.y + node.y + attempted_dy;
    }

    /**
     * Calculates the new absolute x-position of a CollisionNode.
     *
     * @param node
     * @return
     */
    public float newNodeX(CollisionNode node) {
        return left() + node.x;
    }

    /**
     * Calculates the new absolute y-position of a CollisionNode.
     *
     * <p>This should NOT be used to find the y-position used in an x-collision,
     * since x-collisions are generated before any y-movement is applied.
     *
     * @param node
     * @return
     */
    public float newNodeY(CollisionNode node) {
        return top() + node.y;
    }

    ////////////////////////////////////////////////////////////////////////////
    // New Hitbox Positions
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Gets the new Hitbox left, with the nearest Collision applied.
     *
     * @return
     */
    public float left() {
        resolve();
        return newX;
    }

    /**
     * Gets the new Hitbox right.
     *
     * See {@link #left}.
     *
     * @return
     */
    public float right() {
        resolve();
        return newX + hitbox.width - Physics.SMALLEST_DISTANCE;
    }

    /**
     * Gets the new Hitbox centre.
     *
     * See {@link #left}.
     *
     * @return
     */
    public float centreX() {
        resolve();
        return newX + hitbox.width / 2;
    }

    /**
     * Gets the new Hitbox centre.
     *
     * See {@link #left}.
     *
     * @return
     */
    public float centreY() {
        resolve();
        return newY + hitbox.height / 2;
    }

    /**
     * Gets the new Hitbox top.
     *
     * See {@link #left}.
     *
     * @return
     */
    public float top() {
        resolve();
        return newY;
    }

    /**
     * Gets the new Hitbox bottom.
     *
     * See {@link #left}.
     *
     * @return
     */
    public float bottom() {
        resolve();
        return newY + hitbox.height - Physics.SMALLEST_DISTANCE;
    }

}
