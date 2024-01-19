package com.myne145.serverlauncher.utils;

import com.myne145.serverlauncher.server.Config;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static com.myne145.serverlauncher.gui.window.Window.alert;
import static com.myne145.serverlauncher.gui.window.Window.getErrorDialogMessage;

public class DesktopOpener {

    public static void openConfigFile() {
        File file = new File(Config.ABSOLUTE_PATH);
        Desktop desktop = Desktop.getDesktop();

        if (!file.exists()) {
            alert(AlertType.ERROR, "Config file at " + Config.ABSOLUTE_PATH + " not found.");
            return;
        }

        if (!Desktop.isDesktopSupported() || !desktop.isSupported(Desktop.Action.OPEN)) {
            alert(AlertType.ERROR, "Desktop API is not supported on this platform.");
            return;
        }

        try {
            desktop.open(file);
        } catch (IOException e) {
            alert(AlertType.ERROR, "Cannot open \"servers.json\" file.\n" + getErrorDialogMessage(e));
        }
    }

    public static void openServerFolder(int serverIndex) {
        File folder = Config.getData().get(serverIndex).serverPath();
        Desktop desktop = Desktop.getDesktop();

        if (!folder.exists()) {
            alert(AlertType.ERROR, "Directory not found: " + folder.getAbsolutePath());
            return;
        }
        if (!Desktop.isDesktopSupported() || !desktop.isSupported(Desktop.Action.OPEN)) {
            alert(AlertType.ERROR, "Desktop API is not supported on this platform");
            return;
        }

        try {
            desktop.open(folder);
        } catch (IOException e) {
            alert(AlertType.ERROR, "Cannot open server's directory.\n" + getErrorDialogMessage(e));
        }
    }
}
