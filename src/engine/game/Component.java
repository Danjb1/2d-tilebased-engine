package engine.game;

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
     * Creates a Component.
     *
     * @param key
     */
    public Component(String key) {
        this.key = key;
    }

    /**
     * Notifies this Component of an event.
     *
     * <p>This is a generic message-passing mechanism that can be used to signal
     * to components when certain events take place.
     *
     * @param event
     */
    public void notify(ComponentEvent event) {
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
