package Server;

import Gui.WorldsTab;
import Enums.AlertType;
import Gui.DebugWindow;
import Gui.Frame;
import SelectedServer.NBTParser;
import SelectedServer.ServerPropertiesFile;
import SelectedServer.ServerDetails;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.zip.*;

import static Gui.Frame.alert;
import static Gui.Frame.getErrorDialogMessage;

public class WorldCopyHandler extends Thread {
    private static JProgressBar progressBar = null;
    private final WorldsTab worldsTab;
    private static JButton jButtonToDisable;

    private final String serverWorldName;
    private final File selectedWorld;
    private final File serverWorldDir;
    private final boolean copyFilesToServerDir;
    public static boolean isInRightClickMode = false;


    public WorldCopyHandler(WorldsTab worldsTab, JProgressBar progressBar,
                            File originalWorldDir, boolean copyFilesToServerDir, JButton jButtonToDisable) throws IOException {
        ServerPropertiesFile serverPropertiesFile = new ServerPropertiesFile();
        this.worldsTab = worldsTab;
        this.serverWorldName = serverPropertiesFile.getWorldName();
        this.serverWorldDir = new File(ServerDetails.serverPath + "\\" + serverWorldName);
        this.selectedWorld = originalWorldDir;
        WorldCopyHandler.progressBar = progressBar;
        this.copyFilesToServerDir = copyFilesToServerDir;
        WorldCopyHandler.jButtonToDisable = jButtonToDisable;
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


    public static String extractArchive(String archivePath, String destinationPath) throws IOException {
        jButtonToDisable.setEnabled(false);
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
                    alert(AlertType.ERROR, "Cannot create a directory.\nWorldCopyHandler.java extractingArchive()");
                    break;
                }
                if (extractedDirectory == null) {
                    extractedDirectory = newFile.getAbsolutePath();
                }
            } else {
                newFile.getParentFile().mkdirs();
                DebugWindow.debugVariables.put("newFile_extractArchive", newFile.toString());
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

        jButtonToDisable.setEnabled(true);
        return extractedDirectory;
    }

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


    @Override
    public void run() {
        DebugWindow.debugVariables.put("selected_world", selectedWorld.toString());
        if (selectedWorld.isDirectory() && !selectedWorld.toString().contains(ServerDetails.serverPath)) {
            if (!serverWorldDir.exists()) {
                if (!serverWorldDir.mkdirs())
                    alert(AlertType.ERROR, "Cannot create world directory \"" + serverWorldDir.getAbsolutePath() + "\".");
            }
            if (Objects.requireNonNull(serverWorldDir.list()).length > 0 && serverWorldDir.list() != null) { //world dir is not empty
                try {
                    FileUtils.deleteDirectory(serverWorldDir);
                } catch (IOException e) {
                    alert(AlertType.ERROR, "Cannot delete server world directory.\n" + getErrorDialogMessage(e));
                }
            }
            try {
                FileUtils.deleteDirectory(new File(serverWorldDir.getParent() + "\\" + serverWorldName + "_the_end"));
                FileUtils.deleteDirectory(new File(serverWorldDir.getParent() + "\\" + serverWorldName + "_nether"));
            } catch (IOException e) {
                alert(AlertType.ERROR, "Cannot delete nether and end directories.\n" + getErrorDialogMessage(e));
            }

            try {
                copyDirectory(selectedWorld, serverWorldDir);
            } catch (IOException e) {
                alert(AlertType.ERROR, "Cannot copy world dir to server world dir.\n" + getErrorDialogMessage(e));
            }
        } else if (isArchive(selectedWorld)) {
            if (!copyFilesToServerDir || isInRightClickMode) {
                String extractedDirTemp;
                try {
                    File dirToDelete = new File(".\\world_temp\\" + selectedWorld.getName());
                    if (dirToDelete.exists())  //issue #11, #12, #23 fixed by the laziest solution ever
                        FileUtils.deleteDirectory(dirToDelete);
                    String temp = extractArchive(selectedWorld.getAbsolutePath(), ".\\world_temp\\" + selectedWorld.getName());
                    if(temp != null)
                        extractedDirTemp = new File(temp).getParent(); //issue #34 fix by starting at correct directory
                    else {
                        alert(AlertType.WARNING, "File is probably not a minecraft world. Continue at your own risk."); //checking if a file is a minecraft world
                        throw new RuntimeException();
                    }
                    File predictedWorldDir = new File(findWorldDirectory(extractedDirTemp)); //future functionalities
                    WorldsTab.setExtractedWorldDir(extractedDirTemp);
                } catch (IOException e) {
                    alert(AlertType.ERROR, "Cannot extract file or obtain its directory.\n" + getErrorDialogMessage(e));
                    throw new RuntimeException(); //this line's stayin for some reason
                }
                worldsTab.setIcons();
            }
            if (copyFilesToServerDir) {
                jButtonToDisable.setEnabled(false); //issue #15 fix
                if (!serverWorldDir.exists()) {
                    if (!serverWorldDir.mkdirs())
                        alert(AlertType.ERROR, "Cannot create world directory \"" + serverWorldDir.getAbsolutePath() + "\".");
                }

                File dir = new File(WorldsTab.getExtractedWorldDir());
                if (Objects.requireNonNull(serverWorldDir.list()).length > 0 && serverWorldDir.list() != null) { //world dir is not empty
                    try {
                        FileUtils.deleteDirectory(serverWorldDir);
                    } catch (IOException e) {
                        alert(AlertType.ERROR, "Cannot delete server world directory.\n" + getErrorDialogMessage(e));
                    }
                }
                try {
                    FileUtils.deleteDirectory(new File(serverWorldDir.getParent() + "\\" + serverWorldName + "_the_end"));
                    FileUtils.deleteDirectory(new File(serverWorldDir.getParent() + "\\" + serverWorldName + "_nether"));
                } catch (IOException e) {
                    alert(AlertType.ERROR, "Cannot delete server's nether and end direcories.\n" + getErrorDialogMessage(e));
                }
                File predictedWorldDir = null;
                try {
                    predictedWorldDir = new File(findWorldDirectory(dir.getParent()));
                    copyDirectory(predictedWorldDir, serverWorldDir);
                } catch (IOException e) {
                    alert(AlertType.ERROR, "Cannot copy world dir to server world dir.\n" + getErrorDialogMessage(e));
                }

                String temp = ServerDetails.serverLevelDatFile;
                ServerDetails.serverLevelDatFile = predictedWorldDir.getAbsolutePath() + "\\level.dat"; //trick the NBTParser into using extracted world's level.dat
                try { //issue #71 fix
                    NBTParser nbtParser = new NBTParser();
                    nbtParser.start();
                    nbtParser.join();
                    ServerDetails.serverLevelName = nbtParser.getLevelName();
                } catch (Exception e) {
                    alert(AlertType.ERROR, Frame.getErrorDialogMessage(e));
                }
                ServerDetails.serverLevelDatFile = temp; //restore the original level.dat file location for safety
                DebugWindow.debugVariables.put("current_server_name", ServerDetails.serverName);
                DebugWindow.debugVariables.put("current_server_path", ServerDetails.serverPath);
                DebugWindow.debugVariables.put("current_server_id", String.valueOf(ServerDetails.serverId));
            }
        } else if (selectedWorld.toString().contains(ServerDetails.serverPath)) {
            Frame.alert(AlertType.ERROR, "Cannot copy files from server directory to the server.");
        }
        jButtonToDisable.setEnabled(true); //issue #15 fix
        worldsTab.setIcons();
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
}
