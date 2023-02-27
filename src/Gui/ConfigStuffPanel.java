package Gui;

import Servers.Run;
import Servers.Runner;

import javax.swing.*;
import java.awt.*;

public class ConfigStuffPanel extends JPanel {
    private final JButton openServerFolder;
    private static String servName;
    private static String servPath;
    private static ConfigStuffPanel panel;
    public ConfigStuffPanel() {
        setLayout(new BorderLayout(10, 10));
        JButton openCfg = new JButton("Open App's Config File");
        openServerFolder = new JButton("Open Last Opened Server's File Folder");

        openCfg.addActionListener(e -> {
            new Runner(Run.CONFIG_FILE).start();
        });

        openServerFolder.addActionListener(e -> {
            new Runner(Run.SERVER_FOLDER, servPath).start();
        });

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
        if(servName == null)
            openServerFolder.setEnabled(false);
        else {
            openServerFolder.setEnabled(true);
            openServerFolder.setText("Open " + servName + "'s Server Folder");
        }
    }
}
