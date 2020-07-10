package com.danjb.engine_demo.game;

import java.awt.Color;
import java.awt.event.KeyEvent;

import com.danjb.engine.application.Application;
import com.danjb.engine.application.Input;
import com.danjb.engine.application.State;
import com.danjb.engine.game.Logic;
import com.danjb.engine.game.camera.Camera;
import com.danjb.engine.game.level.TileProvider;
import com.danjb.engine.game.tiles.PhysicsTile;
import com.danjb.engine.util.Directions.DirectionX;
import com.danjb.engine.util.GameUtils;
import com.danjb.engine_demo.application.DemoApplication;
import com.danjb.engine_demo.game.entities.EntityGraphic;
import com.danjb.engine_demo.game.entities.player.Player;
import com.danjb.engine_demo.game.entities.player.PlayerCameraControllerComponent;
import com.danjb.engine_demo.game.tiles.TileLayers;
import com.danjb.engine_demo.render.GameRenderer;

/**
 * State that runs the actual game.
 *
 * @author Dan Bryce
 */
public class GameState extends State {

    private static final float VISIBLE_WORLD_WIDTH = GameUtils.worldUnits(25);

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
     * @param app
     * @param logic
     */
    public GameState(Application app, Logic logic) {
        super(app);

        this.logic = logic;
    }

    @Override
    public void init() {
        super.init();

        DemoApplication demoApp = (DemoApplication) app;

        // Initialise Tile graphics
        TileProvider tileProvider = logic.getTileProvider();
        tileProvider.getTile(TileLayers.DEFAULT, PhysicsTile.ID_AIR)
                .components
                .add(new TileGraphic(Color.BLACK));
        tileProvider.getTile(TileLayers.DEFAULT, PhysicsTile.ID_SOLID_BLOCK)
                .components
                .add(new TileGraphic(Color.WHITE));

        // Initialise Camera
        float aspectRatio = (float)
                demoApp.getDisplayWidth() / demoApp.getDisplayHeight();
        camera = new Camera(
                VISIBLE_WORLD_WIDTH,
                1.0f,
                aspectRatio,
                logic.getLevel());

        // Initialise GameRenderer
        renderer = new GameRenderer(demoApp.getGamePanel(), logic, camera);

        // Add our Player
        player = new Player();
        logic.addEntity(player,
                GameUtils.worldUnits(5),
                GameUtils.worldUnits(5));
        player.attach(new EntityGraphic(Color.RED));
        player.attach(new PlayerCameraControllerComponent(camera));
    }

    @Override
    public void update(int delta) {
        consumeInput();
        logic.update(delta);
        camera.update(delta);
    }

    private void consumeInput() {

        Input input = app.getInput();

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
