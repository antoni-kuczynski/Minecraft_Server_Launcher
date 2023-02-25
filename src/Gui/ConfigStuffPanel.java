package Gui;

import javax.swing.*;
import java.awt.*;

public class ConfigStuffPanel extends JPanel {

    public ConfigStuffPanel() {
        setLayout(new BorderLayout(10, 10));
        JButton openCfg = new JButton("Open App's Config File");
        JButton openServerFolder = new JButton("Open Last Opened Server Folder");


        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        add(separator, BorderLayout.PAGE_START);
        add(openCfg, BorderLayout.LINE_START);
        add(openServerFolder, BorderLayout.LINE_END);
    }
}
