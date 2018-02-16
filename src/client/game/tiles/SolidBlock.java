package client.game.tiles;

import client.game.Logic;
import client.game.physics.Collision;
import client.game.physics.CollisionResult;

/**
 * A solid block tile.
 *
 * @author Dan Bryce
 */
public class SolidBlock extends ForegroundTile {

    public SolidBlock(int id) {
        super(id);
    }

    @Override
    public boolean hasCollisionX(CollisionResult result, Logic logic, int tileX,
            int tileY) {
        return true;
    }

    @Override
    public boolean hasCollisionY(CollisionResult result, Logic logic, int tileX,
            int tileY) {
        return true;
    }

    @Override
    public void collisionOccurredX(CollisionResult collision){

        if (collision.getAttemptedDx() > 0){
            // Moving right; collision is between the right of the hitbox and
            // the left of the tile.
            float hitboxX = collision.getRight();
            float collisionX = Tile.getLeft(hitboxX);
            collision.addCollision_X(new Collision(hitboxX, collisionX, this));

        } else if (collision.getAttemptedDx() < 0){
            // Moving left; collision is between the left of the hitbox and the
            // right of the tile.
            float hitboxX = collision.getLeft();
            float collisionX = Tile.getRight(hitboxX);
            collision.addCollision_X(new Collision(hitboxX, collisionX, this));
        }
    }

    @Override
    public void collisionOccurredY(CollisionResult collision){

        if (collision.getAttemptedDy() > 0){
            // Moving down; collision is between the bottom of the hitbox and
            // the top of the tile.
            float hitboxY = collision.getBottom();
            float collisionY = Tile.getTop(hitboxY);
            collision.addCollision_Y(new Collision(hitboxY, collisionY, this));

        } else if (collision.getAttemptedDy() < 0){
            // Moving up; collision is between the top of the hitbox and the
            // bottom of the tile.
            float hitboxY = collision.getTop();
            float collisionY = Tile.getBottom(hitboxY);
            collision.addCollision_Y(new Collision(hitboxY, collisionY, this));
        }
    }

}
