package Gui;

import Servers.ButtonData;
import Servers.Config;
import Servers.Run;
import Servers.Runner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.IOException;
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
        Config config = null;
        try {
            config = new Config();
        } catch(IOException e) {
            Frame.alert(AlertType.FATAL, e.getMessage());
        }
        JLabel selServerTitle = new JLabel(" or select server here:");
        JComboBox<String> serverSelection = new JComboBox<>();
        JPanel selServerManually = new JPanel();

        for(ButtonData btnData : config.getData()) {
            serverSelection.addItem(btnData.getButtonText());
        }

        serverSelection.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED) {

            }
        });

        selServerManually.setLayout(new BorderLayout());
        selServerManually.add(openServerFolder, BorderLayout.LINE_START);
        selServerManually.add(selServerTitle, BorderLayout.CENTER);
        selServerManually.add(serverSelection, BorderLayout.LINE_END);

        add(openCfg, BorderLayout.LINE_START);
//        add(openServerFolder, BorderLayout.CENTER);
        add(selServerManually, BorderLayout.LINE_END);
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
