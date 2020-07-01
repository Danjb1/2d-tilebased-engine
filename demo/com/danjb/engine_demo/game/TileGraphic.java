package com.danjb.engine_demo.game;

import java.awt.Color;

import com.danjb.engine.game.tiles.TileComponent;

/**
 * Component that can be attached to Tiles to give them a colour.
 *
 * @author Dan Bryce
 */
public class TileGraphic extends TileComponent {

    public static final String KEY = "gfx";

    private Color colour;

    public TileGraphic(Color colour) {
        super(KEY);

        this.colour = colour;
    }

    public Color getColour() {
        return colour;
    }

}
