package com.myne145.serverlauncher.gui.tabs.worldsmanager;

import com.myne145.serverlauncher.gui.components.OpenContextMenuItem;
import com.myne145.serverlauncher.utils.DesktopOpener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class WorldsContextMenu extends JPopupMenu {
    private final JPopupMenu contextMenu = new JPopupMenu();
    private final OpenContextMenuItem openFolderItem = new OpenContextMenuItem("Open folder");
    private File currentFolder;

    protected WorldsContextMenu() {
        contextMenu.add(openFolderItem);
    }

    protected void enableContextMenu() {
        openFolderItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON1)
                    DesktopOpener.openFolder(currentFolder);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON1)
                    DesktopOpener.openFolder(currentFolder);
            }
        });
    }

    protected void updateDirectory(File file) {
        if(currentFolder == null && file != null)
            enableContextMenu();
//        openFolder.setText("<html>Open folder\n<center><sub>" + Config.abbreviateFile(file.getAbsolutePath(), 27) + "</sub></center></html>");
        openFolderItem.updatePath(file);
        currentFolder = file;
    }

    protected void showContextMenu(final MouseEvent e, Component component) {
        if (contextMenu == null || currentFolder == null) {
            return;
        }
        SwingUtilities.invokeLater(() -> contextMenu.show(component, e.getX(), e.getY()));
    }
}