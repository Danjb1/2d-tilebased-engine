package game.entities;

import game.Component;
import game.ComponentOwner;
import game.Logic;
import game.physics.CollisionResult;
import game.physics.Hitbox;
import game.physics.HitboxListener;

/**
 * An object that exists within the game world.
 *
 * <p>Some of the key properties of an Entity are as follows:
 *
 * <ul>
 * <li><b>Unique identifier:</b> Unique identifier used to refer to this
 * Entity.</li>
 *
 * <li><b>{@link Hitbox}:</b>  This Entity's physical presence within the game
 * world.</li>
 *
 * <li><b>Optional {@link Component}s:</b> These give Entities additional
 * properties or behaviour that can be changed on the fly.</li>
 * </ul>
 *
 * @author Dan Bryce
 */
public abstract class Entity extends ComponentOwner implements HitboxListener {

    /**
     * Unique identifier used to refer to this Entity.
     * 
     * <p>Assigned by the logic when the Entity is added to the world.
     */
    protected int id = -1;

    /**
     * This Entity's physical presence within the game world.
     */
    protected Hitbox hitbox;

    /**
     * Flag set when this Entity is marked for deletion.
     */
    protected boolean deleted;

    /**
     * Handle to the game logic.
     *
     * <p>This will be null until the Entity is added to the logic.
     */
    protected Logic logic;

    /**
     * Creates a new Entity with the given position and size.
     *
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public Entity(float x, float y, float width, float height) {
        hitbox = new Hitbox(x, y, width, height, this);
    }

    /**
     * Callback for when this Entity is added to the world.
     *
     * <p>If this is overridden, it may be necessary to also override the
     * {@link Entity#delete} method to clean up the Logic when this Entity is
     * removed.
     *
     * @param id
     * @param logic
     */
    public void addedToWorld(int id, Logic logic) {
        this.id = id;
        this.logic = logic;
    }

    /**
     * Updates this Entity (should be called each frame).
     *
     * <p>This should only be called after the Entity has been added to the
     * world, since it is dependent on the logic.
     *
     * @param delta
     */
    public void update(int delta) {

        // Update Components
        for (Component component : components.values()){
            component.update(logic, delta);
        }

        /*
         * Apply physics.
         *
         * We do this last, so that if a collision occurs the Entity's speed
         * will be zero once the update is finished, which may be useful to
         * know.
         */
        
        if (isAffectedByGravity()){
            hitbox.applyGravity(delta);
        }

        if (canMove()){
            hitbox.moveWithCollision(logic, delta);
        }

        if (isAffectedByFriction()){
            hitbox.applyFriction(delta);
        }
    }

    /**
     * Determines whether gravity should be applied to this Entity.
     *
     * @see Hitbox#applyGravity
     * @return
     */
    protected boolean isAffectedByGravity() {
        return true;
    }

    /**
     * Determines whether this Entity can move.
     *
     * <p>Currently there is no way to distinguish between horizontal and
     * vertical movement; either an Entity can move, or it is completely
     * stationary.
     *
     * <p>Note also that the velocity of a stationary Entity can still be
     * changed, e.g. by gravity.
     *
     * @see Hitbox#moveWithCollision
     * @return
     */
    protected boolean canMove() {
        return true;
    }

    /**
     * Determines whether friction should be applied to this Entity.
     *
     * @see Hitbox#applyFriction
     * @return
     */
    protected boolean isAffectedByFriction() {
        return true;
    }

    /**
     * Gets this Entity's {@link Hitbox}.
     * @return
     */
    public Hitbox getHitbox() {
        return hitbox;
    }

    /**
     * Marks this Entity for deletion.
     */
    public void delete() {
        deleted = true;
    }

    /**
     * Determines whether this Entity has been deleted.
     *
     * <p>Deleted Entities are removed by the logic at the end of every frame.
     *
     * @return
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * Gets this Entity's unique ID.
     * 
     * @return
     */
    public int getEntityId(){
        return id;
    }

    @Override
    public void hitboxMoved(CollisionResult result) {
        // To be overridden as required
    }

    @Override
    public void hitboxLanded() {
        // To be overridden as required
    }

    @Override
    public void hitboxLeftGround() {
        // To be overridden as required
    }

    @Override
    public void hitboxFallingOutOfBounds() {
        // By default, do nothing until the Entity has fallen out-of-bounds
        // completely.
    }

    @Override
    public void hitboxFallenOutOfBounds() {
        delete();
    }

}