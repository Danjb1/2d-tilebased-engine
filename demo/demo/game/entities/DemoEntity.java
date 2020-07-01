package demo.game.entities;

import engine.game.entities.Entity;

public class DemoEntity extends Entity {

    public static final int TYPE_PLAYER = 0;

    /**
     * This Entity's type.
     */
    protected int type;

    public DemoEntity(float x, float y, float width, float height, int type) {
        super(x, y, width, height);

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
