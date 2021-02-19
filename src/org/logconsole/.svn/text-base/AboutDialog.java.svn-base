/*
 * Copyright (c) 2002 Ambergini, Inc., All Rights Reserved
 *
 * LogConsole
 *
 * Author: Carl Grundstrom
 * Date: April 2002
 */
package org.logconsole;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Show the about information for Log Console
 */
public class AboutDialog
        extends JDialog {
    public AboutDialog(JFrame owner) {
        super(owner, "About Log Console", true);

        Container pane = getContentPane();
        pane.setLayout(new BoxLayout(pane, BoxLayout.X_AXIS));
        pane.add(Box.createHorizontalStrut(20));
        Box box = new Box(BoxLayout.Y_AXIS);
        pane.add(box);
        pane.add(Box.createHorizontalStrut(20));

        box.add(Box.createVerticalStrut(10));

        JLabel l = new CustomTextLabel("Log Console", LogConsole.font24);
        l.setAlignmentX(Container.CENTER_ALIGNMENT);
        box.add(l);
        box.add(Box.createVerticalStrut(20));

        ImageIcon icon = Util.getIcon("logo-64.png");
        l = new JLabel(icon);
        l.setAlignmentX(Container.CENTER_ALIGNMENT);
        box.add(l);
        box.add(Box.createVerticalStrut(20));

        Box box2 = new Box(BoxLayout.X_AXIS);
        box.add(box2);

        Box box3 = new Box(BoxLayout.Y_AXIS);
        box2.add(box3);
        box2.add(Box.createHorizontalStrut(10));

        box3.add(new CustomTextLabel("Author:", LogConsole.fontBold12));
        box3.add(Box.createVerticalStrut(10));

        box3.add(new CustomTextLabel("Version:", LogConsole.fontBold12));
        box3.add(Box.createVerticalStrut(10));

        box3.add(new CustomTextLabel("License:", LogConsole.fontBold12));
        box3.add(Box.createVerticalStrut(10));

        box3.add(new CustomTextLabel("Copyright:", LogConsole.fontBold12));
        box3.add(Box.createVerticalStrut(10));

        box3.add(new CustomTextLabel("Web Site:", LogConsole.fontBold12));
        box3.add(Box.createVerticalStrut(10));

        box3 = new Box(BoxLayout.Y_AXIS);
        box2.add(box3);

        box3.add(new CustomTextLabel("Carl Grundstrom", LogConsole.font12));
        box3.add(Box.createVerticalStrut(10));

        box3.add(new CustomTextLabel("Version 2.0 beta 2", LogConsole.font12));
        box3.add(Box.createVerticalStrut(10));

        box3.add(new CustomTextLabel("Freeware (MIT License)", LogConsole.font12));
        box3.add(Box.createVerticalStrut(10));

        box3.add(new CustomTextLabel("\u00a9 2001-2008 by Carl Grundstrom", LogConsole.font12));
        box3.add(Box.createVerticalStrut(10));

        box3.add(new CustomTextLabel("www.logconsole.org", LogConsole.font12));
        box3.add(Box.createVerticalStrut(10));

        JButton closeButton = new CustomTextButton("Close");
        closeButton.addActionListener(new CloseButtonListener());
        closeButton.setAlignmentX(Container.CENTER_ALIGNMENT);
        closeButton.setDefaultCapable(true);
        getRootPane().setDefaultButton(closeButton);
        box.add(closeButton);
        box.add(Box.createVerticalStrut(10));

        // Show the window
        pack();
        Point location = owner.getLocation();
        location.x += 50;
        location.y += 100;
        setLocation(location);
//        setResizable(false);
        setVisible(true);
    }

    class CloseButtonListener
            implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    }

}
