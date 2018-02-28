package launcher;

import java.awt.Color;
import java.awt.Rectangle;

import game.Camera;
import game.Logic;
import launcher.State;
import render.GameRenderer;

/**
 * State that runs the actual game.
 *
 * @author Dan Bryce
 */
public class GameState extends State {

    /**
     * Renderer used to render this State.
     */
    private GameRenderer renderer;
    
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
    public GameState(DemoLauncher launcher, Logic logic) {
        super(launcher);
        
        this.logic = logic;
        
        // Initialise Tile graphics
        new TileGraphic(Color.BLACK).attachTo(logic.getTile(0));
        new TileGraphic(Color.WHITE).attachTo(logic.getTile(1));

        // Initialise Camera
        Rectangle viewport = new Rectangle(0, 0, launcher.getDisplayWidth(),
                launcher.getDisplayHeight());
        camera = new Camera(viewport, launcher.getDisplayWidth(),
                logic.getLevel());

        // Initialise GameRenderer
        renderer = new GameRenderer(launcher.getGamePanel(), logic, camera);
    }

    @Override
    public void update(int delta) {
        logic.update(delta);
        camera.update(delta);
    }

    @Override
    public void render() {
        renderer.render(this);
    }
    
    public Camera getCamera() {
        return camera;
    }
    
    public Logic getLogic() {
        return logic;
    }

}
