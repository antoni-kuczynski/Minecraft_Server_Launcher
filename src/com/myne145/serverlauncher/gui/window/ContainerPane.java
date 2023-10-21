package com.myne145.serverlauncher.gui.window;

import com.myne145.serverlauncher.gui.tabs.serverdashboard.ServerConsoleTab;
import com.myne145.serverlauncher.gui.tabs.worldsmanager.WorldsManagerTab;
//import com.myne145.serverlauncher.server.current.ServerProperties;
import com.myne145.serverlauncher.server.MCServer;
import com.myne145.serverlauncher.server.Config;
import com.myne145.serverlauncher.utils.FileDetailsUtils;
import com.myne145.serverlauncher.utils.ServerIcon;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static com.myne145.serverlauncher.gui.window.Window.SERVER_STATUS_ICON_DIMENSION;


public class ContainerPane extends JTabbedPane {
    private static final ArrayList<JTabbedPane> serverTabbedPanes = new ArrayList<>();
    private static final JMenuItem openServerFolderItem = Window.getMenu().getMenu(0).getItem(0);

    @Override
    public void setIconAt(int index, Icon icon) {
        TabLabelWithFileTransfer tabLabel = (TabLabelWithFileTransfer) this.getTabComponentAt(index);
        tabLabel.setIcon(icon);
    }

    @Override
    public Icon getIconAt(int index) {
        TabLabelWithFileTransfer tabLabel = (TabLabelWithFileTransfer) this.getTabComponentAt(index);
        return tabLabel.getIcon();
    }

    @Override
    public void setTitleAt(int index, String title) {
        TabLabelWithFileTransfer tabLabel = (TabLabelWithFileTransfer) this.getTabComponentAt(index);
        tabLabel.setText(title);
    }

    @Override
    public void setEnabledAt(int index, boolean enabled) {
        super.setEnabledAt(index, enabled);
        TabLabelWithFileTransfer tabLabel = (TabLabelWithFileTransfer) this.getTabComponentAt(index);
        tabLabel.setEnabled(enabled);
    }

    public ContainerPane() {
        setLayout(new BorderLayout());
        ArrayList<MCServer> configData = Config.getData();
        for(int i = 0; i < Config.getData().size(); i++) {
//            ServerProperties.reloadLevelNameGlobalValue();
            JTabbedPane tabbedPane = new JTabbedPane(RIGHT);
            tabbedPane.addTab("Console", new ServerConsoleTab(this, i));
            tabbedPane.addTab("Worlds", new WorldsManagerTab(this, i));
            tabbedPane.setTabComponentAt(0, new TabLabelWithFileTransfer("Console", tabbedPane,0));
            tabbedPane.setTabComponentAt(1, new TabLabelWithFileTransfer("Worlds", tabbedPane,1));
            serverTabbedPanes.add(tabbedPane);
        }
        this.setTabPlacement(LEFT);
        for(int i = 0; i < serverTabbedPanes.size(); i++) {
            String serverName = configData.get(i).serverName();
            if(serverName.length() > 25)
                serverName = serverName.substring(0, 25) + "...";
            addTab(serverName, serverTabbedPanes.get(i));
            setTabComponentAt(i, new ServerTabLabel(serverName, this, i));

            if(Config.getData().get(i).serverJarPath().exists()) {
                setIconAt(i, ServerIcon.getServerIcon(ServerIcon.OFFLINE));
                setToolTipTextAt(i, "Offline");
            } else {
                setEnabledAt(i, false);
                setIconAt(i, ServerIcon.getServerIcon(ServerIcon.ERRORED));
                setToolTipTextAt(i, "Errored - Server executable missing");
            }
        }
        System.out.println(serverTabbedPanes);
        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        setSelectedIndex(Window.userValues.getInt("prefs_server_id", 1) - 1);

        onTabSwitched(Window.userValues.getInt("prefs_server_id", 1) - 1);
        addChangeListener(e -> onTabSwitched(this.getSelectedIndex()));
    }

    public void onTabSwitched(int index) {
        for(int i = 0; i <= getTabCount(); i++) {
            if(i == index)
                setBackgroundAt(index, new Color(64, 75, 93));
            else
                setBackgroundAt(index, new Color(51, 51, 52));
        }

        if(openServerFolderItem != null) {
            openServerFolderItem.setText("<html>Open current server's folder\n<center><sub>" + FileDetailsUtils.abbreviate(Config.getData().get(index).serverPath().getAbsolutePath(), 27) + "</sub></center></html>");
        }

        for(int i = 0; i < getTabCount(); i++) {
            ServerConsoleTab consoleTab = (ServerConsoleTab) serverTabbedPanes.get(i).getComponentAt(0);
            if(i != index) {
                consoleTab.cpuChart.setVisible(false);
            } else {
                consoleTab.cpuChart.setVisible(true);
            }
            System.out.println(i + "\t" + consoleTab.cpuChart.isEnabled);
        }

            Runnable runnable = () -> {
                for(int i = 0; i < serverTabbedPanes.size(); i++) {
                    if(i != index) {
                        ServerConsoleTab serverConsoleTab = (ServerConsoleTab) serverTabbedPanes.get(i).getComponentAt(0);
                        serverConsoleTab.disableCharts();
                    }
                }
            };
            new Thread(runnable).start();

            ServerConsoleTab selectedConsoleTab = (ServerConsoleTab) serverTabbedPanes.get(index).getComponentAt(0);

            if(com.myne145.serverlauncher.gui.window.Window.areChartsEnabled)
                selectedConsoleTab.enableCharts();
            else
                setChartsVisibility(false);

            MCServer mcServerConfig = Config.getData().get(index);

//            CurrentServerInfo.serverName = mcServerConfig.serverName();
//            CurrentServerInfo.serverPath = mcServerConfig.serverPath();
//            CurrentServerInfo.serverId = mcServerConfig.serverId();
            com.myne145.serverlauncher.gui.window.Window.userValues.put("SELECTED_SERVER_NAME", mcServerConfig.serverName());
            Window.userValues.put("SELECTED_SERVER_PATH", mcServerConfig.serverPath().getAbsolutePath());
            Window.userValues.putInt("prefs_server_id", index + 1);
//            ServerProperties.reloadLevelNameGlobalValue(mcServerConfig);
            Config.reloadServersWorldPath(mcServerConfig);


            WorldsManagerTab worldsManagerTab = (WorldsManagerTab) serverTabbedPanes.get(index).getComponentAt(1);
            worldsManagerTab.setIcons();
            if(mcServerConfig.worldPath().exists()) {
                worldsManagerTab.getWorldsInfoPanels().updateServerWorldInformation(mcServerConfig.worldPath());
            }

            for (JTabbedPane serverTabbedPane : serverTabbedPanes) {
                ServerConsoleTab c = (ServerConsoleTab) serverTabbedPane.getComponentAt(0);
                c.getServerConsoleArea().isVisible = false;
            }

            selectedConsoleTab.getServerConsoleArea().isVisible = true;
            try {
                selectedConsoleTab.getServerConsoleArea().setTextFromLatestLogFile();
            } catch (Exception e) {
                throw new RuntimeException();
            }

//        } else { //when "add server" was selected
//
//        }
    }

    public void setChartsVisibility(boolean isVisible) {
        for(JTabbedPane tabbedPane : serverTabbedPanes) {
            ServerConsoleTab serverConsoleTab = (ServerConsoleTab) tabbedPane.getComponentAt(0);
//            serverConsoleTab.cpuChart.isEnabled = isVisible;
            serverConsoleTab.cpuChart.setVisible(isVisible);

//            serverConsoleTab.ramChart.isEnabled = isVisible;
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