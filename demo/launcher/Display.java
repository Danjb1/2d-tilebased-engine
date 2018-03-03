package launcher;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import game.Camera;
import game.GameUtils;

public class Display extends JPanel {

    /**
     * Auto-generated.
     */
    private static final long serialVersionUID = 1L;
    
    private DemoLauncher launcher;

    private Graphics2D gfx;

    private float pixelScale;

    public Display(DemoLauncher launcher, int width, int height) {
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
    
    public int worldToScreenX(Camera camera, float worldUnits) {
        
        
        Rectangle2D.Float cameraTarget = camera.getTarget();
        worldUnits -= cameraTarget.x;

        return worldToScreen(worldUnits);
    }

    public int worldToScreenY(Camera camera, float worldUnits) {
        
        Rectangle2D.Float cameraTarget = camera.getTarget();
        worldUnits -= cameraTarget.y;

        return worldToScreen(worldUnits);
    }

    public int worldToScreen(float worldUnits) {
        return (int) (GameUtils.worldToPx(worldUnits) * pixelScale);
    }

}
