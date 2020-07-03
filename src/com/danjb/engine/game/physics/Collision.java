package com.danjb.engine.game.physics;

import com.danjb.engine.game.physics.Hitbox.CollisionNode;
import com.danjb.engine.game.tiles.PhysicsTile;

/**
 * Class that holds information about a single collision in the x- or y-axis.
 *
 * @author Dan Bryce
 */
public class Collision implements Comparable<Collision> {

    /**
     * Absolute position of the collision (x or y), in world units.
     */
    public final float collisionPos;

    /**
     * Distance to the collision, in world units.
     */
    public final float distanceToCollision;

    /**
     * The CollisionNode that triggered this Collision.
     */
    public final CollisionNode node;

    /**
     * Tile with which the collision occurred.
     */
    public final PhysicsTile tile;

    /**
     * Whether this Collision is valid.
     */
    protected boolean valid = true;

    /**
     * Constructs a Collision.
     *
     * @param collisionPos
     * @param node
     * @param tile
     * @param distanceToCollision
     */
    private Collision(
            float collisionPos,
            CollisionNode node,
            PhysicsTile tile,
            float distanceToCollision) {
        this.collisionPos = collisionPos;
        this.node = node;
        this.tile = tile;
        this.distanceToCollision = distanceToCollision;
    }

    /**
     * Allows this Collision to be sorted relative to other Collisions.
     *
     * Collisions are sorted by distance; nearer Collisions first.
     */
    @Override
    public int compareTo(Collision other) {
        float myDist = Math.abs(distanceToCollision);
        float otherDist = Math.abs(other.distanceToCollision);

        return Float.compare(myDist, otherDist);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Factory Methods
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a Collision.
     *
     * @param posBefore
     * Absolute position of the relevant node before the collision.
     * @param posAfter
     * Absolute position of the relevant node after the collision.
     * @param node The Node involved in this Collision.
     * @param tile Tile with which the collision occurred.
     * @return
     */
    public static Collision create(
            float posBefore,
            float posAfter,
            CollisionNode node,
            PhysicsTile tile) {
        float distanceToCollision = posAfter - posBefore;
        return new Collision(posAfter, node, tile, distanceToCollision);
    }

}
