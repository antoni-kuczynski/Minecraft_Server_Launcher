package SelectedServer;

import Enums.AlertType;
import Gui.AddWorldsPanel;
import Gui.DebugWindow;
import Gui.Frame;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;

import static Gui.Frame.alert;
import static Gui.Frame.getErrorDialogMessage;

public class ServerPropertiesFile {
    private String worldName;

    public ServerPropertiesFile() throws IOException {
        File serverProperties = new File(ServerDetails.serverPath + "\\server.properties");
        if(!serverProperties.exists()) {
            alert(AlertType.WARNING, "\"" +ServerDetails.serverName + "\" server's \"server.properties\" file does not exist. " +
                    "Predicted server world folder name was set to \"world\".");
            worldName = "world";
            ServerDetails.serverWorldPath = ServerDetails.serverPath + "\\world";
            ServerDetails.serverLevelDatFile = ServerDetails.serverPath + "\\" + worldName + "\\" + "level.dat";
            AddWorldsPanel.wasServerPropertiesFound = false;
            DebugWindow.debugVariables.put("current_server_name", ServerDetails.serverName);
            DebugWindow.debugVariables.put("current_server_path", ServerDetails.serverPath);
            DebugWindow.debugVariables.put("current_server_id", String.valueOf(ServerDetails.serverId));
            return;
        }
        AddWorldsPanel.wasServerPropertiesFound = true;
        ArrayList<String> serverPropertiesContent = null;
        try {
            serverPropertiesContent = (ArrayList<String>) Files.readAllLines(serverProperties.toPath());
        } catch (NoSuchFileException e) {
            Frame.alert(AlertType.FATAL, "\"" + e.getMessage() + "\"" + " file not found.\n"  + getErrorDialogMessage(e));
            System.exit(1);
        }

        for(String s : serverPropertiesContent) {
            if(s.contains("level-name")) {
                worldName = s.split("=")[1];
                break;
            }
        }
        ServerDetails.serverWorldPath = ServerDetails.serverPath + "\\" + worldName;
        ServerDetails.serverLevelDatFile = ServerDetails.serverPath + "\\" + worldName + "\\" + "level.dat";

        DebugWindow.debugVariables.put("current_server_name", ServerDetails.serverName);
        DebugWindow.debugVariables.put("current_server_path", ServerDetails.serverPath);
        DebugWindow.debugVariables.put("current_server_id", String.valueOf(ServerDetails.serverId));
    }

    public String getWorldName() {
        return worldName;
    }
}
