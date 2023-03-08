package Servers;

import Gui.ConfigStuffPanel;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ServerProperties {
    public ServerProperties() throws IOException {
        final File serverProperties = new File(ConfigStuffPanel.getServPath() + "\\server.properties");
        System.out.println(Files.readAllLines(serverProperties.toPath()));
    }
}
