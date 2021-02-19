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
public class CustomImageButton extends JButton {

    /**
     * Create a button using the specified image prefix and suffix. The suffix is normally
     * the file extension (including the ".")
     */
    public CustomImageButton(String prefix, String suffix) {
        super();
        initializeImage(prefix, suffix);
    }

    /**
     * Create a button using the specified text and the specified image prefix and suffix. The suffix is normally
     * the file extension (including the ".")
     */
    public CustomImageButton(String text, Font font, String prefix, String suffix) {
        super(text);
        initializeImage(prefix, suffix);
        setFont(font);
    }

    private void initializeImage(String prefix, String suffix) {
        ClassLoader classloader = CustomImageButton.class.getClassLoader();
        String directory = "org/logconsole/images/";
        URL url;
        String path;

        // Active icon
        path = directory + prefix + "Active" + suffix;
        url = classloader.getResource(path);
        if (url != null) {
            ImageIcon imageIcon = new ImageIcon(url);
            setIcon(imageIcon);
        }
        else {
            path = directory + prefix + suffix;
            url = classloader.getResource(path);
            if (url != null) {
                ImageIcon imageIcon = new ImageIcon(url);
                setIcon(imageIcon);
            }
            else {
                int start = prefix.indexOf('_');
                start++;
                int end = prefix.indexOf('_', start);
                if (end == -1) {
                    end = prefix.lastIndexOf('.', start);
                    if (end == -1)
                        end = prefix.length();
                }
                String displayName = prefix.substring(start, end);
                setText(displayName);
            }
        }

        // Rollover icon
        path = directory + prefix + "Rollover" + suffix;
        url = classloader.getResource(path);
        if (url != null) {
            ImageIcon imageIcon = new ImageIcon(url);
            setRolloverIcon(imageIcon);
            setRolloverEnabled(true);
        }
        else {
            setRolloverEnabled(false);
        }

        // Pressed icon
        path = directory + prefix + "Click" + suffix;
        url = classloader.getResource(path);
        if (url != null) {
            ImageIcon imageIcon = new ImageIcon(url);
            setPressedIcon(imageIcon);
        }

        // Selected icon
        path = directory + prefix + "Click" + suffix;
        url = classloader.getResource(path);
        if (url != null) {
            ImageIcon imageIcon = new ImageIcon(url);
            setSelectedIcon(imageIcon);
            setRolloverSelectedIcon(imageIcon);
        }

        // Disabled
        path = directory + prefix + "Inactive" + suffix;
        url = classloader.getResource(path);
        if (url != null) {
            ImageIcon imageIcon = new ImageIcon(url);
            setDisabledIcon(imageIcon);
            setDisabledSelectedIcon(imageIcon);
        }
    }

    /**
     * If the user changes the look and feel, we need to reset our customizations to prevent the
     * button from getting a border
     */
    public void updateUI() {
        // This code is simple, but was tricky to figure out. The problem is
        // that the ButtonUI class provided by the System Look and Feel (at least
        // on Windows) totally ignores any attempt to change the border. We
        // therefore change to the Basic Look and Feel, just for buttons, to
        // overcome this problem.
        setUI(new BasicButtonUI());
        setBorder(null);
        setOpaque(false);
        setContentAreaFilled(false);
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
