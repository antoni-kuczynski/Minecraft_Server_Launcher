package SelectedServer;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Map.entry;

public class LevelNameColorConverter {
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
        MC_CODES.put("§r", "<RESET>");    // reset
    }

    private static final LinkedHashMap<String, String> MC_CODES_DEBUG = new LinkedHashMap<String, String>() {{
        put("§0", "\u001b[30m"); // black
        put("§1", "\u001b[34m"); // dark_blue
        put("§2", "\u001b[32m"); // dark_green
        put("§3", "\u001b[36m"); // dark_aqua
        put("§4", "\u001b[31m"); // dark_red
        put("§5", "\u001b[35m"); // dark_purple
        put("§6", "\u001b[33m"); // gold
        put("§7", "\u001b[37m"); // gray
        put("§8", "\u001b[90m"); // dark_gray
        put("§9", "\u001b[94m"); // blue
        put("§a", "\u001b[92m"); // green
        put("§b", "\u001b[96m"); // aqua
        put("§c", "\u001b[91m"); // red
        put("§d", "\u001b[95m"); // light_purple
        put("§e", "\u001b[93m"); // yellow
        put("§f", "\u001b[97m"); // white
        put("§k", "\u001b[5m");  // obfuscated
        put("§l", "\u001b[1m");  // bold
        put("§m", "\u001b[9m");  // strikethrough
        put("§n", "\u001b[4m");  // underline
        put("§o", "\u001b[3m");  // italic
        put("§r", "\u001b[0m");  // reset
    }};



    public static void convertColors(String levelName) {
        StringBuilder finalLevelName = new StringBuilder(levelName);
        boolean isParagraph = false;
        for(int i = 1; i < levelName.length(); i++) {
            if(levelName.charAt(i - 1) == '§') {
                System.out.println("Replacing " + levelName.charAt(i-1) + levelName.charAt(i) + " with \"" + MC_CODES_DEBUG.get("§" + levelName.charAt(i)));
                finalLevelName.replace(i-1, i, MC_CODES.get("§" + levelName.charAt(i)));
            }

        }
        System.out.println("Final level name: " + finalLevelName);
    }
}
