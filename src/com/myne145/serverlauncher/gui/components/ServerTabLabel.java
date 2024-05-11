package com.myne145.serverlauncher.gui.components;

import com.myne145.serverlauncher.gui.tabs.serverdashboard.ServerDashboardTab;
import com.myne145.serverlauncher.gui.window.ContainerPane;
import com.myne145.serverlauncher.server.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ServerTabLabel extends TabLabelWithFileTransfer {

    private final JPopupMenu contextMenu = new JPopupMenu();
    private final int tabIndex;
    private JMenuItem serverRunAction;
    private boolean isServerActionStartServer = true;


    public ServerTabLabel(String text, int tabIndex) {
        super(text, tabIndex);
        this.tabIndex = tabIndex;
        this.setHorizontalAlignment(JLabel.CENTER);
        this.setVerticalAlignment(JLabel.CENTER);
        setBackground(UIManager.getColor("TabbedPane.background"));
        setText("<html><p style=\"text-align: left; width: 110px\">" + text + "</p></html>");
    }

    public void enableContextMenu() {
        serverRunAction = new JMenuItem("<html>Start server\n<center><sub>" + Config.getData().get(tabIndex).getServerName() + "</sub></center></html>");
        serverRunAction.addActionListener(e -> {
            JTabbedPane tabbedPane = (JTabbedPane) parentPane.getComponentAt(tabIndex);
            ServerDashboardTab tab = (ServerDashboardTab) tabbedPane.getComponentAt(0);
            if(isServerActionStartServer)
                tab.startServer();
            else
                tab.stopServer();
        });
        OpenContextMenuItem openContextMenuItem = new OpenContextMenuItem("Open server folder");
        openContextMenuItem.updatePath(Config.getData().get(tabIndex).getServerPath());

        contextMenu.add(openContextMenuItem);
        contextMenu.add(serverRunAction);
    }

    public void changeServerActionContextMenuToServerStart(boolean changeToStart) {
        if(changeToStart) {
            serverRunAction.setText("<html>Start server\n<center><sub>" + Config.getData().get(tabIndex).getServerName() + "</sub></center></html>");
            isServerActionStartServer = false;
        } else {
            serverRunAction.setText("<html>Stop server\n<center><sub>" + Config.getData().get(tabIndex).getServerName() + "</sub></center></html>");
            isServerActionStartServer = true;
        }
    }

    public void showContextMenu(final MouseEvent e, Component component) {
        if (contextMenu == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> contextMenu.show(component, e.getX(), e.getY()));
    }
}