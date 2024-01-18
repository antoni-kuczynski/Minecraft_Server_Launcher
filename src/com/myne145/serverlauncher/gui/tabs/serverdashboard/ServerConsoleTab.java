package com.myne145.serverlauncher.gui.tabs.serverdashboard;

import com.myne145.serverlauncher.gui.tabs.serverdashboard.charts.CPUChart;
import com.myne145.serverlauncher.gui.tabs.serverdashboard.charts.RAMChart;
import com.myne145.serverlauncher.gui.window.ContainerPane;
import com.myne145.serverlauncher.gui.tabs.serverdashboard.components.ServerConsoleArea;
import com.myne145.serverlauncher.gui.window.ServerTabLabel;
import com.myne145.serverlauncher.utils.DesktopOpener;
import com.myne145.serverlauncher.server.MCServer;
import com.myne145.serverlauncher.server.Config;
import com.myne145.serverlauncher.utils.ServerIcon;

import javax.swing.*;
import java.awt.*;

public class ServerConsoleTab extends JPanel {
    private final JButton startServer = new JButton("Start server");
    private final JButton stopServer = new JButton("Stop server");
    private final JButton killServer = new JButton("Kill server");
    private final int index;
    private final ServerConsoleArea serverConsoleArea;
    private final ContainerPane parentPane;
    private final CPUChart cpuChart = new CPUChart();
    private final RAMChart ramChart = new RAMChart();

    public ServerConsoleTab(ContainerPane parentPane, int index) {
        this.index = index;
        this.parentPane = parentPane;

        setLayout(new BorderLayout());
        JPanel upperPanel = new JPanel(new BorderLayout());
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel serverButtons = new JPanel();
        serverConsoleArea = new ServerConsoleArea(parentPane, index, this);

        if(!Config.getData().get(index).serverPath().exists()) {
            JButton button = new JButton("Open config file");
            button.addActionListener(e -> DesktopOpener.openConfigFile());
            add(new JLabel("Add a server in servers.json file to access the server panel!"), BorderLayout.LINE_START);
            add(button, BorderLayout.PAGE_END);

            return;
        }


        upperPanel.add(Box.createRigidArea(new Dimension(5, 10)), BorderLayout.LINE_START);
        upperPanel.add(serverConsoleArea, BorderLayout.CENTER);
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

    public void startServer() {
        MCServer MCServerConfig = Config.getData().get(index);
        serverConsoleArea.startServerWithoutChangingTheButtons(MCServerConfig);
        startServer.setVisible(false);
        stopServer.setVisible(true);
        killServer.setEnabled(true);
        serverConsoleArea.serverPIDText.setVisible(true);

        ServerTabLabel tabLabel = (ServerTabLabel) parentPane.getTabComponentAt(index);
        tabLabel.changeServerActionText(1);
    }

    public void stopServer() {
        serverConsoleArea.executeCommand("stop");
        startServer.setVisible(true);
        stopServer.setVisible(false);
        killServer.setEnabled(false);
        serverConsoleArea.serverPIDText.setVisible(true);

        ServerTabLabel tabLabel = (ServerTabLabel) parentPane.getTabComponentAt(index);
        tabLabel.changeServerActionText(0);
    }

    private void killServer() {
        serverConsoleArea.killServer();
        stopServer.setVisible(false);
        startServer.setVisible(true);
        serverConsoleArea.serverPIDText.setVisible(false);
        serverConsoleArea.wasServerStopCausedByUser = true;
        parentPane.setIconAt(index, ServerIcon.getServerIcon(ServerIcon.OFFLINE));
        killServer.setEnabled(false);
    }

    public void disableCharts() {
        cpuChart.setVisible(false);
        ramChart.setVisible(false);
    }
    public void enableCharts() {
        cpuChart.setVisible(true);
        ramChart.setVisible(true);
    }

    public ServerConsoleArea getServerConsoleArea() {
        return serverConsoleArea;
    }

    public CPUChart getCpuChart() {
        return cpuChart;
    }

    public RAMChart getRamChart() {
        return ramChart;
    }

    public JButton getStartServerButton() {
        return startServer;
    }

    public JButton getStopServerButton() {
        return stopServer;
    }

    public JButton getKillServerButton() {
        return killServer;
    }
}
