package com.myne145.serverlauncher.gui;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.myne145.serverlauncher.gui.tabs.AddServerTab;
import com.myne145.serverlauncher.gui.tabs.ServerConsoleTab;
import com.myne145.serverlauncher.gui.tabs.WorldsTab;
import com.myne145.serverlauncher.server.current.NBTParser;
import com.myne145.serverlauncher.server.current.CurrentServerInfo;
import com.myne145.serverlauncher.server.current.ServerPropertiesFile;
import com.myne145.serverlauncher.server.MCServer;
import com.myne145.serverlauncher.server.Config;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

import static com.myne145.serverlauncher.gui.Window.SERVER_STATUS_ICON_DIMENSION;


public class ContainerPane extends JTabbedPane {
    private final ArrayList<JTabbedPane> serverTabbedPanes = new ArrayList<>();
    public ContainerPane() {
        setLayout(new BorderLayout());
//        setUI(new CustomTabbedPaneUI(new Color(76, 76, 80), new Color(51, 51, 52)));
        ArrayList<MCServer> configData = Config.getData();
        for(int i = 0; i < Config.getData().size(); i++) {
            JTabbedPane tabbedPane = new JTabbedPane(RIGHT);
            tabbedPane.addTab("Console", new ServerConsoleTab(this, i));
            tabbedPane.addTab("Worlds", new WorldsTab());
//            tabbedPane.setIconAt(0, new FlatSVGIcon(new File("resources/console_icon.svg")).derive(16,16));
            serverTabbedPanes.add(tabbedPane);
        }
        this.setTabPlacement(LEFT);
        for(int i = 0; i < serverTabbedPanes.size(); i++) {
            String serverName = configData.get(i).serverName();
            if(serverName.length() > 25)
                serverName = serverName.substring(0, 25) + "...";
            addTab(serverName, serverTabbedPanes.get(i));
            if(Config.getData().get(i).serverPath().exists()) {
                setIconAt(i, new ImageIcon(new ImageIcon("resources/server_offline.png").getImage().getScaledInstance(SERVER_STATUS_ICON_DIMENSION,SERVER_STATUS_ICON_DIMENSION, Image.SCALE_SMOOTH)));
                setToolTipTextAt(i, "Offline");
            } else {
                setEnabledAt(i, false);
                setIconAt(i, new FlatSVGIcon(new File("resources/server_errored.svg")).derive(SERVER_STATUS_ICON_DIMENSION, SERVER_STATUS_ICON_DIMENSION));
                setToolTipTextAt(i, "Errored - Server file missing");
            }
        }

        addTab("Add server", new AddServerTab());
        setIconAt(getTabCount() - 1, new ImageIcon(new ImageIcon("resources/add_server.png").getImage().getScaledInstance(24,24, Image.SCALE_SMOOTH)));
//        for(int i = 0; i < getTabCount() - 1; i++)
//            setIconAt(i, new ImageIcon(new ImageIcon("resources/server_offline.png").getImage().getScaledInstance(SERVER_STATUS_ICON_DIMENSION,SERVER_STATUS_ICON_DIMENSION, Image.SCALE_SMOOTH)));
        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        setSelectedIndex(CurrentServerInfo.serverId - 1);
        onButtonClicked(CurrentServerInfo.serverId - 1);

        ServerConsoleTab selectedConsoleTab = (ServerConsoleTab) serverTabbedPanes.get(CurrentServerInfo.serverId - 1).getComponentAt(0);
        selectedConsoleTab.cpuChart.isEnabled = true;
        selectedConsoleTab.cpuChart.updateChartData();

        addChangeListener(e -> onButtonClicked(this.getSelectedIndex()));
    }

    public void onButtonClicked(int index) {
//        setBackgroundAt(index, Color.RED);
        if (index != this.getTabCount() - 1) { //code that runs when u click all the server tabs
            ServerConsoleTab selectedConsoleTab = (ServerConsoleTab) serverTabbedPanes.get(index).getComponentAt(0);

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

            MCServer mcServerConfig = Config.getData().get(index);
            try {
                CurrentServerInfo.serverName = mcServerConfig.serverName();
                CurrentServerInfo.serverPath = mcServerConfig.serverPath();
                CurrentServerInfo.serverId = mcServerConfig.serverId();
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

            WorldsTab worldsTab = (WorldsTab) serverTabbedPanes.get(index).getComponentAt(1);
            worldsTab.setIcons();

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