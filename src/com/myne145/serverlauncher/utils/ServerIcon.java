package com.myne145.serverlauncher.utils;

import com.myne145.serverlauncher.gui.window.Window;
import com.myne145.serverlauncher.server.Config;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

import static com.myne145.serverlauncher.gui.window.Window.*;

public enum ServerIcon {
    ONLINE(ServerIcons.online),
    OFFLINE(ServerIcons.offline),
    ERRORED(ServerIcons.errored);
    private final ImageIcon icon;
    ServerIcon(ImageIcon icon) {
        this.icon = icon;
    }

    public static ImageIcon getServerIcon(ServerIcon iconType) {
        return new ImageIcon(iconType.icon.getImage().getScaledInstance(SERVER_STATUS_ICON_DIMENSION, SERVER_STATUS_ICON_DIMENSION, Image.SCALE_SMOOTH));
    }
}

class ServerIcons {
    static ImageIcon online;
    static ImageIcon offline;
    static ImageIcon errored;
    static {
        try {
            online = new ImageIcon(ImageIO.read(Config.getResource(Config.RESOURCES_PATH + "/server_online.png")));
            offline = new ImageIcon(ImageIO.read(Config.getResource(Config.RESOURCES_PATH + "/server_offline.png")));
            errored = new ImageIcon(ImageIO.read(Config.getResource(Config.RESOURCES_PATH + "/server_errored.png")));
        } catch (Exception e) {
            alert(AlertType.ERROR, getErrorDialogMessage(e));
        }
    }
}