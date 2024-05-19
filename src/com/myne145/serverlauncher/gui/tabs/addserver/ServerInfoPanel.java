package com.myne145.serverlauncher.gui.tabs.addserver;

import com.formdev.flatlaf.ui.FlatLineBorder;
import com.myne145.serverlauncher.server.MCServer;
import com.myne145.serverlauncher.utils.Colors;
import com.myne145.serverlauncher.utils.DefaultIcons;

import javax.swing.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ServerInfoPanel extends JPanel{
    private final JLabel serverDetailsText = new JLabel("<html><center>Server details are going to appear here.</center></html>", SwingConstants.CENTER);

    protected void updateText(MCServer server) {
        if(server.getServerJarPath() == null)
            return;

        String gamemode = server.getProperty("gamemode");
        gamemode = switch (gamemode) {
            case "0" -> "Survival";
            case "1" -> "Creative";
            case "2" -> "Adventure";
            case "3" -> "Spectator";
            default -> String.valueOf(gamemode.charAt(0)).toUpperCase() + gamemode.substring(1);
        };


        boolean isInOnlineMode = server.getProperty("online-mode").equals("true");

        serverDetailsText.setText(
                "<html><center>" +
                        "<b>" + server.getPlatform().toString() + "</b>" +
                        "<br>" + gamemode + " Mode" +
                        "<br>Version: " + "PLACEHOLDER" +
                        "<br>World: " + "a really long world name for testing purposes lol" +
                        "<br>Port: " + server.getProperty("server-port") +
                        "<br>" + (isInOnlineMode ? "Online Mode" : "Offline Mode") +
                        "</center></html>");

        serverDetailsText.setIcon(DefaultIcons.getIcon(server.getPlatform()));
    }

    protected ServerInfoPanel() {
        BoxLayout boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
        this.setLayout(boxLayout);

        serverDetailsText.setFont(new Font("Arial", Font.PLAIN, 14));
        serverDetailsText.setHorizontalTextPosition(JLabel.CENTER);
        serverDetailsText.setVerticalTextPosition(JLabel.BOTTOM);

        this.setBorder(new FlatLineBorder(new Insets(10, 20, 100, 20), Colors.BORDER_COLOR, 1, 16));
        this.setBackground(Colors.COMPONENT_PRIMARY_COLOR);
        this.setAlignmentX(LEFT_ALIGNMENT);
        this.setPreferredSize(new Dimension(250, 288));
        this.setMaximumSize(new Dimension(250, 288));

//        serverDetailsText.setText("<html><center>PaperMC Server<br>Version: 1.19.4<br>Online mode<br>World name: Parkour Paradise 3<br>Java version: 19.0.2<br>Gamemode: Survival<br>Port: 25565<br></center></html>");
//        serverDetailsText.setText("<html><center><b>PaperMC Server</b><br>Survival Mode<br>Version: 1.19.4<br>World: Parkour Paradise 3<br>Port: 25565<br>Online mode</center></html>");
        serverDetailsText.setIcon(DefaultIcons.getIcon(DefaultIcons.WORLD_MISSING));


//        add(image);
        add(serverDetailsText);
    }
}
