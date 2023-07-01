package com.myne145.serverlauncher.Gui.Tabs;

import com.myne145.serverlauncher.Enums.AlertType;
import com.myne145.serverlauncher.Gui.Charts.CPUChart;
import com.myne145.serverlauncher.Gui.Charts.RAMChart;
import com.myne145.serverlauncher.Gui.ContainerPane;
import com.myne145.serverlauncher.Gui.Frame;
import com.myne145.serverlauncher.Gui.ServerConsoleArea;
import com.myne145.serverlauncher.SelectedServer.ServerDetails;
import com.myne145.serverlauncher.Server.ButtonData;
import com.myne145.serverlauncher.Server.Config;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ServerConsoleTab extends JPanel {
    public final JButton startServer = new JButton("Start Server");
    public final JButton stopServer = new JButton("Stop Server");
    public final JButton killServer = new JButton("Kill Server");
    private final ContainerPane parentPane;
    private final ImageIcon OFFLINE = new ImageIcon(new ImageIcon("resources/offline.png").getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH));
    private final ImageIcon ONLINE = new ImageIcon(new ImageIcon("resources/running.png").getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH));
    private final ImageIcon ERRORED = new ImageIcon(new ImageIcon("resources/errored.png").getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH));
    private final int index;

    public ServerConsoleTab(ContainerPane parent, int index) {
        parentPane = parent;
        this.index = index;
        setLayout(new BorderLayout());
        JPanel upperPanel = new JPanel(new BorderLayout());
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel serverButtons = new JPanel();

        Config config;
        try {
            config = new Config();
        } catch (IOException ex) {
            com.myne145.serverlauncher.Gui.Frame.alert(AlertType.ERROR, Frame.getErrorDialogMessage(ex));
            return;
        }

        ServerConsoleArea serverConsoleArea = new ServerConsoleArea(new Dimension(500, 500), parentPane, index, this);

        upperPanel.add(Box.createRigidArea(new Dimension(5, 10)), BorderLayout.LINE_START);
        upperPanel.add(serverConsoleArea, BorderLayout.CENTER);
        upperPanel.add(Box.createRigidArea(new Dimension(5, 10)), BorderLayout.LINE_END);


        serverButtons.add(startServer);
        serverButtons.add(stopServer);
        serverButtons.add(killServer);


        JPanel cpuMonitor = new JPanel(); //DO NOT DELETE!!! When this panel is deleted, the ProcessorPanel is not spaced correctly.
        cpuMonitor.add(new CPUChart()); //TODO: optimize that to save the enourmous memory amount of 20mb

        JPanel ramMonitor = new JPanel(); //DO NOT DELETE!!! When this panel is deleted, the ProcessorPanel is not spaced correctly.
        ramMonitor.add(new RAMChart());

        bottomPanel.add(cpuMonitor, BorderLayout.LINE_START);
        bottomPanel.add(serverButtons, BorderLayout.CENTER);
        bottomPanel.add(ramMonitor, BorderLayout.LINE_END);

        stopServer.setVisible(false);

        add(upperPanel, BorderLayout.PAGE_START);
        add(bottomPanel, BorderLayout.PAGE_END);

        killServer.setEnabled(false);

        startServer.addActionListener(e -> {
            ButtonData serverConfig = config.getData().get(ServerDetails.serverId - 1);
            serverConsoleArea.startServer(serverConfig);
            startServer.setVisible(false);
            stopServer.setVisible(true);
            killServer.setEnabled(true);
            serverConsoleArea.serverPIDText.setVisible(true);
            parentPane.setIconAt(index, ONLINE);
        });

        stopServer.addActionListener(e -> {
            stopServer.setVisible(false);
            startServer.setVisible(true);
            killServer.setEnabled(false);
            serverConsoleArea.executeCommand("stop");
            serverConsoleArea.isServerStopCausedByAButton = true;
            serverConsoleArea.serverPIDText.setVisible(false);
            parent.setIconAt(index, OFFLINE);
        });

        killServer.addActionListener(e -> {
            serverConsoleArea.killServer();
            stopServer.setVisible(false);
            startServer.setVisible(true);
            serverConsoleArea.serverPIDText.setVisible(false);
            serverConsoleArea.isServerStopCausedByAButton = true;
            parent.setIconAt(index, OFFLINE);
        });
    }
}
