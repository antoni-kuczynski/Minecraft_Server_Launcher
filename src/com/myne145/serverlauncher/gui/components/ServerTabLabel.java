package com.myne145.serverlauncher.gui.components;

import com.myne145.serverlauncher.gui.tabs.serverdashboard.ServerDashboardTab;
import com.myne145.serverlauncher.gui.window.Window;
import com.myne145.serverlauncher.server.MCServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ServerTabLabel extends TabLabelWithFileTransfer {

    private final JPopupMenu contextMenu = new JPopupMenu();
//    private final int tabIndex;
    private JMenuItem serverRunAction;
    private boolean isServerActionStartServer = true;
    private final MCServer server;

    public ServerTabLabel(MCServer server) {
        super(server.getName(), server.getServerId());
        this.server = server;
//        this.tabIndex = tabIndex;
        this.setHorizontalAlignment(JLabel.CENTER);
        this.setVerticalAlignment(JLabel.CENTER);
        setBackground(UIManager.getColor("TabbedPane.background"));
        setText("<html><p style=\"text-align: left; width: 110px\">" + server.getName() + "</p></html>");
    }

    public void enableContextMenu() {
        serverRunAction = new JMenuItem("<html>Start server\n<center><sub>" + server.getName() + "</sub></center></html>");
        serverRunAction.addActionListener(e -> {
            JTabbedPane tabbedPane = (JTabbedPane) parentPane.getComponentAt(server.getServerId());
            ServerDashboardTab tab = (ServerDashboardTab) tabbedPane.getComponentAt(0);
            if(isServerActionStartServer)
                tab.startServer();
            else
                tab.stopServer();
        });
        OpenContextMenuItem openContextMenuItem = new OpenContextMenuItem("Open server folder");
        openContextMenuItem.updatePath(server.getServerPath());

        contextMenu.add(openContextMenuItem);
        contextMenu.add(serverRunAction);
    }

    public void changeServerActionContextMenuToServerStart(boolean changeToStart) {
        if(changeToStart) {
            serverRunAction.setText("<html>Start server\n<center><sub>" + server.getName() + "</sub></center></html>");
            isServerActionStartServer = false;
        } else {
            serverRunAction.setText("<html>Stop server\n<center><sub>" + server.getName() + "</sub></center></html>");
            isServerActionStartServer = true;
        }
    }

    public void showContextMenu(final MouseEvent e, Component component) {
        if (contextMenu == null) {
            return;
        }
        Point p = SwingUtilities.convertPoint(component, e.getPoint(), Window.getWindow().getAddServerButton());
//        contextMenu.setComponentZOrder(Window.getWindow().getGlassPane(), 0);
        SwingUtilities.invokeLater(() -> contextMenu.show(Window.getWindow().getAddServerButton(), p.x, p.y));
    }
}