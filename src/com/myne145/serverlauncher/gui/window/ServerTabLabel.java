package com.myne145.serverlauncher.gui.window;

import com.myne145.serverlauncher.server.Config;
import com.myne145.serverlauncher.utils.DesktopOpener;
import com.myne145.serverlauncher.utils.FileDetailsUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class ServerTabLabel extends TabLabelWithFileTransfer { //ALL OF THIS for future context menu

    private JPopupMenu contextMenu;
    private final int tabIndex;
    private static ContainerPane parentPane;


    public ServerTabLabel(String text, ContainerPane parentPane, int tabIndex) {
        super(text, parentPane, tabIndex);
        this.tabIndex = tabIndex;

        this.setHorizontalAlignment(JLabel.CENTER);
        this.setVerticalAlignment(JLabel.CENTER);
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Add a border

        if(!parentPane.isEnabledAt(tabIndex)) {
            return;
        }
        ServerTabLabel.parentPane = parentPane;
//        initializeContextMenu();
//        initializeMouseListener();
    }

    public void enableContextMenu() {
        initializeContextMenu();
        initializeMouseListener();
    }

    private void initializeContextMenu() {

        contextMenu = new JPopupMenu();

        JMenuItem menuItem1 = new JMenuItem("<html>Open current server's folder\n<center><sub>" + FileDetailsUtils.abbreviate(Config.getData().get(tabIndex).serverPath().getAbsolutePath(), 27) + "</sub></center></html>");
        JMenuItem menuItem2 = new JMenuItem("Is current tab enabled: ");

        menuItem1.addActionListener(e -> DesktopOpener.openServerFolder(tabIndex));

        contextMenu.add(menuItem1);
        contextMenu.add(menuItem2);
    }

    private void initializeMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                SwingUtilities.invokeLater(() -> {
                    if(SwingUtilities.isLeftMouseButton(e) && parentPane.isEnabledAt(tabIndex)) {
                        parentPane.setSelectedIndex(tabIndex);
                    }
                    if (SwingUtilities.isRightMouseButton(e)) {
                        showContextMenu(e);
                    }
                });
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    showContextMenu(e);
                }
            }
        });
    }

    public void showContextMenu(final MouseEvent e) {
        if (contextMenu != null) {
            System.out.println("Showing context menu at: " + e.getPoint());
            SwingUtilities.invokeLater(() -> contextMenu.show(ServerTabLabel.this, e.getX(), e.getY()));
        }
    }
}
