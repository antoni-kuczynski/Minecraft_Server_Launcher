package Servers;

import Gui.AlertType;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import static Gui.Frame.alert;
import static Gui.Frame.exStackTraceToString;

public class Runner extends Thread {
    private final RunMode runMode;
    private String pathToServerJar;
    private String pathToServerFolder;
    private String javaRuntimePath;
    private ArrayList<String> arguments;

    public Runner(String pathToServerJar, RunMode runMode, String javaRuntimePath, String launchArgs) {
        this.pathToServerJar = pathToServerJar;
        this.javaRuntimePath = javaRuntimePath;
        this.runMode = runMode;
        arguments = Arrays.stream(launchArgs.split(" ")).collect(Collectors.toCollection(ArrayList::new));
    }

    public Runner(RunMode runMode) {
        this.runMode = runMode;
    }

    public Runner(RunMode runMode, String serverPath) {
        this.runMode = runMode;
        pathToServerFolder = serverPath;
    }

    private void launchServer(String serverPath, String javaPath) throws IOException {
        ArrayList<String> command = new ArrayList<>();
        command.add("cmd");
        command.add("/c");
        command.add("start");
        command.add("cmd.exe");
        command.add("@cmd");
        command.add("/c");
        command.add("\"" + javaPath + "\"");
        command.addAll(arguments);
        command.add("-jar");
        command.add(serverPath);
        command.add("nogui");

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(new File(serverPath).getParentFile());
        pb.redirectErrorStream(true);

        pb.start();
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
                    alert(AlertType.ERROR, "Cannot open \"servers.json\" file.\n" + exStackTraceToString(e.getStackTrace()));
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
                    alert(AlertType.ERROR, "Cannot open server's directory.\n" + exStackTraceToString(e.getStackTrace()));
                }
            }
            case SERVER_JAR -> {
                try {
                    launchServer(pathToServerJar, javaRuntimePath);
                } catch (IOException e) {
                    alert(AlertType.ERROR, "Cannot start new Process pb. Cannot launch server.\n" + exStackTraceToString(e.getStackTrace()));
                }
            }
        }
    }
}
