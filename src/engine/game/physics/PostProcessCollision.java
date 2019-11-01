package engine.game.physics;

import engine.game.physics.Hitbox.CollisionNode;
import engine.game.tiles.PostProcessingTile;
import engine.game.tiles.Tile;

/**
 * Represents a collision with a PostProcessingTile.
 *
 * @author Dan Bryce
 */
public class PostProcessCollision {

    public final PostProcessingTile tile;

    public final int tileX;

    public final int tileY;

    public final CollisionNode node;

    /**
     * Constructs a PostProcessCollision.
     *
     * @param tile Tile involved in this collision.
     * @param tileX x-index of the tile within the level.
     * @param tileY y-index of the tile within the level.
     * @param node CollisionNode involved in this collision.
     */
    public PostProcessCollision(
            PostProcessingTile tile,
            int tileX,
            int tileY,
            CollisionNode node) {

        this.tile = tile;
        this.tileX = tileX;
        this.tileY = tileY;
        this.node = node;
    }

    /**
     * Gets the absolute position of the left edge of the tile.
     *
     * @return
     */
    public float getTileLeft() {
        return Tile.getLeft(tileX * Tile.WIDTH);
    }

    /**
     * Gets the absolute position of the top edge of the tile.
     *
     * @return
     */
    public float getTileTop() {
        return Tile.getTop(tileY * Tile.HEIGHT);
    }

    /**
     * Gets the absolute position of the right edge of the tile.
     *
     * @return
     */
    public float getTileRight() {
        return Tile.getRight(tileX * Tile.WIDTH);
    }

    /**
     * Gets the absolute position of the bottom edge of the tile.
     *
     * @return
     */
    public float getTileBottom() {
        return Tile.getBottom(tileY * Tile.HEIGHT);
    }

}
