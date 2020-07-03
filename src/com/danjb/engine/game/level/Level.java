package com.danjb.engine.game.level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.danjb.engine.game.ComponentStore;
import com.danjb.engine.game.tiles.Tile;

/**
 * A level which, when loaded into the logic, defines the game world.
 *
 * @author Dan Bryce
 */
public class Level {

    /**
     * {@link LevelComponent}s attached to this Level.
     */
    public ComponentStore<LevelComponent> components;

    /**
     * Width of the world, in world units.
     */
    private float worldWidth;

    /**
     * Height of the world, in world units.
     */
    private float worldHeight;

    /**
     * Map of TileLayers by ID.
     */
    private Map<Integer, TileLayer> layers = new HashMap<>();

    /**
     * The default TileLayer.
     */
    private TileLayer defaultLayer;

    /**
     * Creates a Level.
     *
     * @param defaultLayer The TileLayer with which Entities can collide.
     */
    public Level(TileLayer defaultLayer) {
        this.defaultLayer = defaultLayer;

        recalculateWorldSize();
    }

    /**
     * Cleans up this Level when it is no longer needed.
     */
    public void destroy() {
        components.destroy();
    }

    /**
     * Recalculates the size of the world based on the number of tiles.
     */
    private void recalculateWorldSize() {
        worldWidth = getNumTilesX() * Tile.WIDTH;
        worldHeight = getNumTilesY() * Tile.HEIGHT;
    }

    /**
     * Determines if a tile co-ordinate is valid.
     *
     * @param tileX
     * @return
     */
    public boolean doesTileExist_X(int tileX) {
        return tileX >= 0 && tileX < getNumTilesX();
    }

    /**
     * Determines if a tile co-ordinate is valid.
     *
     * @param tileY
     * @return
     */
    public boolean doesTileExist_Y(int tileY) {
        return tileY >= 0 && tileY < getNumTilesY();
    }

    /**
     * Determines if a tile co-ordinate is valid.
     *
     * @param tileX
     * @param tileY
     * @return
     */
    public boolean doesTileExist(int tileX, int tileY) {
        return doesTileExist_X(tileX) && doesTileExist_Y(tileY);
    }

    /**
     * Gets the level width, in tiles.
     *
     * @return
     */
    public int getNumTilesX() {
        return defaultLayer.getNumTilesX();
    }

    /**
     * Gets the level height, in tiles.
     *
     * @return
     */
    public int getNumTilesY() {
        return defaultLayer.getNumTilesY();
    }

    /**
     * Gets the level width, in world units.
     *
     * @return
     */
    public float getWorldWidth() {
        return worldWidth;
    }

    /**
     * Gets the level height, in world units.
     *
     * @return
     */
    public float getWorldHeight() {
        return worldHeight;
    }

    /**
     * Gets the default TileLayer.
     *
     * @return
     */
    public TileLayer getDefaultLayer() {
        return defaultLayer;
    }

    /**
     * Gets the TileLayer with the given ID.
     *
     * @param layerId
     * @return
     */
    public TileLayer getLayer(int layerId) {
        return layers.get(layerId);
    }

    /**
     * Returns all TileLayers as a list.
     *
     * <p>Modifications to this list will have no effect.
     *
     * @return
     */
    public List<TileLayer> getLayers() {
        return new ArrayList<>(layers.values());
    }

    /**
     * Adds or replaces a TileLayer.
     *
     * @param layer
     */
    public void addLayer(TileLayer layer) {
        layers.put(layer.getLayerId(), layer);
    }

}
