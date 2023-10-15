//package com.myne145.serverlauncher.server.current;
//
//import com.myne145.serverlauncher.utils.AlertType;
//import dev.dewy.nbt.Nbt;
//import dev.dewy.nbt.tags.collection.CompoundTag;
//import org.apache.commons.io.FileUtils;
//
//import javax.swing.*;
//import java.io.File;
//import java.io.IOException;
//import java.util.Calendar;
//import java.util.Date;
//
//import static com.myne145.serverlauncher.gui.window.Window.alert;
//import static com.myne145.serverlauncher.gui.window.Window.getErrorDialogMessage;
//
//@Deprecated
//public class NBTParser extends Thread {
//    private String levelName;
//    private Calendar lastPlayedDate;
//    private File pathToLevelDat;
//    private boolean isServerMode;
//
//    private NBTParser() {
//
//    }
//
//    public static NBTParser createServerNBTParser() {
//        NBTParser nbtParser = new NBTParser();
//        nbtParser.pathToLevelDat = new File("world_temp\\level_" + "server_id_" + CurrentServerInfo.serverId + "_" + ".dat");
//        return nbtParser;
//    }
//
//    public static NBTParser createAddedWorldNBTParser(String extractedWorldDirectory) {
//        NBTParser nbtParser = new NBTParser();
//        nbtParser.pathToLevelDat = new File("world_temp\\level_" + "server_id_" + CurrentServerInfo.serverId + "_" + ".dat");
//        return nbtParser;
//    }
//
//    @Override
//    public void run() {
//
//        if(!CurrentServerInfo.world.getLevelDat().exists()) {
//            CurrentServerInfo.world.levelName = "Level.dat file not found";
//            return;
//        }
//        try {
//            FileUtils.copyFile(CurrentServerInfo.world.getLevelDat(), pathToLevelDat);
//        } catch (Exception e) {
//            if(!e.toString().contains("The process cannot access the file because it is being used by another process"))
//                SwingUtilities.invokeLater(() -> alert(AlertType.ERROR, "Cannot copy level.dat file." + getErrorDialogMessage(e)));
//        }
//        Nbt levelDat = new Nbt();
//        CompoundTag layerOne = null;
//        try {
//            if(pathToLevelDat.exists())
//                layerOne = levelDat.fromFile(pathToLevelDat);
//        } catch (IOException e) {
//            SwingUtilities.invokeLater(() -> alert(AlertType.ERROR, getErrorDialogMessage(e)));
//            return;
//        }
//        if(layerOne == null)
//            return;
//
//        CompoundTag levelDatContent = layerOne.get("Data");
//        this.levelName = String.valueOf(levelDatContent.get("LevelName")).split("\"")[1];
//
//
//        Date lastPlayedDateGMT = new Date(levelDatContent.getLong("LastPlayed").getValue());
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(lastPlayedDateGMT);
//        lastPlayedDate = calendar;
//
//        CurrentServerInfo.world.levelName = this.getLevelName(); //issue #64 fix
//    }
//
//    public String getLevelName() {
//        return LevelNameColorConverter.convertColors(levelName);
//    }
//
//    public Calendar getLastPlayedDate() {
//        return lastPlayedDate;
//    }
//}
