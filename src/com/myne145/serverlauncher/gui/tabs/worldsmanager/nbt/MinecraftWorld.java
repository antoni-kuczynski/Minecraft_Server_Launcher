package com.myne145.serverlauncher.gui.tabs.worldsmanager.nbt;

import com.myne145.serverlauncher.server.Config;
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
    private String levelName;
    private String folderName;
    private Calendar lastPlayedDate;
    private String gamemode;
    private boolean isUsingCheats;
    private String gameVersion;
    private final ImageIcon DEFAULT_WORLD_ICON_PACK_PNG = new ImageIcon(new ImageIcon(Config.RESOURCES_PATH + "/defaultworld.jpg").getImage().getScaledInstance(96, 96, Image.SCALE_SMOOTH));

    public File getLevelDatFile(File worldPath) {
        return new File("world_temp/worlds_level_dat/level_" + worldPath.getName() + ".dat");
    }
    public MinecraftWorld(File worldPath) throws IOException {
        System.out.println(worldPath.getName());
        //Icon
        File iconFile = new File(worldPath.getAbsolutePath() + "/icon.png");
        try {
            worldIcon = new ImageIcon(ImageIO.read(iconFile).getScaledInstance(96, 96, Image.SCALE_SMOOTH));
        } catch (Exception e) {
            worldIcon = DEFAULT_WORLD_ICON_PACK_PNG;
        }
        File levelDatFile = getLevelDatFile(worldPath);

        if(!levelDatFile.exists())
            return;

        //level.dat NBT reading
        Nbt levelDat = new Nbt();
        CompoundTag content = levelDat.fromFile(levelDatFile);

        if(content == null)
            return;

        CompoundTag levelDatData = content.get("Data");
        levelName = levelDatData.getString("LevelName").toString().replace("\"", "");
        folderName = worldPath.getName();

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
        gameVersion = levelDatData.getCompound("Version").getString("Name").getValue();
    }

    public String getLevelNameColors() {
        return convertColors(levelName);
    }

    public String getLevelNameNoColors() {
        return levelName;
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
        if(levelName == null) //issue #72 fix
            return "Level.dat file does not exist";
        for(String s : MC_COLOR_CODES.keySet()) {
            levelName = levelName.replace(s, MC_COLOR_CODES.get(s));
        }
        levelName = levelName + MC_COLOR_CODES.get("§r") + "<font color =\"#cccccc\">";
        return levelName;
    }

}
