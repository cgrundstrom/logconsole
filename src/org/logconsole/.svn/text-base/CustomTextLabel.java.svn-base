package org.logconsole;

import javax.swing.JLabel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Font;

/**
 * Custom version of JLabel that uses anti-aliased fonts to make them
 * appear less jagged.
 *
 * This code was copied (with thanks) from http://mindprod.com/jgloss/antialiasing.html
 *
 * Author: Carl Grundstrom
 * Date: Feb 26, 2007
 */
public class CustomTextLabel extends JLabel {
    public CustomTextLabel(String text, Font font) {
        super(text);
        setFont(font);
    }

    public CustomTextLabel() {
        super();
    }

    /**
     * Paint text with anti-aliasing (smoothes jagged edges)
     */
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        // for antialiasing geometric shapes
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // for antialiasing text
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // to go for quality over speed
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        super.paintComponent(g2d);
    }
}
