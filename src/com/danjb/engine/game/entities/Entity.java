package com.danjb.engine.game.entities;

import com.danjb.engine.game.Component;
import com.danjb.engine.game.ComponentStore;
import com.danjb.engine.game.Logic;
import com.danjb.engine.game.physics.CollisionResult;
import com.danjb.engine.game.physics.Hitbox;
import com.danjb.engine.game.physics.HitboxListener;

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
     * This Entity's physical presence within the game world.
     */
    public Hitbox hitbox;

    /**
     * Unique identifier used to refer to this Entity.
     *
     * <p>Assigned by the logic when the Entity is added to the world.
     */
    protected int id = -1;

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

    ////////////////////////////////////////////////////////////////////////////
    // Getters
    ////////////////////////////////////////////////////////////////////////////

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

        components.notifyAll(new EntityTeleported());
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
     * Called when this Entity is added to the game world.
     *
     * <p>This should be used for any initialisation that depends upon the
     * Entity having a physical presence in the game world.
     *
     * <p>If this is overridden, it may be necessary to also override the
     * {@link Entity#delete} method to clean up the Logic when this Entity is
     * removed.
     *
     * @param y
     * @param x
     * @param id
     * @param logic
     */
    public void addedToWorld(int id, float x, float y, Logic logic) {
        this.id = id;
        this.logic = logic;

        hitbox = createHitbox(x, y);

        // Inform Components
        for (EntityComponent component : components.asList()) {
            component.onSpawn(logic);
        }
    }

    /**
     * Creates this Entity's Hitbox, thus defining its position and size.
     *
     * @param y
     * @param x
     * @return
     */
    protected abstract Hitbox createHitbox(float x, float y);

    /**
     * Updates this Entity using the given delta value.
     *
     * <p>This is called every frame, BEFORE physics is applied.
     *
     * <p>This should only be called after the Entity has been added to the
     * world, since it is dependent on the logic.
     *
     * @param delta Milliseconds passed since the last frame.
     */
    public void update(int delta) {
        components.update(delta);
    }

    /**
     * Updates this Entity.
     *
     * <p>This is called every frame, AFTER physics is applied.
     *
     * <p>This should only be called after the Entity has been added to the
     * world, since it is dependent on the logic.
     *
     * @param delta
     */
    public void lateUpdate(int delta) {
        for (EntityComponent component : components.asList()) {
            component.lateUpdate(delta);
        }
    }

    /**
     * Performs any final clean-up before deletion.
     */
    public void destroy() {
        components.destroy();
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
     * <p>Results in a callback to {@link EntityComponent#onAttach},
     * and, if the Entity is already in the world,
     * {@link EntityComponent#onSpawn}.
     *
     * @param component
     */
    public void attach(EntityComponent component) {

        components.add(component);

        // Inform the new component
        component.onAttach(this);
        if (logic != null) {
            component.onSpawn(logic);
        }
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
