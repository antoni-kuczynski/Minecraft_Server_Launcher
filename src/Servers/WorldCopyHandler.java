package Servers;

import Gui.AddWorldsPanel;
import Gui.ConfigStuffPanel;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class WorldCopyHandler extends Thread {

    private final String serverWorldName;
    private final File originalDir;
    private final File serverWorldDir;
    public WorldCopyHandler() throws IOException {
        ServerProperties serverProperties = new ServerProperties();
        this.serverWorldName = serverProperties.getWorldName();
        this.serverWorldDir = new File(ConfigStuffPanel.getServPath() + "\\" + serverWorldName);
        this.originalDir = AddWorldsPanel.getWorlds().get(0);

        System.out.println("Original dir: " + originalDir + "\nServer world dir: " + serverWorldDir);
    }

    @Override
    public void run() {
        super.run();
        //TODO: copying dir to server dir/{world_name}, but also check for world name variable in server.properties file
    }
}
