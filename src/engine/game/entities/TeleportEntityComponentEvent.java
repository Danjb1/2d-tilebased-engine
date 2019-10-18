package engine.game.entities;

import engine.game.ComponentEvent;

public class TeleportEntityComponentEvent extends ComponentEvent {

    public static final String KEY = "event_teleport";

    public TeleportEntityComponentEvent() {
        super(KEY);
    }

}
