package com.danjb.engine.game.level;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.danjb.engine.application.Logger;
import com.danjb.engine.game.tiles.PhysicsTile;
import com.danjb.engine.game.tiles.Tile;

/**
 * Class representing a 2D grid of Tiles.
 *
 * @author Dan Bryce
 */
public class TileLayer {

    ////////////////////////////////////////////////////////////////////////////
    // TileLayerListener interface
    ////////////////////////////////////////////////////////////////////////////

    public static interface TileLayerListener {

        void tileDataChanged(TileLayer layer, int x, int y);

    }

    ////////////////////////////////////////////////////////////////////////////
    // TileLayer class
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Identifier for this TileLayer.
     */
    private int layerId;

    /**
     * Tiles that make up this TileLayer.
     */
    private int[][] tiles;

    /**
     * IDs of all Tiles used by this TileLayer.
     */
    private Set<Integer> usedTileIds = new HashSet<>();

    /**
     * Listeners to inform when this TileLayer changes.
     */
    private List<TileLayerListener> listeners = new ArrayList<>();

    /**
     * Constructs a TileLayer.
     *
     * @param layerId
     * @param tiles
     */
    public TileLayer(int layerId, int[][] tiles) {
        this.layerId = layerId;

        initialise(tiles);
    }

    /**
     * (Re-)initialises this TileLayer based on the given tiles.
     *
     * @param tiles
     */
    private void initialise(int[][] tiles) {
        this.tiles = tiles;

        // Keep track of all used tile IDs
        usedTileIds = new HashSet<>();
        for (int y = 0; y < tiles[0].length; y++) {
            for (int x = 0; x < tiles.length; x++) {
                usedTileIds.add(tiles[x][y]);
            }
        }
    }

    /**
     * Sets the Tile at the given level co-ordinates.
     *
     * <p>Note that when the last of a particular tile type is removed from a
     * TileLayer, it is not removed from the set of used tile IDs. The TileLayer
     * must be recreated in order for this set to be refreshed.
     *
     * @param tileX
     * @param tileY
     * @param tileId
     */
    public void setTile(int tileX, int tileY, int tileId) {
        try {
            tiles[tileX][tileY] = tileId;
            usedTileIds.add(tileId);

            // Inform listeners
            for (TileLayerListener listener : listeners) {
                listener.tileDataChanged(this, tileX, tileY);
            }

        } catch (ArrayIndexOutOfBoundsException ex) {
            // Somehow, we are trying to set a tile that's out of bounds
            Logger.get().log("Trying to set invalid tile: %d, %d", tileX, tileY);
        }
    }

    /**
     * Sets all the tiles in this TileLayer.
     *
     * @param newTiles
     */
    public void setTiles(int[][] newTiles) {
        initialise(newTiles);
    }

    /**
     * Gets the ID of the Tile at the given level co-ordinates.
     *
     * @param tileX
     * @param tileY
     * @return
     */
    public int getTile(int tileX, int tileY) {
        /*
         * We assume all Tiles outside the level are solid, except for those
         * beneath the level, which are air (this allows Entities to fall out
         * of the level).
         */
        int tileId;
        if (tileX < 0 || tileX >= getNumTilesX() || tileY < 0) {
            tileId = PhysicsTile.ID_SOLID_BLOCK;
        } else if (tileY >= getNumTilesY()) {
            tileId = PhysicsTile.ID_AIR;
        } else {
            tileId = tiles[tileX][tileY];
        }
        return tileId;
    }

    /**
     * Gets the ID of the Tile at the given world co-ordinates.
     *
     * @param x
     * @param y
     * @return
     */
    public int getTileAtWorldCoords(float x, float y) {
        int tileX = (int) (x / Tile.WIDTH);
        int tileY = (int) (y / Tile.HEIGHT);
        return getTile(tileX, tileY);
    }

    /**
     * Determines if the given level co-ordinate exists within this TileLayer.
     *
     * @param tileX
     * @param tileY
     * @return
     */
    public boolean contains(int tileX, int tileY) {
        return 0 <= tileX && tileX < tiles.length &&
                0 <= tileY && tileY < tiles[0].length;
    }

    /**
     * Adds a TileLayerListener.
     *
     * @param listener
     */
    public void addListener(TileLayerListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a TileLayerListener, if present.
     *
     * @param listener
     */
    public void removeListener(TileLayerListener listener) {
        listeners.remove(listener);
    }

    /**
     * Gets this TileLayer's identifier.
     *
     * @return
     */
    public int getLayerId() {
        return layerId;
    }

    /**
     * Gets the tiles within this TileLayer.
     *
     * @return
     */
    public int[][] getTiles() {
        return tiles;
    }

    /**
     * Gets the width of this TileLayer, in tiles.
     *
     * @return
     */
    public int getNumTilesX() {
        return tiles.length;
    }

    /**
     * Gets the height of this TileLayer, in tiles.
     *
     * @return
     */
    public int getNumTilesY() {
        return tiles[0].length;
    }

    /**
     * Gets the set of all Tile IDs used by this TileLayer.
     *
     * @return
     */
    public Set<Integer> getUsedTileIds() {
        return usedTileIds;
    }

}
