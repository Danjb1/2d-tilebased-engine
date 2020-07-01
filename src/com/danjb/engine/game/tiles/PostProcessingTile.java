package com.danjb.engine.game.tiles;

import com.danjb.engine.game.physics.CollisionResult;
import com.danjb.engine.game.physics.PostProcessCollision;

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
