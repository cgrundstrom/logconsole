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
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.border.Border;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

/**
 * Static Utility methods for LogConsole
 */
public class Util {
    /**
     * Fetches an image from the classpath (jar file)
     */
    public static ImageIcon getIcon(String name) {
        URL iconURL = ClassLoader.getSystemResource("org/logconsole/images/" + name);
        if (iconURL == null)
            return null;
        return new ImageIcon(iconURL);
    }

    /**
     * Plays an audio clip. The clip is loaded from the classpath (jar file)
     */
    public static void playAudioClip(String name) {
        try {
            URL soundURL = ClassLoader.getSystemResource("org/logconsole/sounds/" + name);
            if (soundURL != null) {
                AudioClip clip = Applet.newAudioClip(soundURL);
                if (clip != null) {
                    clip.play();

                    //
                    // After we play the clip, we need to garbage collect it.
                    // If we don't do this, then no other application can
                    // use the sound card (ugly but true).
                    //
                    clip = null;
                    Runtime.getRuntime().gc();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a red border to a component. Useful for debugging component placement in a window
     */
    public static void addRedBorder(JComponent comp) {
        Border border1 = BorderFactory.createLineBorder(Color.red);
        Border border2 = BorderFactory.createCompoundBorder(border1, comp.getBorder());
        comp.setBorder(border2);
    }


    /**
     * Converts an exception to a string
     */
    public static String throwableToString(Throwable e) {
        StringWriter sw = new StringWriter(1024);
        PrintWriter out = new PrintWriter(sw);
        e.printStackTrace(out);
        return sw.toString();
    }

    public static String xmlEncode(String s) {
        if (s == null)
            return null;
        StringBuffer b = new StringBuffer();
        int len = s.length();
        for (int i = 0; i < len; i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '<':
                    b.append("&lt;");
                    break;
                case '>':
                    b.append("&gt;");
                    break;
                case '&':
                    b.append("&amp;");
                    break;
                case '"':
                    b.append("&quot;");
                    break;
                case '\'':
                    b.append("&apos;");
                    break;
                default:
                    b.append(ch);
            }
        }
        return b.toString();
    }
}
