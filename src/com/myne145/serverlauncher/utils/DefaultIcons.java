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
        if(DefaultIcon.paperMc == null) {
            try {
                DefaultIcon.loadPlatformIcons();
            } catch (IOException e) {
                showErrorMessage("I/O errors reading assets.", e);
            }
        }
        switch (iconType) {
            case PAPER_MC -> {
                return DefaultIcon.paperMc;
            }
            case  FORGE-> {
                return DefaultIcon.forge;
            }
            case BUKKIT -> {
                return DefaultIcon.craftbukkit;
            }
            case SPIGOT -> {
                return DefaultIcon.spigotMc;
            }
            case VANILLA -> {
                return DefaultIcon.vanilla;
            }
            case FABRIC -> {
                return DefaultIcon.fabricMc;
            }
            default -> {
                return DefaultIcon.unknownPlatform;
            }
        }
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

    static ImageIcon paperMc;
    static ImageIcon forge;
    static ImageIcon craftbukkit;
    static ImageIcon spigotMc;
    static ImageIcon vanilla;
    static ImageIcon fabricMc;
    static ImageIcon unknownPlatform;
//    static LinkedHashMap<ServerPlatform, ImageIcon> serverPlatformIcons = new LinkedHashMap<>();

    static FlatSVGIcon error;

    protected static void loadPlatformIcons() throws IOException {
        paperMc = new ImageIcon(ImageIO.read(Config.getResource(Config.RESOURCES_PATH + "/server_platforms/papermc.png")));
        forge = new ImageIcon(ImageIO.read(Config.getResource(Config.RESOURCES_PATH + "/server_platforms/forge.png")));
        craftbukkit = new ImageIcon(ImageIO.read(Config.getResource(Config.RESOURCES_PATH + "/server_platforms/craftbukkit.png")));
        spigotMc = new ImageIcon(ImageIO.read(Config.getResource(Config.RESOURCES_PATH + "/server_platforms/spigotmc.png")));
        vanilla = new ImageIcon(ImageIO.read(Config.getResource(Config.RESOURCES_PATH + "/server_platforms/vanilla.png")));
        fabricMc = new ImageIcon(ImageIO.read(Config.getResource(Config.RESOURCES_PATH + "/server_platforms/fabricmc.png")));
        unknownPlatform = new ImageIcon(ImageIO.read(Config.getResource(Config.RESOURCES_PATH + "/server_platforms/papermc.png"))); //TODO remove placeholder here
    }

    static {
        try {
            serverOnline = new ImageIcon(ImageIO.read(Config.getResource(Config.RESOURCES_PATH + "/server_online.png")));
            serverOffline = new ImageIcon(ImageIO.read(Config.getResource(Config.RESOURCES_PATH + "/server_offline.png")));
            serverErrored = new ImageIcon(ImageIO.read(Config.getResource(Config.RESOURCES_PATH + "/server_errored.png")));
            defaultWorld = new ImageIcon(ImageIO.read(Config.getResource(Config.RESOURCES_PATH + "/default_world_icon.png")));
            appIcon = new ImageIcon(ImageIO.read(Config.getResource(Config.RESOURCES_PATH + "/app_icon.png")));

            error = new FlatSVGIcon(Config.RESOURCES_PATH + "/error.svg", Config.classLoader);
        } catch (IOException e) {
            showErrorMessage("I/O errors reading assets.", e);
        }
    }
}