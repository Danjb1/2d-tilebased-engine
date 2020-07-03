package com.danjb.engine.game;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.danjb.engine.game.entities.CollisionListener;
import com.danjb.engine.game.entities.Entity;
import com.danjb.engine.game.level.Level;
import com.danjb.engine.game.level.TileProvider;
import com.danjb.engine.game.physics.Hitbox;

/**
 * The game logic.
 *
 * <p>Applies physics to all Entities within the current level and handles
 * collisions.
 *
 * @author Dan Bryce
 */
public class Logic {

    /**
     * The current Level.
     */
    protected Level level;

    /**
     * TileProvider that holds our tile types.
     */
    protected TileProvider tileProvider;

    /**
     * The next available Entity ID.
     */
    protected int nextEntityId;

    /**
     * Map of all Entities present in the game world, keyed by their unique
     * Entity IDs.
     *
     * <p>Using a {@link LinkedHashMap} means that iterating over the Entities
     * will use the same order as the order in which they were added to the
     * world.
     */
    protected Map<Integer, Entity> entities = new LinkedHashMap<>();

    /**
     * Entities that have just been added to the game world.
     *
     * <p>We collect these in a separate list to prevent a
     * ConcurrentModificationException during entity processing. These are then
     * added to {@link #entities} each frame.
     */
    private Map<Integer, Entity> pendingEntities = new LinkedHashMap<>();

    /**
     * List of Entities flagged for deletion.
     */
    protected List<Entity> entitiesToDelete = new ArrayList<>();

    /**
     * Milliseconds passed since the previous frame.
     */
    protected int delta;

    /**
     * Constructs the Logic.
     *
     * @param tileProvider
     */
    public Logic(TileProvider tileProvider) {
        this.tileProvider = tileProvider;
    }

    /**
     * Changes the current Level.
     *
     * <p>This must be called before {@link #update}.
     *
     * @param newLevel
     */
    public void changeLevel(Level newLevel) {
        if (level != null) {
            level.destroy();
        }
        level = newLevel;
    }

    /**
     * Updates the Logic using the given delta value.
     *
     * @param delta Number of milliseconds since the last update.
     */
    public void update(int delta) {

        if (level == null) {
            throw new IllegalStateException("No Level loaded");
        }

        this.delta = delta;

        refreshEntities();
        updateEntities(delta);
        processCollisions();
    }

    /**
     * Adds or removes any new or deleted Entities.
     *
     * <p>To prevent ConcurrentModificationExceptions, this should never be
     * called during Entity processing.
     */
    protected void refreshEntities() {
        // It is important that we always call `deleteEntities` before adding
        // any pending Entities, because if one of the pending Entities
        // OVERWRITES an existing Entity, we will lose the reference to the
        // overwritten Entity, so it will never get destroyed.
        deleteEntities();
        addPendingEntities();
    }

    /**
     * Adds any newly-spawned Entities to our {@link #entities} map.
     */
    protected void addPendingEntities() {
        entities.putAll(pendingEntities);
        pendingEntities.clear();
    }

    /**
     * Remove all Entities that have been marked for deletion.
     */
    protected void deleteEntities() {
        for (Entity e : entitiesToDelete) {
            e.destroy();
            entities.remove(e.getEntityId());
        }
        entitiesToDelete.clear();
    }

    /**
     * Updates all of the Entities in the world, and remembers any that are
     * marked for deletion.
     *
     * @param delta
     */
    protected void updateEntities(int delta) {

        // First update all Entities
        for (Entity entity : entities.values()) {
            entity.update(delta);
        }

        // Then apply physics to all Entities
        for (Entity entity : entities.values()) {

            applyPhysics(entity, delta);

            if (entity.isDeleted()) {
                entitiesToDelete.add(entity);
            }
        }

        // Finally give our Entities another update
        for (Entity entity : entities.values()) {
            entity.lateUpdate(delta);
        }
    }

    /**
     * Applies physics to the given Entity.
     *
     * @param entity
     * @param delta
     */
    protected void applyPhysics(Entity entity, int delta) {

        Hitbox hitbox = entity.hitbox;

        // Gravity
        if (entity.isAffectedByGravity()) {
            hitbox.applyGravity(delta);
        }

        // Movement
        if (entity.canMove()) {
            hitbox.moveWithCollision(level, tileProvider, delta);
        }

        // Friction
        if (hitbox.isGrounded()) {
            if (entity.isAffectedByGroundFriction()) {
                hitbox.applyGroundFriction(delta);
            }
        } else {
            if (entity.isAffectedByAirFrictionX()) {
                hitbox.applyAirFrictionX(delta);
            }
            if (entity.isAffectedByAirFrictionY()) {
                hitbox.applyAirFrictionY(delta);
            }
        }
    }

    /**
     * Checks for collisions between every pair of Entities.
     */
    protected void processCollisions() {

        List<Entity> collidingEntities = new ArrayList<>(entities.values());

        for (int i = 0; i < collidingEntities.size(); i++) {

            Entity e1  = collidingEntities.get(i);
            if (e1.isDeleted()) {
                continue;
            }

            for (int j = i + 1; j < collidingEntities.size(); j++) {

                Entity e2 = collidingEntities.get(j);
                if (e2.isDeleted()) {
                    continue;
                }

                checkForCollision(e1, e2);

                if (e1.isDeleted()) {
                    // Entity has been deleted as the result of a collision
                    break;
                }
            }
        }
    }

    /**
     * Handles any collisions between the given pair of Entities.
     *
     * @param e1
     * @param e2
     */
    protected void checkForCollision(Entity e1, Entity e2) {

        CollisionListener e1CollisionListener = (CollisionListener)
                e1.components.get(CollisionListener.KEY);
        CollisionListener e2CollisionListener = (CollisionListener)
                e2.components.get(CollisionListener.KEY);

        // This flag ensures that we only check for a collision once
        boolean collision = false;

        if (e1CollisionListener != null &&
                e1CollisionListener.canCollideWith(e2)) {
            if (e1.hitbox.intersects(e2.hitbox)) {
                collision = true;
                e1CollisionListener.collidedWith(e2);
            } else {
                return;
            }
        }

        if (e2CollisionListener != null &&
                e2CollisionListener.canCollideWith(e1) &&
                (collision || e2.hitbox.intersects(e1.hitbox))) {
            e2CollisionListener.collidedWith(e1);
        }
    }

    /**
     * Adds an Entity to the game world.
     *
     * <p>Results in a callback to {@link Entity#addedToWorld}.
     *
     * @param x
     * @param y
     * @param entity
     */
    public void addEntity(Entity entity, float x, float y) {
        int entityId = requestEntityId();
        addEntity(entityId, entity, x, y);
    }

    /**
     * Adds an Entity to the game world with a predefined ID.
     *
     * <p>Results in a callback to {@link Entity#addedToWorld}.
     *
     * @param entityId
     * @param x
     * @param y
     * @param entity
     */
    public void addEntity(int entityId, Entity entity, float x, float y) {

        // Delete any Entity that is being overwritten
        Entity previousEntity = entities.get(entityId);
        if (previousEntity != null) {
            previousEntity.delete();
        }

        pendingEntities.put(entityId, entity);
        entity.addedToWorld(entityId, x, y, this);
    }

    /**
     * Returns the next available entity ID.
     *
     * @return
     */
    protected int requestEntityId() {
        int returnValue = nextEntityId;
        nextEntityId++;
        return returnValue;
    }

    /**
     * Gets the current Level.
     *
     * @return
     */
    public Level getLevel() {
        return level;
    }

    /**
     * Gets the Entity with the given Entity ID.
     *
     * @param entityId
     * @return
     */
    public Entity getEntity(int entityId) {
        Entity e = entities.get(entityId);
        if (e == null) {
            // Newly-added Entities will still be in the pending list
            e = pendingEntities.get(entityId);
        }
        return e;
    }

    /**
     * Gets the collection of Entities present in the game world.
     *
     * <p>Changes to this collection will have no effect.
     *
     * @return
     */
    public Map<Integer, Entity> getEntities() {
        Map<Integer, Entity> allEntities = new LinkedHashMap<>(entities);
        allEntities.putAll(pendingEntities);
        return allEntities;
    }

    /**
     * Gets the TileProvider that holds the available tile types.
     *
     * @return
     */
    public TileProvider getTileProvider() {
        return tileProvider;
    }

    public int getDelta() {
        return delta;
    }

}
