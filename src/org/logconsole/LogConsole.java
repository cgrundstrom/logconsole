package org.logconsole;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.Caret;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.BadLocationException;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.FileFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.StringTokenizer;

public class LogConsole extends JFrame {
    public JComponent currentContent;
    public JComponent defaultContent;
    public LogSourcePanel currentLogSourcePanel;

    protected ImageIcon newDataIcon;
    protected ImageIcon seenDataIcon;
    protected ImageIcon pausedNewDataIcon;
    protected ImageIcon pausedSeenDataIcon;

    protected JButton editButton;
    protected JButton clearAllButton;

    protected ImageIcon resumeActiveIcon;
    protected ImageIcon resumeRolloverIcon;
    protected ImageIcon pauseActiveIcon;
    protected ImageIcon pauseRolloverIcon;

    protected JMenuItem findMenuItem;
    protected JMenuItem findAgainMenuItem;
    protected JMenuItem pauseResumeMenuItem;
    protected JMenuItem clearMenuItem;
    protected JMenuItem filterMenuItem;

    protected JMenuItem closeMenuItem;
    protected JMenuItem editMenuItem;
    protected JMenuItem clearAllMenuItem;

    private Container pane;

    static public LogConsole THIS;

    static public Project currentProject;
    static public PropertyManager propertyManager;
    static public VariableManager variableManager;

    static public int logBufferSizeInBytes;
    static public int sleepTimeInMilliSeconds;

    static public Font fontMono12 = new Font("Monospaced", Font.PLAIN, 12);
    static public Font font12 = new Font("SansSerif", Font.PLAIN, 12);
    static public Font fontBold12 = new Font("SansSerif", Font.BOLD, 12);

    static public Font font14 = new Font("SansSerif", Font.PLAIN, 14);
    static public Font fontBold14 = new Font("SansSerif", Font.BOLD, 14);

    static public Font font16 = new Font("SansSerif", Font.BOLD, 16);
    static public Font fontBold16 = new Font("SansSerif", Font.BOLD, 16);

    static public Font font24 = new Font("SansSerif", Font.BOLD, 24);

    public static void main(String[] args) {
        try {
            propertyManager = new PropertyManager();
            variableManager = new VariableManager();

            setLookAndFeel(propertyManager.getStringProperty("lookAndFeel"));
            PlatformAppearance.apply("/org/logconsole/images/logo-128.png");
            
            new LogConsole();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public LogConsole() throws Exception {
        super();
        THIS = this;

        setIconImage(Util.getIcon("logo-128.png").getImage());

        // The following requires Java 1.6... (and seems to messup the "ALT-TAB" icon)
//        ArrayList<Image> list = new ArrayList<Image>();
//        list.add(Util.getIcon("logo-16.gif").getImage());
//        list.add(Util.getIcon("logo-20.gif").getImage());
//        list.add(Util.getIcon("logo-24.gif").getImage());
//        list.add(Util.getIcon("logo-32.gif").getImage());
//        list.add(Util.getIcon("logo-48.gif").getImage());
//        list.add(Util.getIcon("logo-64.gif").getImage());
//        list.add(Util.getIcon("logo-100.gif").getImage());
//        setIconImages(list);

        newDataIcon = Util.getIcon("newData.gif");
        seenDataIcon = Util.getIcon("seenData.gif");
        pausedNewDataIcon = Util.getIcon("pausedNewData.gif");
        pausedSeenDataIcon = Util.getIcon("pausedSeenData.gif");

        resumeActiveIcon = Util.getIcon("resumeActive-24.png");
        resumeRolloverIcon = Util.getIcon("resumeRollover-24.png");
        pauseActiveIcon = Util.getIcon("pauseActive-24.png");
        pauseRolloverIcon = Util.getIcon("pauseRollover-24.png");

        setTitle("Log Console");

        setJMenuBar(new MyMenuBar());

        pane = getContentPane();
        pane.setLayout(new BorderLayout());
        pane.add(new MyIconPanel(), BorderLayout.NORTH);
        defaultContent = new MyDefaultPanel();
        currentContent = defaultContent;
        pane.add(currentContent, BorderLayout.CENTER);

        logBufferSizeInBytes = propertyManager.getIntegerProperty("logBufferSizeInKBytes") * 1024;
        sleepTimeInMilliSeconds = propertyManager.getIntegerProperty("sleepTimeInSeconds") * 1000;

        // Set the window size based on the user's preferences, but make sure we never exceed the current desktop size
        int locationX = propertyManager.getIntegerProperty("mainWindowLocationX");
        int locationY = propertyManager.getIntegerProperty("mainWindowLocationY");
        int sizeX = propertyManager.getIntegerProperty("mainWindowSizeX");
        int sizeY = propertyManager.getIntegerProperty("mainWindowSizeY");
        boolean modified = false;
        Rectangle r = getScreenSize();
        if (r.width < locationX + sizeX) {
            if (r.width < sizeX) {
                locationX = 0;
                sizeX = r.width;
            } else
                locationX = r.width - sizeX;
            propertyManager.setProperty("mainWindowLocationX", locationX);
            propertyManager.setProperty("mainWindowSizeX", sizeX);
            modified = true;
        }
        if (r.height < locationY + sizeY) {
            if (r.height < sizeY) {
                locationY = 0;
                sizeY = r.height;
            } else
                locationY = r.height - sizeY;
            propertyManager.setProperty("mainWindowSizeY", sizeY);
            propertyManager.setProperty("mainWindowLocationY", locationY);
            modified = true;
        }
        if (modified)
            propertyManager.write(false);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeProject();
                dispose();
            }
        });

        addComponentListener(new ComponentAdapter() {
            public void componentMoved(ComponentEvent e) {
                Point location = e.getComponent().getLocation();
                try {
                    propertyManager.setProperty("mainWindowLocationX", location.x);
                    propertyManager.setProperty("mainWindowLocationY", location.y);
                    propertyManager.write(false);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            public void componentResized(ComponentEvent e) {
                Dimension size = e.getComponent().getSize();
                try {
                    propertyManager.setProperty("mainWindowSizeX", size.width);
                    propertyManager.setProperty("mainWindowSizeY", size.height);
                    propertyManager.write(false);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        setSize(sizeX, sizeY);
        setLocation(locationX, locationY);
        setVisible(true);
        
        String lastProjectFile = propertyManager.getStringProperty("lastProjectFile", null);
        if (lastProjectFile != null && lastProjectFile.length() > 0) {
            final File lastProject = new File(lastProjectFile);
            if (lastProject.exists() && lastProject.isFile() && lastProject.length() > 0) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        openProject(lastProject);
                    }
                });
            }
        }
    }

    public static void setLookAndFeel(String lookAndFeel)  {
        String lookAndFeelClass;
        if (lookAndFeel.equals("System"))
            lookAndFeelClass = UIManager.getSystemLookAndFeelClassName();
        else {
            StringBuffer b = new StringBuffer();
            int len = lookAndFeel.length();
            for (int i = 0; i < len; i++) {
                char ch = lookAndFeel.charAt(i);
                if (ch != ' ')
                    b.append(ch);
            }
            String name = b.toString();
            lookAndFeelClass = "org.jvnet.substance.skin.Substance" + name + "LookAndFeel";
        }
        try {
            UIManager.setLookAndFeel(lookAndFeelClass);
            if (!lookAndFeel.equals(propertyManager.getStringProperty("lookAndFeel"))) {
                propertyManager.setProperty("lookAndFeel", lookAndFeel);
                propertyManager.write(false);
            }
        }
        catch (Exception ignored) {
        }
    }

    /**
     * Returns the screen size of the video display. This support multiple monitors by returning a size that
     * spans all attached monitors
     */
    private Rectangle getScreenSize() {
        Rectangle virtualBounds = new Rectangle();

        String ver = System.getProperty("java.version");
        if (ver.startsWith("1.2")) {
            //
            // Hard-code for JDK 1.2.2...
            //
            virtualBounds.setBounds(0, 0, 1280, 1024);
        } else {
            //
            // The following requires JDK 1.3...
            //
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//            System.out.println(ge.getMaximumWindowBounds());
            GraphicsDevice[] gds = ge.getScreenDevices();
            for (GraphicsDevice gd : gds) {
                GraphicsConfiguration gc = gd.getDefaultConfiguration();
//                System.out.println(gc.getBounds());
                virtualBounds = virtualBounds.union(gc.getBounds());
//                System.out.println(virtualBounds);
            }
        }
        return virtualBounds;
    }

    protected void closeProject() {
        if (currentProject != null) {
            currentProject.close();
            for (LogSource logSource : currentProject.getLogSourceList())
                logSource.close();
            currentProject = null;
        }

        if (currentContent != defaultContent) {
            editButton.setEnabled(false);
            clearAllButton.setEnabled(false);

            editMenuItem.setEnabled(false);
            clearAllMenuItem.setEnabled(false);
            closeMenuItem.setEnabled(false);

            findMenuItem.setEnabled(false);
            findAgainMenuItem.setEnabled(false);
            pauseResumeMenuItem.setEnabled(false);
            clearMenuItem.setEnabled(false);
            filterMenuItem.setEnabled(false);

            pane.remove(currentContent);
            currentContent = defaultContent;
            currentLogSourcePanel = null;
            pane.add(currentContent, BorderLayout.CENTER);
            pane.validate();
            pane.repaint();

            setTitle("Log Console");
        }
    }

    protected void openProject(File projectFile) {
        try {
            currentProject = new Project(projectFile);
            propertyManager.setProperty("lastProjectFile", projectFile);
            propertyManager.write(false);
        }
        catch (Exception ex) {
            propertyManager.setProperty("lastProjectFile", "");
            propertyManager.write(false);
            String message = ex.getMessage();
            if (message == null || message.length() == 0)
                message = ex.toString();
            JOptionPane.showMessageDialog(LogConsole.this, projectFile + ": " + message, "Error opening project file", JOptionPane.ERROR_MESSAGE);
        }
        if (currentProject != null) {
            pane.remove(currentContent);
            currentContent = new MyTabbedPane();
            pane.add(currentContent, BorderLayout.CENTER);

            editButton.setEnabled(true);
            clearAllButton.setEnabled(true);

            editMenuItem.setEnabled(true);
            clearAllMenuItem.setEnabled(true);
            closeMenuItem.setEnabled(true);

            findMenuItem.setEnabled(true);
            findAgainMenuItem.setEnabled(true);
            pauseResumeMenuItem.setEnabled(true);
            clearMenuItem.setEnabled(true);
            filterMenuItem.setEnabled(true);

            setTitle("Log Console - " + projectFile.getName());
        }
        pane.validate();
        pane.repaint();
    }

    protected void refreshProject() {
        if (currentProject != null) {
            File projectFile = currentProject.getProjectFile();
            closeProject();
            openProject(projectFile);
        }
    }

    protected void createProject() {
        JFileChooser chooser;
        try {
            File lastProjectDirectory = new File(propertyManager.getStringProperty("lastProjectDirectory"));
            if (!lastProjectDirectory.exists())
                chooser = new JFileChooser();
            else
                chooser = new JFileChooser(lastProjectDirectory);
        }
        catch (LogConsoleException ignored) {
            chooser = new JFileChooser();
        }

        chooser.addChoosableFileFilter(new FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().endsWith(".logconsole");
            }

            public String getDescription() {
                return "Log Console files";
            }
        });

        chooser.setDialogTitle("Create Log Console Project");
        chooser.setApproveButtonText("Create");
        if (chooser.showOpenDialog(LogConsole.this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            if (selectedFile.isDirectory()) {
                JOptionPane.showMessageDialog(LogConsole.this, selectedFile + " is a directory", "Error creating project", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                propertyManager.setProperty("lastProjectDirectory", selectedFile.getParent());
                propertyManager.write(false);
            }
            catch (Exception ignored) {
            }
            if (!selectedFile.getName().endsWith(".logconsole"))
                selectedFile = new File(selectedFile.getAbsolutePath() + ".logconsole");
            if (selectedFile.exists()) {
                int response = JOptionPane.showConfirmDialog(LogConsole.this, "Do you wish to overwrite it?", selectedFile + " already exists", JOptionPane.YES_NO_OPTION);
                if (response != JOptionPane.YES_OPTION)
                    return;
            }

            Project project = new Project();
            project.setProjectFile(selectedFile);
            project.addNewLogSource(null, "", "", true);
            new ProjectDialog(LogConsole.this, project);
        }
    }

    protected void openProject() {
        try {
            JFileChooser chooser;
            File lastProjectDirectory = new File(propertyManager.getStringProperty("lastProjectDirectory"));
            if (!lastProjectDirectory.exists())
                chooser = new JFileChooser();
            else
                chooser = new JFileChooser(lastProjectDirectory);
            chooser.addChoosableFileFilter(new FileFilter() {
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().endsWith(".logconsole");
                }

                public String getDescription() {
                    return "Log Console files";
                }
            });
            chooser.setDialogTitle("Open Log Console Project");
            if (chooser.showOpenDialog(LogConsole.this) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = chooser.getSelectedFile();
                propertyManager.setProperty("lastProjectDirectory", selectedFile.getParent());
                propertyManager.write(false);
                closeProject();
                openProject(selectedFile);
                propertyManager.write(false);
            }
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(LogConsole.this, ex.getMessage(), "Error opening file", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class MyMenuBar extends JMenuBar {
        public MyMenuBar() {
            super();
            JMenu menu;
            JMenuItem item;

            // File menu
            menu = new JMenu("File");
            add(menu);
            menu.setMnemonic(KeyEvent.VK_F);

            item = new JMenuItem("Create Project...");
            menu.add(item);
            item.setToolTipText("Create a new Log Console project file");
            item.setMnemonic(KeyEvent.VK_C);
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    createProject();
                }
            });

            item = new JMenuItem("Open Project...");
            menu.add(item);
            item.setToolTipText("Open an existing Log Console project file");
            item.setMnemonic(KeyEvent.VK_O);
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    openProject();
                }
            });

            menu.addSeparator();

            item = new JMenuItem("Look and Feel...");
            menu.add(item);
            item.setToolTipText("Change the theme of the user interface");
            item.setMnemonic(KeyEvent.VK_L);
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    new LookAndFeelDialog(LogConsole.this);
                }
            });

            menu.addSeparator();

            item = new JMenuItem("Exit");
            item.setToolTipText("Exit the Log Console program");
            item.setMnemonic(KeyEvent.VK_E);
            menu.add(item);
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    closeProject();
                    LogConsole.this.dispose();
                }
            });

            // Configure menu
            menu = new JMenu("Project");
            add(menu);
            menu.setMnemonic(KeyEvent.VK_C);

            editMenuItem = new JMenuItem("Edit...");
            item = editMenuItem;
            menu.add(item);
            item.setMnemonic(KeyEvent.VK_E);
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
            item.setToolTipText("Edit settings for the current project");
            item.setEnabled(false);
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    new ProjectDialog(LogConsole.this, currentProject);
                }
            });

            clearAllMenuItem = new JMenuItem("Clear Buffers");
            item = clearAllMenuItem;
            menu.add(item);
            item.setMnemonic(KeyEvent.VK_B);
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK));
            item.setToolTipText("Clear all log buffers in the current project");
            item.setEnabled(false);
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    for (LogSource logSource : currentProject.getLogSourceList())
                        logSource.clear();
                }
            });

            closeMenuItem = new JMenuItem("Close");
            item = closeMenuItem;
            menu.add(item);
            item.setMnemonic(KeyEvent.VK_C);
            item.setToolTipText("Close the current project");
            item.setEnabled(false);
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    closeProject();
                }
            });

            // Log file menu
            menu = new JMenu("Log Buffer");
            add(menu);
            menu.setMnemonic(KeyEvent.VK_S);

            findMenuItem = new JMenuItem("Find...");
            item = findMenuItem;
            menu.add(item);
            item.setToolTipText("Search for text in the current log buffer");
            item.setMnemonic(KeyEvent.VK_F);
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
            item.setEnabled(false);
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (currentLogSourcePanel != null) {
                        String selectedText = currentLogSourcePanel.textArea.getSelectedText();
                        FindDialog dialog = new FindDialog(LogConsole.this, selectedText);
                        if (!dialog.canceled && dialog.searchString.length() > 0)
                            currentLogSourcePanel.find(dialog.searchString, dialog.caseSensitive, dialog.forward);
                    }
                }
            });

            findAgainMenuItem = new JMenuItem("Find Again");
            item = findAgainMenuItem;
            menu.add(item);
            item.setToolTipText("Find the next (or previous) search string in the log buffer");
            item.setMnemonic(KeyEvent.VK_A);
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
            item.setEnabled(false);
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (currentLogSourcePanel != null && FindDialog.lastSearchString.length() > 0)
                        currentLogSourcePanel.find(FindDialog.lastSearchString, FindDialog.lastCaseSensitive, FindDialog.lastForward);
                }
            });

            pauseResumeMenuItem = new JMenuItem("Pause");
            item = pauseResumeMenuItem;
            menu.add(item);
            item.setToolTipText("Pause the display of the current log buffer");
            item.setMnemonic(KeyEvent.VK_P);
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
            item.setEnabled(false);
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (currentLogSourcePanel != null)
                        currentLogSourcePanel.setPaused(!currentLogSourcePanel.logSource.isPaused());
                }
            });

            clearMenuItem = new JMenuItem("Clear Buffer");
            item = clearMenuItem;
            menu.add(item);
            item.setMnemonic(KeyEvent.VK_C);
            item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
            item.setToolTipText("Clear the text from the current log buffer");
            item.setEnabled(false);
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (currentLogSourcePanel != null)
                        currentLogSourcePanel.logSource.clear();
                }
            });

            filterMenuItem = new JMenuItem("Log Filters...");
            item = filterMenuItem;
            menu.add(item);
            item.setToolTipText("Hide selected records from the current log buffer");
            item.setMnemonic(KeyEvent.VK_F);
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (currentLogSourcePanel != null)
                        new FilterDialog(LogConsole.this, currentLogSourcePanel.logSource.filterManager);
                }
            });

            // Help menu
            menu = new JMenu("Help");
            add(menu);
            menu.setMnemonic(KeyEvent.VK_H);

            item = new JMenuItem("License...");
            menu.add(item);
            item.setToolTipText("View the Log Console license");
            item.setMnemonic(KeyEvent.VK_A);
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    new LicenseDialog(LogConsole.this);
                }
            });

            item = new JMenuItem("About...");
            menu.add(item);
            item.setToolTipText("View the version of the Log Console program");
            item.setMnemonic(KeyEvent.VK_A);
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    new AboutDialog(LogConsole.this);
                }
            });
        }

    }

    private class MyIconPanel extends JPanel {
        public MyIconPanel() {
            super();
            setLayout(new BorderLayout());

            Box box = Box.createHorizontalBox();

            JButton aboutButton = new CustomImageButton("logo", "-24.png");
            aboutButton.setToolTipText("About Log Console");
            aboutButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    new AboutDialog(LogConsole.this);
                }
            });
            box.add(Box.createHorizontalStrut(30));
            box.add(aboutButton);
            box.add(Box.createHorizontalGlue());

            JButton openButton = new CustomImageButton("Open", LogConsole.fontBold14, "open", "-24.png");
            openButton.setToolTipText("Open a logconsole project");
            openButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    openProject();
                }
            });
            box.add(openButton);
            box.add(Box.createHorizontalStrut(30));

            JButton createButton = new CustomImageButton("Create", LogConsole.fontBold14, "create", "-24.png");
            createButton.setToolTipText("Create a new logconsole project");
            createButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    createProject();
                }
            });
            box.add(createButton);
            box.add(Box.createHorizontalStrut(30));

            editButton = new CustomImageButton("Edit", LogConsole.fontBold14, "edit", "-24.png");
            editButton.setToolTipText("Edit the current logconsole project");
            editButton.setEnabled(false);
            editButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    new ProjectDialog(LogConsole.this, currentProject);
                }
            });
            box.add(editButton);
            box.add(Box.createHorizontalStrut(30));

            clearAllButton = new CustomImageButton("Clear All", LogConsole.fontBold14, "clear", "-24.png");
            clearAllButton.setToolTipText("Clear all Log Buffers");
            clearAllButton.setEnabled(false);
            clearAllButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    for (LogSource logSource : currentProject.getLogSourceList())
                        logSource.clear();
                }
            });
            box.add(clearAllButton);
            box.add(Box.createHorizontalGlue());

            add(Box.createVerticalStrut(5), BorderLayout.NORTH);
            add(box, BorderLayout.CENTER);
            add(Box.createVerticalStrut(5), BorderLayout.SOUTH);
        }

    }

    private class MyDefaultPanel extends JPanel {
        public MyDefaultPanel() {
            super(new BorderLayout());
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(10, 10, 10, 10),
                    BorderFactory.createLineBorder(Color.black, 1)
            ));

            JLabel labelIcon = new JLabel(Util.getIcon("logo-64.png"));
            labelIcon.setAlignmentX(Container.CENTER_ALIGNMENT);

            JLabel labelText1 = new CustomTextLabel("Log Console", LogConsole.font24);
            labelText1.setAlignmentX(Container.CENTER_ALIGNMENT);

            JLabel labelText2 = new CustomTextLabel("Real time monitoring of log files", LogConsole.font16);
            labelText2.setAlignmentX(Container.CENTER_ALIGNMENT);

            Box box2 = Box.createVerticalBox();
            box2.add(Box.createVerticalStrut(100));
            box2.add(labelIcon);
            box2.add(Box.createVerticalStrut(20));
            box2.add(labelText1);
            box2.add(Box.createVerticalStrut(20));
            box2.add(labelText2);
            box2.add(Box.createVerticalGlue());

            Box box1 = Box.createHorizontalBox();
            box1.add(Box.createHorizontalGlue());
            box1.add(box2);
            box1.add(Box.createHorizontalGlue());
            add(box1, BorderLayout.CENTER);
        }
    }

    private class MyTabbedPane extends CustomTabbedPane {
        public MyTabbedPane() {
            super(TOP, WRAP_TAB_LAYOUT);
            setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
            setFont(font14);
            int tabIndex = 0;
            int  currentTabIndex = -1;
            for (LogSource logSource : currentProject.getLogSourceList()) {
                if (logSource.isEnabled()) {
                    LogSourcePanel logSourcePanel = new LogSourcePanel(logSource, tabIndex);
                    addTab(logSource.getDisplayName(), seenDataIcon, logSourcePanel, logSource.getExpandedFile());
                    if (currentTabIndex == -1 && logSource.getDisplayName().equals(currentProject.getCurrentLogSource()))
                        currentTabIndex = tabIndex;
                    tabIndex++;
                }
            }

            addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    int selectedIndex = getSelectedIndex();
                    LogSourcePanel logSourcePanel = (LogSourcePanel)getComponentAt(selectedIndex);
                    logSourcePanel.updateState();
                    currentLogSourcePanel = logSourcePanel;
                    currentProject.setCurrentLogSource(currentLogSourcePanel.logSource.getDisplayName());

                    int tabIndex = 0;
                    for (LogSource logSource : currentProject.getLogSourceList()) {
                        if (logSource.isEnabled()) {
                            if (tabIndex == selectedIndex)
                                setTitleAt(tabIndex, "<html><b>" + logSource.getDisplayName() + "</b></html>");
                            else
                                setTitleAt(tabIndex, logSource.getDisplayName());
                            tabIndex++;
                        }
                    }
                }
            });

            if (currentTabIndex == -1)
                currentTabIndex = 0;
            setSelectedIndex(currentTabIndex);
            fireStateChanged();
        }
    }

    private enum LogSourceState {
        newData,
        seenData,
        pausedNewData,
        pausedSeenData
    }

    public class LogSourcePanel extends JPanel implements Runnable {
        private LogSource logSource;
        private JTextArea textArea = new CustomTextArea();
        private final StringBuffer updateBuffer = new StringBuffer();
        private final StringBuffer lastLineBuffer = new StringBuffer();
        private boolean clearOnNextUpdate;
        private boolean modified;
        private JButton pauseResumeButton;
        private JLabel statusLabel;
        private JLabel fileLabel;
        private int tabIndex;
        private LogSourceState currentState;
        private Caret caret;
        private boolean cleared;
        private Document document;

        protected LogSource getLogSource() {
            return logSource;
        }

        public LogSourcePanel(LogSource logSource, int tabIndex) {
            super(new BorderLayout());
            this.logSource = logSource;
            this.tabIndex = tabIndex;
            currentState = LogSourceState.seenData;

            textArea.setFont(LogConsole.fontMono12);
            caret = new DefaultCaret();
            textArea.setCaret(caret);
            document = textArea.getDocument();

            pauseResumeButton = new CustomImageButton("Pause", LogConsole.fontBold14, "pause", "-24.png");
            pauseResumeButton.setToolTipText("Pause the display of the current log buffer");
            pauseResumeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setPaused(!getLogSource().isPaused());
                }
            });

            JButton findButton = new CustomImageButton("Find", LogConsole.fontBold14, "find", "-24.png");
            findButton.setToolTipText("Search log buffer for text");
            findButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String selectedText = textArea.getSelectedText();
                    FindDialog dialog = new FindDialog(LogConsole.this, selectedText);
                    if (!dialog.canceled && dialog.searchString.length() > 0)
                        find(dialog.searchString, dialog.caseSensitive, dialog.forward);
                }
            });

            JButton clearButton = new CustomImageButton("Clear", LogConsole.fontBold14, "clear", "-24.png");
            clearButton.setToolTipText("Clear Log Buffer");
            clearButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    LogSourcePanel.this.logSource.clear();
                }
            });

            JButton filterButton = new CustomImageButton("Filter", LogConsole.fontBold14, "filter", "-24.png");
            filterButton.setToolTipText("Edit Log Buffer Filters");
            filterButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    new FilterDialog(LogConsole.this, LogSourcePanel.this.logSource.filterManager);
                }
            });

            Box box = Box.createHorizontalBox();
            box.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
            box.add(pauseResumeButton);
            box.add(Box.createHorizontalStrut(30));
            box.add(findButton);
            box.add(Box.createHorizontalStrut(30));
            box.add(clearButton);
            box.add(Box.createHorizontalStrut(30));
            box.add(filterButton);
            box.add(Box.createHorizontalStrut(30));

            box.add(new CustomTextLabel("Status: ", LogConsole.fontBold12));
            box.add(Box.createHorizontalStrut(5));
            statusLabel = new CustomTextLabel("Running", LogConsole.font12);
            box.add(statusLabel);
            box.add(Box.createHorizontalStrut(30));

            box.add(new CustomTextLabel("File: ", LogConsole.fontBold12));
            box.add(Box.createHorizontalStrut(5));
            fileLabel = new CustomTextLabel(logSource.getExpandedFile(), LogConsole.font12);
            box.add(fileLabel);

            box.add(Box.createHorizontalGlue());

            add(box, BorderLayout.SOUTH);
            JScrollPane scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            add(scrollPane, BorderLayout.CENTER);
            logSource.setLogSourcePanel(this);
            logSource.start();
        }

        public void setPaused(boolean paused) {
            if (paused) {
                pauseResumeButton.setIcon(resumeActiveIcon);
                pauseResumeButton.setRolloverIcon(resumeRolloverIcon);
                pauseResumeButton.setText("Resume");
                pauseResumeButton.setToolTipText("Resume the display of the current log buffer");
                statusLabel.setText("Paused");
                pauseResumeMenuItem.setText("Resume");
                pauseResumeMenuItem.setToolTipText("Resume the display of the current log buffer");
            }
            else {
                pauseResumeButton.setIcon(pauseActiveIcon);
                pauseResumeButton.setRolloverIcon(pauseRolloverIcon);
                pauseResumeButton.setText("Pause");
                pauseResumeButton.setToolTipText("Pause the display of the current log buffer");
                statusLabel.setText("Running");
                pauseResumeMenuItem.setText("Pause");
                pauseResumeMenuItem.setToolTipText("Pause the display of the current log buffer");
            }
            getLogSource().setPaused(paused);
            updateState();
        }

        /**
         * Finds a string in the current log source
         */
        public void find(String searchString, boolean caseSensitive, boolean forward) {
            int searchStringLen = searchString.length();

            String text = textArea.getText();
            int textLen = text.length();
            if (!caseSensitive) {
                text = text.toLowerCase();
                searchString = searchString.toLowerCase();
            }

            //
            // Figure out where we are starting the search from
            //
            int idx;
            int dot = caret.getDot();
            if (forward && dot == textLen) {
                dot = 0;
            }

            //
            // Search for the string
            //
            if (forward) {
                idx = text.indexOf(searchString, dot);
            } else {
                if (dot - searchStringLen >= 0) {
                    String atDot = text.substring(dot - searchStringLen, dot);
                    if (atDot.equals(searchString)) {
                        dot -= searchStringLen;
                    }
                }
                idx = text.substring(0, dot).lastIndexOf(searchString);
            }

            //
            // If we didn't find the string, find out if they want to search again
            //
            if (idx == -1) {
                if (forward) {
                    if (dot > 0) {
                        int choice = JOptionPane.showConfirmDialog(this, "\"" + searchString + "\" not found\nSearch from beginning of buffer?", "Search", JOptionPane.YES_NO_OPTION);
                        if (choice == 1) {
                            return;
                        }
                    }
                    idx = text.indexOf(searchString);
                } else {
                    if (dot < textLen) {
                        int choice = JOptionPane.showConfirmDialog(this, "\"" + searchString + "\" not found\nSearch from end of buffer?", "Search", JOptionPane.YES_NO_OPTION);
                        if (choice == 1) {
                            return;
                        }
                    }
                    idx = text.lastIndexOf(searchString);
                }
                if (idx == -1) {
                    JOptionPane.showMessageDialog(this, "\"" + searchString + "\" not found", "Text not found", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            int start = idx;
            int end = start + searchStringLen;

            caret.setDot(start);
            caret.moveDot(end);
            caret.setVisible(true);
            caret.setSelectionVisible(true);
        }

        public void updateState() {
            LogSourceState newState;
            if (currentContent instanceof MyTabbedPane) {
                MyTabbedPane myTabbedPane = (MyTabbedPane)currentContent;
                int selectedIndex = myTabbedPane.getSelectedIndex();
                if (logSource.isPaused()) {
                    if (logSource.hasNewDataDuringPause() && !cleared)
                        newState = LogSourceState.pausedNewData;
                    else
                        newState = LogSourceState.pausedSeenData;
                }
                else {
                    if (selectedIndex != tabIndex && !cleared)
                        newState = LogSourceState.newData;
                    else
                        newState = LogSourceState.seenData;
                }
                cleared = false;
                if (newState != currentState) {
                    currentState = newState;
                    ImageIcon newIcon = null;
                    switch (currentState) {
                        case newData:
                            newIcon = newDataIcon;
                            break;
                        case seenData:
                            newIcon = seenDataIcon;
                            break;
                        case pausedNewData:
                            newIcon = pausedNewDataIcon;
                            break;
                        case pausedSeenData:
                            newIcon = pausedSeenDataIcon;
                            break;
                    }
                    myTabbedPane.setIconAt(tabIndex, newIcon);
                }
            }
        }

        public synchronized void addChars(char[] buf, int len) {
            if (len > 0) {
                if (logSource.filterManager.isActive()) {
                    char lastChar = buf[len - 1];
                    boolean endsWithNewline = lastChar == '\n' || lastChar == '\r';

                    String s = new String(buf, 0, len);
                    StringTokenizer t = new StringTokenizer(s, "\r\n");
                    boolean first = true;
                    while (t.hasMoreTokens()) {
                        String l = t.nextToken();
                        if (first) {
                            first = false;
                            if (lastLineBuffer.length() > 0) {
                                l = lastLineBuffer.toString() + l;
                                lastLineBuffer.setLength(0);
                            }
                        }
                        if (!endsWithNewline && !t.hasMoreTokens())
                            lastLineBuffer.append(l);
                        else if (!logSource.filterManager.isFiltered(l)) {
                            updateBuffer.append(l).append('\n');
                            modified = true;
                        }
                    }
                }
                else {
                    updateBuffer.append(buf, 0, len);
                    modified = true;
                }
            }
        }

        public synchronized void addString(String s) {
            updateBuffer.append(s);
            modified = true;
        }

        public synchronized void clear() {
            clearOnNextUpdate = true;
            updateBuffer.setLength(0);
            modified = true;
        }

        public void close() {
            logSource.close();
            logSource.notify();
        }
        
        public synchronized void run() {
            if (clearOnNextUpdate) {
                clearOnNextUpdate = false;
                cleared = true;
                caret.setDot(0);
                try {
                    document.remove(0, document.getLength());
                }
                catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }

            int textLength = document.getLength();
            boolean moveCaretToEnd = textLength == 0 || caret.getDot() == textLength;

            int len = updateBuffer.length();
            if (len > 0) {
                textArea.append(updateBuffer.toString());
                updateBuffer.setLength(0);
                textLength = document.getLength();
            }
            modified = false;

            int overflow = textLength - logBufferSizeInBytes;
            if (overflow > 0) {
                if (overflow >= logBufferSizeInBytes)
                    overflow = logBufferSizeInBytes;
                textArea.replaceRange("", 0, overflow);
            }

            if (moveCaretToEnd)
                caret.setDot(textLength);

            fileLabel.setText(logSource.getExpandedFile());
            
            updateState();
        }

        public boolean isModified() {
            return modified;
        }
    }
}
