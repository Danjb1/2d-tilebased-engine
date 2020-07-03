package com.danjb.engine.game;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.danjb.engine.game.entities.Entity;
import com.danjb.engine.game.level.Level;
import com.danjb.engine.game.level.TileProvider;
import com.danjb.engine.game.physics.Hitbox;
import com.danjb.engine.game.physics.Physics;
import com.danjb.engine.game.tiles.TestTileProvider;
import com.danjb.engine.util.GameUtils;

/**
 * Tests related to the game's handling of inconsistent framerates.
 *
 * <p>These are irrelevant if we are using a fixed timestep.
 *
 * @author Dan Bryce
 */
public class FramerateTest {

    @Test
    public void testPhysicsAt60Fps() {

        // GIVEN an Entity is moving right at 0.5 world units / second
        Level level = TestUtils.createLevel(3, 3,
                "0 0 0",
                "0 0 0",
                "1 1 1"
        );
        TileProvider tileProvider = new TestTileProvider();
        Logic logic = new Logic(tileProvider);
        logic.changeLevel(level);
        Entity e = new TestEntity();
        logic.addEntity(e,
                GameUtils.worldUnits(0),
                GameUtils.worldUnits(1));
        Hitbox hitbox = e.hitbox;

        // WHEN 2 seconds have passed
        int msPerFrame = 15;
        for (int msPassed = 0; msPassed < 2000; msPassed += msPerFrame) {
            hitbox.setSpeedX(GameUtils.worldUnits(0.5f));
            hitbox.moveWithCollision(level, tileProvider, msPerFrame);
        }

        // THEN the Entity has moved approximately 1 world unit
        assertEquals(GameUtils.worldUnits(1), hitbox.x, 0.05f);
    }

    @Test
    public void testPhysicsAt20Fps() {

        // GIVEN an Entity is moving right at 0.5 world units / second
        Level level = TestUtils.createLevel(3, 3,
                "0 0 0",
                "0 0 0",
                "1 1 1"
        );
        TileProvider tileProvider = new TestTileProvider();
        Logic logic = new Logic(tileProvider);
        logic.changeLevel(level);
        Entity e = new TestEntity();
        logic.addEntity(e,
                GameUtils.worldUnits(0),
                GameUtils.worldUnits(1));
        Hitbox hitbox = e.hitbox;

        // WHEN 2 seconds have passed
        int msPerFrame = 45;
        for (int msPassed = 0; msPassed < 2000; msPassed += msPerFrame) {
            hitbox.setSpeedX(GameUtils.worldUnits(0.5f));
            hitbox.moveWithCollision(level, tileProvider, msPerFrame);
        }

        // THEN the Entity has moved approximately 1 world unit
        assertEquals(GameUtils.worldUnits(1), hitbox.x, 0.05f);
    }

    @Test
    public void testLagSpikeDoesntBreakPhysics() {

        // GIVEN an Entity that is grounded
        Level level = TestUtils.createLevel(3, 3,
                "0 0 0",
                "0 0 0",
                "1 1 1"
        );
        Entity entity = new TestEntity();
        TileProvider tileProvider = new TestTileProvider();
        Logic logic = new Logic(tileProvider);
        logic.changeLevel(level);
        logic.addEntity(entity,
                GameUtils.worldUnits(1),
                GameUtils.worldUnits(1));

        // WHEN a lag spike occurs
        entity.update(1000);

        // THEN the Entity has not moved
        Hitbox hitbox = entity.hitbox;
        assertEquals(GameUtils.worldUnits(1), hitbox.y,
                Physics.SMALLEST_DISTANCE);
        assertEquals(GameUtils.worldUnits(1), hitbox.x,
                Physics.SMALLEST_DISTANCE);
    }

}
