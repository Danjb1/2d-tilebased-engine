package game;

import java.awt.Color;

import game.Component;
import game.tiles.Tile;

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
