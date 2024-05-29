package com.myne145.serverlauncher.gui.tabs;

import com.formdev.flatlaf.ui.FlatTabbedPaneUI;
import com.myne145.serverlauncher.gui.components.TabLabelWithFileTransfer;
import com.myne145.serverlauncher.gui.tabs.addserver.AddServerTab;
import com.myne145.serverlauncher.gui.tabs.serverdashboard.ServerDashboardTab;
import com.myne145.serverlauncher.gui.tabs.worldsmanager.WorldsManagerTab;

import javax.swing.*;
import java.awt.*;

public class ServerTabbedPane extends JTabbedPane {
    private ServerDashboardTab serverDashboardTab;
    private WorldsManagerTab worldsManagerTab;
    private AddServerTab addServerTab;

    @Override
    public void addTab(String title, Component component) {
        super.addTab(title, component);
        setTabComponentAt(getTabCount() - 1, new TabLabelWithFileTransfer(title, getTabCount() - 1));
    }

    public ServerTabbedPane(Component... component) {
        setTabPlacement(RIGHT);
        setUI(new FlatTabbedPaneUI() {
            @Override
            protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
                return 65;
            }
        });
        for(Component tab : component) {
            if(tab instanceof ServerDashboardTab) {
                addTab("Console", tab);
                this.serverDashboardTab = (ServerDashboardTab) tab;
            } else if(tab instanceof WorldsManagerTab) {
                addTab("Worlds", tab);
                this.worldsManagerTab = (WorldsManagerTab) tab;
            } else if(tab instanceof AddServerTab) {
                addTab("Add local", tab);
                this.addServerTab = (AddServerTab) tab;
            } else {
                addTab("", tab);
            }
        }
    }

    public ServerDashboardTab getServerDashboardTab() {
        return serverDashboardTab;
    }

    public WorldsManagerTab getWorldsManagerTab() {
        return worldsManagerTab;
    }

    public AddServerTab getAddServerTab() {
        return addServerTab;
    }
}
