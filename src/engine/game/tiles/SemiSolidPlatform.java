package engine.game.tiles;

import engine.game.Logic;
import engine.game.physics.Collision;
import engine.game.physics.CollisionResult;

/**
 * A tile that is only solid from above.
 *
 * @author Dan Bryce
 */
public class SemiSolidPlatform extends ForegroundTile {

    public SemiSolidPlatform(int id) {
        super(id);
    }

    @Override
    public boolean hasCollisionY(CollisionResult result, Logic logic, int tileX,
            int tileY) {

        if (result.getHitbox().bottom() >= tileY * Tile.HEIGHT){
            // We were already inside the SemiSolidPlatform before this collision check
            return false;
        }

        return true;
    }

    /**
     * Process vertical collisions with a SemiSolidPlatform.
     *
     * <p>This allows Entities to jump up through a SemiSolidPlatform, but
     * prevents them from falling through it.
     */
    @Override
    public void collisionOccurredY(CollisionResult collision){

        if (collision.getAttemptedDy() > 0){
            // Moving down; collision is between the bottom of the hitbox and
            // the top of the tile.
            float hitboxY = collision.getBottom();
            float collisionY = Tile.getTop(hitboxY);
            collision.addCollision_Y(new Collision(hitboxY, collisionY, this));

        } else if (collision.getAttemptedDy() < 0){
            // Moving up; no collision!
            return;
        }
    }

}
