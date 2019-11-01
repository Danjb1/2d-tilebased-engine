package engine.game.tiles;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import engine.game.GameUtils;
import engine.game.physics.CollisionResult;
import engine.game.physics.Hitbox;
import engine.game.physics.Hitbox.CollisionNode;
import engine.game.physics.PostProcessCollision;

/**
 * Right Ceiling Slope.
 *
 * <pre>
 *  #####
 *    ###
 *      #
 * </pre>
 */
public class RightCeilingSlopeTest {

    private Slope slope = new RightCeilingSlope(0);

    @Test
    public void resolveCollisions_NoCollisionWhenNotIntersectingSlope() {
        /*
         * GIVEN:
         *
         * Slope is at (2, 1)
         * Hitbox is at (2, 2) and moving by (0, -0.1f)
         *
         *   ####
         *   ``\
         *     E`
         */
        float hX = GameUtils.worldUnits(2);
        float hY = GameUtils.worldUnits(2);
        float dx = GameUtils.worldUnits(0);
        float dy = GameUtils.worldUnits(-0.1f);
        int slopeTileX = 2;
        int slopeTileY = 1;
        Hitbox hitbox = new Hitbox(hX, hY, 1, 1, null);

        // WHEN resolving collisions with this Slope
        CollisionResult result = new CollisionResult(hitbox, dx, dy);
        CollisionNode node = hitbox.getTopNodes()[1];
        PostProcessCollision collision =
                new PostProcessCollision(slope, slopeTileX, slopeTileY, node);
        slope.postProcessing(result, collision);

        // THEN no collision is added
        assertEquals(0, result.getCollisionsY().size());
        assertEquals(null, result.getNearestCollisionY());
    }

    @Test
    public void resolveCollisions_CollideWhenJumpingIntoSlope_Y() {
        /*
         * GIVEN:
         *
         * Slope is at (2, 1)
         * Hitbox is at (2, 2) and moving by (0, -0.75f)
         *
         *   ####
         *   ``\
         *     E`
         */
        float hX = GameUtils.worldUnits(2);
        float hY = GameUtils.worldUnits(2);
        float dx = GameUtils.worldUnits(0);
        float dy = GameUtils.worldUnits(-0.75f);
        int slopeTileX = 2;
        int slopeTileY = 1;
        Hitbox hitbox = new Hitbox(hX, hY, 1, 1, null);

        // WHEN resolving collisions with this Slope
        CollisionResult result = new CollisionResult(hitbox, dx, dy);
        CollisionNode node = hitbox.getTopNodes()[1];
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
    public void resolveCollisions_CollideWhenJumpingIntoSlope_XAndY() {
        /*
         * GIVEN:
         *
         * Slope is at (2, 1)
         * Hitbox is at (1, 2) and moving by (0.75f, -0.75f)
         *
         *   ####
         *   ``\
         *    E `
         */
        float hX = GameUtils.worldUnits(1);
        float hY = GameUtils.worldUnits(2);
        float dx = GameUtils.worldUnits(0.75f);
        float dy = GameUtils.worldUnits(-0.75f);
        int slopeTileX = 2;
        int slopeTileY = 1;
        Hitbox hitbox = new Hitbox(hX, hY, 1, 1, null);

        // WHEN resolving collisions with this Slope
        CollisionResult result = new CollisionResult(hitbox, dx, dy);
        CollisionNode node = hitbox.getTopNodes()[1];
        PostProcessCollision collision =
                new PostProcessCollision(slope, slopeTileX, slopeTileY, node);
        slope.postProcessing(result, collision);

        // THEN a collision is added 0.25f world units into the Slope
        //  => Initial slopeNodeX = 1.5f
        //  => Destination slopeNodeX = 2.25f
        //  => This is 0.25f world units into the Slope
        //  => For a right ceiling slope: 0.25 (x-axis) -> 0.25 (y-axis)
        assertEquals(1, result.getCollisionsY().size());
        assertEquals(
                GameUtils.worldUnits(1.25f),
                result.getNearestCollisionY().collisionPos, 0.001);
    }

    @Test
    public void resolveCollisions_CollideWhenIntersectingTileAboveSlope() {
        /*
         * GIVEN:
         *
         * Slope is at (2, 1)
         * Hitbox is at (1, 1) and moving by (0.5f, -0.25f)
         *
         *   ####
         *   `E\
         *      `
         */
        float hX = GameUtils.worldUnits(1);
        float hY = GameUtils.worldUnits(1);
        float dx = GameUtils.worldUnits(0.5f);
        float dy = GameUtils.worldUnits(-0.25f);
        int slopeTileX = 2;
        int slopeTileY = 1;
        Hitbox hitbox = new Hitbox(hX, hY, 1, 1, null);

        // WHEN resolving collisions with this Slope
        CollisionResult result = new CollisionResult(hitbox, dx, dy);
        CollisionNode node = hitbox.getTopNodes()[1];
        PostProcessCollision collision =
                new PostProcessCollision(slope, slopeTileX, slopeTileY, node);
        slope.postProcessing(result, collision);

        // THEN a collision is added at ceiling level
        // (because the slope node has only just entered the Slope)
        assertEquals(1, result.getCollisionsY().size());
        assertEquals(
                Tile.getTop(GameUtils.worldUnits(1)),
                result.getNearestCollisionY().collisionPos, 0.001);
    }

    @Test
    public void testPointInSlope() {
        // 4 corners of the Tile
        assertEquals(true, slope.isPointInSlope(0, 0));
        assertEquals(true, slope.isPointInSlope(Tile.WIDTH, 0));
        assertEquals(true, slope.isPointInSlope(Tile.WIDTH, Tile.HEIGHT));
        assertEquals(false, slope.isPointInSlope(0, Tile.HEIGHT));
    }

    @Test
    public void testSlopeY_At_X() {
        // When 1/4 of the way into the Tile, we should be 1/4 from the top
        assertEquals(Tile.HEIGHT / 4,
                slope.calculateY(Tile.WIDTH / 4),
                0.1);
    }

}
