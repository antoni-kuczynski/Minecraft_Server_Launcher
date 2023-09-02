package com.myne145.serverlauncher.server.current;

import java.util.LinkedHashMap;

@Deprecated
public final class LevelNameColorConverter {
    private static final LinkedHashMap<String, String> MC_CODES = new LinkedHashMap<>();
    static {
        MC_CODES.put("§0", "<font color=\"#000000\">");    // black
        MC_CODES.put("§1", "<font color=\"#0000AA\">");    // dark_blue
        MC_CODES.put("§2", "<font color=\"#00AA00\">");    // dark_green
        MC_CODES.put("§3", "<font color=\"#00AAAA\">");    // dark_aqua
        MC_CODES.put("§4", "<font color=\"#AA0000\">");    // dark_red
        MC_CODES.put("§5", "<font color=\"#AA00AA\">");    // dark_purple
        MC_CODES.put("§6", "<font color=\"#FFAA00\">");    // gold
        MC_CODES.put("§7", "<font color=\"#AAAAAA\">");    // gray
        MC_CODES.put("§8", "<font color=\"#555555\">");    // dark_gray
        MC_CODES.put("§9", "<font color=\"#5555FF\">");    // blue
        MC_CODES.put("§a", "<font color=\"#55FF55\">");    // green
        MC_CODES.put("§b", "<font color=\"#55FFFF\">");    // aqua
        MC_CODES.put("§c", "<font color=\"#FF5555\">");    // red
        MC_CODES.put("§d", "<font color=\"#FF55FF\">");    // light_purple
        MC_CODES.put("§e", "<font color=\"#FFFF55\">");    // yellow
        MC_CODES.put("§f", "<font color=\"#FFFFFF\">");    // white
        MC_CODES.put("§k", "");    // obfuscated (not supported in HTML)
        MC_CODES.put("§l", "<b>");    // bold
        MC_CODES.put("§m", "<strike>");    // strikethrough
        MC_CODES.put("§n", "<u>");    // underline
        MC_CODES.put("§o", "<i>");    // italic
        MC_CODES.put("§r", "</font></i></u></strike></b>");    // reset
    }

    public static String convertColors(String levelName) {
        if(levelName == null) //issue #72 fix
            return "Level.dat file does not exist";
        for(String s : MC_CODES.keySet()) {
            levelName = levelName.replace(s, MC_CODES.get(s));
        }
        levelName = levelName + MC_CODES.get("§r") + "<font color =\"#cccccc\">";
        return levelName;
    }
}
