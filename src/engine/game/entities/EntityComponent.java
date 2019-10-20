package engine.game.entities;

import engine.game.Component;
import engine.game.Logic;

public abstract class EntityComponent extends Component {

    protected Entity entity;

    public EntityComponent(String key) {
        super(key);
    }

    /**
     * Called when this Component is successfully attached to a ComponentOwner.
     *
     * <p>To avoid repeated casting, subclasses should cast the ComponentOwner
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
    public void entityAddedToWorld(Logic logic) {
        // Do nothing by default
    }

    /**
     * Updates this Component.
     *
     * <p>This should be called every frame by the ComponentOwner, if such
     * functionality is desired.
     *
     * @param logic
     * @param delta
     */
    public void update(Logic logic, int delta) {
        // Do nothing by default
    }

    /**
     * Performs any clean-up before this Component is deleted.
     */
    public void destroy() {
        // Do nothing by default
    }

}
