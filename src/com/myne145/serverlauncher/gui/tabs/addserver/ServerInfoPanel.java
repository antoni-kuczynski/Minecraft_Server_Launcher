package com.myne145.serverlauncher.gui.tabs.addserver;

import com.formdev.flatlaf.ui.FlatLineBorder;
import com.formdev.flatlaf.util.SystemInfo;
import com.myne145.serverlauncher.gui.window.Window;
import com.myne145.serverlauncher.server.MinecraftServer;
import com.myne145.serverlauncher.utils.Colors;
import com.myne145.serverlauncher.utils.DefaultIcons;

import javax.swing.*;

import java.awt.*;

public class ServerInfoPanel extends JPanel {
    private final JLabel serverDetailsText = new JLabel("<html><center>Server.properties file not found!</center></html>", SwingConstants.CENTER);


    @Override
    public void setVisible(boolean aFlag) {
//        int futureSize = Window.getWindow().getWidth() + 310;

        super.setVisible(aFlag);
    }

    public void updateText(MinecraftServer server) {
        if(server.getServerJarPath() == null || !server.hasServerProperties()) {
            serverDetailsText.setIcon(null);
            return;
        }

        String gamemode = server.getProperty("gamemode");
        gamemode = switch (gamemode) {
            case "0" -> "Survival";
            case "1" -> "Creative";
            case "2" -> "Adventure";
            case "3" -> "Spectator";
            default -> String.valueOf(gamemode.charAt(0)).toUpperCase() + gamemode.substring(1);
        };

        boolean isInOnlineMode = server.getProperty("online-mode").equals("true");
        String world = "";
        if(server.getServerWorld().hasLevelDat())
            world = "<br>World: " + server.getServerWorld().getLevelNameColors();

        serverDetailsText.setText(
                "<html><center>" +
                        "<b>" + server.getPlatform().toString() + "</b>" +
                        "<br>" + gamemode + " Mode" +
                        "<br>Version: " + server.getMinecraftVersion() +
                         world +
                        "<br>Port: " + server.getProperty("server-port") +
                        "<br>" + (isInOnlineMode ? "Online Mode" : "Offline Mode") +
                        "</center></html>");

        serverDetailsText.setIcon(DefaultIcons.getServerPlatformIcon(server.getPlatform()).derive(96, 96));
    }

    public ServerInfoPanel() {
        BoxLayout boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
        this.setLayout(boxLayout);

//        serverDetailsText.setFont(new Font("Arial", Font.PLAIN, 14));
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
        if(SystemInfo.isWindows) {
            serverDetailsText.setFont(new Font("Arial", Font.PLAIN, 16));
        } else {
            serverDetailsText.setFont(new Font("Arial", Font.PLAIN, Window.getScaledSize(15)));
        }
//        add(image);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(serverDetailsText);
    }
}
