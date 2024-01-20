package com.myne145.serverlauncher.gui.tabs.worldsmanager.components;

import com.myne145.serverlauncher.server.Config;
import com.myne145.serverlauncher.utils.DesktopOpener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class WorldsContextMenu extends JPopupMenu {
    private final JPopupMenu contextMenu = new JPopupMenu();
    private final JMenuItem openFolder;
    private File currentFolder;

    public WorldsContextMenu() {
        openFolder = new JMenuItem("<html>Open folder\n<center><sub> </sub></center></html>");


        openFolder.addActionListener(e -> DesktopOpener.openFolder(currentFolder));

        openFolder.setVisible(false);
        contextMenu.setVisible(false);
        contextMenu.add(openFolder);
    }

    public void enableContextMenu() {
        openFolder.setVisible(true);
        contextMenu.setVisible(true);
    }

    protected void updateDirectory(File file) {
        if(currentFolder == null && file != null)
            enableContextMenu();
        openFolder.setText("<html>Open folder\n<center><sub>" + Config.abbreviateFile(file.getAbsolutePath(), 27) + "</sub></center></html>");
        currentFolder = file;
    }

    public void showContextMenu(final MouseEvent e, Component component) {
        if (contextMenu == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> contextMenu.show(component, e.getX(), e.getY()));
    }
}