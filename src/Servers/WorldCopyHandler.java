package Servers;

import org.apache.commons.io.FileUtils;

public class WorldCopyHandler extends Thread {

    public WorldCopyHandler() {

    }

    @Override
    public void run() {
        super.run();
        //TODO: copying dir to server dir/{world_name}, but also check for world name variable in server.properties file
    }
}
