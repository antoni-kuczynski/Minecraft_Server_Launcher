package Servers;

import Gui.AlertType;
import Gui.Frame;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static Gui.Frame.alert;

public class Runner extends Thread {
    private String pathToServerJar;
    private String pathToServerFolder;
    private String javaRuntimePath;
    private Run run;

    public Runner(String pathToServerJar, Run run, String javaRuntimePath) {
        this.pathToServerJar = pathToServerJar;
        this.javaRuntimePath = javaRuntimePath;
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

    private void launchServer(String serverPath, ArrayList<String> arguments, String javaPath) throws IOException {
        ArrayList<String> command = new ArrayList<String>();
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

        Process process = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
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
                try {
                    System.out.println(javaRuntimePath);
                    launchServer(pathToServerJar, new ArrayList<>(), javaRuntimePath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }
}
