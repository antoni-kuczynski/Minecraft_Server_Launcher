package com.myne145.serverlauncher.Server.Current;

import com.myne145.serverlauncher.Gui.AlertType;
import dev.dewy.nbt.Nbt;
import dev.dewy.nbt.tags.collection.CompoundTag;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

import static com.myne145.serverlauncher.Gui.Window.alert;
import static com.myne145.serverlauncher.Gui.Window.getErrorDialogMessage;

public class NBTParser extends Thread {
    private String levelName;
    public NBTParser() {

    }



    @Override
    public void run() {
        //even better issue #68, issue #76 and issue #75 fix, now file names use server ids not names
        File pathToCopiedLevelDat = new File("world_temp\\level_" + "server_id_" + CurrentServerInfo.serverId + "_" + ".dat");
        if(!CurrentServerInfo.serverLevelDatFile.exists()) {
            CurrentServerInfo.serverLevelName = "Level.dat file not found";
            return;
        }
        try {
            FileUtils.copyFile(CurrentServerInfo.serverLevelDatFile, pathToCopiedLevelDat);
        } catch (Exception e) {
            if(!e.toString().contains("The process cannot access the file because it is being used by another process")) //fuck this shit - issue #74 and #73 fixed
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
        CurrentServerInfo.serverLevelName = this.getLevelName(); //issue #64 fix
    }

    public String getLevelName() {
        return LevelNameColorConverter.convertColors(levelName);
    }
}
