package com.danjb.engine.game.level;

import java.util.HashMap;
import java.util.Map;

import com.danjb.engine.game.tiles.Tile;

/**
 * Class used to control access to tile types.
 *
 * @author Dan Bryce
 */
public class TileProvider {

    /**
     * All available tiles, by layer, and then by tile ID.
     */
    protected Map<Integer, Map<Integer, Tile>> tiles = new HashMap<>();

    /**
     * Gets the available tile types for the given layer.
     *
     * @param layer
     * @return
     */
    private Map<Integer, Tile> getTilesForLayer(int layer) {
        Map<Integer, Tile> tilesForLayer = tiles.get(layer);
        if (tilesForLayer == null) {
            tilesForLayer = new HashMap<Integer, Tile>();
            tiles.put(layer, tilesForLayer);
        }
        return tilesForLayer;
    }

    /**
     * Adds the given Tile to the list of available tile types.
     *
     * @param layer
     * @param tile
     */
    public void addTileType(int layer, Tile tile) {
        getTilesForLayer(layer).put(tile.getId(), tile);
    }

    /**
     * Gets the Tile with the given ID.
     *
     * @param layer
     * @param tileId
     * @return
     */
    public Tile getTile(int layer, int tileId) {
        return getTilesForLayer(layer).get(tileId);
    }

    /**
     * Gets the list of Tiles available to the given layer.
     *
     * @param layer
     * @return
     */
    public Map<Integer, Tile> getTiles(int layer) {
        return getTilesForLayer(layer);
    }

}
