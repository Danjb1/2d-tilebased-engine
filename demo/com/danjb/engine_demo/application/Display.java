package com.danjb.engine_demo.application;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import com.danjb.engine.application.State;
import com.danjb.engine.application.StateContext;

/**
 * JPanel to which we render the current State.
 *
 * @author Dan Bryce
 */
public class Display extends JPanel {

    /**
     * Auto-generated.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The context that holds the current State.
     */
    private StateContext context;

    /**
     * Graphics context used during rendering.
     */
    private Graphics2D gfx;

    /**
     * Creates a Display with the given dimensions.
     *
     * @param context
     * @param width
     * @param height
     */
    public Display(StateContext context, int width, int height) {
        this.context = context;

        setPreferredSize(new Dimension(width, height));
    }

    /**
     * Draws this Component.
     *
     * <p>Called on repaint().
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Store the graphics context so the current State can access it later
        gfx = (Graphics2D) g;

        // Render the current State
        State currentState = context.getState();
        if (currentState != null) {
            currentState.render();
        }
    }

    /**
     * Gets the graphics context for the current frame.
     *
     * @return
     */
    public Graphics2D getRenderTarget() {
        return gfx;
    }

}
