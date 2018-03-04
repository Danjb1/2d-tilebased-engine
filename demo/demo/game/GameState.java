package demo.game;

import java.awt.Color;
import java.awt.event.KeyEvent;

import demo.game.entities.Player;
import demo.launcher.DemoLauncher;
import demo.render.GameRenderer;
import engine.game.Camera;
import engine.game.GameUtils;
import engine.game.Logic;
import engine.game.GameUtils.DirectionX;
import engine.game.tiles.ForegroundTile;
import engine.launcher.Input;
import engine.launcher.State;

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
     * The player being controlled.
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
        new TileGraphic(Color.BLACK).attachTo(
                logic.getTile(ForegroundTile.ID_AIR));
        new TileGraphic(Color.WHITE).attachTo(
                logic.getTile(ForegroundTile.ID_SOLID_BLOCK));

        // Initialise Camera
        double aspectRatio = (double)
                launcher.getDisplayWidth() / launcher.getDisplayHeight();
        camera = new Camera(1.0, aspectRatio, logic.getLevel());

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
            player.setMovementDirection(DirectionX.LEFT);
        } else if (input.isKeyDown(KeyEvent.VK_D)) {
            player.setMovementDirection(DirectionX.RIGHT);
        } else {
            player.setMovementDirection(DirectionX.NONE);
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
