package com.myne145.serverlauncher.gui.window;

import com.formdev.flatlaf.ui.FlatTabbedPaneUI;
import com.myne145.serverlauncher.gui.components.ServerTabLabel;
import com.myne145.serverlauncher.gui.components.TabLabelWithFileTransfer;
import com.myne145.serverlauncher.gui.components.OpenContextMenuItem;
import com.myne145.serverlauncher.gui.tabs.serverdashboard.ServerDashboardTab;
import com.myne145.serverlauncher.gui.tabs.worldsmanager.WorldsManagerTab;
import com.myne145.serverlauncher.server.MCServer;
import com.myne145.serverlauncher.server.Config;
import com.myne145.serverlauncher.utils.Colors;
import com.myne145.serverlauncher.utils.DefaultIcons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import static com.myne145.serverlauncher.gui.window.Window.SERVER_STATUS_ICON_DIMENSION;


public class ContainerPane extends JTabbedPane {
    private static final ArrayList<JTabbedPane> serverTabbedPanes = new ArrayList<>();
    private static final OpenContextMenuItem openServerFolderItem = (OpenContextMenuItem) Window.getMenu().getMenu(0).getItem(0);
    private static ContainerPane currentPane;
    private boolean isTabbedPaneFocused = true;

    @Override
    public void setIconAt(int index, Icon icon) {
        ServerTabLabel tabLabel = (ServerTabLabel) this.getTabComponentAt(index);
        tabLabel.setIcon(icon);
    }

    @Override
    public Icon getIconAt(int index) {
        ServerTabLabel tabLabel = (ServerTabLabel) this.getTabComponentAt(index);
        if (tabLabel != null) {
            return tabLabel.getIcon();
        } else {
            return null;
        }
    }

    @Override
    public void setTitleAt(int index, String title) {
        ServerTabLabel tabLabel = (ServerTabLabel) this.getTabComponentAt(index);
        tabLabel.setText(title);
    }

    @Override
    public void setEnabledAt(int index, boolean enabled) {
        super.setEnabledAt(index, enabled);
        ServerTabLabel tabLabel = (ServerTabLabel) this.getTabComponentAt(index);
        tabLabel.setEnabled(enabled);
    }

    @Override
    public void setTabComponentAt(int index, Component component) {
        super.setTabComponentAt(index, component);
    }

    @Override
    public void setToolTipTextAt(int index, String toolTipText) {
        super.setToolTipTextAt(index, toolTipText);
    }

    public ContainerPane() {
        setLayout(new BorderLayout());

        setUI(new FlatTabbedPaneUI() {
            @Override
            protected void paintTab(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect) {
                boolean isSelected = tabIndex == tabPane.getSelectedIndex();
                Rectangle currentTabRects = rects[tabIndex];

                if (isTabbedPaneFocused) {
                    this.paintTabBackground(g, tabPlacement, tabIndex, currentTabRects.x, currentTabRects.y, currentTabRects.width, currentTabRects.height, isSelected);
                }

                if (isSelected) {
                    g.setColor(Colors.TAB_SELECTION_COLOR);
                    g.fillRect(rects[tabIndex].x, rects[tabIndex].y, rects[tabIndex].width, rects[tabIndex].height);
                }
            }

            @Override
            public void paint(Graphics g, JComponent c) {
                g.setColor(Colors.TABBEDPANE_BACKGROUND_COLOR);
                g.fillRect(0,0, getWidth(), getHeight());
            }
        });

        setTabPlacement(LEFT);
        setBackground(Colors.TABBEDPANE_BACKGROUND_COLOR);
        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        ArrayList<MCServer> configData = Config.getData();


        int index = Window.getUserValues().getInt("prefs_server_id", 0);
        if(index >= configData.size()) {
            index = 0;
        } else {
            setSelectedIndex(index);
        }

        configData.forEach(this::addServer);


        if(!configData.isEmpty()) {
            onTabSwitched(index);
            addChangeListener(e -> onTabSwitched(this.getSelectedIndex()));
        }


        for(MouseListener mouseListener : getMouseListeners())
            removeMouseListener(mouseListener); //to remove the tab switching on right click
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                handleTabMouseClicks(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                handleTabMouseClicks(e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isTabbedPaneFocused = false;
                repaint();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                isTabbedPaneFocused = true;
                repaint();
            }
        });

        ContainerPane.currentPane = this;
    }

    public void addServer(MCServer server) {
        JTabbedPane tabbedPane = new JTabbedPane(RIGHT);
        tabbedPane.addTab("Console", new ServerDashboardTab(this, server.serverId() - 1));
        tabbedPane.addTab("Worlds", new WorldsManagerTab(this, server.serverId() - 1));
        tabbedPane.setTabComponentAt(0, new TabLabelWithFileTransfer("Console", tabbedPane,0));
        tabbedPane.setTabComponentAt(1, new TabLabelWithFileTransfer("Worlds", tabbedPane,1));
        serverTabbedPanes.add(tabbedPane);

        String serverName = server.serverName();
        if(serverName.length() > 52)
            serverName = serverName.substring(0, 52);
        addTab(serverName, tabbedPane);

        ServerTabLabel tabLabel = new ServerTabLabel(serverName, server.serverId() - 1);
        tabLabel.putClientProperty("is_server", 1);
        setTabComponentAt(server.serverId() - 1, tabLabel);

        setIconAt(server.serverId() - 1, DefaultIcons.getIcon(DefaultIcons.SERVER_OFFLINE));
        setToolTipTextAt(server.serverId() - 1, "Offline");
        tabLabel.enableContextMenu();
    }

    private void handleTabMouseClicks(MouseEvent e) {
        Point point = e.getPoint();
        if(getComponentAt(point) instanceof ContainerPane) {
            return;
        }
        int index = indexAtLocation(point.x, point.y);

        if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 1) {
            setSelectedIndex(index);
        } else if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2){
            ServerDashboardTab consoleTab = (ServerDashboardTab) serverTabbedPanes.get(index).getComponentAt(0);
            if(!consoleTab.getServerConsoleArea().isServerRunning())
                consoleTab.startServer();
        } else if(e.getButton() == MouseEvent.BUTTON3) {
            ServerTabLabel serverTabLabel = (ServerTabLabel) getTabComponentAt(index);
            serverTabLabel.showContextMenu(e, this);
        }
        e.consume();
    }

    public void onTabSwitched(int tabIndex) {
//        TabLabelWithFileTransfer tabLabelWithFileTransfer = (TabLabelWithFileTransfer) getTabComponentAt(tabIndex);
//        if(tabLabelWithFileTransfer.getClientProperty("is_server").equals(0))
//            return;

        if(openServerFolderItem != null && tabIndex != getTabCount() - 1) {
//            openServerFolderItem.setText("<html>Open current server's folder\n<center><sub>" + Config.abbreviateServerPath(tabIndex) + "</sub></center></html>");
            openServerFolderItem.updatePath(Config.getData().get(tabIndex).serverPath());
        }

        ServerDashboardTab selectedConsoleTab = (ServerDashboardTab) serverTabbedPanes.get(tabIndex).getComponentAt(0);

        setChartsVisibility(Window.areChartsEnabled());

        MCServer mcServerConfig = Config.getData().get(tabIndex);

        Window.getUserValues().putInt("prefs_server_id", tabIndex);
        Config.reloadServersWorldPath(mcServerConfig);


        WorldsManagerTab worldsManagerTab = (WorldsManagerTab) serverTabbedPanes.get(tabIndex).getComponentAt(1);
        worldsManagerTab.setIcons();
        if(mcServerConfig.worldPath().exists()) {
            worldsManagerTab.getWorldsInfoPanels().updateServerWorldInformation(mcServerConfig.worldPath());
        }

        for (JTabbedPane serverTabbedPane : serverTabbedPanes) {
            ServerDashboardTab c = (ServerDashboardTab) serverTabbedPane.getComponentAt(0);
            c.getServerConsoleArea().isVisible = false;
        }

        selectedConsoleTab.getServerConsoleArea().isVisible = true;
        try {
            selectedConsoleTab.getServerConsoleArea().setTextFromLatestLogFile();
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public void setChartsVisibility(boolean isVisible) {
        for(JTabbedPane tabbedPane : serverTabbedPanes) {
            ServerDashboardTab serverDashboardTab = (ServerDashboardTab) tabbedPane.getComponentAt(0);
            serverDashboardTab.setChartsEnabled(isVisible);
        }
    }
    public void killAllServerProcesses() {
        for(JTabbedPane tabbedPane : serverTabbedPanes) {
            ServerDashboardTab serverDashboardTab = (ServerDashboardTab) tabbedPane.getComponentAt(0);
            serverDashboardTab.getServerConsoleArea().killAllProcesses();
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

    public static ContainerPane getCurrentPane() {
        return currentPane;
    }
}

