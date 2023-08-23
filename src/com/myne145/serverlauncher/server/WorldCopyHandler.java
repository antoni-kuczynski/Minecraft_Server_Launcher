package com.myne145.serverlauncher.server;

import com.myne145.serverlauncher.gui.tabs.WorldsTab;
import com.myne145.serverlauncher.gui.AlertType;
import com.myne145.serverlauncher.gui.Window;
import com.myne145.serverlauncher.server.current.NBTParser;
import com.myne145.serverlauncher.server.current.ServerPropertiesFile;
import com.myne145.serverlauncher.server.current.CurrentServerInfo;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.zip.*;

import static com.myne145.serverlauncher.gui.Window.alert;
import static com.myne145.serverlauncher.gui.Window.getErrorDialogMessage;

public class WorldCopyHandler extends Thread {
    private static JProgressBar progressBar = null;
    private final WorldsTab worldsTab;
    private static JButton jButtonToDisable;

    private final String serverWorldName;
    private final File selectedWorld;
    private final File serverWorldDir;
    private boolean copyFilesToServerDir;
    private static final Taskbar taskbar = Taskbar.getTaskbar();


    private WorldCopyHandler(WorldsTab worldsTab, boolean copyFilesToServerDir) throws IOException {
        ServerPropertiesFile serverPropertiesFile = new ServerPropertiesFile();
        this.worldsTab = worldsTab;

        this.serverWorldName = serverPropertiesFile.getWorldName();
        this.serverWorldDir = new File(CurrentServerInfo.serverPath.getAbsolutePath() + "\\" + serverWorldName);

        this.selectedWorld = worldsTab.getUserAddedWorld();
        WorldCopyHandler.progressBar = worldsTab.getProgressBar();
        this.copyFilesToServerDir = copyFilesToServerDir;
        WorldCopyHandler.jButtonToDisable = worldsTab.getStartCopyingButton();
    }

    /**
     * Creates a new instance of {@link com.myne145.serverlauncher.server.WorldCopyHandler} and sets the copy mode to false.
     * @param worldsTab world manager tab
     * @return {@link com.myne145.serverlauncher.server.WorldCopyHandler} instance
     */
    public static WorldCopyHandler createWorldCopyHandler(WorldsTab worldsTab) {
        try {
            WorldCopyHandler worldCopyHandler = new WorldCopyHandler(worldsTab, false);
            return worldCopyHandler;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    /**
     * Sets the copy mode of a {@link com.myne145.serverlauncher.server.WorldCopyHandler} instance
     * @param mode copy mode (true - copy files to the server, false - just unzip and set icon an title of the world. For archives only.
     * @return {@link com.myne145.serverlauncher.server.WorldCopyHandler} instance with the desired copy mode
     */
    public WorldCopyHandler setCopyMode(boolean mode) {
        this.copyFilesToServerDir = mode;
        return this;
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

            BasicFileAttributes basicFileAttributes = Files.readAttributes(sourceFile.toPath(), BasicFileAttributes.class);
            if (basicFileAttributes.isDirectory()) {
                // Recursively copy subdirectories
                copyDirectory(sourceFile, destFile);
            } else {
                // Copy files and update progress bar
                FileUtils.copyFile(sourceFile, destFile);
                copiedBytes += basicFileAttributes.size();
                int progress = (int) Math.round((double) copiedBytes / totalBytes * 100);
                progressBar.setValue(progress);
                taskbar.setWindowProgressState(Window.getWindow(), Taskbar.State.INDETERMINATE);
                taskbar.setWindowProgressValue(Window.getWindow(), 100);
            }
        }
        taskbar.setWindowProgressState(Window.getWindow(), Taskbar.State.OFF);
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
                    progressBar.setValue((int) (extractedSize * 100 / totalSize)); // Set progress bar value based on the extracted size
                    taskbar.setWindowProgressValue( Window.getWindow(), (int) (extractedSize * 100 / totalSize)); //same for taskbar
                }
                fos.close();
            }
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();

        jButtonToDisable.setEnabled(true);
        taskbar.setWindowProgressState(Window.getWindow(), Taskbar.State.OFF);
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
            worldsTab.setImportButtonWarning("Not a Minecraft world!");
//            alert(AlertType.WARNING, "Specified file may not be a minecraft world. Remember to take a backup, continue at your own risk!");
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
                return findWorldDirectory(nextDir);
            else
                return dir;
        }
    }

    @Override
    public void run() {
//        worldsTab.removeImportButtonWarning();
        if (selectedWorld.isDirectory() && !selectedWorld.toString().contains(CurrentServerInfo.serverPath.getAbsolutePath())) {
            System.out.println(selectedWorld.getAbsolutePath());
            if (!serverWorldDir.exists() && !serverWorldDir.mkdirs()) {
                    alert(AlertType.ERROR, "Cannot create world directory \"" + serverWorldDir.getAbsolutePath() + "\".");
            }

            ArrayList<String> serverWorldFilenamesList = new ArrayList<>(Arrays.asList(selectedWorld.list()));
            boolean containsWorldFiles =
                    serverWorldFilenamesList.contains("level.dat") ||
                    serverWorldFilenamesList.contains("data") ||
                    serverWorldFilenamesList.contains("region");

            if (serverWorldDir.list() != null && serverWorldFilenamesList.size() > 0 && !containsWorldFiles) {
                worldsTab.setImportButtonWarning("Not a Minecraft world!");
            }

            System.out.println(new File(selectedWorld.getAbsolutePath() + "level.dat") + "asdasdasd");
            if(new File(selectedWorld.getAbsolutePath() + "/level.dat").exists()) {
                try { //copying world's level.dat file analogically like server ones
                    FileUtils.copyFile(new File(selectedWorld.getAbsolutePath() + "/level.dat"), new File("world_temp/worlds_level_dat/level_" + selectedWorld.getName() + ".dat"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if(copyFilesToServerDir) {
                if (serverWorldDir.list() != null && serverWorldFilenamesList.size() > 0 && !containsWorldFiles) {
                    worldsTab.setImportButtonWarning("Not a Minecraft world!");
                } else if (Objects.requireNonNull(serverWorldDir.list()).length > 0 && serverWorldDir.list() != null) { //world dir is not empty
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
            }
        } else if (isArchive(selectedWorld)) {
            if (!copyFilesToServerDir) {
                String extractedDirTemp;
                File predictedWorldDir;
                try {
                    File dirToDelete = new File(".\\world_temp\\" + selectedWorld.getName());
                    if (dirToDelete.exists())  //issue #11, #12, #23 fixed by the laziest solution ever
                        FileUtils.deleteDirectory(dirToDelete);
                    String extractedDirectory = extractArchive(selectedWorld.getAbsolutePath(), ".\\world_temp\\" + selectedWorld.getName());
                    if(extractedDirectory != null) {
                        extractedDirTemp = new File(extractedDirectory).getParent();
                    } else {
                        alert(AlertType.WARNING, "File is probably not a minecraft world. Continue at your own risk."); //checking if a file is a minecraft world
                        throw new RuntimeException();
                    }
                    predictedWorldDir = new File(findWorldDirectory(extractedDirTemp));
                    worldsTab.setExtractedWorldDir(predictedWorldDir.getAbsolutePath());
                } catch (IOException e) {
                    alert(AlertType.ERROR, "This kind of world archive is currently not supported.\nTry extracting the archive and copying the world as a folder.\nDetailed error:\n" + getErrorDialogMessage(e));
                    throw new RuntimeException();
                }

                File extractedWorldsLevelDat = new File(predictedWorldDir.getAbsolutePath() + "/level.dat");
                if(extractedWorldsLevelDat.exists()) {
                    try { //copying world's level.dat file analogically like server ones
                        FileUtils.copyFile(extractedWorldsLevelDat, new File("world_temp/worlds_level_dat/level_" + predictedWorldDir.getName() + ".dat"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }


                worldsTab.extractedWorldSize = FileSize.directorySizeWithConversion(new File(extractedDirTemp));
                worldsTab.setIcons();
            }
            if (copyFilesToServerDir) {
                jButtonToDisable.setEnabled(false); //issue #15 fix
                if (!serverWorldDir.exists()) {
                    if (!serverWorldDir.mkdirs())
                        alert(AlertType.ERROR, "Cannot create world directory \"" + serverWorldDir.getAbsolutePath() + "\".");
                }

                File dir = new File(worldsTab.getExtractedWorldDir());
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

                File temp = CurrentServerInfo.world.getLevelDat();
                CurrentServerInfo.world.levelDat = new File(predictedWorldDir.getAbsolutePath() + "\\level.dat"); //trick the NBTParser into using extracted world's level.dat
                try { //issue #71 fix
                    NBTParser nbtParser = NBTParser.createServerNBTParser();
                    nbtParser.start();
                    nbtParser.join();
                    CurrentServerInfo.world.levelName = nbtParser.getLevelName();
                } catch (Exception e) {
                    alert(AlertType.ERROR, Window.getErrorDialogMessage(e));
                }
                CurrentServerInfo.world.levelDat = temp; //restore the original level.dat file location for safety
            }
        } else if (selectedWorld.toString().contains(CurrentServerInfo.serverPath.getAbsolutePath())) {
            Window.alert(AlertType.ERROR, "Cannot copy files from server directory to the server.");
        }
        jButtonToDisable.setEnabled(true); //issue #15 fix
        worldsTab.setIcons();
    }
}
