package com.myne145.serverlauncher.gui.window;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ServerTabLabel extends TabLabelWithFileTransfer { //ALL OF THIS for future context menu

//    private JPopupMenu contextMenu;
//    private final int tabIndex;

    public ServerTabLabel(String text, JTabbedPane parentPane, int tabIndex) {
        super(text, parentPane, tabIndex);
//        this.tabIndex = tabIndex;
//        initializeContextMenu();
//        initializeMouseListener();
    }

//    private void initializeContextMenu() {
////        contextMenu = new JPopupMenu();
////
////        JMenuItem menuItem1 = new JMenuItem("Current's tab index is: " + tabIndex);
////        JMenuItem menuItem2 = new JMenuItem("Is current tab enabled: ");
////
////        contextMenu.add(menuItem1);
////        contextMenu.add(menuItem2);
//    }

//    private void initializeMouseListener() {
//        addMouseListener(new MouseAdapter() {
//            @Override
//            public void mousePressed(MouseEvent e) {
//                if (SwingUtilities.isRightMouseButton(e)) {
//                    showContextMenu(e);
//                }
//            }
//
//            @Override
//            public void mouseReleased(MouseEvent e) {
//                if (SwingUtilities.isRightMouseButton(e)) {
//                    showContextMenu(e);
//                }
//            }
//        });
//    }

//    private void showContextMenu(MouseEvent e) {
//        if (contextMenu != null) {
//            contextMenu.show(this, e.getX(), e.getY());
//        }
//    }
}
