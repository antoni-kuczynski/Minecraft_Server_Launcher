package com.myne145.serverlauncher.server.current;

import com.myne145.serverlauncher.server.MCServer;
import com.myne145.serverlauncher.utils.AlertType;
import com.myne145.serverlauncher.gui.window.Window;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import static com.myne145.serverlauncher.gui.window.Window.getErrorDialogMessage;

public class ServerProperties {
    private static String worldName;

    public static void reloadLevelNameGlobalValue(MCServer server) {
        File serverProperties = new File(server.serverPath().getAbsolutePath() + "\\server.properties");
        if(!serverProperties.exists()) {
            worldName = "world";
//            CurrentServerInfo.world.path = new File(server.serverPath().getAbsolutePath() + "\\world");
//            CurrentServerInfo.world.levelDat = new File(server.serverPath().getAbsolutePath() + "\\" + worldName + "\\" + "level.dat");
            return;
        }
        ArrayList<String> serverPropertiesContent = null;
        try {
            serverPropertiesContent = (ArrayList<String>) Files.readAllLines(serverProperties.toPath());
        } catch (IOException e) {
            Window.alert(AlertType.FATAL, "\"" + e.getMessage() + "\"" + " file not found.\n"  + getErrorDialogMessage(e));
            System.exit(1);
        }

        for(String s : serverPropertiesContent) {
            if(s.contains("level-name")) {
                worldName = s.split("=")[1];
                break;
            }
        }
//        CurrentServerInfo.world.path = new File(server.serverPath().getAbsolutePath() + "\\" + worldName);
//        CurrentServerInfo.world.levelDat = new File(server.serverPath().getAbsolutePath() + "\\" + worldName + "\\" + "level.dat");
    }

    public static String getWorldName() {
        return worldName;
    }
}
