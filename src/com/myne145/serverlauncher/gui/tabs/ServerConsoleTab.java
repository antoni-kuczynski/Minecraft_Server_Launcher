package com.myne145.serverlauncher.gui.tabs;

import com.myne145.serverlauncher.gui.charts.CPUChart;
import com.myne145.serverlauncher.gui.charts.RAMChart;
import com.myne145.serverlauncher.gui.ContainerPane;
import com.myne145.serverlauncher.gui.tabs.components.ServerConsoleArea;
import com.myne145.serverlauncher.server.current.CurrentServerInfo;
import com.myne145.serverlauncher.server.MCServer;
import com.myne145.serverlauncher.server.Config;

import javax.swing.*;
import java.awt.*;

import static com.myne145.serverlauncher.gui.Window.SERVER_STATUS_ICON_DIMENSION;

public class ServerConsoleTab extends JPanel {
    public final JButton startServer = new JButton("Start Server");
    public final JButton stopServer = new JButton("Stop Server");
    public final JButton killServer = new JButton("Kill Server");
    private final ContainerPane parentPane;
    public final int index;
    private final ServerConsoleArea serverConsoleArea;
    public final CPUChart cpuChart = new CPUChart();
    public final RAMChart ramChart = new RAMChart();

    public ServerConsoleTab(ContainerPane parent, int index) {
        parentPane = parent;
        this.index = index;
        setLayout(new BorderLayout());
        JPanel upperPanel = new JPanel(new BorderLayout());
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel serverButtons = new JPanel();
        serverConsoleArea = new ServerConsoleArea(parentPane, index, this);
//        serverConsoleArea.setPreferredSize(new Dimension(500,500));

        if(!Config.getData().get(index).serverPath().exists()) {
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

        startServer.addActionListener(e -> {
            MCServer MCServerConfig = Config.getData().get(CurrentServerInfo.serverId - 1);
            serverConsoleArea.startServer(MCServerConfig);
            startServer.setVisible(false);
            stopServer.setVisible(true);
            killServer.setEnabled(true);
            serverConsoleArea.serverPIDText.setVisible(true);
            parentPane.setIconAt(index, new ImageIcon(new ImageIcon(Config.RESOURCES_PATH + "/server_online.png").getImage().getScaledInstance(SERVER_STATUS_ICON_DIMENSION, SERVER_STATUS_ICON_DIMENSION, Image.SCALE_SMOOTH)));
        });

        stopServer.addActionListener(e -> {
            stopServer.setVisible(false);
            startServer.setVisible(true);
            killServer.setEnabled(false);
            serverConsoleArea.executeCommand("stop");
            serverConsoleArea.isServerStopCausedByAButton = true;
            serverConsoleArea.serverPIDText.setVisible(false);
            parent.setIconAt(index, new ImageIcon(new ImageIcon(Config.RESOURCES_PATH + "/server_offline.png").getImage().getScaledInstance(SERVER_STATUS_ICON_DIMENSION, SERVER_STATUS_ICON_DIMENSION, Image.SCALE_SMOOTH)));
        });

        killServer.addActionListener(e -> {
            serverConsoleArea.killServer();
            stopServer.setVisible(false);
            startServer.setVisible(true);
            serverConsoleArea.serverPIDText.setVisible(false);
            serverConsoleArea.isServerStopCausedByAButton = true;
            parent.setIconAt(index, new ImageIcon(new ImageIcon(Config.RESOURCES_PATH + "/server_offline.png").getImage().getScaledInstance(SERVER_STATUS_ICON_DIMENSION, SERVER_STATUS_ICON_DIMENSION, Image.SCALE_SMOOTH)));
            killServer.setEnabled(false);
        });
    }
    public void disableCharts() {
        cpuChart.setVisible(false);
        ramChart.setVisible(false);
        cpuChart.isEnabled = false;
        ramChart.isEnabled = false;
    }
    public void enableCharts() {
        cpuChart.setVisible(true);
        ramChart.setVisible(true);
        cpuChart.isEnabled = true;
        ramChart.isEnabled = true;
    }

    public ServerConsoleArea getServerConsoleArea() {
        return serverConsoleArea;
    }
}
