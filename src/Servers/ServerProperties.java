package Servers;

import Gui.ConfigStuffPanel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class ServerProperties {
    private String worldName;
    public ServerProperties() throws IOException {
        final File serverProperties = new File(ConfigStuffPanel.getServPath() + "\\server.properties");
        ArrayList<String> fileContent = (ArrayList<String>) Files.readAllLines(serverProperties.toPath());
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
