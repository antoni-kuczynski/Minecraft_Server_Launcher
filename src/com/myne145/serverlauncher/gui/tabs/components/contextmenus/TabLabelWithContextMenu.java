package com.myne145.serverlauncher.gui.tabs.components.contextmenus;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TabLabelWithContextMenu extends JLabel {

    private JPopupMenu contextMenu;
    private final int tabIndex;
    private final boolean isEnabled;

    public TabLabelWithContextMenu(String text, int tabIndex, boolean isEnabled) {
        super(text);
        this.tabIndex = tabIndex;
        this.isEnabled = isEnabled;
        initializeContextMenu();
        initializeMouseListener();
    }

    private void initializeContextMenu() {
        contextMenu = new JPopupMenu();

        JMenuItem menuItem1 = new JMenuItem("Current's tab index is: " + tabIndex);
        JMenuItem menuItem2 = new JMenuItem("Is current tab enabled: " + isEnabled);

        contextMenu.add(menuItem1);
        contextMenu.add(menuItem2);
    }

    private void initializeMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    showContextMenu(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    showContextMenu(e);
                }
            }
        });
    }

    private void showContextMenu(MouseEvent e) {
        if (contextMenu != null) {
            contextMenu.show(this, e.getX(), e.getY());
        }
    }
}
