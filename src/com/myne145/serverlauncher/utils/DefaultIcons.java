package com.myne145.serverlauncher.utils;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.myne145.serverlauncher.server.Config;
import com.myne145.serverlauncher.server.ServerPlatform;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import static com.myne145.serverlauncher.gui.window.Window.*;

public enum DefaultIcons {
    SERVER_ONLINE(DefaultIcon.serverOnline),
    SERVER_OFFLINE(DefaultIcon.serverOffline),
    SERVER_ERRORED(DefaultIcon.serverErrored),
    ERROR(DefaultIcon.error),
    WORLD_MISSING(DefaultIcon.defaultWorld),
    APP_ICON(DefaultIcon.appIcon),
    ADD_SERVER(DefaultIcon.addServer);

    private ImageIcon icon;
    private FlatSVGIcon svgIcon;

    DefaultIcons(ImageIcon icon) {
        this.icon = icon;
    }

    DefaultIcons(FlatSVGIcon icon) {
        this.svgIcon = icon;
    }

    public static ImageIcon getIcon(DefaultIcons iconType) {
        return iconType.icon;
    }

    public static FlatSVGIcon getServerPlatformIcon(ServerPlatform iconType) {
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

    static FlatSVGIcon paperMc;
//    static FlatSVGIcon paperMcTest;
    static FlatSVGIcon forge;
    static FlatSVGIcon craftbukkit;
    static FlatSVGIcon spigotMc;
    static FlatSVGIcon vanilla;
    static FlatSVGIcon fabricMc;
    static FlatSVGIcon unknownPlatform;
//    static LinkedHashMap<ServerPlatform, ImageIcon> serverPlatformIcons = new LinkedHashMap<>();

    static FlatSVGIcon addServer;
    static FlatSVGIcon error;

    protected static void loadPlatformIcons() throws IOException {
//        paperMc = new ImageIcon(ImageIO.read(Config.getResource(Config.RESOURCES_PATH + "/server_platforms/papermc.png")));
        paperMc = new FlatSVGIcon(Config.RESOURCES_PATH + "/server_platforms/papermc.svg", Config.classLoader);


//        forge = new ImageIcon(ImageIO.read(Config.getResource(Config.RESOURCES_PATH + "/server_platforms/forge.png")));
        forge = new FlatSVGIcon(Config.RESOURCES_PATH + "/server_platforms/forge.svg", Config.classLoader);

//        craftbukkit = new ImageIcon(ImageIO.read(Config.getResource(Config.RESOURCES_PATH + "/server_platforms/craftbukkit.png")));
        craftbukkit = new FlatSVGIcon(Config.RESOURCES_PATH + "/server_platforms/craftbukkit.svg", Config.classLoader);

//        spigotMc = new ImageIcon(ImageIO.read(Config.getResource(Config.RESOURCES_PATH + "/server_platforms/spigotmc.png")));
        spigotMc = new FlatSVGIcon(Config.RESOURCES_PATH + "/server_platforms/spigotmc.svg", Config.classLoader);

//        vanilla = new ImageIcon(ImageIO.read(Config.getResource(Config.RESOURCES_PATH + "/server_platforms/vanilla.png")));
        vanilla = new FlatSVGIcon(Config.RESOURCES_PATH + "/server_platforms/vanilla.svg", Config.classLoader);

//        fabricMc = new ImageIcon(ImageIO.read(Config.getResource(Config.RESOURCES_PATH + "/server_platforms/fabricmc.png")));
        vanilla = new FlatSVGIcon(Config.RESOURCES_PATH + "/server_platforms/vanilla.svg", Config.classLoader);

        unknownPlatform = new FlatSVGIcon(Config.RESOURCES_PATH + "/server_platforms/papermc.svg", Config.classLoader); //TODO remove placeholder here

    }

    static {
        try {
            serverOnline = new ImageIcon(ImageIO.read(Config.getResource(Config.RESOURCES_PATH + "/server_online.png")));
            serverOffline = new ImageIcon(ImageIO.read(Config.getResource(Config.RESOURCES_PATH + "/server_offline.png")));
            serverErrored = new ImageIcon(ImageIO.read(Config.getResource(Config.RESOURCES_PATH + "/server_errored.png")));
            defaultWorld = new ImageIcon(ImageIO.read(Config.getResource(Config.RESOURCES_PATH + "/default_world_icon.png")));
            appIcon = new ImageIcon(ImageIO.read(Config.getResource(Config.RESOURCES_PATH + "/app_icon.png")));
//            appIcon = new FlatSVGIcon(Config.RESOURCES_PATH + "/icon_test.svg", Config.classLoader);


            error = new FlatSVGIcon(Config.RESOURCES_PATH + "/error.svg", Config.classLoader);
            addServer = new FlatSVGIcon(Config.RESOURCES_PATH + "/add_server.svg", Config.classLoader);
//            addServer = new FlatSVGIcon(Config.RESOURCES_PATH + "/add_server.svg", Config.classLoader);
        } catch (IOException e) {
            showErrorMessage("I/O errors reading assets.", e);
        }
    }
}