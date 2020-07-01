package com.danjb.engine.application;

/**
 * Class responsible for managing the lifecycle of the game.
 *
 * @author Dan Bryce
 */
public abstract class Application implements StateContext {

    /**
     * Flag to tell the game to exit cleanly.
     */
    protected boolean exiting;

    /**
     * Status code that was used to end the game.
     */
    protected int exitStatus;

    /**
     * The current State.
     */
    protected State state;

    /**
     * Input due to be processed.
     */
    protected Input input;

    /**
     * Starts the game loop and runs until the game exits.
     *
     * @param initialState
     */
    public final void start(State initialState) {
        init();
        changeState(initialState);
        run();
        exit();
    }

    /**
     * Initialises our Application before the game loop is started.
     */
    protected void init() {
        input = createInput();
    }

    /**
     * Creates the class that will hold our input.
     *
     * @return
     */
    protected Input createInput() {
        return new Input();
    }

    /**
     * Runs the game loop until the Application is exited.
     */
    protected void run() {
        while (!exiting) {
            int delta = calculateDelta();
            tick(delta);
            yield();
        }
    }

    /**
     * Calculates the delta value for the current frame, that is, how much time
     * has elapsed since the last tick.
     *
     * @return
     */
    protected abstract int calculateDelta();

    /**
     * Yields the Thread until the next frame is due.
     */
    protected abstract void yield();

    /**
     * Processes and renders the current frame.
     *
     * @param delta Milliseconds elapsed since the last tick.
     */
    protected void tick(int delta) {
        pollInput();
        update(delta);
        render();
        input.consume();
    }

    /**
     * Polls user input for the current frame.
     *
     * <p>This is called at the start of each frame.
     */
    protected abstract void pollInput();

    /**
     * Updates the game using the given delta value.
     *
     * <p>This is called after polling input, and before rendering.
     *
     * @param delta Milliseconds elapsed since the last tick.
     */
    protected void update(int delta) {
        state.update(delta);
    }

    /**
     * Renders the current frame.
     *
     * <p>This is called at the end of each frame.
     */
    protected abstract void render();

    /**
     * Causes the Application to exit after processing the current frame.
     *
     * @param exitStatus
     */
    public void requestExit(int exitStatus) {
        exiting = true;
        this.exitStatus = exitStatus;
    }

    /**
     * Exits the game cleanly.
     */
    protected void exit() {

        Logger.get().log("Exiting with status: %d", exitStatus);

        // Clean up the current State
        if (state != null) {
            state.destroy();
        }

        System.exit(exitStatus);
    }

    /**
     * Changes the current State.
     *
     * @param newState
     */
    @Override
    public void changeState(State newState) {
        if (state != null) {
            state.destroy();
        }
        state = newState;
        state.init();
    }

    /**
     * Gets the current State.
     *
     * @return
     */
    @Override
    public State getState() {
        return state;
    }

    /**
     * Gets the Input created during initialisation.
     *
     * @return
     */
    public Input getInput() {
        return input;
    }

}
