package com.myne145.serverlauncher.gui;

import javax.swing.*;
import java.awt.*;

public class PickDirectoryButton extends JButton {
    public PickDirectoryButton(String defaultTitle, Dimension defaultSize) {
        this.setText("<html>" + defaultTitle + "</html>");
        this.setMaximumSize(defaultSize);


    }

}
