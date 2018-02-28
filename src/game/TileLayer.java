package game;

import java.util.HashSet;
import java.util.Set;

import game.tiles.ForegroundTile;
import game.tiles.Tile;
import launcher.Logger;

/**
 * Class representing a 2D grid of Tiles.
 * 
 * @author Dan Bryce
 */
public class TileLayer {

    /**
     * Tiles that make up this TileLayer.
     */
    private int[][] tiles;
    
    /**
     * IDs of all Tiles used by this TileLayer.
     */
    private Set<Integer> usedTileIds = new HashSet<>();

    /**
     * Constructs a TileLayer.
     * 
     * @param tiles
     */
    public TileLayer(int[][] tiles) {
        this.tiles = tiles;
        
        // Keep track of all used tile IDs
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
        } catch (ArrayIndexOutOfBoundsException ex){
            // Somehow, we are trying to set a tile that's out of bounds
            Logger.log("Trying to set invalid tile: %d, %d", tileX, tileY);
        }
    }
    
    /**
     * Gets the ID of the Tile at the given level co-ordinates.
     * 
     * @param tileX
     * @param tileY
     * @return
     */
    public int getTile(int tileX, int tileY){
        /*
         * We assume all Tiles outside the level are solid, except for those
         * beneath the level, which are air (this allows Entities to fall out
         * of the level).
         */
        int tileId;
        if (tileX < 0 || tileX >= getNumTilesX() || tileY < 0){
            tileId = ForegroundTile.ID_LEVEL_BORDER;
        } else if (tileY >= getNumTilesY()){
            tileId = ForegroundTile.ID_AIR;
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
