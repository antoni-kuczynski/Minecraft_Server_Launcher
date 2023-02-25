package Main;

import javax.swing.*;
import java.awt.*;

public class ConfigStuffPanel extends JPanel {

    public ConfigStuffPanel() {
        JButton openCfg = new JButton("Open App's Config File");
        JButton openServerFolder = new JButton("Open Last Opened Server Folder");
        add(openCfg);
        add(openServerFolder);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // add your code here
    }

}
