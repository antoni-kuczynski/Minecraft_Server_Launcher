package com.myne145.serverlauncher.server.current;

import java.io.File;
import java.util.Calendar;

public class CurrentServerWorld {
    public Calendar lastPlayedDate;
    public String levelName;
    public File levelDat;
    public File path;

    public CurrentServerWorld(Calendar lastPlayedDate, String levelName, File levelDat, File path) {
//        this.lastPlayedDate = lastPlayedDate;
//        this.levelName = levelName;
        this.levelDat = levelDat;
        this.path = path;
    }

//    public Calendar getLastPlayedDate() {
//        return lastPlayedDate;
//    }

//    public String getLevelName() {
//        return levelName;
//    }

    public File getLevelDat() {
        return levelDat;
    }

    public File getPath() {
        return path;
    }
}
