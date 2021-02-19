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
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.LinkedList;


public class FilterDialog extends JDialog {
    private LinkedList<FilterUI> filterUIs = new LinkedList<FilterUI>();
    private JScrollPane currentScrollPane;
    private FilterManager filterManager;

    public FilterDialog(JFrame owner, FilterManager filterManager) {
        super(owner, "Edit Log Console Filters", true);
        this.filterManager = filterManager;

        for (FilterManager.Filter filter : filterManager.getFilters())
            filterUIs.add(new FilterUI(filter.regex, filter.enabled));

        Container pane = getContentPane();
        pane.setLayout(new BorderLayout());

        JLabel l = new JLabel("Filters");
        JButton addButton = new CustomTextButton("Add Filter");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                filterUIs.add(new FilterUI("", true));
                updateFilterPanel();
            }
        });

        Box topBox = Box.createHorizontalBox();
        topBox.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        topBox.add(Box.createHorizontalGlue());
        topBox.add(l);
        topBox.add(Box.createHorizontalStrut(20));
        topBox.add(addButton);
        topBox.add(Box.createHorizontalGlue());
        pane.add(topBox, BorderLayout.NORTH);

        currentScrollPane = new JScrollPane(createFilterPanel(),
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pane.add(currentScrollPane, BorderLayout.CENTER);

        JButton okButton = new CustomTextButton("OK");
        okButton.setDefaultCapable(true);
        getRootPane().setDefaultButton(okButton);
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (FilterUI filterUI : filterUIs) {
                    String name = filterUI.regex.getText().trim();
                    if (name.length() == 0) {
                        JOptionPane.showMessageDialog(FilterDialog.this, "Please supply regular expressions for all filters", "Error updating filters", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                FilterDialog.this.filterManager.removeAllFilters();
                for (FilterUI filterUI : filterUIs)
                    FilterDialog.this.filterManager.addFilter(filterUI.regex.getText().trim(), filterUI.enabled.isSelected());

                try {
                    LogConsole.currentProject.write();
                }
                catch (IOException ex) {
                    JOptionPane.showMessageDialog(FilterDialog.this, ex.getMessage(), "Error writing properties file", JOptionPane.ERROR_MESSAGE);
                }
                dispose();
                LogConsole.THIS.refreshProject();
            }
        });

        JButton cancelButton = new CustomTextButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        Box buttonBox = Box.createHorizontalBox();
        buttonBox.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonBox.add(Box.createHorizontalGlue());
        buttonBox.add(okButton);
        buttonBox.add(Box.createHorizontalStrut(20));
        buttonBox.add(cancelButton);
        buttonBox.add(Box.createHorizontalGlue());

        pane.add(buttonBox, BorderLayout.SOUTH);

        // Show the window
        Point location = owner.getLocation();
        location.x += 50;
        location.y += 100;
        setLocation(location);
        pack();
        setVisible(true);
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        Insets nameInsets = new Insets(10, 10, 10, 5);
        Insets valueInsets = new Insets(10, 0, 10, 10);
        Insets buttonInsets = new Insets(10, 10, 10, 10);

        c.gridy = 0;
        for (FilterUI filterUI : filterUIs) {
            c.gridx = 0;

            c.insets = nameInsets;
            panel.add(filterUI.enabled, c);
            c.gridx++;

            c.insets = nameInsets;
            panel.add(new JLabel("Regular Expression: "), c);
            c.gridx++;

            c.insets = valueInsets;
            panel.add(filterUI.regex, c);
            c.gridx++;

            c.insets = buttonInsets;
            panel.add(filterUI.delete, c);

            c.gridy++;
        }

        return panel;
    }

    private void updateFilterPanel() {
        Container pane = FilterDialog.this.getContentPane();
        pane.remove(currentScrollPane);
        currentScrollPane = new JScrollPane(createFilterPanel(),
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pane.add(currentScrollPane, BorderLayout.CENTER);
        pane.validate();
        pane.repaint();
        FilterDialog.this.pack();
    }

    private class FilterUI {
        public JCheckBox enabled;
        public JTextField regex;
        public JButton delete;

        public FilterUI(String r, boolean e) {
            enabled = new JCheckBox("Enabled", e);

            regex = new JTextField(r);
            Dimension d = regex.getPreferredSize();
            if (d.width < 200) {
                d.width = 200;
                regex.setPreferredSize(d);
            }

            delete = new CustomTextButton("Delete");
            delete.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    filterUIs.remove(FilterUI.this);
                    updateFilterPanel();
                }
            });
        }
    }
}