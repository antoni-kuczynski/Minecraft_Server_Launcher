package com.myne145.serverlauncher.server;

import com.myne145.serverlauncher.gui.components.ButtonWarning;
import com.myne145.serverlauncher.gui.tabs.worldsmanager.WorldsManagerTab;
import com.myne145.serverlauncher.gui.window.Window;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.FileSystemException;
import java.nio.file.FileSystemLoopException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static com.myne145.serverlauncher.gui.window.Window.showErrorMessage;
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
    private static final Taskbar taskbar = Window.getTaskbar();
    private final String currentServerAbsPath;

    private WorldCopyHandler(WorldsManagerTab worldsManagerTab, boolean copyFilesToServerDir) {
        setName("WORLD_COPY_HANDLER");
        this.worldsManagerTab = worldsManagerTab;

        this.currentServerAbsPath = Config.getData().get(worldsManagerTab.getIndex() - 1).getServerPath().getAbsolutePath();

        this.serverWorldName = Config.getData().get(worldsManagerTab.getIndex() - 1).getWorldPath().getName();
        this.serverWorldDir = new File(currentServerAbsPath + "/" + serverWorldName);

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
        return new WorldCopyHandler(worldsManagerTab, false);
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
        if(!destDir.exists() && !destDir.mkdir()) {
            showErrorMessage("Cannot create " + destDir.getAbsolutePath() + " world directory", new IOException());
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
                if(Taskbar.isTaskbarSupported()) {
                    taskbar.setWindowProgressState(Window.getWindow(), Taskbar.State.INDETERMINATE);
                    taskbar.setWindowProgressValue(Window.getWindow(), 100);
                }
            }
        }
        if(Taskbar.isTaskbarSupported())
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
            worldsManagerTab.getPickDirectoryButton().setImportButtonWarning(ButtonWarning.NOT_A_MINECRAFT_WORLD);
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

    private void deleteEndAndNetherDirs() {
        try {
            FileUtils.deleteDirectory(new File(serverWorldDir.getParent() + "/" + serverWorldName + "_the_end"));
            FileUtils.deleteDirectory(new File(serverWorldDir.getParent() + "/" + serverWorldName + "_nether"));
        } catch (IOException e) {
            showErrorMessage("Cannot remove " + Config.getData().get(worldsManagerTab.getIndex() - 1).getName() + "'s world nether or end dirs.", e);
        }
    }

    private void handler() throws IOException {
         if (selectedWorld.isDirectory() && selectedWorld.toString().contains(currentServerAbsPath)) {
            Window.showErrorMessage("Cannot copy world files from the same server.", new FileSystemLoopException(selectedWorld.getAbsolutePath()));
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
                    showErrorMessage("Cannot create " + serverWorldDir.getAbsolutePath() + " world directory.", new FileSystemException(serverWorldDir.getAbsolutePath()));
                }
                if (!isAddedWorldDirEmpty && !containsWorldFiles) {
                    worldsManagerTab.getPickDirectoryButton().setImportButtonWarning(ButtonWarning.NOT_A_MINECRAFT_WORLD);
                }

                if(new File(selectedWorld.getAbsolutePath() + "/level.dat").exists()) {
                    //copying world's level.dat file analogically like server ones
                    FileUtils.copyFile(new File(selectedWorld.getAbsolutePath() + "/level.dat"), new File("world_temp/worlds_level_dat/level_" + selectedWorld.getName() + ".dat"));
                }
            }
            if(copyFilesToServerDir) {
                if (!isAddedWorldDirEmpty && !containsWorldFiles) {
                    worldsManagerTab.getPickDirectoryButton().setImportButtonWarning(ButtonWarning.NOT_A_MINECRAFT_WORLD);
                } else if (serverWorldDir.list() != null && serverWorldDir.list().length > 0) { //world dir is not empty
                    FileUtils.deleteDirectory(serverWorldDir);
                }

                deleteEndAndNetherDirs();
                try {
                    copyDirectoryWithProgressBar(selectedWorld, serverWorldDir);
                } catch (IOException e) {
                    showErrorMessage("Cannot copy the world files to the server.", e);
                }
            }
        } else if (isArchive(selectedWorld)) {
            if (!copyFilesToServerDir) {
                File worldExtractDirectory = new File("world_temp/" + selectedWorld.getName());
                if (worldExtractDirectory.exists())
                    FileUtils.deleteDirectory(worldExtractDirectory);


                String extractedDirectory;
                try {
                    extractedDirectory = extractArchive(selectedWorld.getAbsolutePath(), worldExtractDirectory.getAbsolutePath(), worldsManagerTab);
                } catch (IOException e) {
                    showErrorMessage("I/O error extracting " + selectedWorld.getName() + " file.", e);
                    return;
                }
                if(!worldExtractDirectory.exists()) {
                    worldsManagerTab.getPickDirectoryButton().setImportButtonWarning(ButtonWarning.NOT_A_MINECRAFT_WORLD);
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
            }
            if (copyFilesToServerDir) {
                startImportingButtonFromWorldManagerTab.setEnabled(false);
                if (!serverWorldDir.exists() && !serverWorldDir.mkdirs()) {
                    showErrorMessage("Cannot create world directory.", new FileSystemException(serverWorldDir.getAbsolutePath()));
                }

                File dir = new File(worldsManagerTab.getExtractedWorldDir());
                if (serverWorldDir.list() != null && serverWorldDir.list().length > 0) { //world dir is not empty
                    FileUtils.deleteDirectory(serverWorldDir);
                }

                deleteEndAndNetherDirs();

                File predictedWorldDir = new File(findWorldDirectory(dir.getParent()));
                copyDirectoryWithProgressBar(predictedWorldDir, serverWorldDir);
            }
        }
        startImportingButtonFromWorldManagerTab.setEnabled(true);
        worldsManagerTab.setIcons();
        if(Config.getData().get(worldsManagerTab.getIndex() - 1).getWorldPath().exists()) {
            worldsManagerTab.getWorldsInfoPanels().updateServerWorldInformation();
        }
    }

    @Override
    public void run() {
        worldsManagerTab.getStartCopying().setEnabled(false);
        try {
            handler();
        } catch (IOException e) {
            worldsManagerTab.getStartCopying().setEnabled(true);
            showErrorMessage("Incompatible archive found. Try unpacking it manually and adding the world from a folder.", e);
        }
        worldsManagerTab.getStartCopying().setEnabled(true);
    }
}
