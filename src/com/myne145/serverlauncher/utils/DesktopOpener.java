package com.myne145.serverlauncher.utils;

import com.myne145.serverlauncher.server.Config;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.myne145.serverlauncher.gui.window.Window.showErrorMessage;

public class DesktopOpener {

    public static void openFolder(File folder) {
        Desktop desktop = Desktop.getDesktop();

        if (!folder.exists()) {
            showErrorMessage("Directory not found: " + folder.getAbsolutePath(), new FileNotFoundException());
            return;
        }
        if (!Desktop.isDesktopSupported() || !desktop.isSupported(Desktop.Action.OPEN)) {
            showErrorMessage("Desktop API is not supported on this platform", new UnsupportedOperationException());
            return;
        }

        try {
            desktop.open(folder);
        } catch (IOException e) {
            showErrorMessage("I/O error opening server's directory.\n", e);
        }
    }

    public static void openFile(File file) {
        Desktop desktop = Desktop.getDesktop();

        if (!file.exists()) {
            showErrorMessage(Config.ABSOLUTE_PATH + " file not found.", new FileNotFoundException());
            return;
        }

        if (!Desktop.isDesktopSupported() || !desktop.isSupported(Desktop.Action.OPEN)) {
            showErrorMessage("Desktop API is not supported on this platform.", new UnsupportedOperationException());
            return;
        }

        try {
            desktop.open(file);
        } catch (IOException e) {
            showErrorMessage("I/O error opening " + Config.ABSOLUTE_PATH + "file.\n", e);
        }
    }
}
