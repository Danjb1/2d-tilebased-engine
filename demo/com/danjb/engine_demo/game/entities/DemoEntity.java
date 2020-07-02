package com.danjb.engine_demo.game.entities;

import com.danjb.engine.game.entities.Entity;

public abstract class DemoEntity extends Entity {

    public static final int TYPE_PLAYER = 0;

    /**
     * This Entity's type.
     */
    protected int type;

    public DemoEntity(int type) {
        this.type = type;
    }

    /**
     * Gets this Entity's type identifier.
     *
     * @return
     */
    public int getType() {
        return type;
    }

}
