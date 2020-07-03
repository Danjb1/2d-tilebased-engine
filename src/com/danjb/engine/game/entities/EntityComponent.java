package com.danjb.engine.game.entities;

import com.danjb.engine.game.Component;
import com.danjb.engine.game.Logic;

public abstract class EntityComponent extends Component {

    protected Entity entity;

    public EntityComponent(String key) {
        super(key);
    }

    /**
     * Called when this Component is successfully attached to a Entity.
     *
     * <p>To avoid repeated casting, subclasses should cast the Entity
     * to the desired type from within this method and store it to a field.
     *
     * @param parent
     */
    public void onAttach(Entity parent) {
        entity = parent;
    }

    /**
     * Called when the parent Entity is added to the world.
     *
     * @param logic
     */
    public void onSpawn(Logic logic) {
        // Do nothing by default
    }

    /**
     * Updates this Component.
     *
     * <p>This is called every frame, BEFORE physics is applied.
     *
     * @param logic
     * @param delta
     */
    public void update(Logic logic, int delta) {
        // Do nothing by default
    }

    /**
     * Updates this Component.
     *
     * <p>This is called every frame, AFTER physics is applied.
     *
     * @param logic
     * @param delta
     */
    public void lateUpdate(Logic logic, int delta) {
        // Do nothing by default
    }

}