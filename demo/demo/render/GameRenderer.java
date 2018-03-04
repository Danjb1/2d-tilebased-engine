package demo.render;

import java.awt.Graphics2D;
import java.util.Collection;

import demo.game.EntityGraphic;
import demo.game.GameState;
import demo.game.TileGraphic;
import demo.launcher.Display;
import engine.game.Camera;
import engine.game.GameUtils;
import engine.game.Level;
import engine.game.Logic;
import engine.game.TileLayer;
import engine.game.entities.Entity;
import engine.game.physics.Hitbox;
import engine.game.tiles.Tile;

/**
 * Class responsible for rendering the GameState.
 *
 * @author Dan Bryce
 */
public class GameRenderer {

    /**
     * Display used to retrieve the graphics context for rendering.
     */
    private Display display;

    /**
     * Logic to render.
     */
    private Logic logic;

    /**
     * Camera used to determine the area of the world to render.
     */
    private Camera camera;

    /**
     * Graphics context to which we will render.
     */
    private Graphics2D gfx;

    /**
     * Scale at which to draw, based on the width of the display relative to the
     * width of the visible portion of the world.
     */
    private float drawScale;

    /**
     * Creates a GameRenderer.
     *
     * @param display
     * @param logic
     * @param camera
     */
    public GameRenderer(Display display, Logic logic, Camera camera) {
        this.display = display;
        this.logic = logic;
        this.camera = camera;

        drawScale = (float) display.getWidth() / Camera.VISIBLE_WORLD_WIDTH;
    }

    /**
     * Calculates the render position of the given x co-ordinate.
     *
     * <p>This takes into account the camera position.
     *
     * @param worldUnits
     * @return
     */
    public int worldToDisplayX(float worldUnits) {
        return worldToDisplay(worldUnits - camera.getTarget().x);
    }

    /**
     * Calculates the render position of the given y co-ordinate.
     *
     * <p>This takes into account the camera position.
     *
     * @param worldUnits
     * @return
     */
    public int worldToDisplayY(float worldUnits) {
        return worldToDisplay(worldUnits - camera.getTarget().y);
    }

    /**
     * Converts the given world units to display units.
     *
     * <p>This should be used for dimensions, or co-ordinates relative to the
     * camera position.
     *
     * @param worldUnits
     * @return
     */
    public int worldToDisplay(float worldUnits) {
        return (int) (GameUtils.worldToPx(worldUnits) * drawScale);
    }

    /**
     * Renders the given GameState.
     *
     * @param game
     */
    public void render(GameState game) {
        gfx = display.getRenderTarget();

        if (gfx != null) {
            renderLevel(logic.getLevel());
            renderEntities(logic.getEntities().values());
        }
    }

    /**
     * Renders the given Level.
     *
     * @param level
     */
    private void renderLevel(Level level) {

        TileLayer foreground = level.getForeground();
        int[][] tiles = foreground.getTiles();

        // Determine which tiles are on-screen
        int minTileX = camera.getFirstVisibleTileX();
        int minTileY = camera.getFirstVisibleTileY();
        int maxTileX = camera.getLastVisibleTileX(minTileX);
        int maxTileY = camera.getLastVisibleTileY(minTileY);

        for (int y = minTileY; y <= maxTileY; y++){
            for (int x = minTileX; x <= maxTileX; x++){
                renderTile(x, y, tiles[x][y]);
            }
        }
    }

    /**
     * Renders a Tile at the given level co-ordinates.
     *
     * @param x
     * @param y
     * @param tileId
     */
    private void renderTile(int x, int y, int tileId) {

        Tile tile = logic.getTile(tileId);
        TileGraphic tileGfx = (TileGraphic) tile.getComponent(TileGraphic.KEY);

        if (tileGfx != null) {
            gfx.setColor(tileGfx.getColour());

            int drawX = worldToDisplayX(x * Tile.WIDTH);
            int drawY = worldToDisplayY(y * Tile.HEIGHT);
            int width = worldToDisplay(Tile.WIDTH);
            int height = worldToDisplay(Tile.HEIGHT);

            gfx.fillRect(drawX, drawY, width, height);
        }
    }

    /**
     * Renders the given collection of Entities.
     *
     * @param entities
     */
    private void renderEntities(Collection<Entity> entities) {
        for (Entity entity : entities) {
            renderEntity(entity);
        }
    }

    /**
     * Renders the given Entity.
     *
     * @param entity
     */
    private void renderEntity(Entity entity) {

        EntityGraphic entityGfx =
                (EntityGraphic) entity.getComponent(EntityGraphic.KEY);

        if (entityGfx != null) {
            gfx.setColor(entityGfx.getColour());

            Hitbox hitbox = entity.getHitbox();
            int drawX = worldToDisplayX(hitbox.getLeft());
            int drawY = worldToDisplayY(hitbox.getTop());
            int width = worldToDisplay(hitbox.getWidth());
            int height = worldToDisplay(hitbox.getHeight());

            gfx.fillRect(drawX, drawY, width, height);
        }
    }

}
