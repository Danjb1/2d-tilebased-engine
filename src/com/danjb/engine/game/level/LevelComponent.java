package com.danjb.engine.game.level;

import com.danjb.engine.game.Component;

public class LevelComponent extends Component {

    protected Level level;

    public LevelComponent(String key) {
        super(key);
    }

    /**
     * Called when this Component is successfully attached to a Level.
     *
     * @param parent
     */
    public void onAttach(Level parent) {
        level = parent;
    }

}
