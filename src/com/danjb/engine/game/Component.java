package com.danjb.engine.game;

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
     * Whether this Component is pending deletion.
     */
    protected boolean deleted;

    /**
     * Creates a Component.
     *
     * @param key
     */
    public Component(String key) {
        this.key = key;
    }

    /**
     * Updates this Component.
     *
     * <p>This is called by {@link ComponentStore#update}.
     *
     * @param delta
     */
    public void update(int delta) {
        // Do nothing by default
    }

    /**
     * Notifies this Component of an event.
     *
     * <p>This is a generic message-passing mechanism that can be used to signal
     * to components when certain events take place.
     *
     * @param eventBeforeCast
     */
    public void notify(ComponentEvent eventBeforeCast) {
        // Do nothing by default
    }

    /**
     * Marks this Component for deletion.
     */
    public void delete() {
        deleted = true;
    }

    /**
     * Determines if this Component has been deleted.
     *
     * @return
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * Performs any necessary clean-up after this Component is deleted.
     */
    protected void destroy() {
        // Do nothing by default
    }

    /**
     * Gets the key used to store this Component.
     *
     * @return
     */
    public String getKey() {
        return key;
    }

}
