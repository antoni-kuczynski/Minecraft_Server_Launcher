package SelectedServer;

import Gui.AlertType;
import Gui.Frame;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;

import static Gui.Frame.alert;
import static Gui.Frame.exStackTraceToString;

public class ServerPropertiesFile {
    private String worldName;

    public ServerPropertiesFile() throws IOException {
        File serverProperties = new File(ServerDetails.serverPath + "\\server.properties");

        File serverPropertiesError = new File("server_properties_error");
        if(!serverPropertiesError.exists())
            if(!serverProperties.createNewFile())
                alert(AlertType.ERROR, "Cannot create server_properties_error file.\n" + exStackTraceToString(new Throwable().getStackTrace()));

        if(!serverProperties.exists())
            serverProperties = new File("server_properties_error");
        ArrayList<String> serverPropertiesContent = null;
        try {
            serverPropertiesContent = (ArrayList<String>) Files.readAllLines(serverProperties.toPath());
        } catch (NoSuchFileException e) {
            Frame.alert(AlertType.FATAL, "\"" + e.getMessage() + "\"" + " file not found.\n"  + exStackTraceToString(e.getStackTrace()));
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
    }

    public String getWorldName() {
        return worldName;
    }
}
