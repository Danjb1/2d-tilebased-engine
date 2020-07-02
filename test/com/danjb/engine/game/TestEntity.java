package com.danjb.engine.game;

import com.danjb.engine.game.entities.Entity;
import com.danjb.engine.game.physics.Hitbox;
import com.danjb.engine.util.GameUtils;

/**
 * Sample Entity used by unit tests.
 *
 * @author Dan Bryce
 */
public class TestEntity extends Entity {

    /**
     * Width, in world units.
     */
    private static final float WIDTH = GameUtils.worldUnits(1);

    /**
     * Height, in world units.
     */
    private static final float HEIGHT = GameUtils.worldUnits(1);

    @Override
    protected Hitbox createHitbox(float x, float y) {
        return new Hitbox(x, y, WIDTH, HEIGHT, this);
    }

}
