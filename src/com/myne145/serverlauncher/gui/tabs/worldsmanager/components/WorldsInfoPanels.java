package com.myne145.serverlauncher.gui.tabs.worldsmanager.components;

import com.formdev.flatlaf.ui.FlatLineBorder;
//import com.myne145.serverlauncher.gui.tabs.worldsmanager.nbt.ClientMinecraftWorld;
import com.myne145.serverlauncher.gui.tabs.worldsmanager.nbt.MinecraftWorld;
//import com.myne145.serverlauncher.gui.tabs.worldsmanager.nbt.ServerMinecraftWorld;
import com.myne145.serverlauncher.server.MCServer;
import com.myne145.serverlauncher.utils.Colors;
import com.myne145.serverlauncher.utils.DefaultIcons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

public class WorldsInfoPanels extends JPanel {
//    private ImageIcon DEFAULT_WORLD_ICON_PACK_PNG;
    private final WorldInformationPanel clientWorldInfo;
    private final WorldInformationPanel serverWorldInfo;
    private final MCServer server;
//    private final int tabIndex;
    private final WorldsContextMenu clientWorldContextMenu = new WorldsContextMenu();
    private final WorldsContextMenu serverWorldContextMenu = new WorldsContextMenu();

    public WorldsInfoPanels(MCServer server) {
//        this.tabIndex = tabIndex;
        this.server = server;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        clientWorldInfo = createInformationPanel("World to import (to replace with)", false);
        serverWorldInfo = createInformationPanel("Server world (to be replaced)", true);

        add(clientWorldInfo.infoPanel);
        add(Box.createVerticalStrut(30));
        add(serverWorldInfo.infoPanel);

        clientWorldInfo.infoPanel.addMouseListener(getMouseListener(clientWorldInfo.infoPanel, clientWorldContextMenu));
        serverWorldInfo.infoPanel.addMouseListener(getMouseListener(serverWorldInfo.infoPanel, serverWorldContextMenu));
    }

    private MouseListener getMouseListener(JPanel panel, WorldsContextMenu contextMenu) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON3)
                    contextMenu.showContextMenu(e, panel);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON3)
                    contextMenu.showContextMenu(e, panel);
            }
        };
    }

    public void updateClientWorldInformation(File worldPath) {
        MinecraftWorld clientWorld = new MinecraftWorld(worldPath);

        updatePanel(clientWorldInfo, clientWorld);
        clientWorldContextMenu.updateDirectory(worldPath);
    }

    public void updateServerWorldInformation() {
        server.getServerWorld().update(server.getTempLevelDat());
        updatePanel(serverWorldInfo, server.getServerWorld());
        serverWorldContextMenu.updateDirectory(server.getWorldPath());
    }



    private void updatePanel(WorldInformationPanel worldInformationPanel, MinecraftWorld minecraftWorld) {
        if(!minecraftWorld.hasLevelDat()) {
            worldInformationPanel.worldInformationLabel.setText("<html><font size=4>Level.dat file not found!</font></html>");
            worldInformationPanel.worldInformationLabel.setIcon(DefaultIcons.getServerPlatformIcon(DefaultIcons.WORLD_MISSING));
            return;
        }


        String details = "<html><font size=4><b>" + minecraftWorld.getLevelNameColors() +"</b>" +
                "<br>"+ minecraftWorld.getFolderName() + " (" + minecraftWorld.getFormattedDate() + ")" +
                "<br>" + minecraftWorld.getGamemode() + " Mode, " + (minecraftWorld.isUsingCheats() ? "Cheats, " : "") + "Version: " + minecraftWorld.getGameVersion() + "</font></html>";
        worldInformationPanel.worldInformationLabel.setText(details);
        worldInformationPanel.worldInformationLabel.setIcon(minecraftWorld.getWorldIcon());
    }

    private WorldInformationPanel createInformationPanel(String title, boolean isServer) {
        JPanel worldPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel(title);
        JLabel iconAndWorldInfo = new JLabel("<html><font size=4>World details are going to appear here.</font></html>");

        worldPanel.setBorder(new FlatLineBorder(new Insets(10, 10, 10, 0), Colors.BORDER_COLOR, 1, 16));
        worldPanel.setBackground(Colors.COMPONENT_PRIMARY_COLOR);
        worldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        iconAndWorldInfo.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        iconAndWorldInfo.setIcon(DefaultIcons.getServerPlatformIcon(DefaultIcons.WORLD_MISSING));


        worldPanel.add(titleLabel, BorderLayout.PAGE_START);
        worldPanel.add(iconAndWorldInfo, BorderLayout.CENTER);

        return new WorldInformationPanel(worldPanel, iconAndWorldInfo, isServer);
    }

    private record WorldInformationPanel(JPanel infoPanel, JLabel worldInformationLabel, boolean isServerPanel){

    }
}
