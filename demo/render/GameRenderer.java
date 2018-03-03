package render;

import java.awt.Graphics2D;
import java.util.Collection;

import game.Camera;
import game.EntityGraphic;
import game.GameState;
import game.Level;
import game.Logic;
import game.TileGraphic;
import game.TileLayer;
import game.entities.Entity;
import game.physics.Hitbox;
import game.tiles.Tile;
import launcher.Display;

public class GameRenderer {

    private Display gamePanel;
    
    private Logic logic;
    
    private Camera camera;
    
    private Graphics2D gfx;

    public GameRenderer(Display gamePanel, Logic logic, Camera camera) {
        this.gamePanel = gamePanel;
        this.logic = logic;
        this.camera = camera;
    }

    public void render(GameState game) {
        gfx = (Graphics2D) gamePanel.getRenderTarget();

        if (gfx != null) {
            renderLevel(logic.getLevel());
            renderEntities(logic.getEntities().values());
        }
    }

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
                renderTile(x, y, tiles);
            }
        }
    }

    private void renderTile(int x, int y, int[][] tiles) {
        
        int tileId = tiles[x][y];
        Tile tile = logic.getTile(tileId);
        TileGraphic tileGfx = (TileGraphic) tile.getComponent(TileGraphic.KEY);
        
        if (tileGfx != null) {
            gfx.setColor(tileGfx.getColour());
            
            int drawX = gamePanel.worldToScreenX(camera, x * Tile.WIDTH);
            int drawY = gamePanel.worldToScreenY(camera, y * Tile.HEIGHT);
            int width = gamePanel.worldToScreen(Tile.WIDTH);
            int height = gamePanel.worldToScreen(Tile.HEIGHT);
            
            gfx.fillRect(drawX, drawY, width, height);
        }
    }

    private void renderEntities(Collection<Entity> entities) {
        for (Entity entity : entities) {
            renderEntity(entity);
        }
    }

    private void renderEntity(Entity entity) {

        EntityGraphic entityGfx = 
                (EntityGraphic) entity.getComponent(EntityGraphic.KEY);

        if (entityGfx != null) {
            gfx.setColor(entityGfx.getColour());

            Hitbox hitbox = entity.getHitbox();
            int drawX = gamePanel.worldToScreenX(camera, hitbox.getLeft());
            int drawY = gamePanel.worldToScreenY(camera, hitbox.getTop());
            int width = gamePanel.worldToScreen(hitbox.getWidth());
            int height = gamePanel.worldToScreen(hitbox.getHeight());
            
            gfx.fillRect(drawX, drawY, width, height);
        }
    }

}
