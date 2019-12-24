package engine.game.tiles;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import engine.game.GameUtils;
import engine.game.physics.Collision;
import engine.game.physics.CollisionResult;
import engine.game.physics.Hitbox;
import engine.game.physics.Hitbox.CollisionNode;
import engine.game.physics.PostProcessCollision;

/**
 * Left Slope.
 *
 * <pre>
 *  #
 *  ###
 *  #####
 * </pre>
 */
public class LeftSlopeTest {

    private Slope slope = new LeftSlope(0);

    @Test
    public void resolveCollisions_NoCollisionWhenNotIntersectingSlope() {
        /*
         * GIVEN:
         *
         * Slope is at (1, 1)
         * Hitbox is at (1, 0) and moving by (0, 0.1f)
         *
         *   _E
         *   #\__
         *   ####
         */
        float hX = GameUtils.worldUnits(1);
        float hY = GameUtils.worldUnits(0);
        float dx = GameUtils.worldUnits(0);
        float dy = GameUtils.worldUnits(0.1f);
        int slopeTileX = 1;
        int slopeTileY = 1;
        Hitbox hitbox = new Hitbox(hX, hY, 1, 1, null);

        // WHEN resolving collisions with this Slope
        CollisionResult result = new CollisionResult(hitbox, dx, dy);
        CollisionNode node = hitbox.getBottomNodes()[0];
        PostProcessCollision collision =
                new PostProcessCollision(slope, slopeTileX, slopeTileY, node);
        slope.postProcessing(result, collision);

        // THEN no collision is added
        assertEquals(0, result.getCollisionsY().size());
        assertEquals(null, result.getNearestCollisionY());
    }

    @Test
    public void resolveCollisions_CollideWhenFallingIntoSlope() {
        /*
         * GIVEN:
         *
         * Slope is at (1, 1)
         * Hitbox is at (1, 0) and moving by (0, 0.75f)
         *
         *   _E
         *   #\__
         *   ####
         */
        float hX = GameUtils.worldUnits(1);
        float hY = GameUtils.worldUnits(0);
        float dx = GameUtils.worldUnits(0);
        float dy = GameUtils.worldUnits(0.75f);
        int slopeTileX = 1;
        int slopeTileY = 1;
        Hitbox hitbox = new Hitbox(hX, hY, 1, 1, null);

        // WHEN resolving collisions with this Slope
        CollisionResult result = new CollisionResult(hitbox, dx, dy);
        CollisionNode node = hitbox.getBottomNodes()[0];
        PostProcessCollision collision =
                new PostProcessCollision(slope, slopeTileX, slopeTileY, node);
        slope.postProcessing(result, collision);

        // THEN a collision is added at the middle of the Slope
        assertEquals(1, result.getCollisionsY().size());
        assertEquals(
                GameUtils.worldUnits(1.5f),
                result.getNearestCollisionY().collisionPos, 0.001);
    }

    @Test
    public void resolveCollisions_CollideWhenIntersectingTileUnderSlope() {
        /*
         * GIVEN:
         *
         * Slope is at (1, 1)
         * Hitbox is at (2, 1) and moving by (-0.5f, 0.25f)
         *   _
         *   #\E_
         *   ####
         */
        float hX = GameUtils.worldUnits(2);
        float hY = GameUtils.worldUnits(1);
        float dx = GameUtils.worldUnits(-0.5f);
        float dy = GameUtils.worldUnits(0.25f);
        int slopeTileX = 1;
        int slopeTileY = 1;
        Hitbox hitbox = new Hitbox(hX, hY, 1, 1, null);

        // WHEN resolving collisions with this Slope
        CollisionResult result = new CollisionResult(hitbox, dx, dy);
        CollisionNode node = hitbox.getBottomNodes()[0];
        PostProcessCollision collision =
                new PostProcessCollision(slope, slopeTileX, slopeTileY, node);
        slope.postProcessing(result, collision);

        // THEN a collision is added at floor level
        // (because the slope node has only just entered the Slope)
        assertEquals(1, result.getCollisionsY().size());
        assertEquals(
                Tile.getBottom(GameUtils.worldUnits(1)),
                result.getNearestCollisionY().collisionPos, 0.001);
    }

    @Test
    public void resolveCollisions_CollideWithGroundBeforeSlopeNodeEntersSlope() {
        /*
         * GIVEN:
         *
         * Slope is at (1, 1)
         * Hitbox is at (2, 1) and moving by (-0.25f, 0.25f)
         *   _
         *   #\E_
         *   ####
         *
         * Thus, the Hitbox will intersect the Slope, but its slode node will
         * lie outside the slope.
         */
        float hX = GameUtils.worldUnits(2);
        float hY = GameUtils.worldUnits(1);
        float dx = GameUtils.worldUnits(0.25f);
        float dy = GameUtils.worldUnits(0.25f);
        int slopeTileX = 1;
        int slopeTileY = 1;
        Hitbox hitbox = new Hitbox(hX, hY, 1, 1, null);

        // AND the Hitbox has already collided with the ground
        CollisionResult result = new CollisionResult(hitbox, dx, dy);
        CollisionNode node = hitbox.getBottomNodes()[0];
        Collision groundCollision = Collision.create(
                hY,
                Tile.getTop(hY + GameUtils.worldUnits(1)),
                node,
                new SolidBlock(0));
        result.addCollision_Y(groundCollision);

        // WHEN resolving collisions with this Slope
        PostProcessCollision collision =
                new PostProcessCollision(slope, slopeTileX, slopeTileY, node);
        slope.postProcessing(result, collision);

        // THEN the ground collision is still present
        assertEquals(1, result.getCollisionsY().size());
        assertEquals(groundCollision, result.getNearestCollisionY());
    }

    @Test
    public void isPointInSlope() {
        // 4 corners of the Tile
        assertEquals(true, slope.isPointInsideSolidArea(0, 0));
        assertEquals(false, slope.isPointInsideSolidArea(Tile.WIDTH, 0));
        assertEquals(true, slope.isPointInsideSolidArea(Tile.WIDTH, Tile.HEIGHT));
        assertEquals(true, slope.isPointInsideSolidArea(0, Tile.HEIGHT));

        // Below the Tile
        assertEquals(true, slope.isPointInsideSolidArea(0, 2 * Tile.HEIGHT));
    }

    @Test
    public void getSlopeY_At_X() {
        // When 1/4 of the way into the Tile, we should be 1/4 from the top
        assertEquals(Tile.HEIGHT / 4,
                slope.calculateY(Tile.WIDTH / 4),
                0.1);
    }

}
