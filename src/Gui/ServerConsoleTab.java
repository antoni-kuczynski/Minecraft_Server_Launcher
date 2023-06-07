package Gui;

import Enums.AlertType;
import Enums.RunMode;
import SelectedServer.NBTParser;
import SelectedServer.ServerDetails;
import SelectedServer.ServerPropertiesFile;
import Server.ButtonData;
import Server.Config;
import Server.Runner;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class ServerConsoleTab extends JPanel {
    private final JButton startServer = new JButton("Start Server");
    private final JButton stopServer = new JButton("Stop Server");
    private final JButton killServer = new JButton("Kill Server");
    public ServerConsoleTab() {
        setLayout(new BorderLayout());
        JPanel upperPanel = new JPanel(new BorderLayout());
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel serverButtons = new JPanel();



        Config config;
        try {
            config = new Config();
        } catch (IOException ex) {
            Frame.alert(AlertType.ERROR, Frame.getErrorDialogMessage(ex));
            return;
        }

        ArrayList<ServerConsoleArea> consoleAreas = new ArrayList<>();
        for(int i = 0; i < config.getData().size(); i++) {
//            ServerConsoleArea serverConsoleArea = new ServerConsoleArea(new Dimension(500, 500));
            consoleAreas.add(new ServerConsoleArea(new Dimension(500, 500)));
        }
        System.out.println(ServerDetails.serverId);
        upperPanel.add(consoleAreas.get(ServerDetails.serverId - 1), BorderLayout.CENTER);

        serverButtons.add(startServer);
        serverButtons.add(stopServer);
        serverButtons.add(killServer);
        bottomPanel.add(serverButtons, BorderLayout.PAGE_END);
        stopServer.setVisible(false);

        add(upperPanel, BorderLayout.PAGE_START);
        add(bottomPanel, BorderLayout.PAGE_END);

        killServer.setEnabled(false);

        startServer.addActionListener(e -> {
            ButtonData serverConfig = config.getData().get(ServerDetails.serverId - 1);
            consoleAreas.get(ServerDetails.serverId - 1).startServer(serverConfig);
            startServer.setVisible(false);
            stopServer.setVisible(true);
            killServer.setEnabled(true);

        });

        stopServer.addActionListener(e -> {
            stopServer.setVisible(false);
            startServer.setVisible(true);
            killServer.setEnabled(false);
            consoleAreas.get(ServerDetails.serverId - 1).executeCommand("stop");
        });

        killServer.addActionListener(e -> {
            consoleAreas.get(ServerDetails.serverId - 1).killServer();
            stopServer.setVisible(false);
            startServer.setVisible(true);
        });
    }
}
