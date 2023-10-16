package com.myne145.serverlauncher.utils;

import com.myne145.serverlauncher.server.Config;

import javax.swing.*;
import java.awt.*;

import static com.myne145.serverlauncher.gui.window.Window.SERVER_STATUS_ICON_DIMENSION;

public enum ServerIcon {
    ONLINE, //cant assign values here - it breaks the button scale option
    OFFLINE,
    ERRORED;

    public static ImageIcon getServerIcon(ServerIcon iconType) {
        if(iconType == ServerIcon.ONLINE)
            return new ImageIcon(new ImageIcon(Config.RESOURCES_PATH + "/server_online.png").getImage().getScaledInstance(SERVER_STATUS_ICON_DIMENSION, SERVER_STATUS_ICON_DIMENSION, Image.SCALE_SMOOTH));
        else if (iconType == ServerIcon.OFFLINE) {
            return new ImageIcon(new ImageIcon(Config.RESOURCES_PATH + "/server_offline.png").getImage().getScaledInstance(SERVER_STATUS_ICON_DIMENSION, SERVER_STATUS_ICON_DIMENSION, Image.SCALE_SMOOTH));
        } else {
            return new ImageIcon(new ImageIcon(Config.RESOURCES_PATH + "/server_errored.png").getImage().getScaledInstance(SERVER_STATUS_ICON_DIMENSION, SERVER_STATUS_ICON_DIMENSION, Image.SCALE_SMOOTH));
        }
    }
}
