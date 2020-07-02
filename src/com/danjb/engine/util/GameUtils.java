package com.danjb.engine.util;

import com.danjb.engine.game.tiles.Tile;

/**
 * Utility functions relating to the game itself.
 *
 * @author Dan Bryce
 */
public class GameUtils {

    /**
     * Produces a length in world units.
     *
     * @param val Length relative to one Tile.
     * @return
     */
    public static float worldUnits(float val) {
        /*
         * In reality, this does nothing, since a Tile is defined to be 1 unit.
         * However, using this method gives us flexibility if the definition of
         * world units ever changes. It also helps to clarify the caller's
         * intent.
         */
        return val * Tile.SIZE;
    }

}
