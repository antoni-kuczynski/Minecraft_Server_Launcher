package Gui;

import Servers.*;

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

        Config finalConfig = config;
        serverSelection.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED) {
                servName = (String) e.getItem();
                servPath = finalConfig.getData().get(serverSelection.getSelectedIndex()).getPathToServerFolder(); //This is a very awful solution - if SOMEHOW indexes of the buttons won't correspond to the JComboBoxes's indexes, this code is fucked
                panel.repaint();
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

    public static String getServName() {
        return servName;
    }

    public static String getServPath() {
        return servPath;
    }
}
