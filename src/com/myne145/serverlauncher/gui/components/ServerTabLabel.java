package com.myne145.serverlauncher.gui.components;

import com.myne145.serverlauncher.gui.window.ServerTabbedPane;
import com.myne145.serverlauncher.gui.tabs.addserver.ServerInfoPanel;
import com.myne145.serverlauncher.gui.tabs.serverdashboard.ServerDashboardTab;
import com.myne145.serverlauncher.gui.window.Window;
import com.myne145.serverlauncher.server.MinecraftServer;
import com.myne145.serverlauncher.utils.Colors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ServerTabLabel extends TabLabelWithFileTransfer {

    private final JPopupMenu contextMenu = new JPopupMenu();
//    private final int tabIndex;
    private JMenuItem serverRunAction;
    private boolean isServerActionStartServer = true;
    private final MinecraftServer server;

    public ServerTabLabel(MinecraftServer server) {
        super(server.getName(50), containerPane, server.getServerId());
        this.server = server;
//        this.tabIndex = tabIndex;
        this.setHorizontalAlignment(JLabel.CENTER);
        this.setVerticalAlignment(JLabel.CENTER);
        setBackground(UIManager.getColor("TabbedPane.background"));
        setText("<html><p style=\"text-align: left; width: 110px\">" + server.getName(50) + "</p></html>");
    }

    public void enableContextMenu() {
        serverRunAction = new JMenuItem("<html>Start server\n<center><sub>" + server.getName(50) + "</sub></center></html>");
        serverRunAction.addActionListener(e -> {
            ServerTabbedPane tabbedPane = (ServerTabbedPane) containerPane.getComponentAt(server.getServerId());
            ServerDashboardTab tab = (ServerDashboardTab) tabbedPane.getComponentAt(0);
            if(isServerActionStartServer)
                tab.startServer();
            else
                tab.stopServer();
        });
        OpenContextMenuItem openContextMenuItem = new OpenContextMenuItem("Open server folder");
        openContextMenuItem.updatePath(server.getServerPath());

        JMenuItem properties = new JMenuItem("<html>Properties\n<center><sub>" + server.getName(50) + "</sub></center></html>");
        properties.addActionListener(e -> {
            JDialog dialog = new JDialog(Window.getWindow(), server.getName(50) + " properties");
            dialog.getRootPane().putClientProperty("JRootPane.titleBarBackground", Colors.TABBEDPANE_BACKGROUND_COLOR);
            dialog.getRootPane().putClientProperty("JRootPane.titleBarForeground", Colors.TEXT_COLOR);
            dialog.setLayout(new BorderLayout());
            ServerInfoPanel panel = new ServerInfoPanel();
            panel.updateText(server);
            JPanel bottomPanel = new JPanel();
            JButton button = new JButton("OK");
            SwingUtilities.getRootPane(dialog).setDefaultButton(button);
            button.setBorder(BorderFactory.createEmptyBorder(20,10,20,10));
            button.setPreferredSize(new Dimension(100, 20));
            button.addActionListener(e1 -> {
                dialog.dispose();
            });

            bottomPanel.add(button);
            dialog.add(Box.createRigidArea(new Dimension(10,10)), BorderLayout.PAGE_START);
            dialog.add(Box.createRigidArea(new Dimension(10,10)), BorderLayout.LINE_START);
            dialog.add(panel, BorderLayout.CENTER);
            dialog.add(Box.createRigidArea(new Dimension(10,10)), BorderLayout.LINE_END);
            dialog.add(bottomPanel, BorderLayout.PAGE_END);

            Point p = Window.getCenter();
            dialog.setBounds(p.x - 175, p.y - 225, 350, 450); //TODO
//            dialog.setPreferredSize(new Dimension(350, 450));
            dialog.setVisible(true);
            dialog.pack();
        });

        contextMenu.add(openContextMenuItem);
        contextMenu.add(serverRunAction);
        contextMenu.add(properties);
    }

    public void changeServerActionContextMenuToServerStart(boolean changeToStart) {
        if(changeToStart) {
            serverRunAction.setText("<html>Start server\n<center><sub>" + server.getName(50) + "</sub></center></html>");
            isServerActionStartServer = false;
        } else {
            serverRunAction.setText("<html>Stop server\n<center><sub>" + server.getName(50) + "</sub></center></html>");
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