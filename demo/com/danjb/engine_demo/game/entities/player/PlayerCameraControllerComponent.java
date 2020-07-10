package com.danjb.engine_demo.game.entities.player;

import com.danjb.engine.game.ComponentEvent;
import com.danjb.engine.game.Logic;
import com.danjb.engine.game.camera.BasicCameraController;
import com.danjb.engine.game.camera.Camera;
import com.danjb.engine.game.entities.Entity;
import com.danjb.engine.game.entities.EntityComponent;
import com.danjb.engine.game.entities.EntityTeleported;

public class PlayerCameraControllerComponent extends EntityComponent {

    private static final String KEY = "player_camera_controller";

    private Camera camera;
    private BasicCameraController controller;

    public PlayerCameraControllerComponent(Camera camera) {
        super(KEY);

        this.camera = camera;
    }

    @Override
    public void onAttach(Entity parent, Logic logic) {
        super.onAttach(parent, logic);

        controller = new BasicCameraController(
                camera, hitbox.centreX(), hitbox.centreY());
        camera.setController(controller);
        camera.teleportToDestination();
    }

    @Override
    public void update(int delta) {
        super.update(delta);

        // Update the target based on the player position
        controller.setTarget(hitbox.centreX(), hitbox.centreY());
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
