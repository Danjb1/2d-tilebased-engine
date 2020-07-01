package com.danjb.engine.game.physics;

import com.danjb.engine.game.physics.Hitbox.CollisionNode;
import com.danjb.engine.game.tiles.PostProcessingTile;
import com.danjb.engine.game.tiles.Tile;

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

    // Auto-generated
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((node == null) ? 0 : node.hashCode());
        result = prime * result + tileX;
        result = prime * result + tileY;
        return result;
    }

    // Auto-generated
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PostProcessCollision other = (PostProcessCollision) obj;
        if (node == null) {
            if (other.node != null)
                return false;
        } else if (!node.equals(other.node))
            return false;
        if (tileX != other.tileX)
            return false;
        if (tileY != other.tileY)
            return false;
        return true;
    }

}
