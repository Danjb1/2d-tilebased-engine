package engine.game.physics;

import engine.game.tiles.ForegroundTile;

/**
 * Class that holds information about a single collision in the x- or y-axis.
 *
 * @author Dan Bryce
 */
public class Collision implements Comparable<Collision> {

    /**
     * Distance to the collision, in world units.
     */
    private float distanceToCollision;

    /**
     * Absolute position of the collision (either x or y), in world units.
     */
    private float collisionPos;

    /**
     * Tile with which the collision occurred.
     */
    private ForegroundTile tile;

    /**
     * Constructor for a Collision.
     *
     * @param hitboxPos
     *          Position of the relevant hitbox edge before the collision (x or y).
     *          Used to calculate the distance to the collision.
     * @param collisionPos
     *          Position in the world where the collision occurred (x or y).
     * @param tile Tile with which the collision occurred.
     */
    public Collision(float hitboxPos, float collisionPos, ForegroundTile tile) {
        this.collisionPos = collisionPos;
        this.tile = tile;

        distanceToCollision = collisionPos - hitboxPos;
    }

    /**
     * Allows this Collision to be sorted relative to other Collisions.
     *
     * Collisions are sorted by distance; nearer Collisions first.
     */
    @Override
    public int compareTo(Collision other) {
        float myDist = Math.abs(distanceToCollision);
        float otherDist = Math.abs(other.getDistanceToCollision());

        if (myDist < otherDist) {
            return -1;
        } else if (myDist == otherDist) {
            return 0;
        } else {
            return 1;
        }
    }

    public float getDistanceToCollision() {
        return distanceToCollision;
    }

    public float getCollisionPos() {
        return collisionPos;
    }

    public ForegroundTile getTile() {
        return tile;
    }

}
