package engine.game;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import engine.game.entities.Entity;
import engine.game.physics.Hitbox;
import engine.game.physics.Physics;

/**
 * Tests related to the game's handling of inconsistent framerates.
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
        Logic logic = new Logic(level);
        Entity e = new TestEntity(
                GameUtils.worldUnits(0),
                GameUtils.worldUnits(1));
        Hitbox hitbox = e.getHitbox();
        hitbox.setSpeedX(GameUtils.worldUnits(0.5f));
        logic.addEntity(e);

        // WHEN 2 seconds have passed
        int msPerFrame = 15;
        for (int msPassed = 0; msPassed < 2000; msPassed += msPerFrame) {
            logic.updateEntities(msPerFrame);
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
        Logic logic = new Logic(level);
        Entity e = new TestEntity(
                GameUtils.worldUnits(0),
                GameUtils.worldUnits(1));
        Hitbox hitbox = e.getHitbox();
        hitbox.setSpeedX(GameUtils.worldUnits(0.5f));
        logic.addEntity(e);

        // WHEN 2 seconds have passed
        int msPerFrame = 45;
        for (int msPassed = 0; msPassed < 2000; msPassed += msPerFrame) {
            logic.updateEntities(msPerFrame);
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
        Entity entity = new TestEntity(
                GameUtils.worldUnits(1),
                GameUtils.worldUnits(1));
        Logic logic = new Logic(level);
        logic.addEntity(entity);

        // WHEN a lag spike occurs
        entity.update(1000);

        // THEN the Entity has not moved
        Hitbox hitbox = entity.getHitbox();
        assertEquals(GameUtils.worldUnits(1), hitbox.top(),
                Physics.SMALLEST_DISTANCE);
        assertEquals(GameUtils.worldUnits(1), hitbox.left(),
                Physics.SMALLEST_DISTANCE);
    }

}
