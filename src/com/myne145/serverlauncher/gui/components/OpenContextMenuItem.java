package com.myne145.serverlauncher.gui.components;

import com.myne145.serverlauncher.server.Config;
import com.myne145.serverlauncher.utils.DesktopOpener;

import javax.swing.*;
import java.io.File;

public class OpenContextMenuItem extends JMenuItem {
    private File file = new File("");
    private String defaultTitle = "";

    public void updatePath(File f) {
        if(f == null)
            return;
        file = f;
        this.setText("<html>" + defaultTitle + "\n<center><sub>" + Config.abbreviateFile(f.getAbsolutePath(), 27) + "</html>");
    }

    public OpenContextMenuItem(String defaultTitle) {
        this.defaultTitle = defaultTitle;
        this.setText("<html>" + defaultTitle);
        this.addActionListener(e -> {
            if(file.isFile()) {
                DesktopOpener.openFile(file);
            } else if(file.isDirectory()) {
                DesktopOpener.openFolder(file);
            }
        });
    }
}
