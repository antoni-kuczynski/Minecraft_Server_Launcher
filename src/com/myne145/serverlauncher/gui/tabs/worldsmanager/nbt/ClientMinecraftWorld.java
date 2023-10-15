package com.myne145.serverlauncher.gui.tabs.worldsmanager.nbt;

import com.myne145.serverlauncher.server.current.CurrentServerInfo;

import java.io.File;
import java.io.IOException;

public class ClientMinecraftWorld extends MinecraftWorld{

    @Override
    public File getLevelDatFile(File worldPath) {
        return new File("world_temp/worlds_level_dat/level_" + worldPath.getName() + ".dat");
    }

    public ClientMinecraftWorld(File worldPath) throws IOException {
        super(worldPath);
    }
}
