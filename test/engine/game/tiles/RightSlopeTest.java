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
 * Right Slope.
 *
 * <pre>
 *      #
 *    ###
 *  #####
 * </pre>
 */
public class RightSlopeTest {

    private Slope slope = new RightSlope(0);

    @Test
    public void resolveCollisions_NoCollisionWhenNotIntersectingSlope() {
        /*
         * GIVEN:
         *
         * Slope is at (2, 1)
         * Hitbox is at (2, 0) and moving by (0, 0.1f)
         *
         *     E_
         *   __/
         *   ####
         */
        float hX = GameUtils.worldUnits(2);
        float hY = GameUtils.worldUnits(0);
        float dx = GameUtils.worldUnits(0);
        float dy = GameUtils.worldUnits(0.1f);
        int slopeTileX = 2;
        int slopeTileY = 1;
        Hitbox hitbox = new Hitbox(hX, hY, 1, 1, null);

        // WHEN resolving collisions with this Slope
        CollisionResult result = new CollisionResult(hitbox, dx, dy);
        CollisionNode node = hitbox.getBottomNodes()[1];
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
         * Slope is at (2, 1)
         * Hitbox is at (2, 0) and moving by (0, 0.75f)
         *
         *     E_
         *   __/
         *   ####
         */
        float hX = GameUtils.worldUnits(2);
        float hY = GameUtils.worldUnits(0);
        float dx = GameUtils.worldUnits(0);
        float dy = GameUtils.worldUnits(0.75f);
        int slopeTileX = 2;
        int slopeTileY = 1;
        Hitbox hitbox = new Hitbox(hX, hY, 1, 1, null);

        // WHEN resolving collisions with this Slope
        CollisionResult result = new CollisionResult(hitbox, dx, dy);
        CollisionNode node = hitbox.getBottomNodes()[1];
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
         * Slope is at (2, 1)
         * Hitbox is at (1, 1) and moving by (0.5f, 0.25f)
         *      _
         *   _E/
         *   ####
         */
        float hX = GameUtils.worldUnits(1);
        float hY = GameUtils.worldUnits(1);
        float dx = GameUtils.worldUnits(0.5f);
        float dy = GameUtils.worldUnits(0.25f);
        int slopeTileX = 2;
        int slopeTileY = 1;
        Hitbox hitbox = new Hitbox(hX, hY, 1, 1, null);

        // WHEN resolving collisions with this Slope
        CollisionResult result = new CollisionResult(hitbox, dx, dy);
        CollisionNode node = hitbox.getBottomNodes()[1];
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
         * Slope is at (2, 1)
         * Hitbox is at (1, 1) and moving by (0.25f, 0.25f)
         *      _
         *   _E/
         *   ####
         *
         * Thus, the Hitbox will intersect the Slope, but its slode node will
         * lie outside the slope.
         */
        float hX = GameUtils.worldUnits(1);
        float hY = GameUtils.worldUnits(1);
        float dx = GameUtils.worldUnits(0.25f);
        float dy = GameUtils.worldUnits(0.25f);
        int slopeTileX = 2;
        int slopeTileY = 1;
        Hitbox hitbox = new Hitbox(hX, hY, 1, 1, null);

        // AND the Hitbox has already collided with the ground
        CollisionResult result = new CollisionResult(hitbox, dx, dy);
        for (CollisionNode node : hitbox.getBottomNodes()) {
            Collision groundCollision = Collision.create(
                    hY,
                    Tile.getTop(hY + GameUtils.worldUnits(1)),
                    node,
                    new SolidBlock(0));
            result.addCollision_Y(groundCollision);
        }

        // WHEN resolving collisions with this Slope
        CollisionNode bottomLeft = hitbox.getBottomNodes()[0];
        CollisionNode bottomRight = hitbox.getBottomNodes()[1];
        PostProcessCollision collision =
                new PostProcessCollision(slope, slopeTileX, slopeTileY, bottomRight);
        slope.postProcessing(result, collision);

        // THEN the ground collision with the bottom-left node is still valid
        assertEquals(bottomLeft, result.getNearestCollisionY().node);
    }

    @Test
    public void testPointInSlope() {
        // 4 corners of the Tile
        assertEquals(false, slope.isPointInsideSolidArea(0, 0));
        assertEquals(true, slope.isPointInsideSolidArea(Tile.WIDTH, 0));
        assertEquals(true, slope.isPointInsideSolidArea(Tile.WIDTH, Tile.HEIGHT));
        assertEquals(true, slope.isPointInsideSolidArea(0, Tile.HEIGHT));

        // Below the Tile
        assertEquals(true, slope.isPointInsideSolidArea(0, 2 * Tile.HEIGHT));
    }

    @Test
    public void testSlopeY_At_X() {
        // When 1/4 of the way into the Tile, we should be 3/4 from the top
        assertEquals(3 * Tile.HEIGHT / 4,
                slope.calculateY(Tile.WIDTH / 4),
                0.1);
    }

}
