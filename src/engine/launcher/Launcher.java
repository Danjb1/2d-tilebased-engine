package engine.launcher;

/**
 * Class responsible for initialising and managing the application as a whole.
 *
 * @author Dan Bryce
 */
public abstract class Launcher {

    /**
     * FPS at which to run the game.
     */
    public static final int FPS = 60;

    /**
     * Milliseconds per frame (approx).
     */
    private static final int MS_PER_FRAME = 1000 / FPS;

    /**
     * Time at which the last frame took place, in ms.
     */
    private long lastFrame;

    /**
     * Flag to tell the game to exit cleanly.
     */
    private boolean exiting;

    /**
     * The current State.
     */
    protected State state;

    /**
     * Input due to be processed.
     */
    protected Input input = new Input();

    /**
     * Constructs the Launcher.
     */
    protected Launcher(int width, int height) {
        createDisplay(width, height);
    }

    /**
     * Creates the display.
     */
    protected abstract void createDisplay(int width, int height);

    /**
     * Gets the width of the display, in pixels.
     *
     * @return
     */
    public abstract int getDisplayWidth();

    /**
     * Gets the height of the display, in pixels.
     *
     * @return
     */
    public abstract int getDisplayHeight();

    /**
     * Starts the game loop and runs until the game exits.
     */
    public void start() {

        // Initialise some values before the loop starts
        int status = 0;
        lastFrame = System.currentTimeMillis();

        // The game loop!
        while (!exiting) {

            // Calculate time elapsed since last frame
            long frameStart = System.currentTimeMillis();
            int delta = (int) (frameStart - lastFrame);
            lastFrame = frameStart;

            try {
                tick(delta);
            } catch (Exception ex) {
                // Game has crashed
                Logger.log(ex);
                status = -1;
                break;
            }

            // Calculate time until next frame is "due"
            long frameEnd = System.currentTimeMillis();
            int timeTaken = (int) (frameEnd - frameStart);
            int sleepTime = MS_PER_FRAME - timeTaken;
            if (sleepTime < 1) {
                Logger.log("Running behind!");
                sleepTime = 1;
            }

            // Sleep until next frame
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException ex) {
                Logger.log(ex);
            }
        }
        
        exit(status);
    }

    /**
     * Processes the current State.
     *
     * @param delta Milliseconds elapsed since the last tick.
     */
    private void tick(int delta) {
        pollInput();
        state.update(delta);
        render();
    }

    /**
     * Polls user input and feeds it to the current State.
     */
    protected void pollInput() {
        input.prepareForNextFrame();
        state.processInput(input);
    }

    /**
     * Renders the current State.
     */
    protected abstract void render();

    /**
     * Causes the Launcher to exit after processing the current frame.
     */
    public void requestExit() {
        exiting = true;
    }

    /**
     * Exits the game cleanly.
     *
     * @param status
     */
    private void exit(int status) {

        Logger.log("Exiting with status: %d", status);

        // Clean up the current State
        if (state != null) {
            state.finish();
        }

        System.exit(status);
    }

    /**
     * Gets the current State.
     *
     * @return
     */
    public State getState() {
        return state;
    }

    /**
     * Changes the current State.
     *
     * @param state
     */
    public void setState(State state) {
        this.state = state;
    }

}
