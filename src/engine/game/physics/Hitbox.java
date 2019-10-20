package engine.game.physics;

import java.util.HashMap;
import java.util.Map;

import engine.game.GameUtils;
import engine.game.Logic;
import engine.game.tiles.Slope;
import engine.game.tiles.Tile;
import engine.launcher.Launcher;

/**
 * Class representing an axis-aligned bounding box (AABB) within the game world.
 *
 * <p>This includes its position, size, velocity, and various properties
 * relating to collision handling.
 *
 * @author Dan Bryce
 */
public class Hitbox {

    /**
     * Collision flag that allows a Hitbox to travel up and down Slopes.
     *
     * <p>If set to false, a Hitbox will collide with slopes instead.
     */
    public static final int SUPPORT_SLOPES = 0;

    /**
     * Listener to inform whenever significant events occur.
     */
    private HitboxListener listener;

    /**
     * Position of this Hitbox, in world units.
     *
     * <p>The position here refers to the top-left of the Hitbox.
     */
    public float x, y;

    /**
     * Size of this Hitbox, in world units.
     */
    public float width, height;

    /**
     * Current movement speed, in world units per second.
     */
    private float speedX, speedY;

    /**
     * Maximum speed, in world units per second.
     *
     * <p>Speed will never exceed this value.
     *
     * <p>In general, this will not need to be overridden unless this Hitbox
     * has a means of accelerating; in many cases a Hitbox will never exceed its
     * initial speed.
     *
     * <p>By default, this is equal to 1 Tile per frame; our collision algorithm
     * can't cope with any more than this!
     *
     * <p>max_units_per_frame * frames_per_second = max_units_per_second
     */
    protected float maxSpeedX = Launcher.FPS *
            (GameUtils.worldUnits(1) - Physics.SMALLEST_DISTANCE);

    /**
     * See {@link Hitbox#maxSpeedX}.
     */
    protected float maxSpeedY = Launcher.FPS *
            (GameUtils.worldUnits(1) - Physics.SMALLEST_DISTANCE);

    /**
     * Distances along this Hitbox's horizontal edges at which to check for
     * collision.
     */
    private float[] horizontalCollisionNodes;

    /**
     * Distances along this Hitbox's vertical edges at which to check for
     * collision.
     */
    private float[] verticalCollisionNodes;

    /**
     * Flags that can be used to control the outcome of collisions.
     */
    private Map<Integer, Boolean> collisionFlags = new HashMap<>();

    /**
     * Flag set whenever this Hitbox is touching the ground.
     */
    private boolean onGround;

    /**
     * Milliseconds since this Hitbox was last touching the ground.
     */
    private int msSinceGrounded;

    /**
     * Multiplier that determines how strongly this Hitbox is affected by
     * gravity.
     */
    private float gravityCoefficient = 1;

    /**
     * Multiplier that determines how colliding with a surface affects this
     * Hitbox's speed.
     */
    private float bounceCoefficient = 0;

    /**
     * Multiplier that determines how strongly this Hitbox is affected by ground
     * friction.
     */
    private float groundFrictionCoefficient = 1;

    /**
     * Multiplier that determines how strongly this Hitbox is affected by air
     * friction.
     */
    private float airFrictionCoefficient = 1;

    /**
     * Whether this Hitbox is affected by collisions.
     */
    private boolean solid = true;

    /**
     * The last CollisionResult computed by this Hitbox.
     */
    private CollisionResult lastCollisionResult;

    /**
     * Creates a new Hitbox with a listener.
     *
     * @param x
     * @param y
     * @param width
     * @param height
     * @param listener
     */
    public Hitbox(float x, float y, float width, float height,
            HitboxListener listener) {

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.listener = listener;

        horizontalCollisionNodes = createCollisionNodes(width);
        verticalCollisionNodes = createCollisionNodes(height);

        lastCollisionResult = new CollisionResult(this, 0, 0);
    }

    /**
     * Cleans up this Hitbox when it is no longer needed.
     */
    public void destroy() {
        listener = null;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Collision
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Determines the points along the given edge at which to check for
     * collisions.
     *
     * <p>As a minimum, every Hitbox will have 2 collision nodes on each side
     * (at the corners). However, larger Entities also need collision nodes in
     * between the corners, otherwise tiles that are "jutting out" of a wall
     * might go straight through them!
     *
     * @param edgeLength Length of the collision edge, in world units.
     */
    private float[] createCollisionNodes(float edgeLength) {

        /*
         * The number of nodes is equal to:
         * (edge length / Tile.SIZE) rounded up, plus 1
         *
         * Thus, a Hitbox whose width is equal to the width of a tile will have
         * 2 nodes, one at each corner.
         */
        int numNodes = (int) Math.ceil(edgeLength / Tile.SIZE) + 1;
        float[] nodes = new float[numNodes];

        // Define all nodes except the last one, Tile.SIZE apart.
        for (int i = 0; i < numNodes - 1; i++){
            nodes[i] = Tile.SIZE * i;
        }

        // The last node should be at the Hitbox's right / bottom edge
        nodes[numNodes - 1] = edgeLength - Physics.SMALLEST_DISTANCE;

        return nodes;
    }

    /**
     * Moves this Hitbox according to its current speed, and handles any
     * collisions with the Level along the way.
     *
     * <p>When colliding with a surface, this Hitbox will bounce off it (change
     * direction), and its speed will be multiplied by the bounce coefficient.
     * A bounce coefficient of zero (the default) will cause the Hitbox to stop.
     *
     * @see Hitbox#setBounceCoefficient
     * @param logic
     * @param delta
     */
    public void moveWithCollision(Logic logic, int delta) {

        if (!onGround) {
            msSinceGrounded += delta;
        }

        // Determine movement distance, given the current delta value
        float dx = (speedX * delta) / 1000;
        float dy = (speedY * delta) / 1000;

        // Move to the nearest collision
        CollisionResult result =
                Physics.getCollisionResult(logic, this, dx, dy);
        setPos(result.left(), result.top());

        // Collide with slopes if this Hitbox doesn't support them
        if (!getCollisionFlag(SUPPORT_SLOPES)
                && result.isCollisionWithSlope()) {
            Slope slope = (Slope) result.getNearestCollisionY().getTile();
            slope.collide(this, bounceCoefficient);
        }

        // Adjust speed according to x-collisions
        if (result.hasCollisionOccurredX()){
            setSpeedX(-speedX * bounceCoefficient);
        }

        // Adjust speed according to y-collisions
        if (result.hasCollisionOccurredY()){
            if (!result.shouldMaintainSpeedY()){
                setSpeedY(-speedY * bounceCoefficient);
            }
            if (result.getAttemptedDy() > 0 &&
                    Math.abs(speedY) < Physics.MOVING_SPEED){
                // Hitbox has hit the ground
                setSpeedY(0);
                if (!onGround){
                    setGrounded(true);
                }
            }
        } else {
            if (onGround){
                // Hitbox has left the ground
                setGrounded(false);
            }
        }

        // Check if this Hitbox is now out-of-bounds
        if (y > logic.getLevel().getWorldHeight()){
            listener.hitboxFallenOutOfBounds();
        } else if (bottom() > logic.getLevel().getWorldHeight()){
            listener.hitboxFallingOutOfBounds();
        }

        // Remember this collision
        lastCollisionResult = result;

        // Inform the listener of this movement
        listener.hitboxMoved(result);
    }

    /**
     * Gets the most recent CollisionResult computed by this Hitbox.
     *
     * @return
     */
    public CollisionResult getLastCollisionResult() {
        return lastCollisionResult;
    }

    /**
     * Gets the distances along this Hitbox's horizontal edges at which to check
     * for collisions.
     *
     * @return
     */
    public float[] getHorizontalCollisionNodes() {
        return horizontalCollisionNodes;
    }

    /**
     * Gets the distances along this Hitbox's vertical edges at which to check
     * for collisions.
     *
     * @return
     */
    public float[] getVerticalCollisionNodes() {
        return verticalCollisionNodes;
    }

    /**
     * Sets a collision flag.
     *
     * @param flag
     * @param value
     */
    public void setCollisionFlag(int flag, boolean value){
        collisionFlags.put(flag, value);
    }

    /**
     * Gets a collision flag.
     *
     * @param flag
     * @return Flag value, or false is not set.
     */
    public boolean getCollisionFlag(int flag){
        Boolean value = collisionFlags.get(flag);
        return value == null ? false : value;
    }

    /**
     * Determines if this Hitbox is solid (can collide with tiles).
     *
     * @return
     */
    public boolean isSolid() {
        return solid;
    }

    /**
     * Sets whether this Hitbox is solid (can collide with tiles).
     *
     * @param solid
     */
    public void setSolid(boolean solid) {
        this.solid = solid;
    }

    /**
     * Determines whether the bottom edge of this Hitbox is resting on a
     * surface.
     *
     * @return
     */
    public boolean isGrounded() {
        return onGround;
    }

    /**
     * Gets the milliseconds since this Hitbox was last grounded.
     * @return
     */
    public int getMsSinceGrounded() {
        return msSinceGrounded;
    }

    /**
     * Setter that allows for manually overriding whether this Hitbox is deemed
     * to be on the ground.
     *
     * @see Hitbox#isGrounded
     * @param nowOnGround
     */
    public void setGrounded(boolean nowOnGround) {
        if (!onGround && nowOnGround){
            msSinceGrounded = 0;
            listener.hitboxLanded();
        } else if (onGround && !nowOnGround){
            listener.hitboxLeftGround();
        }
        onGround = nowOnGround;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Forces
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Adjusts this Hitbox's speed according to ground friction.
     *
     * @see Hitbox#setGroundFrictionCoefficient
     * @param delta
     */
    public void applyGroundFriction(int delta) {
        setSpeedX(Physics.applyGroundFriction(
                speedX, delta, groundFrictionCoefficient));
    }

    /**
     * Adjusts this Hitbox's x-speed according to air friction.
     *
     * @see Hitbox#setAirFrictionCoefficient
     * @param delta
     */
    public void applyAirFrictionX(int delta) {
        setSpeedX(Physics.applyAirFriction(
                speedX, delta, airFrictionCoefficient));
    }

    /**
     * Adjusts this Hitbox's y-speed according to air friction.
     *
     * @see Hitbox#setAirFrictionCoefficient
     * @param delta
     */
    public void applyAirFrictionY(int delta) {
        setSpeedY(Physics.applyAirFriction(
                speedY, delta, airFrictionCoefficient));
    }

    /**
     * Adjusts this Hitbox's y-speed according to gravity.
     *
     * @see Hitbox#setGravityCoefficient
     * @param delta
     */
    public void applyGravity(int delta) {
        setSpeedY(Physics.applyGravity(speedY, delta, gravityCoefficient));
    }

    /**
     * Sets the multiplier used to determine the strength of gravity, as applied
     * to this Hitbox.
     *
     * @param gravityCoefficient
     */
    public void setGravityCoefficient(float gravityCoefficient) {
        this.gravityCoefficient = gravityCoefficient;
    }

    /**
     * Sets the multiplier used to determine the strength of ground friction, as
     * applied to this Hitbox.
     *
     * @param groundFrictionCoefficient
     */
    public void setGroundFrictionCoefficient(float groundFrictionCoefficient) {
        this.groundFrictionCoefficient = groundFrictionCoefficient;
    }

    /**
     * Sets the multiplier used to determine the strength of air friction, as
     * applied to this Hitbox.
     *
     * @param airFrictionCoefficient
     */
    public void setAirFrictionCoefficient(float airFrictionCoefficient) {
        this.airFrictionCoefficient = airFrictionCoefficient;
    }

    /**
     * Sets the multiplier used to determine this Hitbox's speed after a
     * collision with a surface.
     *
     * @param bounceCoefficient
     */
    public void setBounceCoefficient(float bounceCoefficient) {
        this.bounceCoefficient = bounceCoefficient;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Utilities
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Determines if this Hitbox intersects the given Hitbox.
     *
     * @param other The Hitbox with which to check for intersection.
     * @return True if the Hitboxes intersect, false otherwise.
     */
    public boolean intersects(Hitbox other) {
        /*
         * See:
         * https://stackoverflow.com/questions/306316/determine-if-two-rectangles-overlap-each-other
         *
         * This first determines if there is any x-overlap, and then if there is
         * is any y-overlap.
         */
        return x < other.right() && right() > other.x &&
                y < other.bottom() && bottom() > other.y;
    }

    /**
     * Determines if this Hitbox contains the given point.
     *
     * @param px World units.
     * @param py World units.
     * @return
     */
    public boolean contains(float px, float py) {
        return x < px && px < right() &&
                y < py && py < bottom();
    }

    /**
     * Determines if this Hitbox is falling.
     *
     * @return
     */
    public boolean isFalling() {
        return !onGround && speedY > 0;
    }

    /**
     * Determines if this Hitbox is moving at all.
     *
     * @return
     */
    public boolean isMoving() {
        return Math.abs(speedX) >= Physics.MOVING_SPEED
                || Math.abs(speedY) >= Physics.MOVING_SPEED;
    }

    /**
     * Calculates the actual direct distance to another Hitbox.
     *
     * @param other
     * @return
     */
    public float getDistance(Hitbox other) {
        return (float) Math.hypot(
                centreX() - other.centreX(),
                centreY() - other.centreY());
    }

    /**
     * Calculates the approximate distance to another Hitbox.
     *
     * This does not calculate the diagonal, but simply adds the distance in
     * both axes.
     *
     * @param other
     * @return
     */
    public float getDistanceApprox(Hitbox other) {
        return Math.abs(x - other.x) + Math.abs(y - other.y);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Position
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Translates this Hitbox by the given distance.
     *
     * @param dx World units.
     * @param dy World units.
     */
    public void translate(float dx, float dy) {
        x += dx;
        y += dy;
    }

    /**
     * Sets the position (top-left) of this Hitbox.
     *
     * @param x World units.
     * @param y World units.
     */
    public void setPos(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Sets the position of the centre of this Hitbox.
     *
     * @param x World units.
     * @param y World units.
     */
    public void setCentre(float x, float y) {
        setPos(x - width / 2, y - height / 2);
    }

    /**
     * Gets the x-position of the left edge of this Hitbox, in world units.
     *
     * @return
     */
    public float left() {
        return x;
    }

    /**
     * Gets the y-position of the top edge of this Hitbox, in world units.
     *
     * @return
     */
    public float top() {
        return y;
    }

    /**
     * Gets the x-position of the right edge of this Hitbox, in world units.
     *
     * @return
     */
    public float right(){
        return x + width - Physics.SMALLEST_DISTANCE;
    }

    /**
     * Gets the y-position of the bottom edge of this Hitbox, in world units.
     *
     * @return
     */
    public float bottom(){
        return y + height - Physics.SMALLEST_DISTANCE;
    }

    /**
     * Gets the x-position of the centre of this Hitbox, in world units.
     *
     * @return
     */
    public float centreX() {
        return x + width / 2;
    }

    /**
     * Gets the y-position of the centre of this Hitbox, in world units.
     *
     * @return
     */
    public float centreY() {
        return y + height / 2;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Speed
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Sets the speed at which this Hitbox is travelling in the x-axis.
     *
     * <p>This will respect the minimum speed specified by
     * {@link Physics#MOVING_SPEED}, and the maximum speed defined by this
     * Hitbox.
     *
     * @see Hitbox#setMaxSpeedX
     * @param speedX
     */
    public void setSpeedX(float speedX) {
        if (Math.abs(speedX) < Physics.MOVING_SPEED){
            speedX = 0;
        }
        if (Math.abs(speedX) > maxSpeedX){
            speedX = Math.copySign(maxSpeedX, speedX);
        }
        this.speedX = speedX;
    }

    /**
     * See {@link Hitbox#setSpeedX}.
     *
     * <p>This will respect the minimum speed specified by
     * {@link Physics#MOVING_SPEED}, and the maximum speed defined by this
     * Hitbox.
     *
     * @see Hitbox#setMaxSpeedY
     * @param speedY
     */
    public void setSpeedY(float speedY) {
        if (Math.abs(speedY) < Physics.MOVING_SPEED){
            speedY = 0;
        }
        if (Math.abs(speedY) > maxSpeedY){
            speedY = Math.copySign(maxSpeedY, speedY);
        }
        this.speedY = speedY;
    }

    /**
     * The same as {@link Hitbox#setSpeedY}, but uses the given maximum speed
     * as an upper limit.
     *
     * @param speedY
     * @param maxSpeedY
     */
    public void setSpeedY(float speedY, float maxSpeedY) {
        if (Math.abs(speedY) > maxSpeedY){
            speedY = Math.copySign(maxSpeedY, speedY);
        }
        this.speedY = speedY;
    }

    /**
     * Setter for speed using an axis-independent speed limit.
     *
     * <p>This will calculate the actual speed of the Hitbox based on the given
     * x- and y-speeds, and if it exceeds the given limit, the x- and y-speeds
     * will be adjusted accordingly, while preserving the ratio between them.
     *
     * @param newSpeedX
     * @param newSpeedY
     * @param maxSpeed
     */
    public void setSpeed(float newSpeedX, float newSpeedY, float maxSpeed) {

        // Calculate axis-independent speed
        float speed = (float) Math.hypot(newSpeedX, newSpeedY);

        if (speed > maxSpeed){
            // Moving too fast; throttle the given x- and y-speeds
            float ratio = maxSpeed / speed;
            speedX = newSpeedX * ratio;
            speedY = newSpeedY * ratio;
        } else {
            // The given x- and y-speeds are acceptable without modification
            speedX = newSpeedX;
            speedY = newSpeedY;
        }
    }

    /**
     * Gets this Hitbox's speed in the x-axis.
     *
     * @return
     */
    public float getSpeedX() {
        return speedX;
    }

    /**
     * Gets this Hitbox's speed in the y-axis.
     *
     * @return
     */
    public float getSpeedY() {
        return speedY;
    }

    /**
     * Sets the maximum speed of this Hitbox in the x-axis.
     *
     * @param maxSpeedX
     */
    public void setMaxSpeedX(float maxSpeedX) {
        this.maxSpeedX = maxSpeedX;
    }

    /**
     * Sets the maximum speed of this Hitbox in the y-axis.
     *
     * @param maxSpeedY
     */
    public void setMaxSpeedY(float maxSpeedY) {
        this.maxSpeedY = maxSpeedY;
    }

}
