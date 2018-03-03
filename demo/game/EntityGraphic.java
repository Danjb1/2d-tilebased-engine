package game;

import java.awt.Color;

import game.entities.Entity;

/**
 * Component that can be attached to Entities to give them a colour.
 *
 * @author Dan Bryce
 */
public class EntityGraphic extends Component {

    public static final String KEY = "gfx";

    private Color colour;

    public EntityGraphic(Color colour) {
        super(KEY, Entity.class);

        this.colour = colour;
    }

    public Color getColour() {
        return colour;
    }

}
