package Servers;

import Gui.AlertType;
import Gui.Frame;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static Gui.Frame.alert;

public class Runner extends Thread {
    private String pathToServerJar;
    private String pathToServerFolder;
    private Run run;

    public Runner(String pathToServerJar, Run run) {
        this.pathToServerJar = pathToServerJar;
        this.run = run;
    }

    public Runner(Run run) {
        this.run = run;
    }

    public Runner(Run run, String serverPath) {
        this.run = run;
        pathToServerFolder = serverPath;
    }

    private final SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss:SSS");

    private void executeCommand(String command) {
        try {
            System.out.println(format.format(new Date()) + ": " + command + "\n");
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            Frame.alert(AlertType.ERROR, e.getMessage());
        }
    }

    @Override
    public void run() {
        switch (run) {
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
                    System.out.println("File is a directory: " + file.getAbsolutePath());
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
                    e.printStackTrace();
                }
            }
            case SERVER_FOLDER -> {
                File directory = new File(pathToServerFolder);

                // check if the directory exists
                if (!directory.exists()) {
                    alert(AlertType.ERROR, "Directory not found: " + directory.getAbsolutePath());
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
                    desktop.open(directory);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            case SERVER_JAR -> {
                String jarFilePath = "H:\\Minecraft_Serwery\\1.16.5podSyna\\paper-1.16.5-705.jar";
                File jarFile = new File(jarFilePath);
                String jarDirectoryPath = jarFile.getParent();
                File jarDirectory = new File(jarDirectoryPath);

                ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "java", "-jar", "H:\\Minecraft_Serwery\\1.16.5podSyna\\paper-1.16.5-705.jar");
                pb.directory(jarDirectory);

                try {
                    Process process = pb.start();
                    int exitCode = process.waitFor();
                    System.out.println("JAR file exited with code " + exitCode);
                } catch (IOException | InterruptedException e) {
                    alert(AlertType.ERROR, e.getMessage());
                } //TODO: this doesn't work
            }
        }

    }
}
