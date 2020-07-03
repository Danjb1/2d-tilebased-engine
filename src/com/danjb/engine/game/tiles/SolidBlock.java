package com.danjb.engine.game.tiles;

import com.danjb.engine.game.physics.Collision;
import com.danjb.engine.game.physics.CollisionResult;
import com.danjb.engine.game.physics.Hitbox.CollisionNode;

/**
 * A solid block tile.
 *
 * @author Dan Bryce
 */
public class SolidBlock extends PhysicsTile {

    public SolidBlock(int id) {
        super(id);
    }

    @Override
    public boolean isSolid() {
        return true;
    }

    @Override
    public void checkForCollision_X(
            CollisionResult result, float dstX, CollisionNode node) {

        float xBefore = result.hitbox.x + node.x;

        // The Tile edge we collide with depends on the direction of travel
        float xAfter = node.isOnLeftEdge()
                ? Tile.getRight(dstX)
                : Tile.getLeft(dstX);

        result.addCollision_X(
                Collision.create(xBefore, xAfter, node, this));
    }

    @Override
    public void checkForCollision_Y(
            CollisionResult result, float dstY, CollisionNode node) {

        float yBefore = result.hitbox.y + node.y;

        // The Tile edge we collide with depends on the direction of travel
        float yAfter = node.isOnTopEdge()
                ? Tile.getBottom(dstY)
                : Tile.getTop(dstY);

        result.addCollision_Y(
                Collision.create(yBefore, yAfter, node, this));
    }

}
