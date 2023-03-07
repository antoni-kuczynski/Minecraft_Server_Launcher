package Gui;

import Servers.Run;
import Servers.Runner;

import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;

public class ConfigStuffPanel extends JPanel {
    private final JButton openServerFolder;
    private static String servName;
    private static String servPath;
    private static ConfigStuffPanel panel;
    private final Preferences preferences;
    public ConfigStuffPanel(Preferences preferences) {
        this.preferences = preferences;
        setLayout(new BorderLayout(10, 10));
        JButton openCfg = new JButton("Open App's Config File");
        servName = preferences.get("SELECTED_SERVER_NAME", "ERROR");
        servPath = preferences.get("SELECTED_SERVER_PATH", "ERROR");
        openServerFolder = new JButton("Open " + preferences.get("SELECTED_SERVER_NAME", "ERROR") + "'s Server Folder");

        openCfg.addActionListener(e -> new Runner(Run.CONFIG_FILE).start());

        openServerFolder.addActionListener(e -> new Runner(Run.SERVER_FOLDER, servPath).start());

        add(openCfg, BorderLayout.LINE_START);
        add(openServerFolder, BorderLayout.LINE_END);
    }

    public static void setServerVariables(String text, String serverPath) {
        servName = text;
        servPath = serverPath;
        panel.repaint();
    }

    public void setPanel(ConfigStuffPanel panel) {
        ConfigStuffPanel.panel = panel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        preferences.put("SELECTED_SERVER_NAME", servName);
        preferences.put("SELECTED_SERVER_PATH", servPath);
        openServerFolder.setText("Open " + servName + "'s Server Folder");
    }
}
