package Gui;

import CustomJComponents.RoundedPanelBorder;
import Enums.AlertType;
import Enums.RunMode;
import SelectedServer.NBTParser;
import SelectedServer.ServerDetails;
import SelectedServer.ServerPropertiesFile;
import Server.ButtonData;
import Server.Config;
import Server.Runner;
import com.formdev.flatlaf.ui.FlatRoundBorder;
import mdlaf.shadows.RoundedCornerBorder;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class ServerConsoleTab extends JPanel {
    private final JButton startServer = new JButton("Start Server");
    private final JButton stopServer = new JButton("Stop Server");
    private final JButton killServer = new JButton("Kill Server");
    private final ContainerPane parentPane;
    private final ImageIcon OFFLINE = new ImageIcon(new ImageIcon("resources/offline.png").getImage().getScaledInstance(32,32, Image.SCALE_SMOOTH));
    private final ImageIcon ONLINE = new ImageIcon(new ImageIcon("resources/online.png").getImage().getScaledInstance(32,32, Image.SCALE_SMOOTH));
    private final ImageIcon ERRORED = new ImageIcon(new ImageIcon("resources/errored.png").getImage().getScaledInstance(32,32, Image.SCALE_SMOOTH));
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
            Frame.alert(AlertType.ERROR, Frame.getErrorDialogMessage(ex));
            return;
        }

//        startServer.setBorder(new RoundedPanelBorder(new Color(56, 56, 56), 2));
        ServerConsoleArea serverConsoleArea = new ServerConsoleArea(new Dimension(500, 500));

        upperPanel.add(Box.createRigidArea(new Dimension(5,10)), BorderLayout.LINE_START);
        upperPanel.add(serverConsoleArea, BorderLayout.CENTER);
        upperPanel.add(Box.createRigidArea(new Dimension(5,10)), BorderLayout.LINE_END);


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
            serverConsoleArea.startServer(serverConfig);
            startServer.setVisible(false);
            stopServer.setVisible(true);
            killServer.setEnabled(true);
            serverConsoleArea.serverPIDText.setVisible(true);
//            parentPane.setIconAt(0, new ImageIcon("resources/app_icon.png"));
        });

        stopServer.addActionListener(e -> {
            stopServer.setVisible(false);
            startServer.setVisible(true);
            killServer.setEnabled(false);
            serverConsoleArea.executeCommand("stop");
            serverConsoleArea.serverPIDText.setVisible(false);
//            parent.icon
            parent.setIconAt(0, ONLINE);
        });

        killServer.addActionListener(e -> {
            serverConsoleArea.killServer();
            stopServer.setVisible(false);
            startServer.setVisible(true);
            serverConsoleArea.serverPIDText.setVisible(false);
        });
    }
}
