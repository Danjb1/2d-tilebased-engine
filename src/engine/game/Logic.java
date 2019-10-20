package engine.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import engine.game.entities.CollisionListener;
import engine.game.entities.Entity;
import engine.game.physics.Hitbox;
import engine.game.tiles.Air;
import engine.game.tiles.ForegroundTile;
import engine.game.tiles.SolidBlock;
import engine.game.tiles.Tile;

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
     * All available Tile types, by ID.
     */
    protected Map<Integer, Tile> tiles = new HashMap<>();

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
     * List of Entities flagged for deletion.
     */
    protected List<Entity> entitiesToDelete = new ArrayList<>();

    /**
     * Constructs the Logic using the given Level.
     *
     * @param level
     */
    public Logic(Level level) {
        this.level = level;

        // Add the always-available Tile types
        addTileType(new Air(ForegroundTile.ID_AIR));
        addTileType(new SolidBlock(ForegroundTile.ID_SOLID_BLOCK));
    }

    /**
     * Updates the Logic using the given delta value.
     *
     * @param delta Number of milliseconds since the last update.
     */
    public void update(int delta) {
        updateEntities(delta);
        processCollisions();
        deleteEntities();
    }

    /**
     * Updates all of the Entities in the world, and remembers any that are
     * marked for deletion.
     *
     * @param delta
     */
    protected void updateEntities(int delta) {
        for (Entity entity : entities.values()){

            entity.update(delta);

            applyPhysics(entity, delta);

            if (entity.isDeleted()){
                entitiesToDelete.add(entity);
            }
        }
    }

    /**
     * Applies physics to the given Entity.
     *
     * @param entity
     * @param delta
     */
    protected void applyPhysics(Entity entity, int delta) {

        Hitbox hitbox = entity.getHitbox();

        // Gravity
        if (entity.isAffectedByGravity()) {
            hitbox.applyGravity(delta);
        }

        // Movement
        if (entity.canMove()) {
            hitbox.moveWithCollision(this, delta);
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
            if (e1.isDeleted()){
                continue;
            }

            for (int j = i + 1; j < collidingEntities.size(); j++) {

                Entity e2 = collidingEntities.get(j);
                if (e2.isDeleted()){
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
                e1CollisionListener.canCollideWith(e2)){
            if (e1.getHitbox().intersects(e2.getHitbox())) {
                collision = true;
                e1CollisionListener.collidedWith(e2);
            } else {
                return;
            }
        }

        if (e2CollisionListener != null &&
                e2CollisionListener.canCollideWith(e1) &&
                (collision || e2.getHitbox().intersects(e1.getHitbox()))) {
            e2CollisionListener.collidedWith(e1);
        }
    }

    /**
     * Remove all Entities that have been marked for deletion.
     */
    protected void deleteEntities() {
        for (Entity e : entitiesToDelete){
            e.destroy();
            entities.remove(e.getEntityId());
        }
        entitiesToDelete.clear();
    }

    /**
     * Adds the given Entity to the world.
     *
     * <p><b>This should not be called during Entity processing.</b> Adding an
     * Entity from within an Entity's {@link Entity#update} method can cause a
     * ConcurrentModificationException.
     *
     * @param entity
     */
    public void addEntity(Entity entity) {
        int entityId = requestEntityId();
        entities.put(entityId, entity);
        entity.addedToWorld(entityId, this);
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
        return entities.get(entityId);
    }

    /**
     * Gets the collection of Entities present in the game world.
     *
     * @return
     */
    public Map<Integer, Entity> getEntities() {
        return entities;
    }

    /**
     * Adds the given ForegroundTile to the list of available tile types.
     *
     * @param tile
     */
    public void addTileType(ForegroundTile tile) {
        tiles.put(tile.getId(), tile);
    }

    /**
     * Gets the Tile with the given ID.
     *
     * @param tileId
     * @return
     */
    public Tile getTile(int tileId) {
        return tiles.get(tileId);
    }

}
