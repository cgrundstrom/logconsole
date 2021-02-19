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
 * Displays a button without any border by overriding the current Look and Feel
 *
 * Author: Carl Grundstrom
 */
public class BorderlessButton extends JButton {

    public BorderlessButton(String text) {
        super(text);
    }

    public BorderlessButton() {
        super();
    }

    /**
     * Whenever the look and feel is updated, use the basic button UI with no border
     */
    public void updateUI() {
        setUI(new BasicButtonUI());
        setBorder(null);
        setOpaque(false);
        setContentAreaFilled(false);
    }
}