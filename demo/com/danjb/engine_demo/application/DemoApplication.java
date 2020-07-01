package com.danjb.engine_demo.application;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;

import com.danjb.engine.application.Application;
import com.danjb.engine.application.Input;

/**
 * Example Application implementation.
 *
 * @author Dan Bryce
 */
public class DemoApplication extends Application
        implements KeyListener, MouseListener, MouseMotionListener {

    /**
     * Width of the display, in pixels.
     */
    private static final int DISPLAY_WIDTH = 800;

    /**
     * Height of the display, in pixels.
     */
    private static final int DISPLAY_HEIGHT = 600;

    /**
     * Window title.
     */
    private static final String WINDOW_TITLE = "Demo";

    /**
     * Desired frames per second.
     */
    private static final int FPS = 60;

    /**
     * Timestep used to tick the game each frame.
     *
     * <p>We use a fixed time step to keep our physics consistent.
     */
    private static final int MS_PER_FRAME = 1000 / FPS;

    /**
     * Minimum interval used when sleeping the main Thread.
     *
     * <p>If this is set to a very small value (e.g. 1), we will get closer to
     * our desired framerate, but there is a higher risk of "oversleeping".
     *
     * <p>If this is set to a larger value (e.g. 4), each frame is more likely
     * to arrive a little early. We rectify this by busy-waiting between frames.
     */
    private static final int MIN_SLEEP_TIME = 4;

    /**
     * Timestamp taken at the start of every frame.
     */
    private long msBefore;

    /**
     * Timestamp taken at the end of every frame.
     */
    private long msAfter;

    /**
     * Panel to which the game is drawn.
     */
    private Display display;

    @Override
    protected void init() {
        super.init();
        createDisplay(DISPLAY_WIDTH, DISPLAY_HEIGHT);
    }

    @Override
    protected void tick(int delta) {
        msBefore = System.currentTimeMillis();
        super.tick(delta);
    }

    @Override
    protected int calculateDelta() {
        // Since we use a fixed timestep, we always return the same value.
        // To get the "real" delta value here, just calculate:
        //   msAfter - msBefore
        return MS_PER_FRAME;
    }

    @Override
    protected void yield() {

        /*
         * This is not the most sophisticated game loop in the world, but it
         * gives us a pretty steady 60fps.
         */

        // Calculate how much time until the next frame is due
        msAfter = System.currentTimeMillis();
        int timeTaken = (int) (msAfter - msBefore);
        int timeRemaining = MS_PER_FRAME - timeTaken;

        // Always sleep at least once so we're not hogging the CPU
        timeRemaining = Math.max(timeRemaining, MIN_SLEEP_TIME);

        // Sleep until the next frame is due.
        // We sleep in short intervals because the OS gives us no
        // guarantees about the sleep duration, and we don't want to
        // risk overshooting.
        while (timeRemaining > MIN_SLEEP_TIME) {
            try {

                Thread.sleep(MIN_SLEEP_TIME);

                // Recalculate the time remaining
                msAfter = System.currentTimeMillis();
                timeTaken = (int) (msAfter - msBefore);
                timeRemaining = MS_PER_FRAME - timeTaken;

            } catch (InterruptedException ex) {
                break;
            }
        }

        // If we have finished this frame early, busy-wait until the
        // next frame is due
        while (timeRemaining > 0) {
            msAfter = System.currentTimeMillis();
            timeTaken = (int) (msAfter - msBefore);
            timeRemaining = MS_PER_FRAME - timeTaken;
        }
    }

    @Override
    protected void pollInput() {
        // Nothing to do, since input is polled by the event dispatch thread
    }

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

    public int getDisplayWidth() {
        return display.getWidth();
    }

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
