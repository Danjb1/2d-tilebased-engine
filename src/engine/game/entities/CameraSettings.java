package engine.game.entities;

import engine.game.ComponentEvent;

public class CameraSettings extends EntityComponent {

    public static final String KEY = "camera_settings";

    public CameraSettings() {
        super(KEY);
    }

    public float limitDx(float dx) {
        // No limit, by default
        return dx;
    }

    public float limitDy(float dy) {
        // No limit, by default
        return dy;
    }

    public float getTargetOffsetX() {
        // No offset, by default
        return 0;
    }

    public float getTargetOffsetY() {
        // No offset, by default
        return 0;
    }

    public void entityTeleported() {
        // To be overridden as required
    }

    @Override
    public void notify(ComponentEvent event) {
        if (event.getKey() == TeleportEntityComponentEvent.KEY) {
            entityTeleported();
        }
    }

}
