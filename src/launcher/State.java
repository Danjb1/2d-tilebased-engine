package launcher;

public abstract class State {

    protected Launcher launcher;

    /**
     * Constructs a State.
     *
     * @param launcher
     */
    public State(Launcher launcher) {
        this.launcher = launcher;
    }

    /**
     * Processes the given Input.
     *
     * @param input
     */
    public void processInput(Input input) {
        // Perform any Input processing here
    }

    /**
     * Updates the logic.
     *
     * @param delta
     */
    public void update(int delta) {
        // Perform any Logic processing here
    }

    /**
     * Draws to the display.
     */
    public void render() {
        // Perform any rendering here
    }

    /**
     * Called when this State is being left.
     */
    public void finish() {
        // Perform any cleanup here
    }

}
