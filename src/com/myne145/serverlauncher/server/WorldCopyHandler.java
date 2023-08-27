package com.myne145.serverlauncher.server;

import com.myne145.serverlauncher.gui.tabs.WorldsManagerTab;
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

import static com.myne145.serverlauncher.gui.Window.alert;
import static com.myne145.serverlauncher.gui.Window.getErrorDialogMessage;
import static com.myne145.serverlauncher.server.ZipUtils.extractArchive;
import static com.myne145.serverlauncher.server.ZipUtils.isArchive;

public class WorldCopyHandler extends Thread {
    private static JProgressBar progressBar = null;
    private final WorldsManagerTab worldsManagerTab;
    private static JButton startImportingButtonFromWorldManagerTab;
    private final String serverWorldName;
    private final File selectedWorld;
    private final File serverWorldDir;
    private boolean copyFilesToServerDir;
    private static final Taskbar taskbar = Taskbar.getTaskbar();

    private WorldCopyHandler(WorldsManagerTab worldsManagerTab, boolean copyFilesToServerDir) throws IOException {
        ServerPropertiesFile serverPropertiesFile = new ServerPropertiesFile();
        this.worldsManagerTab = worldsManagerTab;

        this.serverWorldName = serverPropertiesFile.getWorldName();
        this.serverWorldDir = new File(CurrentServerInfo.serverPath.getAbsolutePath() + "\\" + serverWorldName);

        this.selectedWorld = worldsManagerTab.getUserAddedWorld();
        WorldCopyHandler.progressBar = worldsManagerTab.getProgressBar();
        this.copyFilesToServerDir = copyFilesToServerDir;
        WorldCopyHandler.startImportingButtonFromWorldManagerTab = worldsManagerTab.getStartCopyingButton();
    }

    /**
     * Creates a new instance of {@link com.myne145.serverlauncher.server.WorldCopyHandler} and sets the copy mode to false.
     * @param worldsManagerTab world manager tab
     * @return {@link com.myne145.serverlauncher.server.WorldCopyHandler} instance
     */
    public static WorldCopyHandler createWorldCopyHandler(WorldsManagerTab worldsManagerTab) {
        try {
            return new WorldCopyHandler(worldsManagerTab, false);
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

    private void copyDirectoryWithProgressBar(File sourceDir, File destDir) throws IOException {
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
                copyDirectoryWithProgressBar(sourceFile, destFile);
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


    private String findWorldDirectory(String dir) {
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
            worldsManagerTab.setImportButtonWarning("Not a Minecraft world!");
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
                    nextDir = new File(f.getParent()).getParent();
                }
            }
            if(!nextDir.equals(dir))
                return findWorldDirectory(nextDir);
            else
                return dir;
        }
    }

    private void deleteEndAndNetherDirs() throws IOException {
        FileUtils.deleteDirectory(new File(serverWorldDir.getParent() + "\\" + serverWorldName + "_the_end"));
        FileUtils.deleteDirectory(new File(serverWorldDir.getParent() + "\\" + serverWorldName + "_nether"));
    }

    private void handler() throws IOException, InterruptedException {
        if (selectedWorld.isDirectory() && !selectedWorld.toString().contains(CurrentServerInfo.serverPath.getAbsolutePath())) {
            ArrayList<String> serverWorldFilenamesList = new ArrayList<>(Arrays.asList(selectedWorld.list()));
            boolean isServerWorldDirEmpty = serverWorldDir.list() == null || selectedWorld.list().length <= 0;
            boolean containsWorldFiles =
                    serverWorldFilenamesList.contains("level.dat") ||
                            serverWorldFilenamesList.contains("data") ||
                            serverWorldFilenamesList.contains("region");

            if (!serverWorldDir.exists() && !serverWorldDir.mkdirs()) {
                alert(AlertType.ERROR, "Cannot create world directory \"" + serverWorldDir.getAbsolutePath() + "\".");
            }
            if (!isServerWorldDirEmpty && !containsWorldFiles) {
                worldsManagerTab.setImportButtonWarning("Not a Minecraft world!");
            }

            if(new File(selectedWorld.getAbsolutePath() + "/level.dat").exists()) {
                 //copying world's level.dat file analogically like server ones
                FileUtils.copyFile(new File(selectedWorld.getAbsolutePath() + "/level.dat"), new File("world_temp/worlds_level_dat/level_" + selectedWorld.getName() + ".dat"));
            }

            if(copyFilesToServerDir) {
                if (!isServerWorldDirEmpty && !containsWorldFiles) {
                    worldsManagerTab.setImportButtonWarning("Not a Minecraft world!");
                } else if (serverWorldDir.list() != null && serverWorldDir.list().length > 0) { //world dir is not empty
                    FileUtils.deleteDirectory(serverWorldDir);
                }

                deleteEndAndNetherDirs();
                copyDirectoryWithProgressBar(selectedWorld, serverWorldDir);
            }
        } else if (isArchive(selectedWorld)) {
            if (!copyFilesToServerDir) {
                File worldExtractDirectory = new File(".\\world_temp\\" + selectedWorld.getName());
                if (worldExtractDirectory.exists())
                    FileUtils.deleteDirectory(worldExtractDirectory);
                
                String extractedDirectory = extractArchive(selectedWorld.getAbsolutePath(), worldExtractDirectory.getAbsolutePath(), worldsManagerTab);
                if(!worldExtractDirectory.exists()) {
                    worldsManagerTab.setImportButtonWarning("File is probably not a minecraft world. Continue at your own risk.");
                    return;
                }
                if(extractedDirectory == null)
                    return;
//                if(extractedDirectory != null) {
//                    extractedDirTemp = new File(extractedDirectory).getParent();
//                } else {
//                    alert(AlertType.WARNING, "File is probably not a minecraft world. Continue at your own risk.");
//                    return;
//                }
                String extractedDirTemp = new File(extractedDirectory).getParent();
                File predictedWorldDir = new File(findWorldDirectory(extractedDirTemp));
                worldsManagerTab.setExtractedWorldDir(predictedWorldDir.getAbsolutePath());


                File extractedWorldsLevelDat = new File(predictedWorldDir.getAbsolutePath() + "/level.dat");
                if(extractedWorldsLevelDat.exists()) {//copying world's level.dat file analogically like server ones
                    FileUtils.copyFile(extractedWorldsLevelDat, new File("world_temp/worlds_level_dat/level_" + predictedWorldDir.getName() + ".dat"));
                }
                
                worldsManagerTab.extractedWorldSize = FileSize.directorySizeWithConversion(new File(extractedDirTemp));
                worldsManagerTab.setIcons();
            }
            if (copyFilesToServerDir) {
                startImportingButtonFromWorldManagerTab.setEnabled(false); //issue #15 fix
                if (!serverWorldDir.exists() && !serverWorldDir.mkdirs()) {
                    alert(AlertType.ERROR, "Cannot create world directory \"" + serverWorldDir.getAbsolutePath() + "\".");
                }

                File dir = new File(worldsManagerTab.getExtractedWorldDir());
                if (serverWorldDir.list() != null && serverWorldDir.list().length > 0) { //world dir is not empty
                    FileUtils.deleteDirectory(serverWorldDir);
                }

                deleteEndAndNetherDirs();

                File predictedWorldDir = new File(findWorldDirectory(dir.getParent()));
                copyDirectoryWithProgressBar(predictedWorldDir, serverWorldDir);
                

                CurrentServerInfo.world.levelDat = new File(predictedWorldDir.getAbsolutePath() + "\\level.dat"); //trick the NBTParser into using extracted world's level.dat
                NBTParser nbtParser = NBTParser.createServerNBTParser();
                nbtParser.start();
                nbtParser.join();
                CurrentServerInfo.world.levelName = nbtParser.getLevelName();
                CurrentServerInfo.world.levelDat = CurrentServerInfo.world.getLevelDat(); //restore the original level.dat file location for safety
            }
        } else if (selectedWorld.toString().contains(CurrentServerInfo.serverPath.getAbsolutePath())) {
            Window.alert(AlertType.ERROR, "Cannot copy files from server directory to the server.");
        }
        startImportingButtonFromWorldManagerTab.setEnabled(true); //issue #15 fix
        worldsManagerTab.setIcons();
    }

    @Override
    public void run() {
        try {
            handler();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
