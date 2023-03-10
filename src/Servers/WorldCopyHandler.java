package Servers;

import Gui.AddWorldsPanel;
import Gui.AlertType;
import Gui.ConfigStuffPanel;
import Gui.Frame;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Objects;
import java.util.zip.*;

public class WorldCopyHandler extends Thread {

    private final String serverWorldName;
    private final File originalDir;
    private final File serverWorldDir;
    private final JProgressBar progressBar;
    public WorldCopyHandler(JProgressBar progressBar) throws IOException {
        ServerProperties serverProperties = new ServerProperties();
        this.serverWorldName = serverProperties.getWorldName();
        this.serverWorldDir = new File(ConfigStuffPanel.getServPath() + "\\" + serverWorldName);
        this.originalDir = AddWorldsPanel.getworldToAdd();
        this.progressBar = progressBar;

        System.out.println("Original dir: " + originalDir + "\nServer world dir: " + serverWorldDir);
    }


    private void copyDirectory(File sourceDir, File destDir) throws IOException {
        long totalBytes = FileUtils.sizeOfDirectory(sourceDir);
        long copiedBytes = 0;

        // Create the destination directory if it doesn't exist
        if (!destDir.exists()) {
            destDir.mkdir();
        }

        // Iterate through all files and directories in the source directory
        for (File sourceFile : Objects.requireNonNull(sourceDir.listFiles())) {
            File destFile = new File(destDir, sourceFile.getName());

            if (sourceFile.isDirectory()) {
                // Recursively copy sub-directories
                copyDirectory(sourceFile, destFile);
            } else {
                // Copy files and update progress bar
                FileUtils.copyFile(sourceFile, destFile);
                copiedBytes += sourceFile.length();
                int progress = (int) Math.round((double) copiedBytes / totalBytes * 100);
                progressBar.setValue(progress);
            }
        }
    }

    public static boolean isArchive(File file) {
        String extension = getFileExtension(file);
        return extension.equals("zip") || extension.equals("jar") || extension.equals("tar") || extension.equals("tar.gz") || extension.equals("tgz");
    }

    private static String getFileExtension(File file) {
        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        } else {
            return fileName.substring(lastDotIndex + 1);
        }
    }

    @Override
    public void run() {
        super.run();
        if(originalDir.isDirectory()) {
            try {
                copyDirectory(originalDir, serverWorldDir);
            } catch (IOException e) {
                Frame.alert(AlertType.ERROR, e.getMessage());
            }
        } else if(isArchive(originalDir)) {
            System.out.println(isArchive(originalDir));
        }
    }
}
