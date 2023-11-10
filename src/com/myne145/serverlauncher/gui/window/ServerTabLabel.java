package com.myne145.serverlauncher.gui.window;

import com.myne145.serverlauncher.server.Config;
import com.myne145.serverlauncher.utils.DesktopOpener;
import com.myne145.serverlauncher.utils.FileDetailsUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ServerTabLabel extends TabLabelWithFileTransfer {

    private final JPopupMenu contextMenu = new JPopupMenu();
    private final int tabIndex;

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
        openFolder.addActionListener(e -> DesktopOpener.openServerFolder(tabIndex));
        contextMenu.add(openFolder);
    }

    public void showContextMenu(final MouseEvent e, Component component) {
        if (contextMenu != null) {
            System.out.println("Showing context menu at: " + e.getPoint());
            SwingUtilities.invokeLater(() -> contextMenu.show(component, e.getX(), e.getY()));
        }
    }
}