package com.myne145.serverlauncher.server;

import com.myne145.serverlauncher.gui.tabs.worldsmanager.WorldsManagerTab;
import com.myne145.serverlauncher.utils.AlertType;
import com.myne145.serverlauncher.gui.window.Window;
import com.myne145.serverlauncher.server.current.ServerProperties;
import com.myne145.serverlauncher.server.current.CurrentServerInfo;
import com.myne145.serverlauncher.utils.FileDetailsUtils;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static com.myne145.serverlauncher.gui.window.Window.alert;
import static com.myne145.serverlauncher.gui.window.Window.getErrorDialogMessage;
import static com.myne145.serverlauncher.utils.ZipUtils.extractArchive;
import static com.myne145.serverlauncher.utils.ZipUtils.isArchive;

public class WorldCopyHandler extends Thread {
    private static JProgressBar progressBar = null;
    private final WorldsManagerTab worldsManagerTab;
    private static JButton startImportingButtonFromWorldManagerTab;
    private final String serverWorldName;
    private final File selectedWorld;
    private final File serverWorldDir;
    private boolean copyFilesToServerDir;
    private static final Taskbar taskbar = Taskbar.getTaskbar();
    private final String currentServerAbsPath;

    private WorldCopyHandler(WorldsManagerTab worldsManagerTab, boolean copyFilesToServerDir) {
        ServerProperties.reloadLevelNameGlobalValue(Config.getData().get(worldsManagerTab.getTabIndex()));
        this.worldsManagerTab = worldsManagerTab;

        this.currentServerAbsPath = Config.getData().get(worldsManagerTab.getTabIndex()).serverPath().getAbsolutePath();

        this.serverWorldName = ServerProperties.getWorldName();
        this.serverWorldDir = new File(currentServerAbsPath + "\\" + serverWorldName);

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
     * @param mode copy mode (true - copy files to the server, false - just unzip and set icon a title of the world.) For archives only.
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
         if (selectedWorld.isDirectory() && selectedWorld.toString().contains(currentServerAbsPath)) {
            Window.alert(AlertType.ERROR, "Cannot copy files from server directory to the server.");
        }

        if (selectedWorld.isDirectory() && !selectedWorld.toString().contains(currentServerAbsPath)) {
            ArrayList<String> selectedWorldFilenamesList = new ArrayList<>(Arrays.asList(selectedWorld.list()));
            boolean isAddedWorldDirEmpty = serverWorldDir.list() == null || selectedWorld.list().length == 0;
            boolean containsWorldFiles =
                    selectedWorldFilenamesList.contains("level.dat") ||
                            selectedWorldFilenamesList.contains("data") ||
                            selectedWorldFilenamesList.contains("region");

            if(!copyFilesToServerDir) {
                if (!serverWorldDir.exists() && !serverWorldDir.mkdirs()) {
                    alert(AlertType.ERROR, "Cannot create world directory \"" + serverWorldDir.getAbsolutePath() + "\".");
                }
                if (!isAddedWorldDirEmpty && !containsWorldFiles) {
                    worldsManagerTab.setImportButtonWarning("Not a Minecraft world!");
                }

                if(new File(selectedWorld.getAbsolutePath() + "/level.dat").exists()) {
                    //copying world's level.dat file analogically like server ones
                    FileUtils.copyFile(new File(selectedWorld.getAbsolutePath() + "/level.dat"), new File("world_temp/worlds_level_dat/level_" + selectedWorld.getName() + ".dat"));
                }
            }
            if(copyFilesToServerDir) {
                if (!isAddedWorldDirEmpty && !containsWorldFiles) {
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

                String extractedDirTemp = new File(extractedDirectory).getParent();
                File predictedWorldDir = new File(findWorldDirectory(extractedDirTemp));
                worldsManagerTab.setExtractedWorldDir(predictedWorldDir.getAbsolutePath());


                File extractedWorldsLevelDat = new File(predictedWorldDir.getAbsolutePath() + "/level.dat");
                if(extractedWorldsLevelDat.exists()) {//copying world's level.dat file analogically like server ones
                    File worldLevelDat = new File("world_temp/worlds_level_dat/level_" + predictedWorldDir.getName() + ".dat");
                    FileUtils.copyFile(extractedWorldsLevelDat, worldLevelDat);
                }
                
                worldsManagerTab.extractedWorldSize = FileDetailsUtils.directorySizeWithConversion(new File(extractedDirTemp));
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
                CurrentServerInfo.world.levelDat = CurrentServerInfo.world.getLevelDat(); //restore the original level.dat file location for safety
            }
        }
        startImportingButtonFromWorldManagerTab.setEnabled(true); //issue #15 fix
        worldsManagerTab.setIcons(); //non-removable
        if(CurrentServerInfo.world.getPath().exists()) {
            worldsManagerTab.getWorldsInfoPanels().updateServerWorldInformation(CurrentServerInfo.world.path);
        }
    }

    @Override
    public void run() {
        worldsManagerTab.getStartCopying().setEnabled(false);
        try {
            handler();
        } catch (IOException | InterruptedException e) {
            worldsManagerTab.getStartCopying().setEnabled(true);
            if(e.toString().startsWith("java.io.FileNotFoundException")) {
                alert(AlertType.ERROR, "Incompatible archive found! Try unpacking it manually and adding it as a folder.\n" + getErrorDialogMessage(e));
            }
        }
        worldsManagerTab.getStartCopying().setEnabled(true);
    }
}
