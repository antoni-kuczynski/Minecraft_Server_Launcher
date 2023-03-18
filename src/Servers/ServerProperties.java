package Servers;

import Gui.AlertType;
import Gui.ConfigStuffPanel;
import Gui.Frame;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;

import static Gui.Frame.exStackTraceToString;

public class ServerProperties {
    private String worldName;
    public ServerProperties() throws IOException {
        File serverProperties = new File(ConfigStuffPanel.getServPath() + "\\server.properties");
        if(!serverProperties.exists())
            serverProperties = new File("server_properties_error");
        ArrayList<String> fileContent = null;
        try {
            fileContent = (ArrayList<String>) Files.readAllLines(serverProperties.toPath());
        } catch (NoSuchFileException e) {
            Frame.alert(AlertType.FATAL, "\"" + e.getMessage() + "\"" + " file not found.\n"  + exStackTraceToString(e.getStackTrace()));
            System.exit(1);
        }

        for(String s : fileContent) {
            if(s.contains("level-name")) {
                worldName = s.split("=")[1];
                break;
            }
        }
    }

    public String getWorldName() {
        return worldName;
    }
}
