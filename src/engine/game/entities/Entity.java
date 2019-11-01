package engine.game.entities;

import engine.game.Component;
import engine.game.ComponentStore;
import engine.game.Logic;
import engine.game.physics.CollisionResult;
import engine.game.physics.Hitbox;
import engine.game.physics.HitboxListener;

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
public abstract class Entity implements HitboxListener {

    /**
     * {@link EntityComponent}s attached to this Entity.
     */
    public ComponentStore<EntityComponent> components = new ComponentStore<>();

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

    ////////////////////////////////////////////////////////////////////////////
    // Getters
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Gets this Entity's {@link Hitbox}.
     *
     * @return
     */
    public Hitbox hitbox {
        return hitbox;
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
    public int getEntityId() {
        return id;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Modifiers
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Teleports this Entity.
     *
     * @param x New x-position
     * @param y New y-position
     */
    public void teleport(float x, float y) {
        hitbox.setPos(x, y);

        components.notifyAll(CameraSettings.KEY, new EntityTeleported());
    }

    /**
     * Marks this Entity for deletion.
     */
    public void delete() {
        deleted = true;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Lifecycle
    ////////////////////////////////////////////////////////////////////////////

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

        // Inform Components
        for (EntityComponent component : components.asList()) {
            component.entityAddedToWorld(logic);
        }
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
        for (EntityComponent component : components.asList()) {
            component.update(logic, delta);
        }
    }

    /**
     * Performs any final clean-up before deletion.
     */
    public void destroy() {
        // Destroy Components
        for (EntityComponent component : components.asList()) {
            component.destroy();
        }
        components.clear();
        hitbox.destroy();
    }

    ////////////////////////////////////////////////////////////////////////////
    // Behaviour
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Determines whether gravity should be applied to this Entity.
     *
     * @see Hitbox#applyGravity
     * @return
     */
    public boolean isAffectedByGravity() {
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
    public boolean canMove() {
        return true;
    }

    /**
     * Determines whether ground friction should be applied to this Entity.
     *
     * @see Hitbox#applyGroundFriction
     * @return
     */
    public boolean isAffectedByGroundFriction() {
        return true;
    }

    /**
     * Determines whether air friction should be applied to this Entity.
     *
     * @see Hitbox#applyAirFrictionX
     * @return
     */
    public boolean isAffectedByAirFrictionX() {
        return true;
    }

    /**
     * Determines whether air friction should be applied to this Entity.
     *
     * <p>By default this is false because it interferes with gravity.
     *
     * @see Hitbox#applyAirFrictionY
     * @return
     */
    public boolean isAffectedByAirFrictionY() {
        return false;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Components
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Attaches an {@link EntityComponent} to this Entity.
     *
     * <p>Results in a callback to {@link EntityComponent#onAttach(Entity)}.
     *
     * @param component
     */
    public void attach(EntityComponent component) {

        components.add(component);

        // Inform the new component
        component.onAttach(this);
    }

    ////////////////////////////////////////////////////////////////////////////
    // HitboxListener methods
    ////////////////////////////////////////////////////////////////////////////

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
