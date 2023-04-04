package Servers;

import Gui.AddWorldsPanel;
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

import static Gui.Frame.alert;
import static Gui.Frame.exStackTraceToString;

public class WorldCopyHandler extends Thread {

    private final String serverWorldName;
    private File originalDir = null;
    private final File serverWorldDir;
    private JProgressBar progressBar = null;
    private boolean copyFilesToServerDir;
    private JPanel panel;
    private JButton button;
    private final Config config = new Config();
    private int configIndex;
    public static boolean isInRightClickMode = false;

    public WorldCopyHandler(JPanel panel, JProgressBar progressBar, File originalWorldDir, boolean copyFilesToServerDir, JButton button, int configIndex) throws IOException {
        this.panel = panel;
        ServerProperties serverProperties = new ServerProperties(configIndex);
        this.serverWorldName = serverProperties.getWorldName();
        this.serverWorldDir = new File(config.getData().get(configIndex).getPathToServerFolder() + "\\" + serverWorldName);
        this.originalDir = originalWorldDir;
        this.progressBar = progressBar;
        this.copyFilesToServerDir = copyFilesToServerDir;
        this.button = button;
        this.configIndex = configIndex;
    }

    public WorldCopyHandler(int configIndex) throws IOException {
        this.serverWorldName = new ServerProperties(configIndex).getWorldName();
        this.serverWorldDir = new File(config.getData().get(configIndex).getPathToServerFolder() + "\\" + serverWorldName);
        this.configIndex = configIndex;
    }


    private void copyDirectory(File sourceDir, File destDir) throws IOException {
        long totalBytes = FileUtils.sizeOfDirectory(sourceDir);
        long copiedBytes = 0;

        // Create the destination directory if it doesn't exist
        if (!destDir.exists()) {
            if(!destDir.mkdir())
                alert(AlertType.ERROR, "Cannot create destination directory.\nAt line " + getStackTrace()[1].getLineNumber());
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
        button.setEnabled(false);
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(archivePath));
        ZipEntry zipEntry = zis.getNextEntry();

        String extractedDirectory = null;
        long totalSize = getTotalSize(archivePath);
        long extractedSize = 0;

        while (zipEntry != null) {
            File newFile = new File(destinationPath, zipEntry.getName());
            if (zipEntry.isDirectory()) {
                if(!newFile.mkdirs()) {
                    alert(AlertType.ERROR, "Cannot create some directory.\nAt line " + getStackTrace()[1].getLineNumber());
                    break;
                }
                if (extractedDirectory == null) {
                    extractedDirectory = newFile.getAbsolutePath();
                }
            } else {
                newFile.getParentFile().mkdirs();
//                    alert(AlertType.ERROR, "Cannot create directory.\nAt line " + getStackTrace()[1].getLineNumber() + " Class WorldCopyHandler.java");
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

        button.setEnabled(true);
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

    public boolean isFolderInFolder(File folder, File parentFolder) {

        // If the folder is null or not a directory, return false
        if (folder == null || !folder.isDirectory()) {
            return false;
        }

        // If the parent folder is null or not a directory, return false
        if (parentFolder == null || !parentFolder.isDirectory()) {
            return false;
        }

        // Check if the folder is directly contained within the parent folder
        if (folder.getParentFile().equals(parentFolder)) {
            return true;
        }

        // Recursively check if the parent folder is contained within another folder
        return isFolderInFolder(parentFolder.getParentFile(), folder);
    }


    @Override
    public void run() {
        if (originalDir.isDirectory() && !originalDir.toString().contains(config.getData().get(configIndex).getPathToServerFolder())) {
            if (!serverWorldDir.exists()) {
                if (!serverWorldDir.mkdirs())
                    alert(AlertType.ERROR, "Cannot create world directory \"" + serverWorldDir.getAbsolutePath() + "\".");
            }
            if (Objects.requireNonNull(serverWorldDir.list()).length > 0 && serverWorldDir.list() != null) { //world dir is not empty
                try {
                    FileUtils.deleteDirectory(serverWorldDir);
                } catch (IOException e) {
                    alert(AlertType.ERROR, "Cannot delete server world directory.\n" + exStackTraceToString(e.getStackTrace()));
                }
            }
            try {
                FileUtils.deleteDirectory(new File(serverWorldDir.getParent() + "\\" + serverWorldName + "_the_end"));
                FileUtils.deleteDirectory(new File(serverWorldDir.getParent() + "\\" + serverWorldName + "_nether"));
            } catch (IOException e) {
                alert(AlertType.ERROR, "Cannot delete nether and end directories.\n" + exStackTraceToString(e.getStackTrace()));
            }

            try {
                copyDirectory(originalDir, serverWorldDir);
            } catch (IOException e) {
                alert(AlertType.ERROR, "Cannot copy world dir to server world dir.\n" + exStackTraceToString(e.getStackTrace()));
            }
        } else if (isArchive(originalDir)) {
            if (!copyFilesToServerDir || isInRightClickMode) {
                String extractedDirTemp;
                try {
                    File dirToDelete = new File(".\\world_temp\\" + originalDir.getName());
                    if (dirToDelete.exists())  //issue #11, #12, #23 fixed by the laziest solution ever
                        FileUtils.deleteDirectory(dirToDelete);
                    String temp = extractArchive(originalDir.getAbsolutePath(), ".\\world_temp\\" + originalDir.getName());
                    if(temp != null)
                        extractedDirTemp = new File(temp).getParent(); //issue #34 fix by starting at correct directory
                    else {
                        alert(AlertType.WARNING, "File is probably not a minecraft world. Continue at your own risk."); //checking if a file is a minecraft world
                        throw new RuntimeException();
                    }
                    File predictedWorldDir = new File(findWorldDirectory(extractedDirTemp)); //future functionalities
                    AddWorldsPanel.setExtractedWorldDir(extractedDirTemp);
                } catch (IOException e) {
                    alert(AlertType.ERROR, "Cannot extract file or obtain its directory.\n" + exStackTraceToString(e.getStackTrace()));
                    throw new RuntimeException(); //this line's stayin for some reason
                }
                panel.repaint();
            }
            if (copyFilesToServerDir) {
                button.setEnabled(false); //issue #15 fix
                if (!serverWorldDir.exists()) {
                    if (!serverWorldDir.mkdirs())
                        alert(AlertType.ERROR, "Cannot create world directory \"" + serverWorldDir.getAbsolutePath() + "\".");
                }

                File dir = new File(AddWorldsPanel.getExtractedWorldDir());
                if (Objects.requireNonNull(serverWorldDir.list()).length > 0 && serverWorldDir.list() != null) { //world dir is not empty
                    try {
                        FileUtils.deleteDirectory(serverWorldDir);
                    } catch (IOException e) {
                        alert(AlertType.ERROR, "Cannot delete server world directory.\n" + exStackTraceToString(e.getStackTrace()));
                    }
                }
                try {
                    FileUtils.deleteDirectory(new File(serverWorldDir.getParent() + "\\" + serverWorldName + "_the_end"));
                    FileUtils.deleteDirectory(new File(serverWorldDir.getParent() + "\\" + serverWorldName + "_nether"));
                } catch (IOException e) {
                    alert(AlertType.ERROR, "Cannot delete server's nether and end direcories.\n" + exStackTraceToString(e.getStackTrace()));
                }

                try {
                    File predictedWorldDir;
                    predictedWorldDir = new File(findWorldDirectory(dir.getParent()));
                    copyDirectory(predictedWorldDir, serverWorldDir);
                } catch (IOException e) {
                    alert(AlertType.ERROR, "Cannot copy world dir to server world dir.\n" + exStackTraceToString(e.getStackTrace()));
                }
                System.out.println("original dir: " + originalDir.toString());
                System.out.println("checking dir: " + ConfigStuffPanel.getServPath());
            }
        } else if (originalDir.toString().contains(ConfigStuffPanel.getServPath())) {
            Frame.alert(AlertType.ERROR, "Cannot copy files from server directory to the server.");
        }
        button.setEnabled(true); //issue #15 fix
        panel.repaint();
    }

    private String findWorldDirectory(String dir) { //function should now work most of the times
        ArrayList<File> arr = new ArrayList<>(Arrays.asList(Objects.requireNonNull(new File(dir).listFiles())));
        ArrayList<String> filenames = new ArrayList<>();
        for (File f : arr) {
            filenames.add(f.getName());
        }
        File foundLevelDat = new File(dir);
        boolean containsLevelDat = false;
        boolean hasDirectory = false;
        for(File f : arr)
            if(f.isDirectory())
                hasDirectory = true;
        for (File f : arr) {
            if (f.getName().equals("level.dat") || !hasDirectory) {
                containsLevelDat = true;
                foundLevelDat = new File(f.getAbsolutePath());
            }

        }
        if(!hasDirectory && !copyFilesToServerDir) {
            alert(AlertType.WARNING, "Specified file may not be a minecraft world. Remember to take a backup, continue at your own risk!");
        }
        if (containsLevelDat) {
            return foundLevelDat.getParent();
        } else {
            String nextDir = null;
            for (File f : arr) {
                if (f.isDirectory()) {
                    nextDir = f.getAbsolutePath();
                    break;
                } else {
                    nextDir = new File(f.getParent()).getParent(); //for the name of fuck, i dont understand how does that work
                }
            }
            if(!nextDir.equals(dir))
                return findWorldDirectory(nextDir); //fixed the function
            else
                return dir;
        }
    }

    public String getServerWorldName () {
        if(serverWorldName == null)
            return "world_name_not_found";
        return serverWorldName;
    }
}
