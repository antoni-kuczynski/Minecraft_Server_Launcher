package Gui;

import CustomJComponents.RoundedPanelBorder;
import Enums.AlertType;
import SelectedServer.NBTParser;
import SelectedServer.ServerDetails;
import SelectedServer.ServerPropertiesFile;
import Server.ButtonData;
import Server.Config;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

import static Gui.GlobalButtonsPanel.worldsTab;

public class ContainerPane extends JTabbedPane {

    public ContainerPane() {
        System.out.println("a");
        Config config;
        try {
            config = new Config();
        } catch (IOException e) {
            Frame.alert(AlertType.ERROR, Frame.getErrorDialogMessage(e));
            throw new RuntimeException(e); //stop going any further
        }
        System.out.println("b");
        System.out.println("config size: " + config.getData().size());

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
        for(int i = 0; i < getTabCount(); i++)
            setIconAt(i, new ImageIcon(new ImageIcon("resources/offline.png").getImage().getScaledInstance(32,32, Image.SCALE_SMOOTH)));
        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

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
            ServerDetails.serverName = serverConfig.getButtonText();
            ServerDetails.serverPath = serverConfig.getPathToServerFolder();
            ServerDetails.serverId = serverConfig.getServerId();
            Frame.userValues.put("SELECTED_SERVER_NAME", ServerDetails.serverName);
            Frame.userValues.put("SELECTED_SERVER_PATH", ServerDetails.serverPath);
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
        }
}