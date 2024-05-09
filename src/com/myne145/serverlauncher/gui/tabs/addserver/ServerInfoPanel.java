package com.myne145.serverlauncher.gui.tabs.addserver;

import com.myne145.serverlauncher.utils.DefaultIcons;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class ServerInfoPanel extends JPanel{
//    private final JLabel image = new JLabel(DefaultIcons.getIcon(DefaultIcons.WORLD_MISSING)); //placeholder icon
    private final JLabel serverDetailsText = new JLabel("Server details will appear here.", SwingConstants.CENTER);

    protected ServerInfoPanel() {
        BoxLayout boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
        this.setLayout(boxLayout);

//        serverDetailsText.setBorder(new LineBorder(Color.BLACK));
        serverDetailsText.setFont(new Font("Arial", Font.BOLD, 14));
        serverDetailsText.setHorizontalTextPosition(JLabel.CENTER);
        serverDetailsText.setVerticalTextPosition(JLabel.BOTTOM);

        serverDetailsText.setText("<html><center>PaperMC Server<br>Version: 1.19.4<br>Online mode<br>World name: Parkour Paradise 3</center></html>");
        serverDetailsText.setIcon(DefaultIcons.getIcon(DefaultIcons.WORLD_MISSING));


//        add(image);
        add(serverDetailsText);
    }
}
