package engine.game;

import java.awt.geom.Rectangle2D;

import engine.game.entities.Entity;
import engine.game.physics.Hitbox;
import engine.game.tiles.Tile;

/**
 * Camera capable of tracking an Entity within the game world.
 *
 * @author Dan Bryce
 */
public class Camera {

    public enum TrackingMode {

        /**
         * TrackingMode used to keep the Camera just ahead of an Entity.
         */
        PREDICTIVE,

        /**
         * TrackingMode used to keep the Camera centred on an Entity.
         */
        CENTRED
    }

    /**
     * The number of tiles that should be visible at a time, in the x-axis.
     *
     * <p>This effectively determines the "zoom" level of the Camera. The height
     * of the target rectangle is calculated based on the viewport size;
     * see {@link #fillViewport}.
     */
    private static final int NUM_VISIBLE_COLUMNS = 25;

    /**
     * Width of the visible portion of the world, in pixels, assuming the
     * viewport fills the display.
     */
    public static final int VISIBLE_WORLD_WIDTH =
            GameUtils.TILE_IMAGE_WIDTH * NUM_VISIBLE_COLUMNS;

    /**
     * Optimal horizontal distance from the tracked Entity, in world units.
     *
     * <p>This determines how far "in front" of an Entity the Camera should aim
     * when the Entity is moving.
     *
     * <p>For example, if the tracked Entity is moving right, the Camera will
     * aim to have its centre at (Entity.centreX + OPTIMAL_DISTANCE_X).
     *
     * <p>A value of zero means the tracked Entity will stay centred.
     */
    private static final float OPTIMAL_DISTANCE_X = GameUtils.worldUnits(3);

    /**
     * Optimal vertical distance from the tracked Entity, in world units.
     *
     * <p>This determines how far *above* an Entity the Camera should aim.
     * Trying to aim "in front" of an Entity doesn't make sense if the Entity
     * is prone to frequently changing direction (e.g. jumping).
     *
     * <p>A value of zero means the tracked Entity will stay centred.
     */
    private static final float OPTIMAL_DISTANCE_Y = GameUtils.worldUnits(0);

    /**
     * Maximum acceptable distance the Camera can be from the tracked Entity in
     * the x-axis.
     */
    private static final float ACCEPTABLE_DISTANCE_X = GameUtils.worldUnits(4);

    /**
     * Maximum acceptable distance the Camera can be from the tracked Entity in
     * the y-axis.
     */
    private static final float ACCEPTABLE_DISTANCE_Y = GameUtils.worldUnits(4);

    /**
     * Minimum x-speed before the Camera is considered stationary.
     */
    private static final float MIN_SPEED_X = GameUtils.worldUnits(0.5f);

    /**
     * Minimum y-speed before the Camera is considered stationary.
     */
    private static final float MIN_SPEED_Y = GameUtils.worldUnits(0.5f);

    /**
     * Multiplier used to determine the Camera's x-speed.
     */
    private static final float SPEED_MULTIPLIER_X = 1;

    /**
     * Multiplier used to determine the Camera's y-speed.
     */
    private static final float SPEED_MULTIPLIER_Y = 1;

    /**
     * The current level.
     */
    private Level level;

    /**
     * Rectangle of the world that is visible to this camera, in world units.
     */
    private Rectangle2D.Float target = new Rectangle2D.Float();

    /**
     * Entity this Camera is tracking.
     */
    private Entity targetEntity;

    /**
     * TrackingMode used to determine the Camera's tracking behaviour.
     */
    private TrackingMode trackingMode;

    /**
     * Creates a Camera to fill the given area of the display.
     *
     * @param widthRatio
     * How much of the game world should be visible to this camera, as a
     * fraction of VISIBLE_WORLD_WIDTH.
     * 
     * <p>This should generally be set to 1, but can be set to a smaller value
     * if we want to change the shape of the viewport. For example, if we wanted
     * to make the viewport half as wide, we would set the widthRatio to 0.5 and
     * change the aspect ratio accordingly.
     * @param aspectRatio Aspect ratio of the camera target.
     * @param level
     */
    public Camera(double widthRatio, double aspectRatio, Level level) {
        this.level = level;

        resize(widthRatio, aspectRatio);
    }

    /**
     * Changes the size of the target based on the desired width and aspect
     * ratio.
     *
     * <p>This should be called whenever the viewport changes. The height of the
     * target rectangle will be adjusted such that the aspect ratio is
     * maintained.
     *
     * @param widthRatio
     * How much of the game world should be visible to this camera, as a
     * fraction of VISIBLE_WORLD_WIDTH.
     * @param aspectRatio Aspect ratio of the camera target.
     */
    public void resize(double widthRatio, double aspectRatio) {

        // Calculate the actual visible world width, in pixels
        int visibleWorldWidth = (int) (widthRatio * VISIBLE_WORLD_WIDTH);

        // Convert to world units
        target.width = GameUtils.pxToWorld(visibleWorldWidth);

        // Calculate the corresponding camera height
        target.height = (float) (target.width / aspectRatio);

        teleportToDestination();
    }

    /**
     * Centres the camera immediately on the target Entity.
     */
    public void teleportToDestination(){

        if (targetEntity == null){
            return;
        }

        // Centre immediately on the tracked Entity
        Hitbox hitbox = targetEntity.getHitbox();
        float x = (float) (hitbox.getCentreX() - target.getWidth() / 2);
        float y = (float) (hitbox.getCentreY() - target.getHeight() / 2);
        setPos(x, y);
    }

    /**
     * Instructs the Camera to track the given Entity.
     *
     * @param entity
     */
    public void trackEntity(Entity entity) {
        targetEntity = entity;
    }

    /**
     * Called every frame to move the Camera.
     *
     * @param delta
     */
    public void update(int delta) {

        if (targetEntity == null) {
            return;
        }

        /*
         * Camera move algorithm:
         *
         * This basically calculates the Camera's speed each frame based on the
         * distance to the destination, then applies a few rules to smooth out
         * the movement.
         */

        float speedX = getSpeedX();
        float speedY = getSpeedY();
        float dx = (speedX * delta) / 1000;
        float dy = (speedY * delta) / 1000;
        move(dx, dy);
    }

    /**
     * Determines how fast the camera should move in the x-axis this frame.
     *
     * @return
     */
    private float getSpeedX() {

        if (level.getWorldWidth() <= target.getWidth()){
            // The full width of the level is visible; no need to move
            return 0;
        }

        // Calculate how far the Entity is from the target centre
        Hitbox hitbox = targetEntity.getHitbox();
        float distToEntity = (float)
                (hitbox.getCentreX() - target.getCenterX());

        /*
         * As a minimum, match the target Entity's speed.
         * This prevents the Entity from escaping from view, and helps ensure
         * the Camera moves smoothly.
         */
        float speedX = hitbox.getSpeedX();

        if (trackingMode == TrackingMode.CENTRED){
            // Just centre on the target Entity
            return speedX + distToEntity;
        }

        // If Entity is too far away, move towards it
        if (Math.abs(distToEntity) > ACCEPTABLE_DISTANCE_X){
            // Move just enough to reach an acceptable distance
            speedX += distToEntity;

        // If Entity is moving, move towards the "optimal" position
        } else if (hitbox.getSpeedX() != 0){
            float optimalTargetX = hitbox.getCentreX()
                    + Math.copySign(OPTIMAL_DISTANCE_X, hitbox.getSpeedX());
            float distToOptimalTargetX = (float)
                    (optimalTargetX - target.getCenterX());
            speedX += distToOptimalTargetX;
        }

        /*
         * else:
         * Entity is not moving, so do not move.
         * We could keep moving towards the previous "optimal" destination, but
         * it is quite annoying for the camera to keep moving every time you
         * tap a different direction (e.g. when the player is controlling the
         * tracked entity).
         */

        // Consider Camera to be stationary if moving very slowly
        if (Math.abs(speedX) < MIN_SPEED_X){
            return 0;
        }

        return speedX * SPEED_MULTIPLIER_X;
    }

    /**
     * Determines how fast the camera should move in the y-axis this frame.
     *
     * @return
     */
    private float getSpeedY() {

        if (level.getWorldHeight() <= target.getHeight()){
            // The full height of the level is visible; no need to move
            return 0;
        }

        // Calculate how far the Entity is from the target centre
        Hitbox hitbox = targetEntity.getHitbox();
        float distToEntity = (float)
                (hitbox.getCentreY() - target.getCenterY());

        /*
         * As a minimum, match the target Entity's speed.
         * This prevents the Entity from escaping from view, and helps ensure
         * the Camera moves smoothly.
         */
        float speedY = hitbox.getSpeedY();

        if (trackingMode == TrackingMode.CENTRED){
            // Just centre on the target Entity
            return speedY + distToEntity;
        }

        // If Entity is too far away, move towards it
        if (Math.abs(distToEntity) > ACCEPTABLE_DISTANCE_Y){
            // Move just enough to reach an acceptable distance
            speedY += distToEntity;

        // Otherwise, move towards the "optimal" position
        } else {
            float optimalTargetY = hitbox.getCentreY() - OPTIMAL_DISTANCE_Y;
            float distToOptimalTargetY = (float)
                    (optimalTargetY - target.getCenterY());
            speedY += distToOptimalTargetY;
        }

        // Consider Camera to be stationary if moving very slowly
        if (Math.abs(speedY) < MIN_SPEED_Y){
            return 0;
        }

        return speedY * SPEED_MULTIPLIER_Y;
    }

    /**
     * Moves this Camera by the given amount.
     *
     * @param dx
     * @param dy
     */
    private void move(float dx, float dy) {
        setPos(target.x + dx, target.y + dy);
    }

    /**
     * Centres this Camera at the given world co-ordinate.
     *
     * @param x
     * @param y
     */
    public void setCentre(float x, float y) {
        /*
         * We cast the target dimensions to integers, otherwise jitter occurs
         * due to the way the final result is rounded during rendering.
         */
        setPos(x - (int)(target.width / 2),
                y - (int)(target.height / 2));
    }

    /**
     * Moves this Camera to the given position.
     *
     * @param x Left edge of the desired target, in world units.
     * @param y Top edge of the desired target, in world units.
     */
    public void setPos(float x, float y){
        float newX = keepWithinBoundsX(x);
        float newY = keepWithinBoundsY(y);
        target.setRect(newX, newY, target.width, target.height);
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
        float maxCameraX = maxVisibleX - target.width;

        if (level.getWorldWidth() <= target.getWidth()){
            // The full width of the level is visible; keep camera at left edge
            cameraX = minVisibleX;
        } else if (cameraX < minVisibleX){
            cameraX = minVisibleX;
        } else if (cameraX > maxCameraX){
            cameraX = maxCameraX;
        }

        return cameraX;
    }

    /**
     * Keeps the given camera co-ordinate within the visible portion of the
     * level.
     *
     * @param cameraX
     * @return
     */
    private float keepWithinBoundsY(float cameraY) {

        float minVisibleY = 0;
        float maxVisibleY = minVisibleY + level.getNumTilesY() * Tile.HEIGHT;
        float maxCameraY = maxVisibleY - target.height;

        if (level.getWorldHeight() <= target.getHeight()){
            // The full height of the level is visible; keep camera at top edge
            cameraY = minVisibleY;
        } else if (cameraY < minVisibleY){
            cameraY = minVisibleY;
        } else if (cameraY > maxCameraY){
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
        return target.contains(hitbox.getLeft(), hitbox.getTop()) ||
                target.contains(hitbox.getLeft(), hitbox.getBottom()) ||
                target.contains(hitbox.getRight(), hitbox.getTop()) ||
                target.contains(hitbox.getRight(), hitbox.getBottom());
    }

    /**
     * Gets the index of the first visible tile in the x-axis.
     *
     * @return
     */
    public int getFirstVisibleTileX() {
        return Math.max((int) (target.x / Tile.WIDTH), 0);
    }

    /**
     * Gets the index of the first visible tile in the y-axis.
     *
     * @return
     */
    public int getFirstVisibleTileY() {
        return Math.max((int) (target.y / Tile.HEIGHT), 0);
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
        return (int) (target.width / Tile.WIDTH) + 2;
    }

    /**
     * Gets the maximum number of tiles that may be visible in the y-axis.
     *
     * @return
     */
    private int getNumVisibleTilesY() {
        // See comment in getNumVisibleTilesX().
        return (int) (target.height / Tile.HEIGHT) + 2;
    }

    public Rectangle2D.Float getTarget() {
        return target;
    }

    public Entity getTargetEntity() {
        return targetEntity;
    }

    public void setTrackingMode(TrackingMode trackingMode){
        this.trackingMode = trackingMode;
    }

}
