package com.myne145.serverlauncher.Server;

import com.myne145.serverlauncher.Enums.AlertType;
import com.myne145.serverlauncher.Enums.RunMode;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static com.myne145.serverlauncher.Gui.Frame.alert;
import static com.myne145.serverlauncher.Gui.Frame.getErrorDialogMessage;

public class Runner extends Thread {
    private final RunMode runMode;
    private String pathToServerFolder;

    public Runner(RunMode runMode) {
        this.runMode = runMode;
    }

    public Runner(RunMode runMode, String serverPath) {
        this.runMode = runMode;
        pathToServerFolder = serverPath;
    }

    private void openFolder(File folder) {

        // check if the directory exists
        if (!folder.exists()) {
            alert(AlertType.ERROR, "Directory not found: " + folder.getAbsolutePath());
            return;
        }

        // check if the Desktop API is supported
        if (!Desktop.isDesktopSupported()) {
            alert(AlertType.ERROR, "Desktop API is not supported on this platform");
            return;
        }

        // get the Desktop instance
        Desktop desktop = Desktop.getDesktop();

        // check if the directory can be opened
        if (!desktop.isSupported(Desktop.Action.OPEN)) {
            alert(AlertType.ERROR, "Open action is not supported on this platform");
            return;
        }

        // open the directory with the default file manager
        try {
            desktop.open(folder);
        } catch (IOException e) {
            alert(AlertType.ERROR, "Cannot open server's directory.\n" + getErrorDialogMessage(e));
        }
    }
    @Override
    public void run() {
        switch (runMode) {
            case CONFIG_FILE -> {
                File file = new File("servers.json");

                // check if the file exists
                if (!file.exists()) {
                    alert(AlertType.ERROR, "Config file " + file.getAbsolutePath() + " not found.");
                    return;
                }

                // check if the Desktop API is supported
                if (!Desktop.isDesktopSupported()) {
                    alert(AlertType.ERROR, "Desktop API is not supported on this platform.");
                    return;
                }

                // get the Desktop instance
                Desktop desktop = Desktop.getDesktop();

                // check if the file is a directory
                if (file.isDirectory()) {
                    return;
                }

                // check if the file can be opened
                if (!desktop.isSupported(Desktop.Action.OPEN)) {
                    alert(AlertType.ERROR, "Open Action is not supported on this platform.");
                    return;
                }

                // open the file with the default editor
                try {
                    desktop.open(file);
                } catch (IOException e) {
                    alert(AlertType.ERROR, "Cannot open \"servers.json\" file.\n" + getErrorDialogMessage(e));
                }
            }
            case SERVER_FOLDER -> openFolder(new File(pathToServerFolder));
            case GLOBAL_FOLDER -> openFolder(new File(Config.globalServerFolder));
        }
    }
}
