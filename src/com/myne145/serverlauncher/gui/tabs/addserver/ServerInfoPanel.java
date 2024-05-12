package com.myne145.serverlauncher.gui.tabs.addserver;

import com.myne145.serverlauncher.server.MCServer;
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
//    private final JLabel image = new JLabel(DefaultIcons.getIcon(DefaultIcons.WORLD_MISSING)); //placeholder icon
    private final JLabel serverDetailsText = new JLabel("Server details will appear here.", SwingConstants.CENTER);

    protected void updateText(MCServer server) {
        if(server.getServerJarPath() == null)
            return;

        String gamemode = server.getProperty("gamemode");
        boolean isInOnlineMode = server.getProperty("online-mode").equals("true");

        serverDetailsText.setText(
                "<html><center>" +
                        "<b>" + server.getPlatform().toString() + "</b>" +
                        "<br>" + server.getProperty("gamemode") + " Mode" +
                        "<br>Version: " + "PLACEHOLDER" +
                        "<br>World: " + "PLACEHOLDER" +
                        "<br>Port: " + server.getProperty("server-port") +
                        "<br>" + (isInOnlineMode ? "Online mode" : "Offline mode") +
                        "</center></html>");
    }

    protected ServerInfoPanel() {
        BoxLayout boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
        this.setLayout(boxLayout);

        serverDetailsText.setFont(new Font("Arial", Font.PLAIN, 14));
        serverDetailsText.setHorizontalTextPosition(JLabel.CENTER);
        serverDetailsText.setVerticalTextPosition(JLabel.BOTTOM);

//        serverDetailsText.setText("<html><center>PaperMC Server<br>Version: 1.19.4<br>Online mode<br>World name: Parkour Paradise 3<br>Java version: 19.0.2<br>Gamemode: Survival<br>Port: 25565<br></center></html>");
//        serverDetailsText.setText("<html><center><b>PaperMC Server</b><br>Survival Mode<br>Version: 1.19.4<br>World: Parkour Paradise 3<br>Port: 25565<br>Online mode</center></html>");
        serverDetailsText.setIcon(DefaultIcons.getIcon(DefaultIcons.WORLD_MISSING));


//        add(image);
        add(serverDetailsText);
    }
}
