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
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Show the about information for Log Console
 */
public class LicenseDialog
        extends JDialog {
    public LicenseDialog(JFrame owner) {
        super(owner, "Log Console License", true);

        Container pane = getContentPane();
        pane.setLayout(new BoxLayout(pane, BoxLayout.X_AXIS));
        pane.add(Box.createHorizontalStrut(20));
        Box box = new Box(BoxLayout.Y_AXIS);
        pane.add(box);
        pane.add(Box.createHorizontalStrut(20));

        box.add(Box.createVerticalStrut(10));

        String copyright1 =
                "LogConsole uses the standard \"MIT\" license";
        String copyright2 =
                "Copyright (c) 2001-2008 Carl Grundstrom";

        String copyright3 =
                "Permission is hereby granted, free of charge, to any person obtaining a copy" +
                "of this software and associated documentation files (the \"Software\"), to deal" +
                "in the Software without restriction, including without limitation the rights" +
                "to use, copy, modify, merge, publish, distribute, sublicense, and/or sell" +
                "copies of the Software, and to permit persons to whom the Software is" +
                "furnished to do so, subject to the following conditions:";

        String copyright4 =
                "The above copyright notice and this permission notice shall be included in" +
                "all copies or substantial portions of the Software.";

        String  copyright5 =
                "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR" +
                "IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY," +
                "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE" +
                "AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER" +
                "LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM," +
                "OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN" +
                "THE SOFTWARE.";
        CustomMultiLineLabel l = new CustomMultiLineLabel();
        l.setMaxWidth(400);
        l.setText(copyright1);
        box.add(l);
        box.add(Box.createVerticalStrut(5));

        l = new CustomMultiLineLabel();
        l.setMaxWidth(400);
        l.setText(copyright2);
        box.add(l);
        box.add(Box.createVerticalStrut(5));

        l = new CustomMultiLineLabel();
        l.setMaxWidth(400);
        l.setText(copyright3);
        box.add(l);
        box.add(Box.createVerticalStrut(5));

        l = new CustomMultiLineLabel();
        l.setMaxWidth(400);
        l.setText(copyright4);
        box.add(l);
        box.add(Box.createVerticalStrut(5));

        l = new CustomMultiLineLabel();
        l.setMaxWidth(400);
        l.setText(copyright5);
        box.add(l);
        box.add(Box.createVerticalStrut(5));

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
