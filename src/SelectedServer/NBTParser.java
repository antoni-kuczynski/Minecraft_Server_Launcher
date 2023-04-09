package SelectedServer;

import Gui.AlertType;
import Gui.Frame;
import Server.WorldCopyHandler;
import dev.dewy.nbt.Nbt;
import dev.dewy.nbt.tags.collection.CompoundTag;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static Gui.Frame.alert;
import static Gui.Frame.exStackTraceToString;

public class NBTParser extends Thread {
    private String levelName;

    //contructor for server world
    public NBTParser() {

    }

    //prevent from extracting file to existing directory
    private File findFutureExtractDirectory(File startDir) {
        File finalPath = startDir;
        int numberAfterDirectory = 1;
        if(!startDir.exists())
            return startDir;
        while(startDir.exists()) {
            finalPath = new File(startDir.getAbsolutePath() + "_" + numberAfterDirectory);
        }
        return finalPath;
    }

    public static String extractArchive(String archivePath, String destinationPath) throws IOException {
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(archivePath));
        ZipEntry zipEntry = zis.getNextEntry();

        String extractedDirectory = null;

        while (zipEntry != null) {
            File newFile = new File(destinationPath, zipEntry.getName());
            if (zipEntry.isDirectory()) {
                if(!newFile.mkdirs()) {
                    alert(AlertType.ERROR, "Cannot create a directory.\nWorldCopyHandler.java extractingArchive()");
                    break;
                }
                if (extractedDirectory == null) {
                    extractedDirectory = newFile.getAbsolutePath();
                }
            } else {
                newFile.getParentFile().mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();

        return extractedDirectory;
    }

    @Override
    public void run() {
        System.out.println("Level.dat file location: " + ServerDetails.serverLevelDatFile);
        System.out.println("Selected Server: " + ServerDetails.serverName);
        Nbt levelDat = new Nbt();
        CompoundTag layerOne = null;
        try {
            layerOne = levelDat.fromFile(new File(ServerDetails.serverLevelDatFile));

        } catch (Exception e) {
            alert(AlertType.ERROR, exStackTraceToString(e.getStackTrace()));
        }
        CompoundTag levelDatContent = layerOne.get("Data");
        String levelName = String.valueOf(levelDatContent.get("LevelName")).split("\"")[1];
        System.out.println(levelName);
    }

    public String getLevelName() {
        return levelName;
    }
}
