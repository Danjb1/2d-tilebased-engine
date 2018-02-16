package client.game;

import java.awt.Rectangle;

import client.launcher.Launcher;
import client.launcher.State;

/**
 * State that runs the actual game.
 *
 * @author Dan Bryce
 */
public class GameState extends State {

    /**
     * The game logic to run.
     */
    private Logic logic;
    
    /**
     * The camera that determines the visible region of the world.
     */
    private Camera camera;

    /**
     * Creates a GameState.
     *
     * @param launcher
     */
    public GameState(Launcher launcher, Logic logic) {
        super(launcher);

        // Initialise Camera
        Rectangle viewport = new Rectangle(0, 0, 800, 600);
        camera = new Camera(viewport, Launcher.DISPLAY_WIDTH, logic.getLevel());
    }

    @Override
    public void update(int delta) {
        logic.update(delta);
        camera.update(delta);
    }

    @Override
    public void render() {
        // TODO
    }

}
