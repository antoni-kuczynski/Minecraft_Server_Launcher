package com.myne145.serverlauncher.utils;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.myne145.serverlauncher.server.Config;
import com.myne145.serverlauncher.server.ServerPlatform;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.LinkedHashMap;

import static com.myne145.serverlauncher.gui.window.Window.*;

public enum DefaultIcons {
    SERVER_ONLINE(DefaultIcon.serverOnline),
    SERVER_OFFLINE(DefaultIcon.serverOffline),
    SERVER_ERRORED(DefaultIcon.serverErrored),
    ERROR(DefaultIcon.error),
    WORLD_MISSING(DefaultIcon.defaultWorld),
    APP_ICON(DefaultIcon.appIcon);
    private ImageIcon icon;
    private FlatSVGIcon svgIcon;

    DefaultIcons(ImageIcon icon) {
        this.icon = icon;
    }

    DefaultIcons(FlatSVGIcon icon) {
        this.svgIcon = icon;
    }

    public static ImageIcon getIcon(DefaultIcons iconType) {
        if(iconType == DefaultIcons.SERVER_OFFLINE || iconType == DefaultIcons.SERVER_ONLINE || iconType == DefaultIcons.SERVER_ERRORED)
            return new ImageIcon(iconType.icon.getImage().getScaledInstance(SERVER_STATUS_ICON_DIMENSION, SERVER_STATUS_ICON_DIMENSION, Image.SCALE_SMOOTH));
        else
            return iconType.icon;
    }

    public static ImageIcon getIcon(ServerPlatform iconType) {
        return DefaultIcon.serverPlatformIcons.get(iconType);
    }

    public static FlatSVGIcon getSVGIcon(DefaultIcons iconType) {
        return iconType.svgIcon;
    }
}

class DefaultIcon {
    static ImageIcon serverOnline;
    static ImageIcon serverOffline;
    static ImageIcon serverErrored;
    static ImageIcon defaultWorld;
    static ImageIcon appIcon;
    static LinkedHashMap<ServerPlatform, ImageIcon> serverPlatformIcons = new LinkedHashMap<>();

    static FlatSVGIcon error;
    static {
        try {
            serverOnline = new ImageIcon(ImageIO.read(Config.getResource(Config.RESOURCES_PATH + "/server_online.png")));
            serverOffline = new ImageIcon(ImageIO.read(Config.getResource(Config.RESOURCES_PATH + "/server_offline.png")));
            serverErrored = new ImageIcon(ImageIO.read(Config.getResource(Config.RESOURCES_PATH + "/server_errored.png")));
            defaultWorld = new ImageIcon(ImageIO.read(Config.getResource(Config.RESOURCES_PATH + "/default_world_icon.png")).getScaledInstance(96,96, Image.SCALE_SMOOTH));
            appIcon = new ImageIcon(ImageIO.read(Config.getResource(Config.RESOURCES_PATH + "/app_icon.png")).getScaledInstance(96,96, Image.SCALE_SMOOTH));


            serverPlatformIcons.put(ServerPlatform.PAPER_MC,
                    new ImageIcon(ImageIO.read(Config.getResource(Config.RESOURCES_PATH + "/server_platforms/papermc.png")).getScaledInstance(96,96, Image.SCALE_SMOOTH)));
            serverPlatformIcons.put(ServerPlatform.FORGE,
                    new ImageIcon(ImageIO.read(Config.getResource(Config.RESOURCES_PATH + "/server_platforms/forge.png")).getScaledInstance(96,96, Image.SCALE_SMOOTH)));
            serverPlatformIcons.put(ServerPlatform.BUKKIT,
                    new ImageIcon(ImageIO.read(Config.getResource(Config.RESOURCES_PATH + "/server_platforms/craftbukkit.png")).getScaledInstance(96,96, Image.SCALE_SMOOTH)));
            serverPlatformIcons.put(ServerPlatform.SPIGOT,
                    new ImageIcon(ImageIO.read(Config.getResource(Config.RESOURCES_PATH + "/server_platforms/spigotmc.png")).getScaledInstance(96,96, Image.SCALE_SMOOTH)));
            serverPlatformIcons.put(ServerPlatform.VANILLA,
                    new ImageIcon(ImageIO.read(Config.getResource(Config.RESOURCES_PATH + "/server_platforms/vanilla.png")).getScaledInstance(96,96, Image.SCALE_SMOOTH)));
            serverPlatformIcons.put(ServerPlatform.FABRIC,
                    new ImageIcon(ImageIO.read(Config.getResource(Config.RESOURCES_PATH + "/server_platforms/fabricmc.png")).getScaledInstance(96,96, Image.SCALE_SMOOTH)));
            serverPlatformIcons.put(ServerPlatform.UNKNOWN,
                    new ImageIcon(ImageIO.read(Config.getResource(Config.RESOURCES_PATH + "/server_platforms/papermc.png")).getScaledInstance(96,96, Image.SCALE_SMOOTH)));

            error = new FlatSVGIcon(Config.RESOURCES_PATH + "/error.svg", Config.classLoader);
        } catch (IOException e) {
            showErrorMessage("I/O errors reading assets.", e);
        }
    }
}