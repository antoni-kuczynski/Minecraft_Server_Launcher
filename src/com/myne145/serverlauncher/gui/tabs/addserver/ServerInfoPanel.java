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

    }

    protected ServerInfoPanel() {
        BoxLayout boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
        this.setLayout(boxLayout);

//        try {
////            System.out.println(getJavaVersion(new File("C:\\Program Files\\Eclipse Adoptium\\jdk-19.0.2.7-hotspot\\")));
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        serverDetailsText.setBorder(new LineBorder(Color.BLACK));
        serverDetailsText.setFont(new Font("Arial", Font.PLAIN, 14));
        serverDetailsText.setHorizontalTextPosition(JLabel.CENTER);
        serverDetailsText.setVerticalTextPosition(JLabel.BOTTOM);

//        serverDetailsText.setText("<html><center>PaperMC Server<br>Version: 1.19.4<br>Online mode<br>World name: Parkour Paradise 3<br>Java version: 19.0.2<br>Gamemode: Survival<br>Port: 25565<br></center></html>");
        serverDetailsText.setText("<html><center><b>PaperMC Server</b><br>Survival Mode<br>Version: 1.19.4<br>World: Parkour Paradise 3<br>Port: 25565<br>Online mode</center></html>");
        serverDetailsText.setIcon(DefaultIcons.getIcon(DefaultIcons.WORLD_MISSING));


//        add(image);
        add(serverDetailsText);
    }
}
