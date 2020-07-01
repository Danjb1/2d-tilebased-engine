package com.danjb.engine_demo.main;

import com.danjb.engine.application.Logger;
import com.danjb.engine.game.Level;
import com.danjb.engine.game.Logic;
import com.danjb.engine.game.TileLayer;
import com.danjb.engine_demo.application.DemoApplication;
import com.danjb.engine_demo.game.GameState;

public class Main {

    /**
     * Entry point for the application.
     *
     * @param args
     */
    public static void main(String[] args) {

        try {

            DemoApplication app = new DemoApplication();

            Logic logic = new Logic();
            Level level = createLevel();
            logic.setLevel(level);
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
        TileLayer foreground = new TileLayer(new int[40][30]);

        // Generate a floor
        for (int x = 0; x < foreground.getNumTilesX(); x++) {
            foreground.setTile(x, foreground.getNumTilesY() - 1, 1);
        }

        // Generate some random blocks
        for (int i = 0; i < 50; i++) {
            int x = (int) (Math.random() * foreground.getNumTilesX());
            int y = (int) (Math.random() * foreground.getNumTilesY());
            foreground.setTile(x, y, 1);
        }

        return new Level(foreground);
    }

}
