package com.myne145.serverlauncher.Server;

import com.myne145.serverlauncher.Gui.AlertType;
import com.myne145.serverlauncher.Server.Current.CurrentServerInfo;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import static com.myne145.serverlauncher.Gui.Window.alert;
import static com.myne145.serverlauncher.Gui.Window.getErrorDialogMessage;

public class FileOpener {

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

    public static void openServerFolder() {
        File folder = CurrentServerInfo.serverPath;
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
