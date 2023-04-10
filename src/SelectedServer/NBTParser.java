package SelectedServer;

import Gui.AlertType;
import Gui.Frame;
import dev.dewy.nbt.Nbt;
import dev.dewy.nbt.tags.collection.CompoundTag;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import static Gui.Frame.alert;
import static Gui.Frame.exStackTraceToString;

public class NBTParser extends Thread {
    private String levelName;
    //contructor for server world
    public NBTParser() {

    }

    @Override
    public void run() {
        System.out.println("Level.dat file location: " + ServerDetails.serverLevelDatFile);
        System.out.println("Server Path: " + ServerDetails.serverPath);
        System.out.println("Server World Path: " + ServerDetails.serverWorldPath);
        System.out.println("Selected Server: " + ServerDetails.serverName);
        File pathToCopiedLevelDat = new File("world_temp\\level_" + ServerDetails.serverName + ".dat");
        try {
            FileUtils.copyFile(new File(ServerDetails.serverLevelDatFile), pathToCopiedLevelDat);
        } catch (IOException e) {
            alert(AlertType.ERROR, "Cannot copy level.dat file." + exStackTraceToString(e.getStackTrace()));
        }
        Nbt levelDat = new Nbt();
        CompoundTag layerOne = null;
        try {
            System.out.println(new File(ServerDetails.serverLevelDatFile));
            System.out.println(pathToCopiedLevelDat);
            layerOne = levelDat.fromFile(pathToCopiedLevelDat);
        } catch (IOException e) {
//            if(Frame.isFrameInitialized)
//                alert(AlertType.ERROR, exStackTraceToString(e.getStackTrace()));
//            return;
        }
        if(layerOne != null) {
            CompoundTag levelDatContent = layerOne.get("Data");
            this.levelName = String.valueOf(levelDatContent.get("LevelName")).split("\"")[1];
        }
        System.out.println("Level name: " + levelName);
        System.out.println();
    }

    public String getLevelName() {
        return levelName;
    }
}
