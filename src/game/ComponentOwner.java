package game;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to which Components can be attached.
 */
public abstract class ComponentOwner {

    /**
     * {@link Component}s attached to this object.
     *
     * <p>Components should be added using {@link Component#attachTo}, and 
     * retrieved using {@link getComponent}.
     */
    protected Map<String, Component> components = new HashMap<>();

    /**
     * Adds a Component to this ComponentOwner.
     * 
     * @param key
     * @param component
     */
    public void addComponent(String key, Component component) {
        components.put(key, component);
    }

    /**
     * Retrives the Component with the given key.
     * 
     * @param key
     * @return
     */
    public Component getComponent(String key) {
        return components.get(key);
    }

    /**
     * Sends an event, with no arguments, to the Component with the given key.
     *
     * @param key
     * @param event Event identifier.
     */
    protected void notifyComponent(String key, String event) {
        notifyComponent(key, event, null);
    }

    /**
     * Sends an event, with arguments, to the Component with the given key.
     *
     * @param key
     * @param event Event identifier.
     * @param args Parameters relating to the event.
     */
    protected void notifyComponent(String key, String event, String[] args) {
        Component component = components.get(key);
        if (component != null){
            component.notify(event, args);
        }
    }

}
