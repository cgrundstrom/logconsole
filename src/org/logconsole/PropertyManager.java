package org.logconsole;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class PropertyManager {
    private File configurationFile;
    private HashMap<String,String> properties = new HashMap<String, String>();
    private boolean modified;

    public PropertyManager() throws Exception{
        configurationFile = new File(System.getProperty("user.home"), ".logconsole");
        if (configurationFile.exists()) {
            if (!configurationFile.isFile())
                throw new LogConsoleException(configurationFile + " is not a file");

            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
            Document document = domBuilder.parse(configurationFile);

            Node root = document.getDocumentElement();
            if (root.getNodeName().equals("settings")) {
                NodeList settingNodes = root.getChildNodes();
                int len0 = settingNodes.getLength();
                for (int i = 0 ; i < len0; i++) {
                    Node settingNode = settingNodes.item(i);
                    if (!(settingNode instanceof Element))
                        continue;
                    if (settingNode.getNodeName().equals("properties")) {
                        NodeList propertyNodes = settingNode.getChildNodes();
                        int len1 = propertyNodes.getLength();
                        for (int j = 0 ; j < len1; j++) {
                            Node propertyNode = propertyNodes.item(j);
                            if (!(propertyNode instanceof Element))
                                continue;
                            if (!propertyNode.getNodeName().equals("property"))
                                throw new Exception("Invalid XML tag in " + configurationFile + " :" + propertyNode.getNodeName());

                            NamedNodeMap map = propertyNode.getAttributes();
                            Node nameNode = map.getNamedItem("name");
                            if (nameNode == null)
                                throw new LogConsoleException("xml attribute 'name' not found in " + configurationFile);
                            String name = nameNode.getNodeValue();

                            Node valueNode = map.getNamedItem("value");
                            if (valueNode == null)
                                throw new LogConsoleException("xml tag 'value' not found for property '" + name + "' in " + configurationFile);
                            String value = valueNode.getNodeValue();

                            setProperty(name, value);
                        }
                    }
                    else
                        throw new LogConsoleException("Invalid Setting element in " + configurationFile + ": " + settingNode.getNodeName());
                }
            }
            else
                throw new LogConsoleException("Invalid XML root in " + configurationFile + ": " + root.getNodeName());
        }
        setDefaultValues();
        write(false);
    }

    private void setDefaultValues() throws LogConsoleException {
        if (properties.get("logBufferSizeInKBytes") == null) {
            setProperty("logBufferSizeInKBytes", 1024);
            modified = true;
        }

        if (properties.get("wrapLines") == null) {
            setProperty("wrapLines", true);
            modified = true;
        }

        if (properties.get("mainWindowLocationX") == null) {
            setProperty("mainWindowLocationX", 100);
            modified = true;
        }

        if (properties.get("mainWindowLocationY") == null) {
            setProperty("mainWindowLocationY", 100);
            modified = true;
        }

        if (properties.get("mainWindowSizeX") == null) {
            setProperty("mainWindowSizeX", 800);
            modified = true;
        }

        if (properties.get("mainWindowSizeY") == null) {
            setProperty("mainWindowSizeY", 600);
            modified = true;
        }

        if (properties.get("lastProjectDirectory") == null) {
            String home = System.getProperty("user.home");
            if (System.getProperty("os.name").startsWith("Windows"))
                home = home + "\\My Documents";
            setProperty("lastProjectDirectory", home);
            modified = true;
        }

        if (properties.get("lastLogFileDirectory") == null) {
            String home = System.getProperty("user.home");
            if (System.getProperty("os.name").startsWith("Windows"))
                home = home + "\\My Documents";
            setProperty("lastLogFileDirectory", home);
            modified = true;
        }

        if (properties.get("sleepTimeInSeconds") == null) {
            setProperty("sleepTimeInSeconds", 2);
            modified = true;
        }

        if (properties.get("lookAndFeel") == null) {
            setProperty("lookAndFeel", "MistAqua");
            modified = true;
        }
    }

    public File getConfigurationFile() {
        return configurationFile;
    }

    public String getStringProperty(String name) throws LogConsoleException {
        String value = properties.get(name);
        if (value == null)
            throw new LogConsoleException("invalid property: " + name);
        return value;
    }

    public String getStringProperty(String name, String defaultValue) {
        String value = properties.get(name);
        if (value == null)
            value = defaultValue;
        return value;
    }

    public int getIntegerProperty(String name) throws LogConsoleException {
        String s = getStringProperty(name);
        return Integer.parseInt(s);
    }

    public int getIntegerProperty(String name, int defaultValue) {
        String s = getStringProperty(name, null);
        if (s == null)
            return defaultValue;
        return Integer.parseInt(s);
    }

    public boolean getBooleanProperty(String name) throws LogConsoleException {
        String s = getStringProperty(name);
        return Boolean.parseBoolean(s);
    }

    public boolean getBooleanProperty(String name, boolean defaultValue) {
        String s = getStringProperty(name, null);
        if (s == null)
            return defaultValue;
        return Boolean.parseBoolean(s);
    }

    public void setProperty(String name, Object value) {
        String s = properties.get(name);
        String v = String.valueOf(value);
        if (s == null || !s.equals(v)) {
            properties.put(name, v);
            modified = true;
        }
    }

    public void write(boolean forceWrite) {
        if (modified || forceWrite) {
            try {
                PrintWriter out = new PrintWriter(configurationFile);
                try {
                    out.println("<settings>");

                    out.println("  <properties>");
                    ArrayList<String> names = new ArrayList<String>();
                    for (String name : properties.keySet())
                        names.add(name);
                    Collections.sort(names);

                    for (String name : names) {
                        String value = properties.get(name);
                        out.println("    <property name=\"" + name + "\" value=\"" + Util.xmlEncode(value) + "\"/>");
                    }
                    out.println("  </properties>");

                    out.println("</settings>");
                }
                finally {
                    out.close();
                }
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            modified = false;
        }
    }
}
