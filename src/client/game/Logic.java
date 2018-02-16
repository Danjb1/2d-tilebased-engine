package client.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import client.game.entities.CollisionListener;
import client.game.entities.Entity;
import client.game.tiles.Air;
import client.game.tiles.ForegroundTile;
import client.game.tiles.SolidBlock;
import client.game.tiles.Tile;

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
    private Level level;

    /**
     * All available Tile types, by ID.
     */
    private Map<Integer, Tile> tiles = new HashMap<>();
    
    /**
     * The next available Entity ID.
     */
    private int nextEntityId;
    
    /**
     * Map of all Entities present in the game world, keyed by their unique
     * Entity IDs.
     *
     * <p>Using a {@link LinkedHashMap} means that iterating over the Entities
     * will use the same order as the order in which they were added to the
     * world.
     */
    private Map<Integer, Entity> entities = new LinkedHashMap<>();

    /**
     * IDs of all Entities flagged for deletion.
     */
    private List<Integer> entitiesToDelete = new ArrayList<>();

    /**
     * Constructs the Logic using the given Level.
     * 
     * @param level
     */
    public Logic(Level level) {
        this.level = level;

        // Create the available Tile types
        tiles.put(ForegroundTile.ID_LEVEL_BORDER,
                new SolidBlock(ForegroundTile.ID_LEVEL_BORDER));
        tiles.put(ForegroundTile.ID_AIR, new Air(ForegroundTile.ID_AIR));
        tiles.put(1, new SolidBlock(1));
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
    private void updateEntities(int delta) {
        for (Entity entity : entities.values()){
            
            entity.update(delta);
            
            if (entity.isDeleted()){
                entitiesToDelete.add(entity.getEntityId());
            }
        }
    }

    /**
     * Checks for collisions between every pair of Entities.
     */
    private void processCollisions() {

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
            }
        }
    }

    /**
     * Handles any collisions between the given pair of Entities.
     *
     * @param e1
     * @param e2
     */
    private void checkForCollision(Entity e1, Entity e2) {

        CollisionListener e1CollisionListener = (CollisionListener)
                e1.getComponent(CollisionListener.KEY);
        CollisionListener e2CollisionListener = (CollisionListener)
                e2.getComponent(CollisionListener.KEY);

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
    private void deleteEntities() {
        for (Integer i : entitiesToDelete){
            entities.remove(i);
        }
        entitiesToDelete.clear();
    }

    /**
     * Adds the given Entity to the world.
     *
     * <p>This also initialises any Components that the Entity requires.
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
    private int requestEntityId() {
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
     * Gets the Tile with the given ID.
     * 
     * @param tileId
     * @return
     */
    public Tile getTile(int tileId) {
        return tiles.get(tileId);
    }

}
