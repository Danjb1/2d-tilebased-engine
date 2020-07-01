package demo.game.entities.player;

import engine.game.ComponentEvent;
import engine.game.Logic;
import engine.game.camera.BasicCameraController;
import engine.game.camera.Camera;
import engine.game.camera.CameraController;
import engine.game.entities.EntityComponent;
import engine.game.entities.EntityTeleported;

public class PlayerCameraControllerComponent extends EntityComponent {

    private static final String KEY = "player_camera_controller";

    private Camera camera;
    private CameraController controller;

    public PlayerCameraControllerComponent(Camera camera) {
        super(KEY);

        this.camera = camera;
    }

    @Override
    public void entityAddedToWorld(Logic logic) {
        controller = new BasicCameraController(entity);
        camera.setController(controller);
        camera.teleportToDestination();
    }

    @Override
    public void notify(ComponentEvent eventBeforeCast) {
        if (eventBeforeCast instanceof EntityTeleported) {
            entityTeleported();
        }
    }

    public void entityTeleported() {
        camera.teleportToDestination();
    }

}
