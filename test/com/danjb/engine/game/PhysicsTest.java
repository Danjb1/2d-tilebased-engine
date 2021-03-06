package com.danjb.engine.game;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.danjb.engine.game.entities.Entity;
import com.danjb.engine.game.level.Level;
import com.danjb.engine.game.level.TileProvider;
import com.danjb.engine.game.physics.CollisionResult;
import com.danjb.engine.game.physics.Physics;
import com.danjb.engine.game.tiles.TestTileProvider;
import com.danjb.engine.util.GameUtils;

/**
 * Tests of the game's Physics.
 *
 * @author Dan Bryce
 */
public class PhysicsTest {

    /**
     * Tests an Entity moving without collisions.
     */
    @Test
    public void testCollision_None() {

        // GIVEN an Entity that is in mid-air
        Level level = TestUtils.createLevel(3, 3,
                "0 0 0",
                "0 0 0",
                "0 0 0"
        );
        TileProvider tileProvider = new TestTileProvider();
        Logic logic = new Logic(tileProvider);
        logic.changeLevel(level);
        Entity entity = new TestEntity();
        logic.addEntity(entity,
                GameUtils.worldUnits(1),
                GameUtils.worldUnits(1));

        // WHEN trying to move down by half a tile, right by half a tile
        CollisionResult collision = Physics.getCollisionResult(
                level, tileProvider, entity.hitbox, 0.5f, 0.5f);

        // THEN no collision is detected
        assertEquals(false, collision.hasCollisionOccurredX());
        assertEquals(false, collision.hasCollisionOccurredY());
    }

    /**
     * Tests collision with a solid floor.
     */
    @Test
    public void testCollision_Floor() {

        // GIVEN an Entity that is grounded
        Level level = TestUtils.createLevel(3, 3,
                "0 0 0",
                "0 0 0",
                "1 1 1"
        );
        TileProvider tileProvider = new TestTileProvider();
        Logic logic = new Logic(tileProvider);
        logic.changeLevel(level);
        Entity entity = new TestEntity();
        logic.addEntity(entity,
                GameUtils.worldUnits(1),
                GameUtils.worldUnits(1));

        // WHEN trying to move down by half a tile
        CollisionResult collision = Physics.getCollisionResult(
                level, tileProvider, entity.hitbox, 0, 0.5f);

        // THEN the nearest collision detected is at the top of the floor
        assertEquals(GameUtils.worldUnits(2),
                collision.getNearestCollisionY().collisionPos,
                Physics.SMALLEST_DISTANCE);
    }

    /**
     * Tests collision with a solid ceiling.
     */
    @Test
    public void testCollision_Ceiling() {

        // GIVEN an Entity that is against a ceiling
        Level level = TestUtils.createLevel(3, 3,
                "1 1 1",
                "0 0 0",
                "0 0 0"
        );
        TileProvider tileProvider = new TestTileProvider();
        Logic logic = new Logic(tileProvider);
        logic.changeLevel(level);
        Entity entity = new TestEntity();
        logic.addEntity(entity,
                GameUtils.worldUnits(1),
                GameUtils.worldUnits(1));

        // WHEN trying to move up by half a tile
        CollisionResult collision = Physics.getCollisionResult(
                level, tileProvider, entity.hitbox, 0, -0.5f);

        // THEN the nearest collision detected is at the bottom of the ceiling
        assertEquals(GameUtils.worldUnits(1) - Physics.SMALLEST_DISTANCE,
                collision.getNearestCollisionY().collisionPos,
                Physics.SMALLEST_DISTANCE);
    }

    /**
     * Tests collision with a solid left wall.
     */
    @Test
    public void testCollision_LeftWall() {

        // GIVEN an Entity that is against a left wall
        Level level = TestUtils.createLevel(3, 3,
                "1 0 0",
                "1 0 0",
                "1 0 0"
        );
        TileProvider tileProvider = new TestTileProvider();
        Logic logic = new Logic(tileProvider);
        logic.changeLevel(level);
        Entity entity = new TestEntity();
        logic.addEntity(entity,
                GameUtils.worldUnits(1),
                GameUtils.worldUnits(1));

        // WHEN trying to move left by half a tile
        CollisionResult collision = Physics.getCollisionResult(
                level, tileProvider, entity.hitbox, -0.5f, 0);

        // THEN the nearest collision detected is at the right edge of the wall
        assertEquals(GameUtils.worldUnits(1) - Physics.SMALLEST_DISTANCE,
                collision.getNearestCollisionX().collisionPos,
                Physics.SMALLEST_DISTANCE);
    }

    /**
     * Tests collision with a solid right wall.
     */
    @Test
    public void testCollision_RightWall() {

        // GIVEN an Entity that is against a right wall
        Level level = TestUtils.createLevel(3, 3,
                "0 0 1",
                "0 0 1",
                "0 0 1"
        );
        TileProvider tileProvider = new TestTileProvider();
        Logic logic = new Logic(tileProvider);
        logic.changeLevel(level);
        Entity entity = new TestEntity();
        logic.addEntity(entity,
                GameUtils.worldUnits(1),
                GameUtils.worldUnits(1));

        // WHEN trying to move left by half a tile
        CollisionResult collision = Physics.getCollisionResult(
                level, tileProvider, entity.hitbox, 0.5f, 0);

        // THEN the nearest collision detected is at the left edge of the wall
        assertEquals(GameUtils.worldUnits(2),
                collision.getNearestCollisionX().collisionPos,
                Physics.SMALLEST_DISTANCE);
    }

}
