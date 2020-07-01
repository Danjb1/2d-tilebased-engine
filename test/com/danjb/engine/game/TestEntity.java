package com.danjb.engine.game;

import com.danjb.engine.game.GameUtils;
import com.danjb.engine.game.entities.Entity;

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

    public TestEntity(float x, float y) {
        super(x, y, WIDTH, HEIGHT);
    }

}
