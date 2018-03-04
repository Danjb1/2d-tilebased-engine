package engine.game;

import engine.launcher.Logger;

/**
 * A Component that can be overridden to provide some specific behaviour.
 *
 * @author Dan Bryce
 */
public abstract class Component {

    /**
     * Key used to attach this Component to its parent.
     */
    protected String key;

    /**
     * ComponentOwner to which this Component is attached.
     */
    protected ComponentOwner parent;

    /**
     * The permitted subclass of ComponentOwner to which this Component can be
     * attached.
     */
    protected Class<? extends ComponentOwner> permittedParentClass;

    /**
     * Creates a Component.
     *
     * @param key
     * @param permittedParentClass
     */
    public Component(String key,
            Class<? extends ComponentOwner> permittedParentClass) {
        this.key = key;
        this.permittedParentClass = permittedParentClass;
    }

    /**
     * Adds this Component to the given ComponentOwner, as long as the type of
     * ComponentOwner matches the allowed type.
     *
     * <p>Results in a call to onAttach() if successful; logs a warning
     * otherwise.
     *
     * <p>This should only be called once per Component.
     *
     * @param parent
     */
    public final void attachTo(ComponentOwner parent) {

        if (!permittedParentClass.isAssignableFrom(parent.getClass())){
            Logger.log("Tried to attach %s to %s", this, parent);
            return;
        }

        onAttach(parent);
    }

    /**
     * Called when this Component is successfully attached to a ComponentOwner.
     *
     * <p>To avoid repeated casting, subclasses should cast the ComponentOwner
     * to the desired type from within this method and store it to a field.
     *
     * @param parent
     */
    protected void onAttach(ComponentOwner parent) {
        this.parent = parent;

        parent.addComponent(key, this);
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
    public void update(Logic logic, int delta){
        // Do nothing by default
    }

    /**
     * Notifies this Component of an event.
     *
     * <p>This is a generic message-passing mechanism that can be used by
     * ComponentOwners to signal to their components when certain events take
     * place.
     *
     * @param event Event identifier.
     * @param args Optional parameters relating to the event.
     */
    public void notify(String event, String[] args) {
        // Do nothing by default
    }

}
