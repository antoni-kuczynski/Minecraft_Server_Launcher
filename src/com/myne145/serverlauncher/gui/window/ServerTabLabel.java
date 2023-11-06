package com.myne145.serverlauncher.gui.window;

import com.myne145.serverlauncher.server.Config;
import com.myne145.serverlauncher.utils.DesktopOpener;
import com.myne145.serverlauncher.utils.FileDetailsUtils;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;

public class ServerTabLabel extends TabLabelWithFileTransfer {

    private JPopupMenu contextMenu = new JPopupMenu();
    private final int tabIndex;
    private static ContainerPane parentPane;

    public ServerTabLabel(String text, ContainerPane parentPane, int tabIndex) {
        super(text, parentPane, tabIndex);
        this.tabIndex = tabIndex;
        setBackground(UIManager.getColor("TabbedPane.background"));

        setText("<html><p style=\"text-align: left; width: 110px\">" + text + "</p></html>");
//        setToolTipText(null);

        this.setHorizontalAlignment(JLabel.CENTER);
        this.setVerticalAlignment(JLabel.CENTER);

        if(!parentPane.isEnabledAt(tabIndex))
            return;
        ServerTabLabel.parentPane = parentPane;
//        for(MouseListener mouseListener : getMouseListeners())
//            removeMouseListener(mouseListener);
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