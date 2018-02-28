package game;

import java.awt.Rectangle;

import launcher.Launcher;
import launcher.State;

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
        
        this.logic = logic;

        // Initialise Camera
        Rectangle viewport = new Rectangle(0, 0, launcher.getDisplayWidth(), launcher.getDisplayHeight());
        camera = new Camera(viewport, launcher.getDisplayWidth(),
                logic.getLevel());
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
