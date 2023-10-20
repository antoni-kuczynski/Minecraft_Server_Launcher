package com.myne145.serverlauncher.gui.tabs.worldsmanager.nbt;

import com.myne145.serverlauncher.server.current.CurrentServerInfo;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

public class ServerMinecraftWorld extends MinecraftWorld{

    @Override
    public File getLevelDatFile(File worldPath) {
        File levelDat = new File(worldPath.getAbsolutePath() + "/level.dat");
        File tempLevelDatFile = new File("world_temp\\level_" + "server_id_" + CurrentServerInfo.serverId + ".dat");

        try {
            try (FileChannel sourceChannel = FileChannel.open(levelDat.toPath(), StandardOpenOption.READ);
                 FileChannel destChannel = FileChannel.open(tempLevelDatFile.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                sourceChannel.transferTo(0, sourceChannel.size(), destChannel);
            }

            return tempLevelDatFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ServerMinecraftWorld(File worldPath) throws IOException {
        super(worldPath);
//        CurrentServerInfo.world.levelDat = getLevelDatFile(worldPath);
//        CurrentServerInfo.world.lastPlayedDate = getLastPlayedDate();
        CurrentServerInfo.world.path = worldPath;
//        CurrentServerInfo.world.levelName = getLevelNameColors();
    }
}
