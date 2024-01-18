package com.myne145.serverlauncher.gui.window;

import com.myne145.serverlauncher.gui.tabs.serverdashboard.ServerConsoleTab;
import com.myne145.serverlauncher.server.Config;
import com.myne145.serverlauncher.utils.DesktopOpener;
import com.myne145.serverlauncher.utils.FileDetailsUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ServerTabLabel extends TabLabelWithFileTransfer {

    private final JPopupMenu contextMenu = new JPopupMenu();
    private final int tabIndex;
    private JMenuItem serverRunAction;
    private int serverActionMode = 0;

    public ServerTabLabel(String text, int tabIndex) {
        super(text, tabIndex);
        this.tabIndex = tabIndex;
        this.setHorizontalAlignment(JLabel.CENTER);
        this.setVerticalAlignment(JLabel.CENTER);
        setBackground(UIManager.getColor("TabbedPane.background"));
        setText("<html><p style=\"text-align: left; width: 110px\">" + text + "</p></html>");
    }

    public void enableContextMenu() {
        JMenuItem openFolder = new JMenuItem("<html>Open folder\n<center><sub>" + FileDetailsUtils.abbreviate(Config.getData().get(tabIndex).serverPath().getAbsolutePath(), 27) + "</sub></center></html>");
        serverRunAction = new JMenuItem("<html>Start server\n<center><sub>" + Config.getData().get(tabIndex).serverName() + "</sub></center></html>");
        serverRunAction.addActionListener(e -> {
            JTabbedPane tabbedPane = (JTabbedPane) ContainerPane.getCurrentPane().getComponentAt(tabIndex);
            ServerConsoleTab tab = (ServerConsoleTab) tabbedPane.getComponentAt(0);
            if(serverActionMode == 0)
                tab.startServer();
            else if(serverActionMode == 1)
                tab.stopServer();
        });

        openFolder.addActionListener(e -> DesktopOpener.openServerFolder(tabIndex));
        contextMenu.add(openFolder);
        contextMenu.add(serverRunAction);
    }

    /**
     * @param mode 0=start, 1=stop
     */
    public void changeServerActionText(int mode) {
        if(mode == 0) {
            serverRunAction.setText("<html>Start server\n<center><sub>" + Config.getData().get(tabIndex).serverName() + "</sub></center></html>");
        } else if(mode == 1) {
            serverRunAction.setText("<html>Stop server\n<center><sub>" + Config.getData().get(tabIndex).serverName() + "</sub></center></html>");
        }
    }

    public void showContextMenu(final MouseEvent e, Component component) {
        if (contextMenu != null) {
            System.out.println("Showing context menu at: " + e.getPoint());
            SwingUtilities.invokeLater(() -> contextMenu.show(component, e.getX(), e.getY()));
        }
    }
}