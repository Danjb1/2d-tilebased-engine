package com.danjb.engine_demo.game.tiles;

import com.danjb.engine.game.level.TileProvider;
import com.danjb.engine.game.tiles.Air;
import com.danjb.engine.game.tiles.PhysicsTile;
import com.danjb.engine.game.tiles.SolidBlock;

public class DemoTileProvider extends TileProvider {

    public DemoTileProvider() {
        addStandardTileTypes();
    }

    private void addStandardTileTypes() {
        addTileType(TileLayers.DEFAULT, new Air(PhysicsTile.ID_AIR));
        addTileType(TileLayers.DEFAULT, new SolidBlock(PhysicsTile.ID_SOLID_BLOCK));
    }

}
