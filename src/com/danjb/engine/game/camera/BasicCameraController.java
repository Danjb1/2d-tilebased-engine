package com.danjb.engine.game.camera;

import com.danjb.engine.game.entities.Entity;

/**
 * CameraSettings that remains permanently centred on the target Entity.
 *
 * @author Dan Bryce
 */
public class BasicCameraController implements CameraController {

    private Entity targetEntity;

    public BasicCameraController(Entity targetEntity) {
        this.targetEntity = targetEntity;
    }

    @Override
    public float getCameraUpdateX(Camera camera, int delta) {
        // Move such that the target Entity is centred
        return targetEntity.hitbox.centreX() - camera.getVisibleRegion().getCenterX();
    }

    @Override
    public float getCameraUpdateY(Camera camera, int delta) {
        // Move such that the target Entity is centred
        return targetEntity.hitbox.centreY() - camera.getVisibleRegion().getCenterY();
    }

    @Override
    public float getCameraCentreX(Camera camera) {
        // Centre on the target Entity
        return targetEntity.hitbox.centreX();
    }

    @Override
    public float getCameraCentreY(Camera camera) {
        // Centre on the target Entity
        return targetEntity.hitbox.centreY();
    }

}
