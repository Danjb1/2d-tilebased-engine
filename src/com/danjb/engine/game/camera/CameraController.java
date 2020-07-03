package com.danjb.engine.game.camera;

public interface CameraController {

    /**
     * Method called every frame to determine how far to move the camera in
     * the x-axis.
     *
     * @param delta
     * @return
     */
    float getCameraUpdateX(int delta);

    /**
     * Method called every frame to determine how far to move the camera in
     * the y-axis.
     *
     * @param delta
     * @return
     */
    float getCameraUpdateY(int delta);

    /**
     * Method called to determine the centre (x-axis) of the camera after
     * the camera is teleported.
     *
     * @return
     */
    float getCameraCentreX();

    /**
     * Method called to determine the centre (y-axis) of the camera after
     * the camera is teleported.
     *
     * @return
     */
    float getCameraCentreY();

}
