package com.danjb.engine_demo.game.entities.player;

import com.danjb.engine.game.physics.Hitbox;
import com.danjb.engine.game.physics.Physics;
import com.danjb.engine.util.Directions.DirectionX;
import com.danjb.engine.util.GameUtils;
import com.danjb.engine_demo.game.entities.DemoEntity;

/**
 * Example Entity that can move and jump.
 *
 * @author Dan Bryce
 */
public class Player extends DemoEntity {

    /**
     * Width, in world units.
     */
    private static final float WIDTH = GameUtils.worldUnits(1);

    /**
     * Height, in world units.
     */
    private static final float HEIGHT = GameUtils.worldUnits(1);

    /**
     * Horizontal acceleration, in world units per second per second.
     */
    private static final float ACCELERATION = GameUtils.worldUnits(60);

    /**
     * Maximum horizontal speed, in world units per second.
     */
    private static final float MAX_SPEED_X = GameUtils.worldUnits(16);

    /**
     * Jump speed, in world units per second.
     */
    private static final float JUMP_SPEED = GameUtils.worldUnits(10);

    /**
     * Horizontal direction of travel.
     */
    private DirectionX movementDirection = DirectionX.NONE;

    /**
     * Creates a Player.
     */
    public Player() {
        super(DemoEntity.TYPE_PLAYER);
    }

    @Override
    protected Hitbox createHitbox(float x, float y) {
        Hitbox hitbox = new Hitbox(x, y, WIDTH, HEIGHT, this);

        // Player should slow down dramatically in the air
        hitbox.airFrictionCoefficient = 10f;

        hitbox.setMaxSpeedX(MAX_SPEED_X);

        return hitbox;
    }

    @Override
    public void update(int delta) {
        super.update(delta);

        applyAcceleration(delta);
    }

    /**
     * Accelerates / decelerates based on the current movement direction.
     *
     * @param delta
     */
    private void applyAcceleration(int delta) {
        if (movementDirection != DirectionX.NONE) {
            // Accelerate in direction of travel
            hitbox.setSpeedX(Physics.applyAcceleration(
                    hitbox.getSpeedX(),
                    delta,
                    movementDirection.getMultiplier() * ACCELERATION));
        }

    }

    @Override
    public boolean isAffectedByGroundFriction() {
        // Only when no direction is pressed
        return movementDirection == DirectionX.NONE;
    }

    @Override
    public boolean isAffectedByAirFrictionX() {
        // Only when no direction is pressed
        return movementDirection == DirectionX.NONE;
    }

    /**
     * Sets the current direction of travel.
     *
     * @param dir
     */
    public void setMovementDirection(DirectionX dir) {
        this.movementDirection = dir;
    }

    /**
     * Causes the Player to jump.
     */
    public void jump() {
        hitbox.setSpeedY(-JUMP_SPEED);
    }

}
