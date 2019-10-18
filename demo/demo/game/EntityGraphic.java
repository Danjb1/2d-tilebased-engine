package demo.game;

import java.awt.Color;

import engine.game.entities.EntityComponent;

/**
 * Component that can be attached to Entities to give them a colour.
 *
 * @author Dan Bryce
 */
public class EntityGraphic extends EntityComponent {

    public static final String KEY = "gfx";

    private Color colour;

    public EntityGraphic(Color colour) {
        super(KEY);

        this.colour = colour;
    }

    public Color getColour() {
        return colour;
    }

}
