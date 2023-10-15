package com.myne145.serverlauncher.gui.tabs.worldsmanager.nbt;

import com.myne145.serverlauncher.server.current.CurrentServerInfo;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class ServerMinecraftWorld extends MinecraftWorld{

    @Override
    public File getLevelDatFile(File worldPath) {
        Runnable runnable = () -> {
            try {
                if(new File("world_temp\\level_" + "server_id_" + CurrentServerInfo.serverId + "_" + ".dat").exists())
                    FileUtils.forceDelete(new File("world_temp\\level_" + "server_id_" + CurrentServerInfo.serverId + "_" + ".dat"));
                FileUtils.copyFile(CurrentServerInfo.world.levelDat, new File("world_temp\\level_" + "server_id_" + CurrentServerInfo.serverId + "_" + ".dat"));
            } catch (Exception ignored) {
                //this 99% of times throws an exception, i just wanna finish this
            }
        };
        try {
            Thread thread = new Thread(runnable);
            thread.start();
            thread.join();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new File("world_temp\\level_" + "server_id_" + CurrentServerInfo.serverId + "_" + ".dat");
    }

    public ServerMinecraftWorld(File worldPath) throws IOException {
        super(worldPath);
        CurrentServerInfo.world.levelDat = getLevelDatFile(worldPath);
        CurrentServerInfo.world.lastPlayedDate = getLastPlayedDate();
        CurrentServerInfo.world.path = worldPath;
        CurrentServerInfo.world.levelName = getLevelNameColors();
    }
}
