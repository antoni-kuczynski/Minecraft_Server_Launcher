package Gui;

import Enums.RunMode;
import SelectedServer.NBTParser;
import SelectedServer.ServerDetails;
import SelectedServer.ServerPropertiesFile;
import Server.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class GlobalButtonsPanel extends JPanel {
    private static GlobalButtonsPanel currentPanel;
    public static WorldsTab worldsTab;
//    private final Preferences userValues;


    public GlobalButtonsPanel() throws IOException, InterruptedException {
//        this.userValues = userValues;
        setLayout(new BorderLayout(10, 10));
        JButton openAppsConfigFile = new JButton("Config File");
//        ServerDetails.serverName = userValues.get("SELECTED_SERVER_NAME", "ERROR");
//        ServerDetails.serverPath = userValues.get("SELECTED_SERVER_PATH", "ERROR");

        JButton openGlobalFolder = new JButton("Global Server Directory");
        openGlobalFolder.addActionListener(e -> new Runner(RunMode.GLOBAL_FOLDER).start());
        openAppsConfigFile.addActionListener(e -> new Runner(RunMode.CONFIG_FILE).start());

        JPanel selServerManually = new JPanel();

        selServerManually.setLayout(new BorderLayout());

        JPanel openConfigAndBackupWorldButtons = new JPanel(new BorderLayout());
        openConfigAndBackupWorldButtons.add(openAppsConfigFile, BorderLayout.LINE_START);
        openConfigAndBackupWorldButtons.add(Box.createRigidArea(new Dimension(5,5)), BorderLayout.CENTER);
        if(!Config.globalServerFolder.equals(""))
            openConfigAndBackupWorldButtons.add(openGlobalFolder, BorderLayout.LINE_END);

        Dimension dimension = new Dimension(10, 1);
        add(Box.createRigidArea(dimension), BorderLayout.PAGE_START);
        add(openConfigAndBackupWorldButtons, BorderLayout.LINE_START);
        add(selServerManually, BorderLayout.LINE_END);
        add(Box.createRigidArea(dimension), BorderLayout.PAGE_END);

        new ServerPropertiesFile(); //this needs a refactor - makes level-name actually update TODO
        NBTParser nbtParser = new NBTParser(); //reading NBT level.dat file for level name
        nbtParser.start();
        nbtParser.join();
        ServerDetails.serverLevelName = nbtParser.getLevelName(); //issue #64 fix
//        addWorldsPanel.setIcons();
    }

    public static void setServerVariables(String text, String serverPath, int serverId) {
        ServerDetails.serverName = text;
        ServerDetails.serverPath = serverPath;
        ServerDetails.serverId = serverId;
        ServerDetails.serverLatestLogFilePath = new File(serverPath + "/logs/latest.log");
        currentPanel.reloadButtonText(); //removed redundant addWorldPanel.repaint() calls and replaces panel.repaint() to decrease RAM usage
    }

    public void setPanels(GlobalButtonsPanel panel, WorldsTab worldsTab) {
        GlobalButtonsPanel.currentPanel = panel;
        GlobalButtonsPanel.worldsTab = worldsTab;
        worldsTab.setIcons();
    }

    private void reloadButtonText() {
//        userValues.put("SELECTED_SERVER_NAME", ServerDetails.serverName);
//        userValues.put("SELECTED_SERVER_PATH", ServerDetails.serverPath);
    }
}