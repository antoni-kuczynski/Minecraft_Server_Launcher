package Servers;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Runner extends Thread {
    private final String pathToServerJar;

    public Runner(String pathToServerJar) {
        this.pathToServerJar = pathToServerJar;
    }

    private final SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss:SSS");

    private void executeCommand(String command) {
        try {
            System.out.println(format.format(new Date()) + ": " + command + "\n");
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
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
            e.printStackTrace();
        }
    }
}
