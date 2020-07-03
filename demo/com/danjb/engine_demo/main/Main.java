package com.danjb.engine_demo.main;

import com.danjb.engine.application.Logger;
import com.danjb.engine.application.Logger.SimpleLogger;
import com.danjb.engine.game.Logic;
import com.danjb.engine.game.level.Level;
import com.danjb.engine.game.level.TileLayer;
import com.danjb.engine.game.level.TileProvider;
import com.danjb.engine_demo.application.DemoApplication;
import com.danjb.engine_demo.game.GameState;
import com.danjb.engine_demo.game.tiles.DemoTileProvider;
import com.danjb.engine_demo.game.tiles.TileLayers;

public class Main {

    /**
     * Entry point for the application.
     *
     * @param args
     */
    public static void main(String[] args) {

        Logger.use(new SimpleLogger());

        try {
            DemoApplication app = new DemoApplication();

            Level level = createLevel();
            TileProvider tileProvider = new DemoTileProvider();
            Logic logic = new Logic(tileProvider);
            logic.changeLevel(level);
            app.start(new GameState(app, logic));

        } catch (Exception ex) {
            Logger.get().log(ex);
            System.exit(-1);
        }
    }

    /**
     * Creates a demo level.
     *
     * @return
     */
    private static Level createLevel() {
        TileLayer layer =
                new TileLayer(TileLayers.DEFAULT, new int[40][30]);

        // Generate a floor
        for (int x = 0; x < layer.getNumTilesX(); x++) {
            layer.setTile(x, layer.getNumTilesY() - 1, 1);
        }

        // Generate some random blocks
        for (int i = 0; i < 50; i++) {
            int x = (int) (Math.random() * layer.getNumTilesX());
            int y = (int) (Math.random() * layer.getNumTilesY());
            layer.setTile(x, y, 1);
        }

        return new Level(layer);
    }

}
