package com.danjb.engine.game.entities;

/**
 * Component that gives Entities the ability to collide with each other.
 *
 * @author Dan Bryce
 */
public abstract class CollisionListener extends EntityComponent {

    public static final String KEY = "collision_listener";

    public CollisionListener() {
        super(KEY);
    }

    /**
     * Determines whether this component's parent can collide with the given
     * Entity.
     *
     * <p>This is used to prevent us checking for collisions unnecessarily.
     *
     * @param e
     * @return
     */
    public boolean canCollideWith(Entity e) {
        return false;
    }

    /**
     * Handle a collision between this component's parent and another Entity.
     *
     * @param other The Entity with which this component's parent collided.
     */
    public abstract void collidedWith(Entity other);

}
