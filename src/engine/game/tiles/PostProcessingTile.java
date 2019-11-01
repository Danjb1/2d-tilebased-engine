package engine.game.tiles;

import engine.game.physics.CollisionResult;
import engine.game.physics.PostProcessCollision;

/**
 * A special tile that can inspect and modify a CollisionResult after all x- and
 * y-collisions have been added.
 *
 * @author Dan Bryce
 */
public interface PostProcessingTile {

    void postProcessing(
            CollisionResult result, PostProcessCollision thisCollision);

}
