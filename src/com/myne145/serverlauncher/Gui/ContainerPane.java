package com.myne145.serverlauncher.Gui;

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
import java.util.ArrayList;


public class ContainerPane extends JTabbedPane {
    private final ArrayList<JTabbedPane> serverTabbedPanes = new ArrayList<>();
    public ContainerPane() {
        setLayout(new BorderLayout());
        ArrayList<ButtonData> configData = Config.getData();
        for(int i = 0; i < Config.getData().size(); i++) {
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
            setToolTipTextAt(i, "Offline");
        }
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
            ServerConsoleTab selectedConsoleTab = (ServerConsoleTab) serverTabbedPanes.get(index).getComponentAt(0);
            WorldsTab worldsTab = (WorldsTab) serverTabbedPanes.get(index).getComponentAt(1);
            worldsTab.setIcons();

            if(Frame.areChartsEnabled)
                selectedConsoleTab.enableCharts();

            Runnable runnable = () -> {
                for(int i = 0; i < serverTabbedPanes.size(); i++) {
                    if(i != index) {
                        ServerConsoleTab serverConsoleTab = (ServerConsoleTab) serverTabbedPanes.get(i).getComponentAt(0);
                        serverConsoleTab.disableCharts();
                    }
                }
            };
            new Thread(runnable).start();

            ButtonData serverConfig = Config.getData().get(index);
            try {
                ServerDetails.serverName = serverConfig.serverName();
                ServerDetails.serverPath = serverConfig.serverPath();
                ServerDetails.serverId = serverConfig.serverId();
                Frame.userValues.put("SELECTED_SERVER_NAME", ServerDetails.serverName);
                Frame.userValues.put("SELECTED_SERVER_PATH", ServerDetails.serverPath.getAbsolutePath());
                new ServerPropertiesFile();
                NBTParser nbtParser = new NBTParser(); //reading NBT level.dat file for level name
                nbtParser.start();
                nbtParser.join();
                ServerDetails.serverLevelName = nbtParser.getLevelName();
            } catch (Exception ex) {
                // Frame.alert(AlertType.ERROR, Frame.getErrorDialogMessage(ex));
            }
        } else { //when "add server" was selected
            System.out.println("asdfdsaf");
        }
    }

    public void setChartsVisibility(boolean isVisible) {
        for(JTabbedPane tabbedPane : serverTabbedPanes) {
            ServerConsoleTab serverConsoleTab = (ServerConsoleTab) tabbedPane.getComponentAt(0);
            serverConsoleTab.cpuChart.isEnabled = isVisible;
            serverConsoleTab.cpuChart.setVisible(isVisible);

            serverConsoleTab.ramChart.isEnabled = isVisible;
            serverConsoleTab.ramChart.setVisible(isVisible);
        }
    }
    public void killAllServerProcesses() {
        for(JTabbedPane tabbedPane : serverTabbedPanes) {
            ServerConsoleTab serverConsoleTab = (ServerConsoleTab) tabbedPane.getComponentAt(0);
            serverConsoleTab.getServerConsoleArea().killAllProcesses();
        }
    }
}