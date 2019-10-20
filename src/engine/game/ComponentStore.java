package engine.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComponentStore<T extends Component> {

    /**
     * {@link Component}s attached to this object.
     */
    private List<T> components = new ArrayList<>();

    /**
     * {@link Component}s attached to this object, by key.
     */
    private Map<String, List<T>> componentsByKey = new HashMap<>();

    /**
     * Adds a {@link Component} to this ComponentStore.
     *
     * @param component
     */
    public void add(T component) {

        // Find the list of components that share this key
        String key = component.getKey();
        List<T> componentsWithKey = componentsByKey.get(key);

        if (componentsWithKey == null) {
            // This is the first component with this key
            componentsWithKey = new ArrayList<>();
            componentsByKey.put(key, componentsWithKey);
        }

        // Add the new component
        componentsWithKey.add(component);
        components.add(component);
    }

    /**
     * Removes a {@link Component} from this ComponentStore.
     *
     * @param component
     */
    public void remove(T component) {
        List<T> componentsWithKey = getAll(component.getKey());
        componentsWithKey.remove(component);
    }

    /**
     * Sends an event to all the Components with the given key.
     *
     * @param key
     * @param event
     */
    public void notifyAll(String key, ComponentEvent event) {
        List<T> components = getAll(key);
        for (T component : components) {
            component.notify(event);
        }
    }

    /**
     * Gets all the Components with the given key.
     *
     * <p>Changes to this list will not be reflected in the store.
     *
     * @param key
     * @return
     */
    public List<T> getAll(String key) {
        List<T> components = componentsByKey.get(key);
        return components == null ?
                new ArrayList<>() :
                new ArrayList<>(components);
    }

    /**
     * Gets the first Component found with the given key.
     *
     * @param key
     * @return
     */
    public T get(String key) {
        List<T> components = getAll(key);
        return components.isEmpty() ? null : components.get(0);
    }

    /**
     * Returns all the Components in this ComponentStore.
     *
     * <p>Changes to this list will not be reflected in the store.
     *
     * @return
     */
    public List<T> asList() {
        return new ArrayList<>(components);
    }

    /**
     * Determines if the store contains no Components.
     *
     * @return
     */
    public boolean isEmpty() {
        return components.isEmpty();
    }

    /**
     * Removes all Components from this ComponentStore.
     */
    public void clear() {
        components.clear();
        componentsByKey.clear();
    }

}
