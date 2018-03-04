package demo.game;

import java.awt.Color;

import engine.game.Component;
import engine.game.tiles.Tile;

/**
 * Component that can be attached to Tiles to give them a colour.
 *
 * @author Dan Bryce
 */
public class TileGraphic extends Component {

    public static final String KEY = "gfx";

    private Color colour;

    public TileGraphic(Color colour) {
        super(KEY, Tile.class);

        this.colour = colour;
    }

    public Color getColour() {
        return colour;
    }

}
