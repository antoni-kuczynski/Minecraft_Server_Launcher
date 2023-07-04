package com.myne145.serverlauncher.Server.Current;

import com.myne145.serverlauncher.Gui.AlertType;
import com.myne145.serverlauncher.Gui.Tabs.WorldsTab;
import com.myne145.serverlauncher.Gui.Window;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;

import static com.myne145.serverlauncher.Gui.Window.getErrorDialogMessage;

public class ServerPropertiesFile {
    private String worldName;

    public ServerPropertiesFile() throws IOException {
        File serverProperties = new File(CurrentServerInfo.serverPath + "\\server.properties");
        if(!serverProperties.exists()) {
            worldName = "world";
            CurrentServerInfo.serverWorldPath = new File(CurrentServerInfo.serverPath.getAbsolutePath() + "\\world");
            CurrentServerInfo.serverLevelDatFile = new File(CurrentServerInfo.serverPath.getAbsolutePath() + "\\" + worldName + "\\" + "level.dat");
            WorldsTab.wasServerPropertiesFound = false;
            return;
        }
        WorldsTab.wasServerPropertiesFound = true;
        ArrayList<String> serverPropertiesContent = null;
        try {
            serverPropertiesContent = (ArrayList<String>) Files.readAllLines(serverProperties.toPath());
        } catch (NoSuchFileException e) {
            Window.alert(AlertType.FATAL, "\"" + e.getMessage() + "\"" + " file not found.\n"  + getErrorDialogMessage(e));
            System.exit(1);
        }

        for(String s : serverPropertiesContent) {
            if(s.contains("level-name")) {
                worldName = s.split("=")[1];
                break;
            }
        }
        CurrentServerInfo.serverWorldPath = new File(CurrentServerInfo.serverPath.getAbsolutePath() + "\\" + worldName);
        CurrentServerInfo.serverLevelDatFile = new File(CurrentServerInfo.serverPath.getAbsolutePath() + "\\" + worldName + "\\" + "level.dat");

    }

    public String getWorldName() {
        return worldName;
    }
}
