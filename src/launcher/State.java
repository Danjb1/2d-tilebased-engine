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
     * Handles any user input.
     */
    public void pollInput(){
        // TODO
    }

    /**
     * Updates the logic.
     *
     * @param delta
     */
    public abstract void update(int delta);

    /**
     * Draws to the display.
     */
    public abstract void render();

    /**
     * Called when this State is being left.
     */
    public void finish() {
        // Perform any cleanup here
    }

}
