package com.myne145.serverlauncher.Gui;

import com.myne145.serverlauncher.Gui.Tabs.AddServerTab;
import com.myne145.serverlauncher.Gui.Tabs.ServerConsoleTab;
import com.myne145.serverlauncher.Gui.Tabs.WorldsTab;
import com.myne145.serverlauncher.Server.Current.NBTParser;
import com.myne145.serverlauncher.Server.Current.CurrentServerInfo;
import com.myne145.serverlauncher.Server.Current.ServerPropertiesFile;
import com.myne145.serverlauncher.Server.MCServer;
import com.myne145.serverlauncher.Server.Config;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static com.myne145.serverlauncher.Gui.Window.SERVER_STATUS_ICON_DIMENSION;


public class ContainerPane extends JTabbedPane {
    private final ArrayList<JTabbedPane> serverTabbedPanes = new ArrayList<>();
    public ContainerPane() {
        setLayout(new BorderLayout());
        ArrayList<MCServer> configData = Config.getData();
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
        setIconAt(getTabCount() - 1, new ImageIcon(new ImageIcon("resources/add_server.png").getImage().getScaledInstance(24,24, Image.SCALE_SMOOTH)));
        for(int i = 0; i < getTabCount() - 1; i++)
            setIconAt(i, new ImageIcon(new ImageIcon("resources/server_offline.png").getImage().getScaledInstance(SERVER_STATUS_ICON_DIMENSION,SERVER_STATUS_ICON_DIMENSION, Image.SCALE_SMOOTH)));
        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        setSelectedIndex(CurrentServerInfo.serverId - 1);
        addChangeListener(e -> onButtonClicked(this.getSelectedIndex()));
    }

    public void onButtonClicked(int index) {
        if (index != this.getTabCount() - 1) { //code that runs when u click all the server tabs
            ServerConsoleTab selectedConsoleTab = (ServerConsoleTab) serverTabbedPanes.get(index).getComponentAt(0);
            WorldsTab worldsTab = (WorldsTab) serverTabbedPanes.get(index).getComponentAt(1);
            worldsTab.setIcons();

            if(Window.areChartsEnabled)
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

            MCServer MCServerConfig = Config.getData().get(index);
            try {
                CurrentServerInfo.serverName = MCServerConfig.serverName();
                CurrentServerInfo.serverPath = MCServerConfig.serverPath();
                CurrentServerInfo.serverId = MCServerConfig.serverId();
                Window.userValues.put("SELECTED_SERVER_NAME", CurrentServerInfo.serverName);
                Window.userValues.put("SELECTED_SERVER_PATH", CurrentServerInfo.serverPath.getAbsolutePath());
                new ServerPropertiesFile();
                NBTParser nbtParser = new NBTParser(); //reading NBT level.dat file for level name
                nbtParser.start();
                nbtParser.join();
                CurrentServerInfo.serverLevelName = nbtParser.getLevelName();
            } catch (Exception ex) {
                // Frame.alert(AlertType.ERROR, Frame.getErrorDialogMessage(ex));
            }

            for(int i = 0; i < serverTabbedPanes.size(); i++) {
                ServerConsoleTab c = (ServerConsoleTab) serverTabbedPanes.get(i).getComponentAt(0);
                c.getServerConsoleArea().isVisible = false;
            }

            selectedConsoleTab.getServerConsoleArea().isVisible = true;
            try {
                selectedConsoleTab.getServerConsoleArea().setTextFromLatestLogFile();
            } catch (Exception e) {
                throw new RuntimeException();
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

    public void updateServerButtonsSizes() {
        ArrayList<Icon> icons = new ArrayList<>();
        for(int i = 0; i < serverTabbedPanes.size(); i++) {
            icons.add(getIconAt(i));
        }
        for(int i = 0; i < serverTabbedPanes.size(); i++) {
            ImageIcon imageIcon = (ImageIcon) icons.get(i);
            setIconAt(i, new ImageIcon(imageIcon.getImage().getScaledInstance(SERVER_STATUS_ICON_DIMENSION, SERVER_STATUS_ICON_DIMENSION, Image.SCALE_SMOOTH)));
        }
    }
}