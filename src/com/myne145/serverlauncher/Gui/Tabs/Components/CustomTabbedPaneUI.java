package com.myne145.serverlauncher.Gui.Tabs.Components;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.event.MouseEvent;

public class CustomTabbedPaneUI extends BasicTabbedPaneUI {
    private Color selectedTabColor;
    private Color tabAreaBackgroundColor;

    public CustomTabbedPaneUI(Color selectedTabColor, Color tabAreaBackgroundColor) {
        this.selectedTabColor = selectedTabColor;
        this.tabAreaBackgroundColor = tabAreaBackgroundColor;
    }

    @Override
    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        if (isSelected) {
            g.setColor(selectedTabColor);
            g.fillRect(x, y, w, h);
        } else {
            g.setColor(tabAreaBackgroundColor);
            g.fillRect(x, y, w, h);
        }
    }

    @Override
    protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
        // Remove content border
    }

    @Override
    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
        // Remove tab borders
    }

    @Override
    protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
        // Remove focus indicator
    }


}
