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
import javax.swing.JFileChooser;
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
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;


public class ProjectDialog extends JDialog {
    private LinkedList<LogSourceUI> logSourceUIs = new LinkedList<LogSourceUI>();
    private JScrollPane currentScrollPane;
    private Project currentProject;

    public ProjectDialog(JFrame owner, Project projectIn) {
        super(owner, "Edit Log Console project", true);
        this.currentProject = projectIn;

        for (LogSource logSource : currentProject.getLogSourceList())
            logSourceUIs.add(new LogSourceUI(logSource, logSource.getDisplayName(), logSource.getUnexpandedFile(), logSource.isEnabled()));

        Container pane = getContentPane();
        pane.setLayout(new BorderLayout());

        JLabel l = new JLabel("Log Sources");
        JButton addButton = new CustomTextButton("Add Log Source");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logSourceUIs.add(new LogSourceUI(null, "", "", true));
                updateLogSourcePanel();
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

        currentScrollPane = new JScrollPane(createLogSourcePanel(),
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pane.add(currentScrollPane, BorderLayout.CENTER);

        JButton okButton = new CustomTextButton("OK");
        okButton.setDefaultCapable(true);
        getRootPane().setDefaultButton(okButton);
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (LogSourceUI logSourceUI : logSourceUIs) {
                    String name = logSourceUI.name.getText().trim();
                    if (name.length() == 0) {
                        JOptionPane.showMessageDialog(ProjectDialog.this, "Please supply names for all log sources", "Error updating project", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    String file = logSourceUI.file.getText().trim();
                    if (file.length() == 0) {
                        JOptionPane.showMessageDialog(ProjectDialog.this, "Please supply files for all log sources", "Error updating project", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                Project newProject = new Project(currentProject);
                for (LogSourceUI logSourceUI : logSourceUIs) {
                    boolean enabled = logSourceUI.enabled.isSelected();
                    String name = logSourceUI.name.getText().trim();
                    String file = logSourceUI.file.getText().trim();
                    newProject.addNewLogSource(logSourceUI.originalLogSource, name, file, enabled);
                }

                try {
                    newProject.write();
                    LogConsole.THIS.closeProject();
                    LogConsole.THIS.openProject(newProject.getProjectFile());
                    LogConsole.propertyManager.write(false);
                }
                catch (IOException ex) {
                    JOptionPane.showMessageDialog(ProjectDialog.this, currentProject.getProjectFile().getAbsolutePath() + ": " + ex.getMessage(), "Error writing file", JOptionPane.ERROR_MESSAGE);
                }
                dispose();
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

    private JPanel createLogSourcePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        Insets nameInsets = new Insets(10, 10, 10, 5);
        Insets valueInsets = new Insets(10, 0, 10, 10);
        Insets buttonInsets = new Insets(10, 10, 10, 10);

        c.gridy = 0;
        for (LogSourceUI logSourceUI : logSourceUIs) {
            c.gridx = 0;

            c.insets = nameInsets;
            panel.add(logSourceUI.enabled, c);
            c.gridx++;

            c.insets = nameInsets;
            panel.add(new JLabel("Name: "), c);
            c.gridx++;

            c.insets = valueInsets;
            panel.add(logSourceUI.name, c);
            c.gridx++;

            c.insets = nameInsets;
            panel.add(new JLabel("File: "), c);
            c.gridx++;

            c.insets = valueInsets;
            panel.add(logSourceUI.file, c);
            c.gridx++;

            c.insets = buttonInsets;
            panel.add(logSourceUI.browse, c);
            c.gridx++;

            c.insets = buttonInsets;
            panel.add(logSourceUI.delete, c);

            c.gridy++;
        }

        return panel;
    }

    private void updateLogSourcePanel() {
        Container pane = ProjectDialog.this.getContentPane();
        pane.remove(currentScrollPane);
        currentScrollPane = new JScrollPane(createLogSourcePanel(),
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pane.add(currentScrollPane, BorderLayout.CENTER);
        pane.validate();
        pane.repaint();
        ProjectDialog.this.pack();
    }

    private class LogSourceUI {
        public LogSource originalLogSource;
        public JCheckBox enabled;
        public JTextField name;
        public JTextField file;
        public JButton browse;
        public JButton delete;
        public FilterManager filterManager;

        public LogSourceUI(LogSource originalLogSource, String n, String f, boolean e) {
            this.originalLogSource = originalLogSource;
            enabled = new JCheckBox("Enabled", e);

            name = new JTextField(n);
            Dimension d = name.getPreferredSize();
            if (d.width < 100) {
                d.width = 100;
                name.setPreferredSize(d);
            }

            file = new JTextField(f);
            d = file.getPreferredSize();
            if (d.width < 250) {
                d.width = 250;
                file.setPreferredSize(d);
            }

            delete = new CustomTextButton("Delete");
            delete.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (logSourceUIs.size() == 1)
                        JOptionPane.showMessageDialog(ProjectDialog.this, "You must have at least one Log Source", "Error deleting Log Source", JOptionPane.ERROR_MESSAGE);
                    else {
                        logSourceUIs.remove(LogSourceUI.this);
                        updateLogSourcePanel();
                    }
                }
            });

            browse = new CustomTextButton("Browse");
            browse.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JFileChooser chooser;
                    try {
                        File lastLogFileDirectory = null;
                        String f = file.getText();
                        if (f.trim().length() > 0) {
                            File p = new File(f).getParentFile();
                            if (p.exists())
                                lastLogFileDirectory = p;
                        }
                        if (lastLogFileDirectory == null)
                            lastLogFileDirectory = new File(LogConsole.propertyManager.getStringProperty("lastLogFileDirectory"));
                        if (lastLogFileDirectory.exists())
                            chooser = new JFileChooser(lastLogFileDirectory);
                        else
                            chooser = new JFileChooser();
                    }
                    catch (LogConsoleException e1) {
                        chooser = new JFileChooser();
                    }
                    if (chooser.showOpenDialog(ProjectDialog.this) == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = chooser.getSelectedFile();
                        try {
                            LogConsole.propertyManager.setProperty("lastLogFileDirectory", selectedFile.getParent());
                            LogConsole.propertyManager.write(false);
                        }
                        catch (Exception ignored) {
                        }
                        file.setText(selectedFile.getAbsolutePath());
                    }
                }
            });
        }
    }
}
