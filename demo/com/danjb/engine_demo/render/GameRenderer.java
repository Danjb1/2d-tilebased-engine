package com.danjb.engine_demo.render;

import java.awt.Graphics2D;
import java.util.Collection;

import com.danjb.engine.game.Logic;
import com.danjb.engine.game.camera.Camera;
import com.danjb.engine.game.entities.Entity;
import com.danjb.engine.game.level.Level;
import com.danjb.engine.game.level.TileLayer;
import com.danjb.engine.game.physics.Hitbox;
import com.danjb.engine.game.tiles.Tile;
import com.danjb.engine_demo.application.Display;
import com.danjb.engine_demo.game.GameState;
import com.danjb.engine_demo.game.TileGraphic;
import com.danjb.engine_demo.game.entities.EntityGraphic;
import com.danjb.engine_demo.game.tiles.TileLayers;

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

        drawScale = RenderUtils.pxToWorld(display.getWidth())
                / camera.getVisibleRegion().width;
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
        return worldToDisplay(worldUnits - camera.getVisibleRegion().x);
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
        return worldToDisplay(worldUnits - camera.getVisibleRegion().y);
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
        return (int) (RenderUtils.worldToPx(worldUnits) * drawScale);
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

        TileLayer foreground = level.getDefaultLayer();
        int[][] tiles = foreground.getTiles();

        // Determine which tiles are on-screen
        int minTileX = camera.getFirstVisibleTileX();
        int minTileY = camera.getFirstVisibleTileY();
        int maxTileX = camera.getLastVisibleTileX(minTileX);
        int maxTileY = camera.getLastVisibleTileY(minTileY);

        for (int y = minTileY; y <= maxTileY; y++) {
            for (int x = minTileX; x <= maxTileX; x++) {
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

        Tile tile = logic.getTileProvider().getTile(TileLayers.DEFAULT, tileId);
        TileGraphic tileGfx = (TileGraphic)
                tile.components.get(TileGraphic.KEY);

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

        EntityGraphic entityGfx = (EntityGraphic)
                entity.components.get(EntityGraphic.KEY);

        if (entityGfx != null) {
            gfx.setColor(entityGfx.getColour());

            Hitbox hitbox = entity.hitbox;
            int drawX = worldToDisplayX(hitbox.x);
            int drawY = worldToDisplayY(hitbox.y);
            int width = worldToDisplay(hitbox.width);
            int height = worldToDisplay(hitbox.height);

            gfx.fillRect(drawX, drawY, width, height);
        }
    }

}
