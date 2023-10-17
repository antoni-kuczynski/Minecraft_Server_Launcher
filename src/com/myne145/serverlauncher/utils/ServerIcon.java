package com.myne145.serverlauncher.utils;

import com.myne145.serverlauncher.server.Config;

import javax.swing.*;
import java.awt.*;

import static com.myne145.serverlauncher.gui.window.Window.SERVER_STATUS_ICON_DIMENSION;

public enum ServerIcon {
    ONLINE(ServerIcons.online),
    OFFLINE(ServerIcons.offline),
    ERRORED(ServerIcons.errored);
    private final ImageIcon icon;
    ServerIcon(ImageIcon icon) {
        this.icon = icon;
    }

    public static ImageIcon getServerIcon(ServerIcon iconType) {
        return iconType.icon;
    }
}

class ServerIcons {
    static ImageIcon online = new ImageIcon(new ImageIcon(Config.RESOURCES_PATH + "/server_online.png").getImage().getScaledInstance(SERVER_STATUS_ICON_DIMENSION, SERVER_STATUS_ICON_DIMENSION, Image.SCALE_SMOOTH));
    static ImageIcon offline = new ImageIcon(new ImageIcon(Config.RESOURCES_PATH + "/server_offline.png").getImage().getScaledInstance(SERVER_STATUS_ICON_DIMENSION, SERVER_STATUS_ICON_DIMENSION, Image.SCALE_SMOOTH));
    static ImageIcon errored = new ImageIcon(new ImageIcon(Config.RESOURCES_PATH + "/server_errored.png").getImage().getScaledInstance(SERVER_STATUS_ICON_DIMENSION, SERVER_STATUS_ICON_DIMENSION, Image.SCALE_SMOOTH));
}
