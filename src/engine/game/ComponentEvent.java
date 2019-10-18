package engine.game;

public abstract class ComponentEvent {

    private String key;

    public ComponentEvent(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
