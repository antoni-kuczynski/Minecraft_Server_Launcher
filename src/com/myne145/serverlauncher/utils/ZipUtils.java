package com.myne145.serverlauncher.utils;

import com.myne145.serverlauncher.gui.window.Window;
import com.myne145.serverlauncher.gui.tabs.worldsmanager.WorldsManagerTab;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.myne145.serverlauncher.gui.window.Window.alert;

public class ZipUtils {
    private static final Taskbar taskbar = Taskbar.getTaskbar();
    private static long getTotalSize(String archivePath) throws IOException {
        long totalSize = 0;
        ZipInputStream zis = new ZipInputStream(new FileInputStream(archivePath));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            if (!zipEntry.isDirectory()) {
                totalSize += zipEntry.getSize();
            }
            zipEntry = zis.getNextEntry();
        }
        zis.close();
        return totalSize;
    }

    public static String extractArchive(String archivePath, String destinationPath, WorldsManagerTab worldsManagerTab) throws IOException {
        worldsManagerTab.getStartCopying().setEnabled(false);
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(archivePath));
        ZipEntry zipEntry = zis.getNextEntry();
        taskbar.setWindowProgressState(Window.getWindow(), Taskbar.State.NORMAL);


        String extractedDirectory = null;
        long totalSize = getTotalSize(archivePath);
        long extractedSize = 0;

        while (zipEntry != null) {
            File newFile = new File(destinationPath, zipEntry.getName());
            if (extractedDirectory == null) {
                extractedDirectory = newFile.getAbsolutePath();
            }
            if (zipEntry.isDirectory()) {
                if(!newFile.mkdirs()) {
                    alert(AlertType.ERROR, "Cannot create a directory.\nWorldCopyHandler.java extractingArchive()");
                    break;
                }
            } else {
                newFile.getParentFile().mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                    extractedSize += len;
                    worldsManagerTab.getProgressBar().setValue((int) (extractedSize * 100 / totalSize)); // Set progress bar value based on the extracted size
                    taskbar.setWindowProgressValue( Window.getWindow(), (int) (extractedSize * 100 / totalSize)); //same for taskbar
                }
                fos.close();
            }
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();

        worldsManagerTab.getStartCopying().setEnabled(true);
        taskbar.setWindowProgressState(Window.getWindow(), Taskbar.State.OFF);
        return extractedDirectory;
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

    public static boolean isArchive(File file) {
        String extension = getFileExtension(file);
        return extension.equals("zip") || extension.equals("jar") || extension.equals("tar") || extension.equals("tar.gz") || extension.equals("tgz");
    }

}
