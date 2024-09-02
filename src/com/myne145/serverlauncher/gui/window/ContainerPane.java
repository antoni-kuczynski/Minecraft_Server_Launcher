package com.myne145.serverlauncher.gui.window;

import com.formdev.flatlaf.ui.FlatTabbedPaneUI;
import com.myne145.serverlauncher.gui.components.ServerTabLabel;
import com.myne145.serverlauncher.gui.components.TabLabelWithFileTransfer;
import com.myne145.serverlauncher.gui.components.OpenContextMenuItem;
import com.myne145.serverlauncher.gui.tabs.addserver.AddServerTab;
import com.myne145.serverlauncher.gui.tabs.serverdashboard.ServerDashboardTab;
import com.myne145.serverlauncher.gui.tabs.worldsmanager.WorldsManagerTab;
import com.myne145.serverlauncher.server.MinecraftServer;
import com.myne145.serverlauncher.server.Config;
import com.myne145.serverlauncher.utils.Colors;
import com.myne145.serverlauncher.utils.DefaultIcons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

import static com.myne145.serverlauncher.gui.window.Window.serverStatusIconScaleMode;
import static com.myne145.serverlauncher.gui.window.Window.showErrorMessage;


public class ContainerPane extends JTabbedPane {
    private static final ArrayList<ServerTabbedPane> serverTabbedPanes = new ArrayList<>();
    private static final OpenContextMenuItem openServerFolderItem = (OpenContextMenuItem) Window.getMenu().getMenu(0).getItem(0);
    private boolean isTabbedPaneFocused = true;

    private TransferHandler getScrollButtonTransferHandler(int direction) {
        // 1 5
        return new TransferHandler() {
            @Override
            public boolean canImport(TransferSupport support) {
                Thread t = new Thread(() -> {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e); //TODO
                    }
                    if(direction == SOUTH && getSelectedIndex() + 1 < getTabCount())
                        setSelectedIndex(getSelectedIndex() + 1);
                    else if(direction == NORTH && getSelectedIndex() - 1 >= 0)
                        setSelectedIndex(getSelectedIndex() - 1);
                });
                t.setPriority(Thread.MIN_PRIORITY);
                t.start();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e); //TODO
                }

                return true;
            }
        };
    }

    private FlatTabbedPaneUI containerPaneUI = new FlatTabbedPaneUI() {
        @Override
        protected void paintTab(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect) {
            super.paintTab(g, tabPlacement, rects, tabIndex, iconRect, textRect);

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

        @Override
        protected int calculateTabAreaWidth(int tabPlacement, int vertRunCount, int maxTabWidth) {
            return 220;
        }

        @Override
        protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
            return 220;
        }

        @Override
        protected JButton createScrollButton(int direction) {
            JButton button =  super.createScrollButton(direction);
            button.setTransferHandler(getScrollButtonTransferHandler(direction));
            return button;
        }

        @Override
        protected JButton createMoreTabsButton() {
            JButton button = super.createMoreTabsButton();
            button.setToolTipText(null);
            return button;
        }
    };

    @Override
    public void setIconAt(int index, Icon icon) {
        TabLabelWithFileTransfer tabLabel = (TabLabelWithFileTransfer) this.getTabComponentAt(index);
        tabLabel.setIcon(icon);
    }

    @Override
    public Icon getIconAt(int index) {
        TabLabelWithFileTransfer tabLabel = (TabLabelWithFileTransfer) this.getTabComponentAt(index);
        if (tabLabel != null) {
            return tabLabel.getIcon();
        } else {
            return null;
        }
    }

    @Override
    public void setTitleAt(int index, String title) {
        TabLabelWithFileTransfer tabLabel = (TabLabelWithFileTransfer) this.getTabComponentAt(index);
        tabLabel.setText(title);
    }

    @Override
    public String getTitleAt(int index) {
        if(this.getTabComponentAt(index) == null)
            return "";
        TabLabelWithFileTransfer tabLabel = (TabLabelWithFileTransfer) this.getTabComponentAt(index);
        return tabLabel.getText();
    }

    @Override
    public void setEnabledAt(int index, boolean enabled) {
        super.setEnabledAt(index, enabled);
        TabLabelWithFileTransfer tabLabel = (TabLabelWithFileTransfer) this.getTabComponentAt(index);
        tabLabel.setEnabled(enabled);
    }

    public ContainerPane() {
        setLayout(new BorderLayout());
        setUI(containerPaneUI);
        setTabPlacement(LEFT);
        setBackground(Colors.TABBEDPANE_BACKGROUND_COLOR);
        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);


        ArrayList<MinecraftServer> configData = Config.getData();
        ServerTabbedPane addServerPane = new ServerTabbedPane(
                new AddServerTab(this)
        );

        addTab("<html><p style=\"text-align: left; width: 110px\">" + "Add server" + "</p></html>", addServerPane);
        setTabComponentAt(0, new TabLabelWithFileTransfer("<html><p style=\"text-align: left; width: 110px\">" + "Add server" + "</p></html>", this, 0));
        setIconAt(0, DefaultIcons.getSVGIcon(DefaultIcons.ADD_SERVER).derive(32, 32));
        serverTabbedPanes.add(addServerPane);

        configData.forEach(this::addServer);

        int index = Window.getUserValues().getInt("prefs_server_id", 0);
        if(index >= configData.size()) {
            index = 0;
        }

        if(getTabCount() != 0) {
            setSelectedIndex(index);
        }

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
                TabLabelWithFileTransfer.fileEntered = false;
                repaint();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                isTabbedPaneFocused = false;
                TabLabelWithFileTransfer.fileEntered = true;
                repaint();
            }
        });
    }

    public void addServer(MinecraftServer server) {
        ServerTabbedPane serverTabbedPane = new ServerTabbedPane(
                new ServerDashboardTab(this, server),
                new WorldsManagerTab(this, server)
        );

        serverTabbedPanes.add(serverTabbedPane);
        addTab(server.getName(50), serverTabbedPane);

        ServerTabLabel tabLabel = new ServerTabLabel(server);
        setTabComponentAt(server.getServerId(), tabLabel);

        setIconAt(server.getServerId(), DefaultIcons.getIcon(DefaultIcons.SERVER_OFFLINE));
        setToolTipTextAt(server.getServerId(), "Offline");
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
        } else if(index != 0 && e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2){
            ServerDashboardTab consoleTab = serverTabbedPanes.get(index).getServerDashboardTab();
            if(!consoleTab.getServerConsoleArea().isServerRunning())
                consoleTab.startServer();
        } else if(e.getButton() == MouseEvent.BUTTON3 && index != 0) {
            ServerTabLabel serverTabLabel = (ServerTabLabel) getTabComponentAt(index);
            serverTabLabel.showContextMenu(e, this);
        }
        e.consume();
    }

    public void onTabSwitched(int tabIndex) {
        if(tabIndex == 0) {
            return;
        }


        if(openServerFolderItem != null) {
            openServerFolderItem.updatePath(Config.getData().get(tabIndex - 1).getServerPath());
        }

        setChartsVisibility(Window.areChartsEnabled());

        MinecraftServer minecraftServerConfig = Config.getData().get(tabIndex - 1);

        Window.getUserValues().putInt("prefs_server_id", tabIndex);
        minecraftServerConfig.updateWorldPath();

        WorldsManagerTab worldsManagerTab = serverTabbedPanes.get(tabIndex).getWorldsManagerTab();
        worldsManagerTab.setIcons();
        if(minecraftServerConfig.getWorldPath().exists()) {
            worldsManagerTab.getWorldsInfoPanels().updateServerWorldInformation();
        }

        ServerDashboardTab selectedConsoleTab = serverTabbedPanes.get(tabIndex).getServerDashboardTab();
        try {
            selectedConsoleTab.getServerConsoleArea().setTextFromLatestLogFile();
        } catch (IOException e) {
            showErrorMessage("I/O error reading latest.log file.", e);
        }
    }

    public void setChartsVisibility(boolean isVisible) {
        for(int i = 1; i < serverTabbedPanes.size(); i++) {
            serverTabbedPanes.get(i).getServerDashboardTab().setChartsEnabled(isVisible);
        }
    }
    public void killAllServerProcesses() {
        for(ServerTabbedPane tabbedPane : serverTabbedPanes) {
            ServerDashboardTab serverDashboardTab = tabbedPane.getServerDashboardTab();
            if(serverDashboardTab == null)
                continue;
            serverDashboardTab.getServerConsoleArea().killAllProcesses();
        }
    }

    public void updateServerButtonsSizes(int size) {
        for(int i = 1; i < serverTabbedPanes.size(); i++) {
            ImageIcon imageIcon = (ImageIcon) getIconAt(i);
            setIconAt(i, new ImageIcon(imageIcon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH)));
        }
    }

    public boolean isTabbedPaneFocused() {
        return isTabbedPaneFocused;
    }
}

