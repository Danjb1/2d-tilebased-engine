package engine.game.tiles;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests that the various Slope tiles are correctly defined.
 *
 * @author Dan Bryce
 */
public class SlopeTest {

    /**
     * Left Slope.
     *
     * <pre>
     *  #
     *  ###
     *  #####
     * </pre>
     */
    public static class TestSlopeLeft {

        private Slope slope = new Slope.Left(0);

        @Test
        public void testPointInSlope() {
            // 4 corners of the Tile
            assertEquals(true, slope.isPointInSlope(0, 0));
            assertEquals(false, slope.isPointInSlope(Tile.WIDTH, 0));
            assertEquals(true, slope.isPointInSlope(Tile.WIDTH, Tile.HEIGHT));
            assertEquals(true, slope.isPointInSlope(0, Tile.HEIGHT));

            // Below the Tile
            assertEquals(true, slope.isPointInSlope(0, 2 * Tile.HEIGHT));
        }

        @Test
        public void testSlopeY_At_X() {
            // When 1/4 of the way into the Tile, we should be 3/4 up the slope
            assertEquals(3 * Tile.HEIGHT / 4,
                    slope.getSlopeY_At_X(Tile.WIDTH / 4),
                    0.1);
        }

    }

    /**
     * Right Slope.
     *
     * <pre>
     *      #
     *    ###
     *  #####
     * </pre>
     */
    public static class TestSlopeRight {

        private Slope slope = new Slope.Right(0);

        @Test
        public void testPointInSlope() {
            // 4 corners of the Tile
            assertEquals(false, slope.isPointInSlope(0, 0));
            assertEquals(true, slope.isPointInSlope(Tile.WIDTH, 0));
            assertEquals(true, slope.isPointInSlope(Tile.WIDTH, Tile.HEIGHT));
            assertEquals(true, slope.isPointInSlope(0, Tile.HEIGHT));

            // Below the Tile
            assertEquals(true, slope.isPointInSlope(0, 2 * Tile.HEIGHT));
        }

        @Test
        public void testSlopeY_At_X() {
            // When 1/4 of the way into the Tile, we should be 1/4 up the slope
            assertEquals(Tile.HEIGHT / 4,
                    slope.getSlopeY_At_X(Tile.WIDTH / 4),
                    0.1);
        }

    }

    /**
     * Left Ceiling Slope.
     *
     * <pre>
     *  #####
     *  ###
     *  #
     * </pre>
     */
    public static class TestSlopeLeftCeiling {

        private Slope slope =
                new Slope.LeftCeiling(0);

        @Test
        public void testPointInSlope() {
            // 4 corners of the Tile
            assertEquals(true, slope.isPointInSlope(0, 0));
            assertEquals(true, slope.isPointInSlope(Tile.WIDTH, 0));
            assertEquals(false, slope.isPointInSlope(Tile.WIDTH, Tile.HEIGHT));
            assertEquals(true, slope.isPointInSlope(0, Tile.HEIGHT));
        }

        @Test
        public void testSlopeY_At_X() {
            // When 1/4 of the way into the Tile, we should be 3/4 up the slope
            assertEquals(3 * Tile.HEIGHT / 4,
                    slope.getSlopeY_At_X(Tile.WIDTH / 4),
                    0.1);
        }

    }

    /**
     * Right Ceiling Slope.
     *
     * <pre>
     *  #####
     *    ###
     *      #
     * </pre>
     */
    public static class TestSlopeRightCeiling {

        private Slope slope = new Slope.RightCeiling(0);

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
            // When 1/4 of the way into the Tile, we should be 1/4 up the slope
            assertEquals(Tile.HEIGHT / 4,
                    slope.getSlopeY_At_X(Tile.WIDTH / 4),
                    0.1);
        }

    }

}
