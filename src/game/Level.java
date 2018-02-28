package game;

import game.tiles.Tile;

/**
 * A level which, when loaded into the logic, defines the game world.
 * 
 * @author Dan Bryce
 */
public class Level {

    /**
     * Width of the world, in world units.
     */
    private int worldWidth;

    /**
     * Height of the world, in world units.
     */
    private int worldHeight;

    /**
     * Foreground layer.
     */
    private TileLayer foreground;

    /**
     * Creates a Level.
     * 
     * @param foreground
     */
    public Level(TileLayer foreground) {
        this.foreground = foreground;

        recalculateWorldSize();
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
     * @param tileY
     * @return
     */
    public boolean doesTileExist_Y(int tileY) {
        return tileY >= 0 && tileY < getNumTilesY();
    }

    /**
     * Gets the level width, in tiles.
     * 
     * @return
     */
    public int getNumTilesX() {
        return foreground.getNumTilesX();
    }

    /**
     * Gets the level height, in tiles.
     * 
     * @return
     */
    public int getNumTilesY() {
        return foreground.getNumTilesY();
    }

    /**
     * Gets the level width, in world units.
     * 
     * @return
     */
    public int getWorldWidth() {
        return worldWidth;
    }

    /**
     * Gets the level height, in world units.
     * 
     * @return
     */
    public int getWorldHeight() {
        return worldHeight;
    }

    /**
     * Gets the foreground layer.
     * 
     * @return
     */
    public TileLayer getForeground() {
        return foreground;
    }

}
