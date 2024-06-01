package com.myne145.serverlauncher.gui.window;

import com.formdev.flatlaf.ui.FlatTabbedPaneUI;
import com.myne145.serverlauncher.gui.components.ServerTabLabel;
import com.myne145.serverlauncher.gui.components.TabLabelWithFileTransfer;
import com.myne145.serverlauncher.gui.components.OpenContextMenuItem;
import com.myne145.serverlauncher.gui.tabs.ServerTabbedPane;
import com.myne145.serverlauncher.gui.tabs.addserver.AddServerTab;
import com.myne145.serverlauncher.gui.tabs.serverdashboard.ServerDashboardTab;
import com.myne145.serverlauncher.gui.tabs.worldsmanager.WorldsManagerTab;
import com.myne145.serverlauncher.server.MCServer;
import com.myne145.serverlauncher.server.Config;
import com.myne145.serverlauncher.utils.Colors;
import com.myne145.serverlauncher.utils.DefaultIcons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

import static com.myne145.serverlauncher.gui.window.Window.SERVER_STATUS_ICON_DIMENSION;
import static com.myne145.serverlauncher.gui.window.Window.showErrorMessage;


public class ContainerPane extends JTabbedPane {
    private static final ArrayList<ServerTabbedPane> serverTabbedPanes = new ArrayList<>();
    private static final OpenContextMenuItem openServerFolderItem = (OpenContextMenuItem) Window.getMenu().getMenu(0).getItem(0);
    private boolean isTabbedPaneFocused = true;

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

    @Override
    public void setToolTipTextAt(int index, String toolTipText) {
//        String name = Config.getData().get(index - 1).getName();
//        if(name.length() > 52)
//            super.setToolTipTextAt(index, name + "\n" + toolTipText); //TODO
//        else
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
                button.setTransferHandler(new TransferHandler() {

                    @Override
                    public boolean canImport(TransferSupport support) {
                        Thread thread = new Thread(() -> {
                            if(getSelectedIndex() >= getTabCount())
                                return;

                            setSelectedIndex(getSelectedIndex() + 1);

                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        thread.start();
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        return true;
                    }


                });
                return button;
            }

            @Override
            protected JButton createMoreTabsButton() {
                JButton button = super.createMoreTabsButton();
                button.setToolTipText(null);
                return button;
            }
        });
        setTabPlacement(LEFT);
        setBackground(Colors.TABBEDPANE_BACKGROUND_COLOR);
        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);


        ArrayList<MCServer> configData = Config.getData();
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
                repaint();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                isTabbedPaneFocused = true;
                repaint();
            }
        });

//        TabLabelWithFileTransfer.setParentPane(this);
    }

    public void addServer(MCServer server) {
        ServerTabbedPane serverTabbedPane = new ServerTabbedPane(
                new ServerDashboardTab(this, server),
                new WorldsManagerTab(this, server)
        );

        serverTabbedPanes.add(serverTabbedPane);

        addTab(server.getAbbreviatedName(50), serverTabbedPane);

        ServerTabLabel tabLabel = new ServerTabLabel(server);
        setTabComponentAt(server.getServerId(), tabLabel);

        setIconAt(server.getServerId(), DefaultIcons.getServerPlatformIcon(DefaultIcons.SERVER_OFFLINE));
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

        if(openServerFolderItem != null && tabIndex != getTabCount() - 1) {
//            openServerFolderItem.setText("<html>Open current server's folder\n<center><sub>" + Config.abbreviateServerPath(tabIndex) + "</sub></center></html>");
            openServerFolderItem.updatePath(Config.getData().get(tabIndex).getServerPath());
        }

        setChartsVisibility(Window.areChartsEnabled());

        MCServer mcServerConfig = Config.getData().get(tabIndex - 1);

        Window.getUserValues().putInt("prefs_server_id", tabIndex);
        mcServerConfig.updateWorldPath();

        WorldsManagerTab worldsManagerTab = serverTabbedPanes.get(tabIndex).getWorldsManagerTab();
        worldsManagerTab.setIcons();
        if(mcServerConfig.getWorldPath().exists()) {
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

    public void updateServerButtonsSizes() {
        for(int i = 1; i < serverTabbedPanes.size(); i++) {
            ImageIcon imageIcon = (ImageIcon) getIconAt(i);
            setIconAt(i, new ImageIcon(imageIcon.getImage().getScaledInstance(SERVER_STATUS_ICON_DIMENSION, SERVER_STATUS_ICON_DIMENSION, Image.SCALE_SMOOTH)));
        }
    }
}

