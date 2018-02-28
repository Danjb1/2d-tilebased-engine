package game.physics;

import game.GameUtils;
import game.Logic;
import game.tiles.Slope;
import game.tiles.Tile;
import launcher.Launcher;

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
     * <p>By default, this is equal to 1 Tile per frame; our collision algorithm
     * can't cope with any more than this! Assuming a delta value of 16,
     * (speed * delta) / 1000 should never exceed Tile.WIDTH/HEIGHT.
     *
     * <p>max_units_per_frame * frames_per_second = max_units_per_second
     *
     * <p>In general, this will not need to be overridden unless this Hitbox
     * has a means of accelerating; in many cases a Hitbox will never exceed its
     * initial speed.
     */
    protected float maxSpeedX =
            Launcher.FPS *
            (GameUtils.worldUnits(1) - Physics.SMALLEST_DISTANCE);

    /**
     * See {@link Hitbox#maxSpeedX}.
     */
    protected float maxSpeedY =
            Launcher.FPS *
            (GameUtils.worldUnits(1) - Physics.SMALLEST_DISTANCE);

    /**
     * Distances along this Hitbox's horizontal edges at which to check for
     * collision.
     */
    private float[] collisionNodesX;

    /**
     * Distances along this Hitbox's vertical edges at which to check for
     * collision.
     */
    private float[] collisionNodesY;

    /**
     * Flag set whenever this Hitbox is touching the ground.
     */
    private boolean onGround;

    /**
     * Multiplier that determines how strongly this Hitbox is affected by
     * gravity.
     */
    protected float gravityCoefficient = 1.0f;

    /**
     * Multiplier that determines how colliding with a surface affects this
     * Hitbox's speed.
     */
    protected float bounceCoefficient = 0;

    /**
     * Multiplier that determines how the Hitbox's speed is affected each frame
     * when on the ground.
     */
    protected float groundFriction = 1;

    /**
     * Multiplier that determines how the Hitbox's speed is affected each frame
     * when in the air.
     */
    protected float airFriction = 1;

    /**
     * Whether this Hitbox can travel up and down Slopes.
     *
     * <p>If set to false, this Hitbox will collide with slopes instead.
     */
    protected boolean supportsSlopes = false;

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
            HitboxListener listener){
        
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.listener = listener;

        collisionNodesX = createCollisionNodes(width);
        collisionNodesY = createCollisionNodes(height);
    }

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
     * @see Hitbox#setSupportsSlopes
     * @param logic
     * @param delta
     */
    public void moveWithCollision(Logic logic, int delta) {

        // Determine movement distance, given the current delta value
        float dx = (speedX * delta) / 1000;
        float dy = (speedY * delta) / 1000;

        // Move to the nearest collision
        CollisionResult result = 
                Physics.getCollisionResult(logic, this, dx, dy);
        setPos(result.getLeft(), result.getTop());

        // Collide with slopes if this Hitbox doesn't support them
        if (!supportsSlopes && result.isCollisionWithSlope()){
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
                    setOnGround(true);
                }
            }
        } else {
            if (onGround){
                // Hitbox has left the ground
                setOnGround(false);
            }
        }

        // Check if this Hitbox is now out-of-bounds
        if (y > logic.getLevel().getWorldHeight()){
            listener.hitboxFallenOutOfBounds();
        } else if (getBottom() > logic.getLevel().getWorldHeight()){
            listener.hitboxFallingOutOfBounds();
        }

        // Inform the listener of this movement
        listener.hitboxMoved(result);
    }

    /**
     * Adjusts this Hitbox's speed according to friction.
     *
     * <p>There are 2 different values for friction; one is used when this
     * Hitbox is on the ground, the other is used when this Hitbox is in the
     * air.
     *
     * @see Hitbox#setAirFriction
     * @see Hitbox#setGroundFriction
     * @param delta
     */
    public void applyFriction(int delta) {
        if (onGround){
            setSpeedX(Physics.applyDeceleration(speedX, delta, groundFriction));
        } else {
            setSpeedX(Physics.applyDeceleration(speedX, delta, airFriction));
            setSpeedY(Physics.applyDeceleration(speedY, delta, airFriction));
        }
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
        return x < other.getRight() && getRight() > other.x &&
                y < other.getBottom() && getBottom() > other.y;
    }

    /**
     * Determines if this Hitbox contains the given point.
     *
     * @param px World units.
     * @param py World units.
     * @return
     */
    public boolean contains(float px, float py) {
        return x < px && px < getRight() &&
                y < py && py < getBottom();
    }

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
     * Gets the x-position of the left edge of this Hitbox, in world units.
     *
     * @return
     */
    public float getLeft() {
        return x;
    }

    /**
     * Gets the y-position of the top edge of this Hitbox, in world units.
     *
     * @return
     */
    public float getTop() {
        return y;
    }

    /**
     * Gets the x-position of the right edge of this Hitbox, in world units.
     *
     * @return
     */
    public float getRight(){
        return x + width - Physics.SMALLEST_DISTANCE;
    }

    /**
     * Gets the y-position of the bottom edge of this Hitbox, in world units.
     *
     * @return
     */
    public float getBottom(){
        return y + height - Physics.SMALLEST_DISTANCE;
    }

    /**
     * Gets the width of this Hitbox, in world units.
     *
     * @return
     */
    public float getWidth(){
        return width;
    }

    /**
     * Gets the height of this Hitbox, in world units.
     *
     * @return
     */
    public float getHeight(){
        return height;
    }

    /**
     * Gets the x-position of the centre of this Hitbox, in world units.
     *
     * @return
     */
    public float getCentreX() {
        return x + width / 2;
    }

    /**
     * Gets the y-position of the centre of this Hitbox, in world units.
     *
     * @return
     */
    public float getCentreY() {
        return y + height / 2;
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
     * @see Hitbox#setMaxSpeedY
     * @param speedY
     */
    public void setSpeedY(float speedY) {
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

    /**
     * Gets the distances along this Hitbox's horizontal edges at which to check
     * for collisions.
     *
     * @return
     */
    public float[] getCollisionNodesX() {
        return collisionNodesX;
    }

    /**
     * Gets the distances along this Hitbox's vertical edges at which to check
     * for collisions.
     *
     * @return
     */
    public float[] getCollisionNodesY() {
        return collisionNodesY;
    }

    /**
     * Enables or disables slope support.
     *
     * <p>Hitboxes that support slopes will slide up/down them; Hitboxes that do
     * not support slopes will collide with them.
     *
     * @param supportsSlopes
     */
    public void setSupportsSlopes(boolean supportsSlopes) {
        this.supportsSlopes = supportsSlopes;
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
     * @param groundFriction
     */
    public void setGroundFriction(float groundFriction) {
        this.groundFriction = groundFriction;
    }

    /**
     * Sets the multiplier used to determine the strength of air friction, as
     * applied to this Hitbox.
     *
     * @param airFriction
     */
    public void setAirFriction(float airFriction) {
        this.airFriction = airFriction;
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

    /**
     * Determines whether the bottom edge of this Hitbox is resting on a
     * surface.
     *
     * @return
     */
    public boolean isOnGround() {
        return onGround;
    }

    /**
     * Setter that allows for manually overriding whether this Hitbox is deemed
     * to be on the ground.
     *
     * @see Hitbox#isOnGround
     * @param nowOnGround
     */
    public void setOnGround(boolean nowOnGround) {
        if (!onGround && nowOnGround){
            listener.hitboxLanded();
        } else if (onGround && !nowOnGround){
            listener.hitboxLeftGround();
        }
        onGround = nowOnGround;
    }

}
