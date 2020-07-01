package com.danjb.engine.application;

public abstract class State {

    protected Application app;

    /**
     * Constructs a State.
     *
     * @param app
     */
    public State(Application app) {
        this.app = app;
    }

    /**
     * Called when entering this State.
     */
    public void init() {
        // To be overridden as required
    }

    /**
     * Called when exiting this State.
     */
    public void destroy() {
        // To be overridden as required
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

}
