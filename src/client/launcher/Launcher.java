package client.launcher;

import client.game.GameState;
import client.game.Level;
import client.game.Logic;
import client.game.TileLayer;

/**
 * Class responsible for initialising and managing the application as a whole.
 *
 * @author Dan Bryce
 */
public class Launcher {

    ////////////////////////////////////////////////////////////////////////////

    /**
     * Entry point for the application.
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            
            Launcher launcher = new Launcher();
            
            // Create demo level
            TileLayer foreground = new TileLayer(new int[][] {
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            });
            Level level = new Level(foreground);
            
            Logic logic = new Logic(level);
            launcher.setState(new GameState(launcher, logic));
            launcher.start();
            
        } catch (Exception ex) {
            Logger.log(ex);
            System.exit(-1);
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Width of the display, in pixels.
     */
    public static final int DISPLAY_WIDTH = 800;

    /**
     * Height of the display, in pixels.
     */
    public static final int DISPLAY_HEIGHT = 600;

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
    private State state;

    /**
     * Constructs the Launcher.
     */
    private Launcher() {
        createDisplay(DISPLAY_WIDTH, DISPLAY_HEIGHT);
    }

    /**
     * Creates the display.
     */
    private void createDisplay(int width, int height) {
        // TODO
    }

    /**
     * Starts the game loop and runs until the game exits.
     */
    private void start() {

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
            } catch (Exception ex){
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
        state.pollInput();
        state.update(delta);
        state.render();
    }

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
        if (state != null){
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
