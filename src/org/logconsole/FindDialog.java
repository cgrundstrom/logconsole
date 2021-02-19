/*
 * Copyright (c) 2002 Ambergini, Inc., All Rights Reserved
 *
 * LogConsole
 *
 * Author: Carl Grundstrom
 * Date: April 2002
 */
package org.logconsole;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import java.awt.Container;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * A dialog that allows the user to search for a string
 */
public class FindDialog
        extends JDialog {
    public boolean canceled;
    public String searchString;
    public boolean caseSensitive;
    public boolean forward;

    private JTextField searchStringText;
    private JCheckBox caseSensitiveCheck;
    private JRadioButton forwardRadio;

    public static String lastSearchString = "";
    public static boolean lastCaseSensitive = false;
    public static boolean lastForward = true;

    public FindDialog(JFrame owner, String selectedText) {
        super(owner, "Find Text", true);

        canceled = true;
        searchString = null;
        caseSensitive = lastCaseSensitive;
        forward = lastForward;
        if (selectedText != null && selectedText.indexOf('\n') == -1)
            searchString = selectedText;
        else
            searchString = lastSearchString;

        BoxLayout layout;
        JPanel panel;

        // Set up main box with two part, one for the fields and one for the buttons
        Container pane = getContentPane();
        Box boxMain = new Box(BoxLayout.X_AXIS);
        pane.add(boxMain);
        boxMain.add(Box.createHorizontalStrut(10));
        Box boxLeft = new Box(BoxLayout.Y_AXIS);
        boxMain.add(boxLeft);
        boxMain.add(Box.createHorizontalStrut(10));
        Box boxRight = new Box(BoxLayout.Y_AXIS);
        boxMain.add(boxRight);
        boxMain.add(Box.createHorizontalStrut(10));

        boxLeft.add(Box.createVerticalStrut(10));

        // Create a box for the text field
        panel = new JPanel();
        boxLeft.add(panel);
        boxLeft.add(Box.createVerticalStrut(10));
        layout = new BoxLayout(panel, BoxLayout.X_AXIS);
        panel.setLayout(layout);
        panel.add(Box.createHorizontalStrut(10));

        JLabel label = new JLabel("Text to find");
        panel.add(label);
        panel.add(Box.createHorizontalStrut(10));

        searchStringText = new JTextField(searchString, 20);
        panel.add(searchStringText);
        panel.add(Box.createGlue());

        // Create a box for the options
        panel = new JPanel();
        boxLeft.add(panel);
        boxLeft.add(Box.createVerticalStrut(10));
        layout = new BoxLayout(panel, BoxLayout.X_AXIS);
        panel.setLayout(layout);
        panel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), "Options"));
        panel.add(Box.createHorizontalStrut(10));

        caseSensitiveCheck = new JCheckBox("Case sensitive");
        caseSensitiveCheck.setSelected(caseSensitive);
        panel.add(caseSensitiveCheck);
        panel.add(Box.createGlue());

        // Create a box for the direction
        panel = new JPanel();
        boxLeft.add(panel);
        boxLeft.add(Box.createVerticalStrut(10));
        layout = new BoxLayout(panel, BoxLayout.X_AXIS);
        panel.setLayout(layout);
        panel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), "Direction"));
        panel.add(Box.createHorizontalStrut(10));
        ButtonGroup group = new ButtonGroup();

        forwardRadio = new JRadioButton("Forward", true);
        group.add(forwardRadio);
        panel.add(forwardRadio);
        panel.add(Box.createHorizontalStrut(10));

        JRadioButton backwardsRadio = new JRadioButton("Backwards");
        group.add(backwardsRadio);
        panel.add(backwardsRadio);
        panel.add(Box.createGlue());

        if (forward)
            forwardRadio.setSelected(true);
        else
            backwardsRadio.setSelected(true);
        boxRight.add(Box.createVerticalStrut(10));

        JButton findButton = new JButton("Find");
        findButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchString = searchStringText.getText();
                caseSensitive = caseSensitiveCheck.isSelected();
                forward = forwardRadio.isSelected();
                canceled = false;

                lastSearchString = searchString;
                lastCaseSensitive = caseSensitive;
                lastForward = forward;

                dispose();
            }
        });
        findButton.setDefaultCapable(true);
        getRootPane().setDefaultButton(findButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // Create a box for the buttons
        panel = new JPanel();
        boxRight.add(panel);
        boxRight.add(Box.createVerticalStrut(10));
        layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(layout);

        panel.add(findButton);
        panel.add(Box.createVerticalStrut(10));

        panel.add(cancelButton);
        panel.add(Box.createGlue());

        // Show the window
        Point location = owner.getLocation();
        location.x += 50;
        location.y += 100;
        setLocation(location);
        pack();
        setVisible(true);
    }
}


