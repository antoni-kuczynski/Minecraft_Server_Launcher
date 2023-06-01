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

public class ServerConsoleTab extends JPanel {
    private final JButton startServer = new JButton("Start Server");
    public ServerConsoleTab() {
        setLayout(new BorderLayout());
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel serverButtons = new JPanel();

        serverButtons.add(startServer);
        bottomPanel.add(serverButtons, BorderLayout.PAGE_END);

        add(bottomPanel, BorderLayout.PAGE_END);

        startServer.addActionListener(e -> {
            Config config;
            try {
                config = new Config();
            } catch (IOException ex) {
                Frame.alert(AlertType.ERROR, Frame.getErrorDialogMessage(ex));
                return;
            }

            ButtonData serverConfig = config.getData().get(ServerDetails.serverId);
            try {
                ServerSelectionPanel.setServerVariables(serverConfig.getButtonText(), serverConfig.getPathToServerFolder(), serverConfig.getServerId());
                new ServerPropertiesFile(); //this needs a refactor - makes level-name actually update TODO
                NBTParser nbtParser = new NBTParser(); //reading NBT level.dat file for level name
                nbtParser.start();
                nbtParser.join();
                ServerDetails.serverLevelName = nbtParser.getLevelName();
            } catch (Exception ex) {
                // Frame.alert(AlertType.ERROR, Frame.getErrorDialogMessage(ex));
            }
            new Runner(serverConfig.getPathToServerJarFile(), RunMode.SERVER_JAR, serverConfig.getPathToJavaRuntime(),
                serverConfig.getServerLaunchArguments()).start();
        });
    }


}
