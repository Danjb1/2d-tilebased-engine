package engine.game.camera;

public interface CameraController {

    /**
     * Method called every frame to determine how far to move the camera in
     * the x-axis.
     *
     * @param camera
     * @param delta
     * @return
     */
    float getCameraUpdateX(Camera camera, int delta);

    /**
     * Method called every frame to determine how far to move the camera in
     * the y-axis.
     *
     * @param camera
     * @param delta
     * @return
     */
    float getCameraUpdateY(Camera camera, int delta);

    /**
     * Method called to determine the centre (x-axis) of the camera after
     * the camera is teleported.
     *
     * @param camera
     * @return
     */
    float getCameraCentreX(Camera camera);

    /**
     * Method called to determine the centre (y-axis) of the camera after
     * the camera is teleported.
     *
     * @param camera
     * @return
     */
    float getCameraCentreY(Camera camera);

}
