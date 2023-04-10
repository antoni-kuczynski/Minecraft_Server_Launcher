package SelectedServer;

import Gui.AlertType;
import dev.dewy.nbt.Nbt;
import dev.dewy.nbt.tags.collection.CompoundTag;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

import static Gui.Frame.alert;
import static Gui.Frame.getErrorDialogMessage;

public class NBTParser extends Thread {
    private String levelName;
    //contructor for server world
    public NBTParser() {

    }

    @Override
    public void run() {
        System.out.println("Level.dat file location: " + ServerDetails.serverLevelDatFile);
        File pathToCopiedLevelDat = new File("world_temp\\level_" + ServerDetails.serverName + ".dat");
        System.out.println("Path to copied level.dat: " + pathToCopiedLevelDat);
        try {
            if(new File(ServerDetails.serverLevelDatFile).exists())
                FileUtils.copyFile(new File(ServerDetails.serverLevelDatFile), pathToCopiedLevelDat);
            else
                ServerDetails.serverLevelName = "Level.dat file not found";
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> alert(AlertType.ERROR, "Cannot copy level.dat file." + getErrorDialogMessage(e))); //issue #61 fix, it was conflicting with ui dispatch thread
        }
        Nbt levelDat = new Nbt();
        CompoundTag layerOne = null;
        try {
            if(pathToCopiedLevelDat.exists())
                layerOne = levelDat.fromFile(pathToCopiedLevelDat);
        } catch (IOException e) {
            SwingUtilities.invokeLater(() -> alert(AlertType.ERROR, getErrorDialogMessage(e))); //issue #61 fix, it was conflicting with ui dispatch thread
            return;
        }
        if(layerOne != null) {
            CompoundTag levelDatContent = layerOne.get("Data");
            this.levelName = String.valueOf(levelDatContent.get("LevelName")).split("\"")[1];
        }
        System.out.println("Level name: " + levelName);
        System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
    }

    public String getLevelName() {
        return LevelNameColorConverter.convertColors(levelName);
    }
}
