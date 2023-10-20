package com.myne145.serverlauncher.utils;

import com.myne145.serverlauncher.server.Config;
import com.myne145.serverlauncher.server.current.CurrentServerInfo;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static com.myne145.serverlauncher.gui.window.Window.alert;
import static com.myne145.serverlauncher.gui.window.Window.getErrorDialogMessage;

public class DesktopOpener {

    public static void openConfigFile() {
        File file = new File("servers.json");

        if (!file.exists()) {
            alert(AlertType.ERROR, "Config file " + file.getAbsolutePath() + " not found.");
            return;
        }

        if (!Desktop.isDesktopSupported()) {
            alert(AlertType.ERROR, "Desktop API is not supported on this platform.");
            return;
        }

        Desktop desktop = Desktop.getDesktop();

        if (file.isDirectory()) {
            return;
        }

        if (!desktop.isSupported(Desktop.Action.OPEN)) {
            alert(AlertType.ERROR, "Open Action is not supported on this platform.");
            return;
        }

        try {
            desktop.open(file);
        } catch (IOException e) {
            alert(AlertType.ERROR, "Cannot open \"servers.json\" file.\n" + getErrorDialogMessage(e));
        }
    }

    public static void openServerFolder(int index) {
        File folder = Config.getData().get(index).serverPath();
        if (!folder.exists()) {
            alert(AlertType.ERROR, "Directory not found: " + folder.getAbsolutePath());
            return;
        }
        if (!Desktop.isDesktopSupported()) {
            alert(AlertType.ERROR, "Desktop API is not supported on this platform");
            return;
        }

        Desktop desktop = Desktop.getDesktop();

        if (!desktop.isSupported(Desktop.Action.OPEN)) {
            alert(AlertType.ERROR, "Open action is not supported on this platform");
            return;
        }
        try {
            desktop.open(folder);
        } catch (IOException e) {
            alert(AlertType.ERROR, "Cannot open server's directory.\n" + getErrorDialogMessage(e));
        }
    }
}
