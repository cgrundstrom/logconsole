package org.logconsole;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.plaf.basic.BasicButtonUI;
import java.net.URL;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Font;

/**
 * This custom button displays a button that contains only an icon
 * and does not have any border. This gives the UI designer complete
 * control over what the button looks like.
 *
 * Author: Carl Grundstrom
 */
public class CustomTextButton extends JButton {

    /**
     * Create a button using the specified text
     */
    public CustomTextButton(String text) {
        super(text);
        setFont(LogConsole.fontBold14);
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