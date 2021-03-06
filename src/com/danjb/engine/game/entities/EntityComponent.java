package com.danjb.engine.game.entities;

import com.danjb.engine.game.Component;
import com.danjb.engine.game.Logic;
import com.danjb.engine.game.physics.Hitbox;

public abstract class EntityComponent extends Component {

    protected Entity entity;
    protected Hitbox hitbox;
    protected Logic logic;

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
     * @param logic
     */
    public void onAttach(Entity parent, Logic logic) {
        entity = parent;
        hitbox = entity.hitbox;

        this.logic = logic;
    }

    /**
     * Updates this Component.
     *
     * <p>This is called every frame, AFTER physics is applied.
     *
     * @param delta
     */
    public void lateUpdate(int delta) {
        // Do nothing by default
    }

}
