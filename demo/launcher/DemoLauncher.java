package launcher;

import javax.swing.JFrame;

import game.Level;
import game.Logic;
import game.TileLayer;

public class DemoLauncher extends Launcher {

    /**
     * Entry point for the application.
     * 
     * @param args
     */
    public static void main(String[] args) {
        try {
            
            DemoLauncher launcher = new DemoLauncher();
            
            // Create demo level
            TileLayer foreground = new TileLayer(new int[40][30]);
            // Generate some random blocks
            for (int i = 0; i < 50; i++) {
                int x = (int) (Math.random() * foreground.getNumTilesX());
                int y = (int) (Math.random() * foreground.getNumTilesY());
                foreground.setTile(x, y, 1);
            }
            Level level = new Level(foreground);
            
            Logic logic = new Logic(level);
            launcher.setState(new GameState(launcher, logic));
            launcher.start();
            
        } catch (Exception ex) {
            Logger.log(ex);
            System.exit(-1);
        }
    }
    
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Width of the display, in pixels.
     */
    public static final int DISPLAY_WIDTH = 800;

    /**
     * Height of the display, in pixels.
     */
    public static final int DISPLAY_HEIGHT = 600;

    /**
     * Window title.
     */
    private static final String WINDOW_TITLE = "Demo";

    /**
     * Panel to which the game is drawn.
     */
    private GamePanel gamePanel;
    
    public DemoLauncher() {
        super(DISPLAY_WIDTH, DISPLAY_HEIGHT);
    }

    @Override
    protected void createDisplay(int width, int height) {
        
        gamePanel = new GamePanel(this, width, height);
        
        JFrame frame = new JFrame(WINDOW_TITLE);
        frame.setContentPane(gamePanel);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @Override
    public int getDisplayWidth() {
        return gamePanel.getWidth();
    }

    @Override
    public int getDisplayHeight() {
        return gamePanel.getHeight();
    }
    
    public GamePanel getGamePanel() {
        return gamePanel;
    }

    @Override
    protected void render() {
        gamePanel.repaint();
    }
    
}
