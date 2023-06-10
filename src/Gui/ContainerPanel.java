package Gui;

import Enums.AlertType;
import SelectedServer.NBTParser;
import SelectedServer.ServerDetails;
import SelectedServer.ServerPropertiesFile;
import Server.ButtonData;
import Server.Config;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static Gui.ServerSelectionPanel.worldsTab;

public class ContainerPanel extends JTabbedPane {

    public ContainerPanel() {
        ServerConsoleTab serverConsoleTab = new ServerConsoleTab();
//        JTabbedPane serverPageSwitcher = new JTabbedPane(JTabbedPane.RIGHT);
//        serverPageSwitcher.addTab("Console", serverConsoleTab);
//        serverPageSwitcher.addTab("Worlds", worldsTab);
//
//        JTabbedPane serverPageSwitcher2 = new JTabbedPane(JTabbedPane.RIGHT);
//        serverPageSwitcher2.addTab("Console", new ServerConsoleTab());
//        serverPageSwitcher2.addTab("Worlds", new WorldsTab());

        Config config;
        try {
            config = new Config();
        } catch (IOException e) {
            Frame.alert(AlertType.ERROR, Frame.getErrorDialogMessage(e));
            throw new RuntimeException(e); //stop going any further
        }
        System.out.println("config size: " + config.getData().size());

        ArrayList<JTabbedPane> serverTabbedPanes = new ArrayList<>();
        ArrayList<ButtonData> configData = config.getData();
        for(int i = 0; i < configData.size(); i++) {
            JTabbedPane tabbedPane = new JTabbedPane(RIGHT);
            tabbedPane.addTab("Console", new ServerConsoleTab());
            tabbedPane.addTab("Worlds", new WorldsTab());
            serverTabbedPanes.add(tabbedPane);
        }

        for(int i = 0; i < serverTabbedPanes.size(); i++) {
            String serverName = configData.get(i).serverName();
            if(serverName.length() > 25)
                serverName = serverName.substring(0, 25) + "...";
            addTab(serverName, serverTabbedPanes.get(i));
        }

        this.setTabPlacement(LEFT);
//        this.addTab("Test", serverPageSwitcher);
//        this.addTab("Test2", serverPageSwitcher2);

        addChangeListener(e -> onButtonClicked(this.getSelectedIndex()));
    }

    public void onButtonClicked(int index) {
        Config config;
        try {
            config = new Config();
        } catch (IOException ex) {
            Frame.alert(AlertType.ERROR, Frame.getErrorDialogMessage(ex));
            return;
        }
//        int index = Integer.parseInt(e.getActionCommand());
        ButtonData serverConfig = config.getData().get(index);
        try {
            ServerSelectionPanel.setServerVariables(serverConfig.getButtonText(), serverConfig.getPathToServerFolder(), serverConfig.getServerId());
            new ServerPropertiesFile(); //this needs a refactor - makes level-name actually update TODO
            NBTParser nbtParser = new NBTParser(); //reading NBT level.dat file for level name
            nbtParser.start();
            nbtParser.join();
            ServerDetails.serverLevelName = nbtParser.getLevelName();
        } catch (Exception ex) {
            // Frame.alert(AlertType.ERROR, Frame.getErrorDialogMessage(ex));
        }
//        ServerSelectionPanel.getServerSelection().setSelectedIndex(index);
        worldsTab.setIcons();
            //ij darcula theme
            //button color rgb(85, 88, 90)
            //bg color rgb(60, 63, 65)
        }
    }
