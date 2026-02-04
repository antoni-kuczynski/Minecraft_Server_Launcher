package com.myne145.serverlauncher.utils;

import com.myne145.serverlauncher.gui.window.Window;
import com.myne145.serverlauncher.gui.tabs.worldsmanager.WorldsManagerTab;
import org.apache.commons.io.FilenameUtils;

import java.awt.*;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import static com.myne145.serverlauncher.gui.window.Window.showErrorMessage;

public class ZipUtils {
    private static final Taskbar taskbar = Window.getTaskbar();
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
        if(Taskbar.isTaskbarSupported())
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
                    showErrorMessage("Cannot extract the archive.", new ZipException());
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
                    if(Taskbar.isTaskbarSupported())
                        taskbar.setWindowProgressValue( Window.getWindow(), (int) (extractedSize * 100 / totalSize)); //same for taskbar
                }
                fos.close();
            }
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();

        worldsManagerTab.getStartCopying().setEnabled(true);
        if(Taskbar.isTaskbarSupported())
            taskbar.setWindowProgressState(Window.getWindow(), Taskbar.State.OFF);
        return extractedDirectory;
    }


    public static boolean isArchive(File file) {
        if(file.isDirectory())
            return false;
        String extension = FilenameUtils.getExtension(file.getName());
        return extension.equals("zip") || extension.equals("rar");
    }

}
