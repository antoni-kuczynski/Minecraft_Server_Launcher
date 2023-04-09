package SelectedServer;

import Gui.AlertType;
import dev.dewy.nbt.Nbt;
import dev.dewy.nbt.tags.collection.CompoundTag;

import java.io.File;

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
        Nbt levelDat = new Nbt();
        CompoundTag layerOne = null;
        try {
            layerOne = levelDat.fromFile(new File(ServerDetails.serverLevelDatFile));
        } catch (Exception e) {
//            alert(AlertType.ERROR, exStackTraceToString(e.getStackTrace()));
//            return;
        }
        CompoundTag levelDatContent = layerOne.get("Data");
        String levelName = String.valueOf(levelDatContent.get("LevelName")).split("\"")[1];
        this.levelName = levelName;
        System.out.println("Level name: " + levelName);
        System.out.println();
    }

    public String getLevelName() {
        return levelName;
    }
}
