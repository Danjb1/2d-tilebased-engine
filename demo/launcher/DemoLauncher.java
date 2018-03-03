package launcher;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;

import game.GameState;
import game.Level;
import game.Logic;
import game.TileLayer;

/**
 * Example Launcher implementation.
 *
 * @author Dan Bryce
 */
public class DemoLauncher extends Launcher implements KeyListener,
        MouseListener, MouseMotionListener {

    /**
     * Entry point for the application.
     *
     * @param args
     */
    public static void main(String[] args) {
        try {

            DemoLauncher launcher = new DemoLauncher();

            Level level = createLevel();
            Logic logic = new Logic(level);
            launcher.setState(new GameState(launcher, logic));

            launcher.start();

        } catch (Exception ex) {
            Logger.log(ex);
            System.exit(-1);
        }
    }

    /**
     * Creates a demo level.
     *
     * @return
     */
    private static Level createLevel() {
        TileLayer foreground = new TileLayer(new int[40][30]);

        // Generate a floor
        for (int x = 0; x < foreground.getNumTilesX(); x++) {
            foreground.setTile(x, foreground.getNumTilesY() - 1, 1);
        }

        // Generate some random blocks
        for (int i = 0; i < 50; i++) {
            int x = (int) (Math.random() * foreground.getNumTilesX());
            int y = (int) (Math.random() * foreground.getNumTilesY());
            foreground.setTile(x, y, 1);
        }

        return new Level(foreground);
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
    private Display display;

    public DemoLauncher() {
        super(DISPLAY_WIDTH, DISPLAY_HEIGHT);
    }

    @Override
    protected void createDisplay(int width, int height) {

        display = new Display(this, width, height);
        display.addMouseListener(this);
        display.addMouseMotionListener(this);

        JFrame frame = new JFrame(WINDOW_TITLE);
        frame.setContentPane(display);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        frame.addKeyListener(this);
    }

    @Override
    public int getDisplayWidth() {
        return display.getWidth();
    }

    @Override
    public int getDisplayHeight() {
        return display.getHeight();
    }

    public Display getGamePanel() {
        return display;
    }

    @Override
    protected void render() {
        display.repaint();
    }

    ////////////////////////////////////////////////////////////////////////////
    // KeyListener methods
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        input.addKeyEvent(new Input.KeyEvent(
                e.getKeyCode(),
                e.getKeyChar(),
                Input.EventType.PRESSED));
    }

    @Override
    public void keyReleased(KeyEvent e) {
        input.addKeyEvent(new Input.KeyEvent(
                e.getKeyCode(),
                e.getKeyChar(),
                Input.EventType.RELEASED));
    }

    ////////////////////////////////////////////////////////////////////////////
    // MouseListener methods
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        input.addMouseEvent(new Input.MouseEvent(
                e.getButton(),
                e.getX(),
                e.getY(),
                Input.EventType.PRESSED));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        input.addMouseEvent(new Input.MouseEvent(
                e.getButton(),
                e.getX(),
                e.getY(),
                Input.EventType.RELEASED));
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    ////////////////////////////////////////////////////////////////////////////
    // MouseMotionListener methods
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {
        input.setMousePos(e.getX(), e.getY());
    }

}
