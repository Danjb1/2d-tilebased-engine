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
    public void testFramerateIndependence() {

        // GIVEN we have 2 Entities at the same position
        Level level = TestUtils.createLevel(3, 3,
                "0 0 0",
                "0 0 0",
                "1 1 1"
        );
        Entity e1 = new TestEntity(
                GameUtils.worldUnits(0),
                GameUtils.worldUnits(1));
        Hitbox hitbox1 = e1.getHitbox();
        Entity e2 = new TestEntity(hitbox1.x, hitbox1.y);
        Hitbox hitbox2 = e2.getHitbox();
        Logic logic = new Logic(level);
        logic.addEntity(e1);
        logic.addEntity(e2);
        
        // AND both Entities are moving right at 0.5 world units / second
        hitbox1.setSpeedX(0.5f);
        hitbox2.setSpeedX(0.5f);
        
        // WHEN 2 seconds have passed
        // (here we simulate different framerates for each Entity)
        
        // e1: 15 ms per frame
        int msPerFrame = 15;
        for (int msPassed = 0; msPassed < 2000; msPassed += msPerFrame) {
            e1.update(msPerFrame);
        }

        // e2: 11 frames @ 45 ms per frame
        msPerFrame = 45;
        for (int msPassed = 0; msPassed < 2000; msPassed += msPerFrame) {
            e2.update(msPerFrame);
        }
        
        // THEN the Entities have both moved approximately 1 world unit
        System.out.println(hitbox1.x);
        assertEquals(GameUtils.worldUnits(1), hitbox1.x, 0.05f);
        assertEquals(GameUtils.worldUnits(1), hitbox2.x, 0.05f);
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
        assertEquals(GameUtils.worldUnits(1), hitbox.getTop(),
                Physics.SMALLEST_DISTANCE);
        assertEquals(GameUtils.worldUnits(1), hitbox.getLeft(),
                Physics.SMALLEST_DISTANCE);
    }

}
