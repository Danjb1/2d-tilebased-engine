package demo.game.entities;

import engine.game.GameUtils;
import engine.game.GameUtils.DirectionX;
import engine.game.entities.Entity;
import engine.game.physics.Physics;

/**
 * Example Entity that can move and jump.
 *
 * @author Dan Bryce
 */
public class Player extends Entity {

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
     * Creates a Player at the given position.
     *
     * @param x
     * @param y
     */
    public Player(float x, float y) {
        super(x, y, WIDTH, HEIGHT);

        hitbox.setMaxSpeedX(GameUtils.worldUnits(MAX_SPEED_X));
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

        float speedX = hitbox.getSpeedX();

        if (movementDirection == DirectionX.NONE) {
            // Decelerate
            speedX = Physics.applyFriction(hitbox.getSpeedX(), delta);

        } else {
            // Accelerate in direction of travel
            speedX = Physics.applyAcceleration(hitbox.getSpeedX(), delta,
                    movementDirection.getMultiplier() * ACCELERATION);
        }

        hitbox.setSpeedX(speedX);
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