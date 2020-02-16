package engine.game.tiles;

import engine.game.ComponentStore;
import engine.game.GameUtils;
import engine.game.physics.Physics;

/**
 * Class representing a Tile type.
 *
 * <p>Note that an instance of this class does not represent a single tile in
 * the level, but is shared by all tiles of that type.
 *
 * @author Dan Bryce
 */
public abstract class Tile {

    /**
     * The size (width or height) of one Tile, in world units.
     *
     * <p>Note that this is used to define the notion of world units (see
     * {@link GameUtils#worldUnits}). We define 1 Tile to be 1 square world
     * unit, for simplicity.
     */
    public static final float SIZE = 1;

    /**
     * Width of one Tile, in world units.
     */
    public static final float WIDTH = SIZE;

    /**
     * Height of one Tile, in world units.
     */
    public static final float HEIGHT = SIZE;

    /**
     * {@link TileComponent}s attached to this Tile.
     */
    public ComponentStore<TileComponent> components = new ComponentStore<>();

    /**
     * This Tile's unique identifier.
     *
     * <p>Note that 2 Tiles of the same type could have a different identifier.
     * For example, we might have 2 different solid block tiles that behave
     * the same, but have different graphical components.
     */
    protected int id;

    /**
     * Creates a Tile with the given ID.
     *
     * @param id
     */
    public Tile(int id) {
        this.id = id;
    }

    /**
     * Attaches a TileComponent.
     *
     * <p>Results in a callback to {@link TileComponent#onAttach}.
     *
     * @param component
     */
    public void attach(TileComponent component) {
        components.add(component);
        component.onAttach(this);
    }

    /**
     * Gets this Tile's unique identifier.
     *
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the left edge of the Tile containing the given x co-ordinate, in
     * world units.
     *
     * @param x Position in world units.
     * @return Tile edge in world units.
     */
    public static int getLeft(float x) {
        return (int) (x / WIDTH);
    }

    /**
     * Gets the right edge of the Tile containing the given x co-ordinate, in
     * world units.
     *
     * @param x Position in world units.
     * @return Tile edge in world units.
     */
    public static float getRight(float x) {
        return x < 0 ? 0 :
                getLeft(x) + WIDTH - Physics.SMALLEST_DISTANCE;
    }

    /**
     * Gets the top edge of the Tile containing the given y co-ordinate, in
     * world units.
     *
     * @param y Position in world units.
     * @return Tile edge in world units.
     */
    public static int getTop(float y) {
        return (int) (y / HEIGHT);
    }

    /**
     * Gets the bottom edge of the Tile containing the given y co-ordinate, in
     * world units.
     *
     * @param y Position in world units.
     * @return Tile edge in world units.
     */
    public static float getBottom(float y) {
        return y < 0 ? 0 :
                getTop(y) + HEIGHT - Physics.SMALLEST_DISTANCE;
    }

    /**
     * Gets the x-index of the Tile containing the given x-position.
     *
     * @param x Position in world units.
     * @return Index of the containing Tile within the tile grid.
     */
    public static int getTileX(float x) {
        return x < 0 ? -1 : (int) (x / WIDTH);
    }

    /**
     * Gets the y-index of the Tile containing the given y-position.
     *
     * @param y Position in world units.
     * @return Index of the containing Tile within the tile grid.
     */
    public static int getTileY(float y) {
        return y < 0 ? -1 : (int) (y / HEIGHT);
    }

}
