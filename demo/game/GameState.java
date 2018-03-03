package game;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import game.Camera;
import game.GameUtils.DirectionX;
import game.Logic;
import game.entities.Player;
import launcher.DemoLauncher;
import launcher.Input;
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
     * The player.
     */
    private Player player;
    
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
        
        // Add our Player
        player = new Player(GameUtils.worldUnits(5), GameUtils.worldUnits(5));
        new EntityGraphic(Color.RED).attachTo(player);
        logic.addEntity(player);
        
        // Track the Player
        camera.trackEntity(player);
    }
    
    @Override
    public void processInput(Input input) {
        
        // Horizontal movement
        if (input.isKeyDown(KeyEvent.VK_A)) {
            player.setDir(DirectionX.LEFT);
        } else if (input.isKeyDown(KeyEvent.VK_D)) {
            player.setDir(DirectionX.RIGHT);
        } else {
            player.setDir(DirectionX.NONE);
        }
        
        // Jumping
        if (input.isKeyDown(KeyEvent.VK_SPACE)) {
            player.jump();
        }
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
