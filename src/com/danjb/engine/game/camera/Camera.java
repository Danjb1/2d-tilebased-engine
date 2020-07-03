package com.danjb.engine.game.camera;

import com.danjb.engine.game.level.Level;
import com.danjb.engine.game.physics.Hitbox;
import com.danjb.engine.game.tiles.Tile;
import com.danjb.engine.util.Rectangle;

/**
 * Camera capable of tracking an Entity within the game world.
 *
 * @author Dan Bryce
 */
public class Camera {

    /**
     * Distance of the Camera from the game.
     */
    public static final float Z_DISTANCE = 1;

    /**
     * The current level.
     */
    private Level level;

    /**
     * Rectangle of the world that is visible to this camera, in world units.
     */
    private Rectangle visibleRegion = new Rectangle();

    /**
     * Controller responsible for moving the Camera.
     */
    private CameraController controller;

    /**
     * Creates a Camera to fill the given area of the display.
     *
     * @param visibleWorldWidth
     * How much of the game world should be visible to this camera, in world
     * units.
     *
     * @param widthRatio
     * How much of the game world should be visible to this camera, as a
     * fraction of the visible world width.
     *
     * <p>This should generally be set to 1, but can be set to a smaller value
     * if we want to change the shape of the viewport. For example, if we wanted
     * to make the viewport half as wide, we would set the widthRatio to 0.5 and
     * change the aspect ratio accordingly.
     *
     * @param aspectRatio Aspect ratio of the camera target.
     *
     * @param level The Level the Camera is looking at.
     */
    public Camera(
            float visibleWorldWidth,
            float widthRatio,
            float aspectRatio,
            Level level) {

        this.level = level;

        resize(visibleWorldWidth, widthRatio, aspectRatio);
    }

    /**
     * Changes the size of the target based on the desired width and aspect
     * ratio.
     *
     * <p>This should be called whenever the viewport changes. The height of the
     * target rectangle will be adjusted such that the aspect ratio is
     * maintained.
     *
     * @param visibleWorldWidth
     * How much of the game world should be visible to this camera, in world
     * units.
     *
     * @param widthRatio
     * How much of the game world should be visible to this camera, as a
     * fraction of visibleWorldWidth.
     *
     * @param aspectRatio
     * Aspect ratio of the camera target.
     */
    public void resize(float visibleWorldWidth, float widthRatio, float aspectRatio) {

        // Calculate the new visible world width
        visibleRegion.width = (int) (widthRatio * visibleWorldWidth);

        // Calculate the corresponding camera height
        visibleRegion.height = visibleRegion.width / aspectRatio;

        teleportToDestination();
    }

    /**
     * Teleports the camera immediately to the target Entity.
     */
    public void teleportToDestination() {

        if (controller == null) {
            return;
        }

        float x = controller.getCameraCentreX();
        float y = controller.getCameraCentreY();

        setCentre(x, y);
    }

    /**
     * Called every frame to move the Camera.
     *
     * @param delta
     */
    public void update(int delta) {

        if (controller == null) {
            return;
        }

        float dx;
        float dy;

        if (level.getWorldWidth() <= visibleRegion.width) {
            // No need to move if the whole level is visible
            dx = 0;
        } else {
            dx = controller.getCameraUpdateX(delta);
        }

        if (level.getWorldHeight() <= visibleRegion.height) {
            // No need to move if the whole level is visible
            dy = 0;
        } else {
            dy = controller.getCameraUpdateY(delta);
        }

        move(dx, dy);
    }

    /**
     * Moves this Camera by the given amount.
     *
     * @param dx
     * @param dy
     */
    private void move(float dx, float dy) {
        setPos(visibleRegion.x + dx, visibleRegion.y + dy);
    }

    /**
     * Centres this Camera at the given world co-ordinate.
     *
     * @param x
     * @param y
     */
    public void setCentre(float x, float y) {
        setPos(x - (visibleRegion.width / 2),
                y - (visibleRegion.height / 2));
    }

    /**
     * Moves this Camera to the given position.
     *
     * @param x Left edge of the desired target, in world units.
     * @param y Top edge of the desired target, in world units.
     */
    public void setPos(float x, float y) {
        float newX = keepWithinBoundsX(x);
        float newY = keepWithinBoundsY(y);
        visibleRegion.setRect(newX, newY, visibleRegion.width, visibleRegion.height);
    }

    /**
     * Keeps the given camera co-ordinate within the visible portion of the
     * level.
     *
     * @param cameraX
     * @return
     */
    private float keepWithinBoundsX(float cameraX) {

        float minVisibleX = 0;
        float maxVisibleX = minVisibleX + level.getNumTilesX() * Tile.WIDTH;
        float maxCameraX = maxVisibleX - visibleRegion.width;

        if (level.getWorldWidth() <= visibleRegion.width) {
            // The full width of the level is visible; keep camera at left edge
            cameraX = minVisibleX;
        } else if (cameraX < minVisibleX) {
            cameraX = minVisibleX;
        } else if (cameraX > maxCameraX) {
            cameraX = maxCameraX;
        }

        return cameraX;
    }

    /**
     * Keeps the given camera co-ordinate within the visible portion of the
     * level.
     *
     * @param cameraY
     * @return
     */
    private float keepWithinBoundsY(float cameraY) {

        float minVisibleY = 0;
        float maxVisibleY = minVisibleY + level.getNumTilesY() * Tile.HEIGHT;
        float maxCameraY = maxVisibleY - visibleRegion.height;

        if (level.getWorldHeight() <= visibleRegion.height) {
            // The full height of the level is visible; keep camera at top edge
            cameraY = minVisibleY;
        } else if (cameraY < minVisibleY) {
            cameraY = minVisibleY;
        } else if (cameraY > maxCameraY) {
            cameraY = maxCameraY;
        }

        return cameraY;
    }

    /**
     * Determines if any part of the given Hitbox is visible to this camera.
     *
     * @param hitbox
     * @return
     */
    public boolean isOnscreen(Hitbox hitbox) {
        return visibleRegion.contains(hitbox.x, hitbox.y) ||
                visibleRegion.contains(hitbox.x, hitbox.bottom()) ||
                visibleRegion.contains(hitbox.right(), hitbox.y) ||
                visibleRegion.contains(hitbox.right(), hitbox.bottom());
    }

    /**
     * Gets the index of the first visible tile in the x-axis.
     *
     * @return
     */
    public int getFirstVisibleTileX() {
        return Math.max((int) (visibleRegion.x / Tile.WIDTH), 0);
    }

    /**
     * Gets the index of the first visible tile in the y-axis.
     *
     * @return
     */
    public int getFirstVisibleTileY() {
        return Math.max((int) (visibleRegion.y / Tile.HEIGHT), 0);
    }

    /**
     * Gets the index of the last visible tile in the x-axis.
     *
     * @param minTileX
     * @return
     */
    public int getLastVisibleTileX(int minTileX) {
        return Math.min(minTileX + getNumVisibleTilesX(),
                level.getNumTilesX() - 1);
    }

    /**
     * Gets the index of the last visible tile in the y-axis.
     *
     * @param minTileY
     * @return
     */
    public int getLastVisibleTileY(int minTileY) {
        return Math.min(minTileY + getNumVisibleTilesY(),
                level.getNumTilesY() - 1);
    }

    /**
     * Gets the maximum number of tiles that may be visible in the x-axis.
     *
     * @return
     */
    private int getNumVisibleTilesX() {
        /*
         * To find the number of tiles needed to cover the camera, we divide
         * the camera width by the width of a tile.
         *
         * We then cast to an int and add 1 to cover the remainder (equivalent
         * to rounding up).
         *
         * We often start drawing offscreen, which effectively "pulls" all the
         * tiles to one side. This can leave a gap at the opposite side, so we
         * add another 1 to cover this.
         */
        return (int) (visibleRegion.width / Tile.WIDTH) + 2;
    }

    /**
     * Gets the maximum number of tiles that may be visible in the y-axis.
     *
     * @return
     */
    private int getNumVisibleTilesY() {
        // See comment in getNumVisibleTilesX()
        return (int) (visibleRegion.height / Tile.HEIGHT) + 2;
    }

    public Rectangle getVisibleRegion() {
        return visibleRegion;
    }

    public void setController(CameraController controller) {
        this.controller = controller;
    }

    public CameraController getController() {
        return controller;
    }

}
