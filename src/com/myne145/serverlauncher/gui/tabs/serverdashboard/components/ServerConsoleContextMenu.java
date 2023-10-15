package com.myne145.serverlauncher.gui.tabs.serverdashboard.components;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ServerConsoleContextMenu extends JPopupMenu {
    private JMenuItem copy;
    private JMenuItem selectAll;

    private JTextComponent textComponent;

    public ServerConsoleContextMenu() {
        addPopupMenuItems();
    }

    private void addPopupMenuItems() {
        copy = new JMenuItem("Copy");
        copy.setEnabled(false);
        copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        copy.addActionListener(event -> textComponent.copy());
        add(copy);

        add(new JSeparator());

        selectAll = new JMenuItem("Select All");
        selectAll.setEnabled(false);
        selectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        selectAll.addActionListener(event -> textComponent.selectAll());
        add(selectAll);
    }

    private void addTo(Component textComponent) {
        textComponent.addKeyListener(new KeyAdapter() {

        });

        textComponent.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent releasedEvent) {
                handleContextMenu(releasedEvent);
            }

            @Override
            public void mouseReleased(MouseEvent releasedEvent) {
                handleContextMenu(releasedEvent);
            }
        });
    }

    private void handleContextMenu(MouseEvent releasedEvent) {
        if (releasedEvent.getButton() == MouseEvent.BUTTON3) {
            processClick(releasedEvent);
        }
    }

    private void processClick(MouseEvent event) {
        textComponent = (JTextComponent) event.getSource();
        textComponent.requestFocus();

        boolean enableCopy = false;
        boolean enableSelectAll = false;

        String selectedText = textComponent.getSelectedText();
        String text = textComponent.getText();

        if (text != null) {
            if (!text.isEmpty()) {
                enableSelectAll = true;
            }
        }

        if (selectedText != null) {
            if (!selectedText.isEmpty()) {
                enableCopy = true;
            }
        }

        copy.setEnabled(enableCopy);
        selectAll.setEnabled(enableSelectAll);

        // Shows the popup menu
        show(textComponent, event.getX(), event.getY());
    }

    public static void addDefaultContextMenu(Component component) {
        ServerConsoleContextMenu serverConsoleContextMenu = new ServerConsoleContextMenu();
        serverConsoleContextMenu.addTo(component);
    }
}