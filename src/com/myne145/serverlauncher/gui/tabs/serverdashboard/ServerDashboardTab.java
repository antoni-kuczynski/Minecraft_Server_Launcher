package com.myne145.serverlauncher.gui.tabs.serverdashboard;

import com.myne145.serverlauncher.gui.tabs.serverdashboard.charts.CPUChart;
import com.myne145.serverlauncher.gui.tabs.serverdashboard.charts.RAMChart;
import com.myne145.serverlauncher.gui.window.ContainerPane;
import com.myne145.serverlauncher.gui.components.ServerTabLabel;
import com.myne145.serverlauncher.server.MCServer;
import com.myne145.serverlauncher.utils.DefaultIcons;

import javax.swing.*;
import java.awt.*;

public class ServerDashboardTab extends JPanel {
    private final JButton startServer = new JButton("Start server");
    private final JButton stopServer = new JButton("Stop server");
    private final JButton killServer = new JButton("Kill server");
    private final MCServer server;
//    private final int index;
    private final ServerConsole serverConsole;
    private final ContainerPane parentPane;
    private final CPUChart cpuChart = new CPUChart();
    private final RAMChart ramChart = new RAMChart();

    public ServerDashboardTab(ContainerPane parentPane, MCServer server) {
//        this.index = index;
        this.parentPane = parentPane;
        this.server = server;

        setLayout(new BorderLayout());

        JPanel upperPanel = new JPanel(new BorderLayout());
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel serverButtons = new JPanel();
        serverConsole = new ServerConsole(parentPane, server, this);


        upperPanel.add(Box.createRigidArea(new Dimension(5, 10)), BorderLayout.LINE_START);
        upperPanel.add(serverConsole, BorderLayout.CENTER);
        upperPanel.add(Box.createRigidArea(new Dimension(5, 10)), BorderLayout.LINE_END);

        serverButtons.add(startServer);
        serverButtons.add(stopServer);
        serverButtons.add(killServer);


        JPanel cpuMonitor = new JPanel(); //DO NOT DELETE!!! When this panel is deleted, the ProcessorPanel is not spaced correctly.
        cpuMonitor.add(cpuChart);

        JPanel ramMonitor = new JPanel(); //DO NOT DELETE!!! When this panel is deleted, the ProcessorPanel is not spaced correctly.
        ramMonitor.add(ramChart);

        bottomPanel.add(cpuMonitor, BorderLayout.LINE_START);
        bottomPanel.add(serverButtons, BorderLayout.CENTER);
        bottomPanel.add(ramMonitor, BorderLayout.LINE_END);

        stopServer.setVisible(false);

        add(upperPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.PAGE_END);

        killServer.setEnabled(false);

        startServer.addActionListener(e -> startServer());

        stopServer.addActionListener(e -> stopServer());

        killServer.addActionListener(e -> killServer());
    }

    protected void setWaitingStop(boolean isWaiting) {
        stopServer.setEnabled(!isWaiting);
        if(isWaiting) {
            stopServer.setText("Stopping server...");

            return;
        }
        stopServer.setText("Stop server");
    }

    protected void changeServerActionButtonsVisibility(boolean isServerStarting) {
        startServer.setVisible(!isServerStarting);
        stopServer.setVisible(isServerStarting);
        killServer.setEnabled(isServerStarting);
        serverConsole.setPIDTextVisible(isServerStarting);

        ServerTabLabel tabLabel = (ServerTabLabel) parentPane.getTabComponentAt(server.getServerId());
        tabLabel.changeServerActionContextMenuToServerStart(!isServerStarting);
    }

    public void startServer() {
        serverConsole.startServerWithoutChangingTheButtons();
        changeServerActionButtonsVisibility(true);
    }

    public void stopServer() {
        serverConsole.executeCommand("stop");
    }

    private void killServer() {
        serverConsole.killServer();
        changeServerActionButtonsVisibility(false);
        parentPane.setIconAt(server.getServerId(), DefaultIcons.getServerPlatformIcon(DefaultIcons.SERVER_OFFLINE));
    }

    public void setChartsEnabled(boolean setEnabled) {
        cpuChart.setVisible(setEnabled);
        ramChart.setVisible(setEnabled);
    }

    public ServerConsole getServerConsoleArea() {
        return serverConsole;
    }
}
