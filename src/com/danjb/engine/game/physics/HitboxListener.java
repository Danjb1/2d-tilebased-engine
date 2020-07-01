package com.danjb.engine.game.physics;

/**
 * Interface that allows for callbacks from a Hitbox.
 *
 * @author Dan Bryce
 */
public interface HitboxListener {

    /**
     * Called after a Hitbox has been moved by the game's physics.
     *
     * @param result CollisionResult that was applied to the Hitbox.
     */
    void hitboxMoved(CollisionResult result);

    /**
     * Called whenever a Hitbox lands on the ground.
     */
    void hitboxLanded();

    /**
     * Called whenever a Hitbox leaves the ground.
     */
    void hitboxLeftGround();

    /**
     * Called every frame when a Hitbox starts to fall below the bottom row of
     * the level.
     */
    void hitboxFallingOutOfBounds();

    /**
     * Called every frame when a Hitbox is entirely below the bottom row of the
     * level.
     */
    void hitboxFallenOutOfBounds();

}
