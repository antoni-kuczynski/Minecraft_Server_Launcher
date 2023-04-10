package SelectedServer;

import java.util.LinkedHashMap;

public class LevelNameColorConverter {
    private final LinkedHashMap<String, String> colorMap = new LinkedHashMap<>();
    private final LinkedHashMap<String, String> styleMap = new LinkedHashMap<>();

    public LevelNameColorConverter() {
        // Color codes
        colorMap.put("§0", "black");
        colorMap.put("§1", "dark_blue");
        colorMap.put("§2", "dark_green");
        colorMap.put("§3", "dark_aqua");
        colorMap.put("§4", "dark_red");
        colorMap.put("§5", "dark_purple");
        colorMap.put("§6", "gold");
        colorMap.put("§7", "gray");
        colorMap.put("§8", "dark_gray");
        colorMap.put("§9", "blue");
        colorMap.put("§a", "green");
        colorMap.put("§b", "aqua");
        colorMap.put("§c", "red");
        colorMap.put("§d", "light_purple");
        colorMap.put("§e", "yellow");
        colorMap.put("§f", "white");

        // Style codes
        styleMap.put("§k", "obfuscated");
        styleMap.put("§l", "bold");
        styleMap.put("§m", "strikethrough");
        styleMap.put("§n", "underline");
        styleMap.put("§o", "italic");
        styleMap.put("§r", "reset");
    }

}
