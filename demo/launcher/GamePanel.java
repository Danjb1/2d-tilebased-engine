package launcher;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import game.Camera;
import game.GameUtils;

public class GamePanel extends JPanel {

    /**
     * Auto-generated.
     */
    private static final long serialVersionUID = 1L;
    
    private DemoLauncher launcher;

    private Graphics2D gfx;

    private float pixelScale;

    public GamePanel(DemoLauncher launcher, int width, int height) {
        this.launcher = launcher;
        
        setPreferredSize(new Dimension(width, height));
        
        pixelScale = (float) width / Camera.VISIBLE_WORLD_WIDTH;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        this.gfx = (Graphics2D) g;
        
        State currentState = launcher.getState();
        if (currentState != null) {
            currentState.render();
        }
    }
    
    public Graphics2D getRenderTarget() {
        return gfx;
    }
    
    public int worldToScreen(float worldUnits) {
        return (int) (GameUtils.worldToPx(worldUnits) * pixelScale);
    }

}
