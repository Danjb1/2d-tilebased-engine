package engine.game.tiles;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import engine.game.GameUtils;
import engine.game.physics.CollisionResult;
import engine.game.physics.Hitbox;
import engine.game.physics.Hitbox.CollisionNode;
import engine.game.physics.PostProcessCollision;

/**
 * Left Ceiling Slope.
 *
 * <pre>
 *  #####
 *  ###
 *  #
 * </pre>
 */
public class LeftCeilingSlopeTest {

    private Slope slope = new LeftCeilingSlope(0);

    @Test
    public void resolveCollisions_NoCollisionWhenNotIntersectingSlope() {
        /*
         * GIVEN:
         *
         * Slope is at (1, 1)
         * Hitbox is at (1, 2) and moving by (0, -0.1f)
         *
         *   ####
         *   #/``
         *   `E
         */
        float hX = GameUtils.worldUnits(1);
        float hY = GameUtils.worldUnits(2);
        float dx = GameUtils.worldUnits(0);
        float dy = GameUtils.worldUnits(-0.1f);
        int slopeTileX = 1;
        int slopeTileY = 1;
        Hitbox hitbox = new Hitbox(hX, hY, 1, 1, null);

        // WHEN resolving collisions with this Slope
        CollisionResult result = new CollisionResult(hitbox, dx, dy);
        CollisionNode node = hitbox.getTopNodes()[0];
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
         * Slope is at (1, 1)
         * Hitbox is at (1, 2) and moving by (0, -0.75f)
         *
         *   ####
         *   #/``
         *   `E
         */
        float hX = GameUtils.worldUnits(1);
        float hY = GameUtils.worldUnits(0);
        float dx = GameUtils.worldUnits(0);
        float dy = GameUtils.worldUnits(-0.75f);
        int slopeTileX = 1;
        int slopeTileY = 1;
        Hitbox hitbox = new Hitbox(hX, hY, 1, 1, null);

        // WHEN resolving collisions with this Slope
        CollisionResult result = new CollisionResult(hitbox, dx, dy);
        CollisionNode node = hitbox.getTopNodes()[0];
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
         * Hitbox is at (3, 2) and moving by (-0.75f, -0.75f)
         *
         *   ####
         *   #/``
         *   ` E
         */
        float hX = GameUtils.worldUnits(3);
        float hY = GameUtils.worldUnits(2);
        float dx = GameUtils.worldUnits(-0.75f);
        float dy = GameUtils.worldUnits(-0.75f);
        int slopeTileX = 2;
        int slopeTileY = 1;
        Hitbox hitbox = new Hitbox(hX, hY, 1, 1, null);

        // WHEN resolving collisions with this Slope
        CollisionResult result = new CollisionResult(hitbox, dx, dy);
        CollisionNode node = hitbox.getTopNodes()[0];
        PostProcessCollision collision =
                new PostProcessCollision(slope, slopeTileX, slopeTileY, node);
        slope.postProcessing(result, collision);

        // THEN a collision is added 0.25f world units into the Slope
        //  => Initial slopeNodeX = 3.5f
        //  => Destination slopeNodeX = 2.75f
        //  => This is 0.75f world units into the Slope
        //  => For a left ceiling slope: 0.75 (x-axis) -> 0.25 (y-axis)
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
         * Slope is at (1, 1)
         * Hitbox is at (2, 1) and moving by (-0.75f, -0.25f)
         *
         *   ####
         *   #/E`
         *   `
         */
        float hX = GameUtils.worldUnits(2);
        float hY = GameUtils.worldUnits(1);
        float dx = GameUtils.worldUnits(-0.75f);
        float dy = GameUtils.worldUnits(-0.25f);
        int slopeTileX = 1;
        int slopeTileY = 1;
        Hitbox hitbox = new Hitbox(hX, hY, 1, 1, null);

        // WHEN resolving collision with this Slope
        CollisionResult result = new CollisionResult(hitbox, dx, dy);
        CollisionNode node = hitbox.getTopNodes()[0];
        PostProcessCollision collision =
                new PostProcessCollision(slope, slopeTileX, slopeTileY, node);
        slope.postProcessing(result, collision);

        // THEN a collision is added 1/4 of the way down the Slope
        // (moving 0.5 units would put the slope node at the top of the slope)
        assertEquals(1, result.getCollisionsY().size());
        assertEquals(
                Tile.getTop(GameUtils.worldUnits(1)) + 0.25f,
                result.getNearestCollisionY().collisionPos, 0.001);
    }

    @Test
    public void testPointInSlope() {
        // 4 corners of the Tile
        assertEquals(true, slope.isPointInsideSolidArea(0, 0));
        assertEquals(true, slope.isPointInsideSolidArea(Tile.WIDTH, 0));
        assertEquals(false, slope.isPointInsideSolidArea(Tile.WIDTH, Tile.HEIGHT));
        assertEquals(true, slope.isPointInsideSolidArea(0, Tile.HEIGHT));
    }

    @Test
    public void testSlopeY_At_X() {
        // When 1/4 of the way into the Tile, we should be 3/4 from the top
        assertEquals(3 * Tile.HEIGHT / 4,
                slope.calculateY(Tile.WIDTH / 4),
                0.1);
    }

}
