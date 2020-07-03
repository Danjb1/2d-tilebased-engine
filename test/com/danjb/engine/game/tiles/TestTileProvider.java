package com.danjb.engine.game.tiles;

import com.danjb.engine.game.level.TileProvider;

public class TestTileProvider extends TileProvider {

    public TestTileProvider() {
        addStandardTileTypes();
    }

    private void addStandardTileTypes() {
        addTileType(0, new Air(PhysicsTile.ID_AIR));
        addTileType(0, new SolidBlock(PhysicsTile.ID_SOLID_BLOCK));
    }

}
