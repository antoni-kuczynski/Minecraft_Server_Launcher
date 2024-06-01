package com.myne145.serverlauncher.gui.tabs.worldsmanager.nbt;

import com.myne145.serverlauncher.gui.window.Window;
import com.myne145.serverlauncher.server.MCServer;
import com.myne145.serverlauncher.utils.DateFormat;
import com.myne145.serverlauncher.utils.DefaultIcons;
import dev.dewy.nbt.Nbt;
import dev.dewy.nbt.tags.collection.CompoundTag;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class MinecraftWorld {
    private ImageIcon worldIcon;
    private String levelName = "";
    private String folderName = "";
    private Calendar lastPlayedDate;
    private String gamemode = "";
    private boolean isUsingCheats;
    private String gameVersion;
    private boolean hasLevelDat;

    public void update(File levelDatFile) {
        hasLevelDat = levelDatFile.exists();

        //level.dat NBT reading
        Nbt levelDat = new Nbt();
        CompoundTag content;
        try {
            content = levelDat.fromFile(levelDatFile);
        } catch (IOException e) {
            hasLevelDat = false;
//            Window.showErrorMessage("I/O error reading the level.dat file", e);
            return;
        }

        if(content == null || content.isEmpty()) {
            hasLevelDat = false;
            return;
        }

        CompoundTag levelDatData = content.get("Data");
        if(levelDatData == null) {
            hasLevelDat = false;
            return;
        }

        levelName = levelDatData.getString("LevelName").toString().replace("\"", "");
        folderName = levelDatFile.getParentFile().getName();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(levelDatData.getLong("LastPlayed").getValue()));
        lastPlayedDate = calendar;

        switch (levelDatData.getInt("GameType").intValue()) {
            case 0 -> gamemode = "Survival";
            case 1 -> gamemode = "Creative";
            case 2 -> gamemode = "Adventure";
            case 3 -> gamemode = "Spectator";
            default -> gamemode = "Unknown";
        }
        isUsingCheats = levelDatData.getByte("allowCommands").intValue() == 1;

        if(levelDatData.getCompound("Version") == null)
            gameVersion = "Unknown";
        else
            gameVersion = levelDatData.getCompound("Version").getString("Name").getValue();
    }


    //server constructor
    public MinecraftWorld(MCServer server) {
        //Icon
        File iconFile = new File(server.getWorldPath().getAbsolutePath() + "/icon.png");
        try {
            worldIcon = new ImageIcon(ImageIO.read(iconFile).getScaledInstance(96, 96, Image.SCALE_SMOOTH));
        } catch (IOException e) {
            worldIcon = DefaultIcons.getServerPlatformIcon(DefaultIcons.WORLD_MISSING);
        }

        update(server.getTempLevelDat());
    }

    //client constructor
    public MinecraftWorld(File worldPath) {
        //Icon
        File iconFile = new File(worldPath.getAbsolutePath() + "/icon.png");
        try {
            worldIcon = new ImageIcon(ImageIO.read(iconFile).getScaledInstance(96, 96, Image.SCALE_SMOOTH));
        } catch (IOException e) {
            worldIcon = DefaultIcons.getServerPlatformIcon(DefaultIcons.WORLD_MISSING);
        }

        File levelDatFile = new File("world_temp/worlds_level_dat/level_" + worldPath.getName() + ".dat");
        if(!levelDatFile.exists())
            return;

        update(levelDatFile);
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
