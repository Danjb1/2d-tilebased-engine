package com.danjb.engine_demo.render;

import com.danjb.engine.game.tiles.Tile;

public class RenderUtils {

    /**
     * Width of a Tile's image, in pixels.
     */
    public static final int TILE_IMAGE_WIDTH = 16;

    /**
     * Ratio of world units to pixels.
     */
    private static final float UNITS_PER_PIXEL =
            Tile.WIDTH / TILE_IMAGE_WIDTH;

    /**
     * Convert from world units to pixels.
     *
     * <p>Note that a pixel in this context is not necessarily the same as a
     * pixel of the screen. The size of a pixel is actually determined by the
     * camera; a camera with a higher zoom level will "see" all pixels at a
     * larger scale.
     *
     * @param worldUnits
     * @return
     */
    public static float worldToPx(float worldUnits) {
        return worldUnits / UNITS_PER_PIXEL;
    }

    /**
     * Convert from pixels to world units.
     *
     * @param px
     * @return
     */
    public static float pxToWorld(int px) {
        return px * UNITS_PER_PIXEL;
    }

}
