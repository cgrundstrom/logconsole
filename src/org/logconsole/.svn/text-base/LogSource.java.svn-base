package org.logconsole;

import javax.swing.SwingUtilities;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;

public class LogSource extends Thread {
    private String displayName;
    private String unexpandedFile;
    private String expandedFile;
    private LogConsole.LogSourcePanel logSourcePanel;
    private boolean shutdown;
    private boolean paused;
    private boolean newDataDuringPause;
    private boolean enabled;
    public FilterManager filterManager;

    public LogSource(String displayName, String unexpandedFile, boolean enabled) {
        this.displayName = displayName;
        this.unexpandedFile = unexpandedFile;
        this.enabled = enabled;
        expandedFile = LogConsole.variableManager.expandVariables(unexpandedFile);
        filterManager = new FilterManager();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getExpandedFile() {
        return expandedFile;
    }

    public String getUnexpandedFile() {
        return unexpandedFile;
    }

    public void setLogSourcePanel(LogConsole.LogSourcePanel logSourcePanel) {
        this.logSourcePanel = logSourcePanel;
    }

    public LogConsole.LogSourcePanel getLogSourcePanel() {
        return logSourcePanel;
    }

    public void close() {
        shutdown = true;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
        if (!paused)
            newDataDuringPause = false;
    }

    public boolean isPaused() {
        return paused;
    }

    public void clear() {
        if (logSourcePanel != null)
            logSourcePanel.clear();
    }

    public boolean hasNewDataDuringPause() {
        return newDataDuringPause;
    }

    public void run() {
        long lastReadLocation = 0;
        char[] buf = new char[4 * 1024];
        boolean fileNotFound = false;

        while (!shutdown) {
            if (!newDataDuringPause) {
                expandedFile = LogConsole.variableManager.expandVariables(unexpandedFile);
                File f = new File(expandedFile);
                if (f.exists()) {
                    if (fileNotFound) {
                        fileNotFound = false;
                        lastReadLocation = 0;
                        logSourcePanel.clear();
                    }

                    long len = f.length();
                    if (len < lastReadLocation) {
                        lastReadLocation = 0;
                        logSourcePanel.clear();
                    }

                    if (len > lastReadLocation) {
                        if (paused) {
                            newDataDuringPause = true;
                            SwingUtilities.invokeLater(logSourcePanel);
                        }
                        else {
                            try {
                                BufferedReader in = new BufferedReader(new FileReader(f));
                                try {
                                    if (len - lastReadLocation > LogConsole.logBufferSizeInBytes)
                                        lastReadLocation = len - LogConsole.logBufferSizeInBytes;

                                    if (lastReadLocation > 0)
                                        in.skip(lastReadLocation);

                                    // Read in as much data as we can
                                    while (in.ready()) {
                                        int ret = in.read(buf);
                                        if (ret <= 0)
                                            break;
                                        lastReadLocation += ret;
                                        logSourcePanel.addChars(buf, ret);
                                    }
                                }
                                finally {
                                    in.close();
                                }
                            }
                            catch (IOException e) {
                                logSourcePanel.clear();
                                logSourcePanel.addString("Error reading " + expandedFile + ": " + e.toString());
                            }
                        }
                    }
                }
                else {
                    if (!fileNotFound) {
                        fileNotFound = true;
                        logSourcePanel.clear();
                        logSourcePanel.addString(expandedFile + " not found");
                    }
                }
            }

            if (logSourcePanel.isModified())
                SwingUtilities.invokeLater(logSourcePanel);

            try {
                synchronized (this) {
                    wait(LogConsole.sleepTimeInMilliSeconds);
                }
            }
            catch (InterruptedException ignored) {
            }
        }
    }
}
