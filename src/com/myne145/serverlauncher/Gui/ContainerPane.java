package com.myne145.serverlauncher.Gui;

import com.myne145.serverlauncher.Enums.AlertType;
import com.myne145.serverlauncher.Gui.Charts.CPUChart;
import com.myne145.serverlauncher.Gui.Charts.RAMChart;
import com.myne145.serverlauncher.Gui.Tabs.AddServerTab;
import com.myne145.serverlauncher.Gui.Tabs.ServerConsoleTab;
import com.myne145.serverlauncher.Gui.Tabs.WorldsTab;
import com.myne145.serverlauncher.SelectedServer.NBTParser;
import com.myne145.serverlauncher.SelectedServer.ServerDetails;
import com.myne145.serverlauncher.SelectedServer.ServerPropertiesFile;
import com.myne145.serverlauncher.Server.ButtonData;
import com.myne145.serverlauncher.Server.Config;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

import static com.myne145.serverlauncher.Gui.GlobalButtonsPanel.worldsTab;

public class ContainerPane extends JTabbedPane {
    public static CPUChart cpuChart = new CPUChart();
    public static RAMChart ramChart = new RAMChart();

    public ContainerPane() {
        Config config;
        try {
            config = new Config();
        } catch (IOException e) {
            Frame.alert(AlertType.ERROR, Frame.getErrorDialogMessage(e));
            throw new RuntimeException(e); //stop going any further
        }

        ArrayList<JTabbedPane> serverTabbedPanes = new ArrayList<>();
        ArrayList<ButtonData> configData = config.getData();
        for(int i = 0; i < configData.size(); i++) {
            JTabbedPane tabbedPane = new JTabbedPane(RIGHT);
            tabbedPane.addTab("Console", new ServerConsoleTab(this, i));
            tabbedPane.addTab("Worlds", new WorldsTab());
            serverTabbedPanes.add(tabbedPane);
        }
        this.setTabPlacement(LEFT);
        for(int i = 0; i < serverTabbedPanes.size(); i++) {
            String serverName = configData.get(i).serverName();
            if(serverName.length() > 25)
                serverName = serverName.substring(0, 25) + "...";
            addTab(serverName, serverTabbedPanes.get(i));
        }
//        setBackground(new Color(201, 10, 10));
//        setBorder(new RoundedPanelBorder(Color.BLACK, 10));
        addTab("Add server", new AddServerTab());
        setIconAt(getTabCount() - 1, new ImageIcon(new ImageIcon("resources/addServer.png").getImage().getScaledInstance(24,24, Image.SCALE_SMOOTH)));
        for(int i = 0; i < getTabCount() - 1; i++)
            setIconAt(i, new ImageIcon(new ImageIcon("resources/offline.png").getImage().getScaledInstance(32,32, Image.SCALE_SMOOTH)));
        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        setSelectedIndex(ServerDetails.serverId - 1);
        addChangeListener(e -> onButtonClicked(this.getSelectedIndex()));
    }

    public void onButtonClicked(int index) {
        if (index != this.getTabCount() - 1) { //code that runs when u click all the server tabs
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
                ServerDetails.serverName = serverConfig.serverName();
                ServerDetails.serverPath = serverConfig.serverPath();
                ServerDetails.serverId = serverConfig.serverId();
                Frame.userValues.put("SELECTED_SERVER_NAME", ServerDetails.serverName);
                Frame.userValues.put("SELECTED_SERVER_PATH", ServerDetails.serverPath.getAbsolutePath());
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
        } else { //when "add server" was selected
            System.out.println("asdfdsaf");
        }
    }
}