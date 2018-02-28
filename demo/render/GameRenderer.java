package render;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import game.Camera;
import game.Level;
import game.Logic;
import game.TileLayer;
import game.tiles.Tile;
import launcher.GamePanel;
import launcher.GameState;
import launcher.TileGraphic;

public class GameRenderer {

    private GamePanel gamePanel;
    
    private Logic logic;
    
    private Camera camera;
    
    private Graphics2D gfx;

    public GameRenderer(GamePanel gamePanel, Logic logic, Camera camera) {
        this.gamePanel = gamePanel;
        this.logic = logic;
        this.camera = camera;
    }

    public void render(GameState game) {
        gfx = (Graphics2D) gamePanel.getRenderTarget();
        renderLevel(logic.getLevel());
    }

    private void renderLevel(Level level) {

        TileLayer foreground = level.getForeground();
        int[][] tiles = foreground.getTiles();
        
        // Determine which tiles are on-screen
        Rectangle2D.Float cameraTarget = camera.getTarget();
        int minTileX = Math.max((int) (cameraTarget.x / Tile.WIDTH), 0);
        int minTileY = Math.max((int) (cameraTarget.y / Tile.HEIGHT), 0);
        int maxTileX = Math.min(minTileX + camera.getNumVisibleTilesX(),
                level.getNumTilesX() - 1);
        int maxTileY = Math.min(minTileY + camera.getNumVisibleTilesY(),
                level.getNumTilesY() - 1);
        
        float drawStartX = minTileX * Tile.WIDTH;
        float drawX = drawStartX;
        float drawY = minTileY * Tile.HEIGHT;

        for (int y = minTileY; y <= maxTileY; y++){
            for (int x = minTileX; x <= maxTileX; x++){
                renderTile(x, y, drawX, drawY, tiles);
                drawX += Tile.WIDTH;
            }
            
            // Next row
            drawX = drawStartX;
            drawY += Tile.HEIGHT;
        }
    }

    private void renderTile(int x, int y, float drawX, float drawY,
            int[][] tiles) {
        
        int tileId = tiles[x][y];
        Tile tile = logic.getTile(tileId);
        TileGraphic tileGfx = (TileGraphic) tile.getComponent(TileGraphic.KEY);
        
        if (gfx != null) {
            gfx.setColor(tileGfx.getColour());
            gfx.fillRect(
                    gamePanel.worldToScreen(drawX),
                    gamePanel.worldToScreen(drawY),
                    gamePanel.worldToScreen(Tile.WIDTH),
                    gamePanel.worldToScreen(Tile.HEIGHT));
        }
    }

}
