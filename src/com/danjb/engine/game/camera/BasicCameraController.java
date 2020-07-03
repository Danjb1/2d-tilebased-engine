package com.danjb.engine.game.camera;

/**
 * CameraController that remains permanently centred on a target.
 *
 * @author Dan Bryce
 */
public class BasicCameraController implements CameraController {

    protected Camera camera;
    protected float targetX;
    protected float targetY;

    public BasicCameraController(Camera camera, float targetX, float targetY) {
        this.camera = camera;
        this.targetX = targetX;
        this.targetY = targetY;
    }

    @Override
    public float getCameraUpdateX(int delta) {
        // Move such that the target is centred
        return targetX - camera.getVisibleRegion().getCenterX();
    }

    @Override
    public float getCameraUpdateY(int delta) {
        // Move such that the target is centred
        return targetY - camera.getVisibleRegion().getCenterY();
    }

    @Override
    public float getCameraCentreX() {
        // Centre on the target
        return targetX;
    }

    @Override
    public float getCameraCentreY() {
        // Centre on the target
        return targetY;
    }

    public void setTarget(float targetX, float targetY) {
        this.targetX = targetX;
        this.targetY = targetY;
    }

}
