package launcher;

import javax.swing.JFrame;

import game.GameState;
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
            
            Launcher launcher = new DemoLauncher();
            
            // Create demo level
            TileLayer foreground = new TileLayer(new int[][] {
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            });
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

    public DemoLauncher() {
        super(DISPLAY_WIDTH, DISPLAY_HEIGHT);
    }

    @Override
    protected void createDisplay(int width, int height) {
        
        GamePanel panel = new GamePanel(width, height);
        
        JFrame frame = new JFrame(WINDOW_TITLE);
        frame.setContentPane(panel);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @Override
    public int getDisplayWidth() {
        return DISPLAY_WIDTH;
    }

    @Override
    public int getDisplayHeight() {
        return DISPLAY_HEIGHT;
    }
    
}
