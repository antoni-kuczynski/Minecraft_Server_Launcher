package Servers;

import Gui.AlertType;
import Gui.ConfigStuffPanel;
import Gui.Frame;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.zip.*;

public class WorldCopyHandler extends Thread {

    private final String serverWorldName;
    private File originalDir = null;
    private final File serverWorldDir;
    private JProgressBar progressBar = null;


    public WorldCopyHandler(JProgressBar progressBar, File originalWorldDir) throws IOException {
        ServerProperties serverProperties = new ServerProperties();
        this.serverWorldName = serverProperties.getWorldName();
        this.serverWorldDir = new File(ConfigStuffPanel.getServPath() + "\\" + serverWorldName);
        this.originalDir = originalWorldDir;
        this.progressBar = progressBar;
    }

    public WorldCopyHandler() throws IOException {
        this.serverWorldName = new ServerProperties().getWorldName();
        this.serverWorldDir = new File(ConfigStuffPanel.getServPath() + "\\" + serverWorldName);
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
                // Recursively copy subdirectories
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


    private String extractArchive(String archivePath, String destinationPath) throws IOException {
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(archivePath));
        ZipEntry zipEntry = zis.getNextEntry();

        String extractedDirectory = null;
        long totalSize = getTotalSize(archivePath);
        long extractedSize = 0;

        while (zipEntry != null) {
            File newFile = new File(destinationPath, zipEntry.getName());
            if (zipEntry.isDirectory()) {
                newFile.mkdirs();
                if (extractedDirectory == null) {
                    extractedDirectory = newFile.getAbsolutePath();
                }
            } else {
                newFile.getParentFile().mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                    extractedSize += len;
                    progressBar.setValue((int) (extractedSize * 100 / totalSize)); // Set progress bar value based on the extracted size
                }
                fos.close();
            }
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();

        return extractedDirectory;
    }

    private long getTotalSize(String archivePath) throws IOException {
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




    @Override
    public void run() {
        super.run();
        if (originalDir.isDirectory()) {
            try {
                copyDirectory(originalDir, serverWorldDir);
            } catch (IOException e) {
                Frame.alert(AlertType.ERROR, e.getMessage());
            }
        } else if (isArchive(originalDir)) {
            String extractedDirectory;
            try {
                extractedDirectory = extractArchive(originalDir.getAbsolutePath(), ".\\world_temp\\" + originalDir.getName());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            File dir = new File(extractedDirectory);
//            if(dir.getParent())
//            System.out.println("Dir: " + dir);
//            System.out.println("Parent: " + dir.getParent());
//            System.out.println(findWorldDirectory(dir.getParent()));
            System.out.println(serverWorldDir);
            System.out.println(Arrays.stream(serverWorldDir.list()).toList());
            if(Objects.requireNonNull(serverWorldDir.list()).length > 0 && serverWorldDir.list() != null) { //world dir is not empty
                try {
                    FileUtils.deleteDirectory(serverWorldDir);
                } catch (IOException e) {
                    Frame.alert(AlertType.ERROR, e.getMessage());
                }
            }

            try {
                copyDirectory(new File(Objects.requireNonNull(findWorldDirectory(dir.getParent()))), serverWorldDir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String findWorldDirectory(String dir) { //TODO: fixme
        ArrayList<File> arr = new ArrayList<>(Arrays.asList(Objects.requireNonNull(new File(dir).listFiles())));
        ArrayList<String> filenames = new ArrayList<>();
        for (File f : arr)
            filenames.add(f.getName());
        if (filenames.contains("level.dat")) {
            return dir;
        } else {
            String nextDir = null;
            for(File f : arr) {
                if(f.isDirectory()) {
                    nextDir = f.getAbsolutePath();
                    break;
                }
            }
            findWorldDirectory(nextDir);
        }
        return null;
    }
    public String getServerWorldName () {
        if(serverWorldName == null)
            return "world_name_not_found";
        return serverWorldName;
    }
}
