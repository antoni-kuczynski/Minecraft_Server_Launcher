package com.myne145.serverlauncher.gui.tabs.worldsmanager.nbt;

import com.myne145.serverlauncher.gui.components.ButtonWarning;
import com.myne145.serverlauncher.gui.window.Window;
import com.myne145.serverlauncher.server.MCServer;
import com.myne145.serverlauncher.utils.DateFormat;
import com.myne145.serverlauncher.utils.DefaultIcons;
import net.lenni0451.mcstructs.nbt.INbtTag;
import net.lenni0451.mcstructs.nbt.io.NbtIO;
import net.lenni0451.mcstructs.nbt.io.NbtReadTracker;
import net.lenni0451.mcstructs.nbt.tags.CompoundTag;
import org.apache.commons.io.FileUtils;
//import dev.dewy.nbt.Nbt;
//import dev.dewy.nbt.tags.collection.CompoundTag;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.*;

import static com.myne145.serverlauncher.gui.window.Window.showErrorMessage;
import static com.myne145.serverlauncher.utils.ZipUtils.extractArchive;

public class MinecraftWorld {
    private ImageIcon worldIcon;
    private String levelName = "";
    private String folderName = "";
    private Calendar lastPlayedDate;
    private String gamemode = "";
    private boolean isUsingCheats;
    private String gameVersion;
    private boolean hasLevelDat;
    private File path;

//    public static String findWorldDirectory(String dir) {
//        ArrayList<File> arr = new ArrayList<>(Arrays.asList(Objects.requireNonNull(new File(dir).listFiles())));
//        File foundLevelDat = new File(dir);
//        boolean containsLevelDat = false;
//        boolean hasDirectory = false;
//        for(File f : arr)
//            if(f.isDirectory())
//                hasDirectory = true;
//        for (File f : arr) {
//            if (f.getName().equals("level.dat") || !hasDirectory) {
//                containsLevelDat = true;
//                foundLevelDat = new File(f.getAbsolutePath());
//            }
//        }
//
//        if (containsLevelDat) {
//            return foundLevelDat.getParent();
//        } else {
//            String nextDir = null;
//            for (File f : arr) {
//                if (f.isDirectory()) {
//                    nextDir = f.getAbsolutePath();
//                    break;
//                } else {
//                    nextDir = new File(f.getParent()).getParent();
//                }
//            }
//            if(!nextDir.equals(dir))
//                return findWorldDirectory(nextDir);
//            else
//                return dir;
//        }
//    }

//    public static MinecraftWorld extractWorld(File world) {
//        File worldExtractDirectory = new File("world_temp/" + world.getName());
//        if (worldExtractDirectory.exists())
//            FileUtils.deleteDirectory(worldExtractDirectory);
//
//
//        String extractedDirectory;
//        try {
//            extractedDirectory = extractArchive(world.getAbsolutePath(), worldExtractDirectory.getAbsolutePath(), worldsManagerTab);
//        } catch (IOException e) {
//            showErrorMessage("I/O error extracting " + world.getName() + " file.", e);
//            return null;
//        }
//        if(!worldExtractDirectory.exists()) {
//            return null;
////            worldsManagerTab.getPickDirectoryButton().setImportButtonWarning(ButtonWarning.NOT_A_MINECRAFT_WORLD);
////            return;
//        }
//
//        String extractedDirTemp = new File(extractedDirectory).getParent();
//        File predictedWorldDir = new File(findWorldDirectory(extractedDirTemp));
//        worldsManagerTab.setExtractedWorldDir(predictedWorldDir.getAbsolutePath());
//
//
//        File extractedWorldsLevelDat = new File(predictedWorldDir.getAbsolutePath() + "/level.dat");
//        if(!extractedWorldsLevelDat.exists()) {//copying world's level.dat file analogically like server ones
////            File worldLevelDat = new File("world_temp/worlds_level_dat/level_" + predictedWorldDir.getName() + ".dat");
////            FileUtils.copyFile(extractedWorldsLevelDat, worldLevelDat);
//            return null;
//        }
//        MinecraftWorld world1 = new MinecraftWorld();
//        world1.update(extractedWorldsLevelDat);
//        return world1;
//    }

    public void update(File levelDatFile) {
        path = levelDatFile.getParentFile();
        hasLevelDat = levelDatFile.exists();

        //Icon
        File iconFile = new File(levelDatFile.getParentFile().getAbsolutePath() + "/icon.png");
        try {
            worldIcon = new ImageIcon(ImageIO.read(iconFile).getScaledInstance(96, 96, Image.SCALE_SMOOTH));
        } catch (IOException e) {
            worldIcon = DefaultIcons.getIcon(DefaultIcons.WORLD_MISSING);
        }


        try (FileChannel sourceChannel = FileChannel.open(levelDatFile.toPath(), StandardOpenOption.READ);
            InputStream stream = Channels.newInputStream(sourceChannel);

        ) {
            INbtTag tag = NbtIO.JAVA.read(stream, true, NbtReadTracker.unlimited());
            CompoundTag tag1 = tag.asCompoundTag();
            CompoundTag content = tag1.get("Data");

            stream.close();
            sourceChannel.close();

            String levelName = content.get("LevelName").asStringTag().getValue();
            String folderName = levelDatFile.getParentFile().getName();
            long lastPlayed = content.getLong("LastPlayed");
            int gameMode = content.getInt("GameType");
            boolean isUsingCheats = content.getBoolean("allowCommands");
            String version = content.get("Version").asCompoundTag().getString("Name");


            if(content.isEmpty()) {
                hasLevelDat = false;
                return;
            }


            this.levelName = levelName;
            this.folderName = folderName;

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(lastPlayed));
            this.lastPlayedDate = calendar;

            switch (gameMode) {
                case 0 -> this.gamemode = "Survival";
                case 1 -> this.gamemode = "Creative";
                case 2 -> this.gamemode = "Adventure";
                case 3 -> this.gamemode = "Spectator";
                default -> this.gamemode = "Unknown";
            }
            this.isUsingCheats = isUsingCheats;
            this.gameVersion = version;

        } catch (IOException ex) {
            hasLevelDat = false;
        }

    }


    //server constructor
    public MinecraftWorld(MCServer server) {
//        //Icon
//        File iconFile = new File(server.getWorldPath().getAbsolutePath() + "/icon.png");
//        try {
//            worldIcon = new ImageIcon(ImageIO.read(iconFile).getScaledInstance(96, 96, Image.SCALE_SMOOTH));
//        } catch (IOException e) {
//            worldIcon = DefaultIcons.getIcon(DefaultIcons.WORLD_MISSING);
//        }

        update(server.getTempLevelDat());
    }

    //client constructor
    public MinecraftWorld() {
//        //Icon
//        File iconFile = new File(worldPath.getAbsolutePath() + "/icon.png");
//        try {
//            worldIcon = new ImageIcon(ImageIO.read(iconFile).getScaledInstance(96, 96, Image.SCALE_SMOOTH));
//        } catch (IOException e) {
//            worldIcon = DefaultIcons.getIcon(DefaultIcons.WORLD_MISSING);
//        }

//        File levelDatFile = new File("world_temp/worlds_level_dat/level_" + worldPath.getName() + ".dat");
//        if(!levelDatFile.exists())
//            return;

//        update(levelDat);
    }

    public File getPath() {
        return path;
    }

    public String getLevelNameColors() {
        return convertColors(levelName);
    }

    public String getFolderName() {
        return folderName;
    }

    public Calendar getLastPlayedDate() {
        return lastPlayedDate;
    }

    public String getGamemode() {
        return gamemode;
    }

    public boolean isUsingCheats() {
        return isUsingCheats;
    }

    public String getGameVersion() {
        return gameVersion;
    }

    public ImageIcon getWorldIcon() {
        return worldIcon;
    }

    public boolean hasLevelDat() {
        return hasLevelDat;
    }

    public String getFormattedDate() {
        String AM_PM = "AM";
        if(getLastPlayedDate().get(Calendar.AM_PM) == Calendar.PM)
            AM_PM = "PM";


        String formattedDate = "Invalid date format";
        if(Window.getDateFormat() == DateFormat.DD_MM_YYYY) {
            formattedDate = this.getLastPlayedDate().get(Calendar.DAY_OF_MONTH) + "∕" +
                    (this.getLastPlayedDate().get(Calendar.MONTH) + 1) + "∕" +
                    this.getLastPlayedDate().get(Calendar.YEAR) + ", " +
                    this.getLastPlayedDate().get(Calendar.HOUR) + ":" + this.getLastPlayedDate().get(Calendar.MINUTE) + " " + AM_PM;
        } else if(Window.getDateFormat() == DateFormat.YYYY_MM_DD) {
            formattedDate = this.getLastPlayedDate().get(Calendar.YEAR) + "∕" +
                    (this.getLastPlayedDate().get(Calendar.MONTH) + 1) + "∕" +
                    this.getLastPlayedDate().get(Calendar.DAY_OF_MONTH) + ", " +
                    this.getLastPlayedDate().get(Calendar.HOUR) + ":" + this.getLastPlayedDate().get(Calendar.MINUTE) + " " + AM_PM;
        }
        return formattedDate;
    }

    private static final Map<String, String> MC_COLOR_CODES = Map.ofEntries(
            Map.entry("§0", "<font color=\"#000000\">"),    // black
            Map.entry("§1", "<font color=\"#0000AA\">"),    // dark_blue
            Map.entry("§2", "<font color=\"#00AA00\">"),    // dark_green
            Map.entry("§3", "<font color=\"#00AAAA\">"),    // dark_aqua
            Map.entry("§4", "<font color=\"#AA0000\">"),   // dark_red
            Map.entry("§5", "<font color=\"#AA00AA\">"),    // dark_purple
            Map.entry("§6", "<font color=\"#FFAA00\">"),    // gold
            Map.entry("§7", "<font color=\"#AAAAAA\">"),    // gray
            Map.entry("§8", "<font color=\"#555555\">"),    // dark_gray
            Map.entry("§9", "<font color=\"#5555FF\">"),    // blue
            Map.entry("§a", "<font color=\"#55FF55\">"),    // green
            Map.entry("§b", "<font color=\"#55FFFF\">"),    // aqua
            Map.entry("§c", "<font color=\"#FF5555\">"),    // red
            Map.entry("§d", "<font color=\"#FF55FF\">"),    // light_purple
            Map.entry("§e", "<font color=\"#FFFF55\">"),    // yellow
            Map.entry("§f", "<font color=\"#FFFFFF\">"),    // white
            Map.entry("§k", ""),   // obfuscated (not supported in HTML)
            Map.entry("§l", "<b>"),    // bold
            Map.entry("§m", "<strike>"),    // strikethrough
            Map.entry("§n", "<u>"),    // underline
            Map.entry("§o", "<i>"),    // italic
            Map.entry("§r", "</font></i></u></strike></b>")    // reset
    );

    private static String convertColors(String levelName) {
        if(levelName == null)
            return "Level.dat file does not exist";
        int changeTagsCounter = 0;
        for(String s : MC_COLOR_CODES.keySet()) {
            String tempLevelName = levelName;
            levelName = levelName.replace(s, MC_COLOR_CODES.get(s));
            if(!levelName.equals(tempLevelName))
                changeTagsCounter++;
        }
        StringBuilder resettingTags = new StringBuilder();
        for(int i = 0; i < changeTagsCounter - 1; i++)
            resettingTags.append(MC_COLOR_CODES.get("§r"));

        levelName = levelName + resettingTags + "<font color =\"#cccccc\">";
        return levelName;
    }

}
