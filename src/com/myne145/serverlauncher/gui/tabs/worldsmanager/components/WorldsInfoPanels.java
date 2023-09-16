package com.myne145.serverlauncher.gui.tabs.worldsmanager.components;

import com.formdev.flatlaf.ui.FlatLineBorder;
import com.myne145.serverlauncher.gui.tabs.worldsmanager.nbt.MinecraftWorld;
import com.myne145.serverlauncher.server.Config;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Calendar;

public class WorldsInfoPanels extends JPanel {
    private final ImageIcon DEFAULT_WORLD_ICON_PACK_PNG = new ImageIcon(new ImageIcon(Config.RESOURCES_PATH + "/defaultworld.jpg").getImage().getScaledInstance(96, 96, Image.SCALE_SMOOTH));
    private final WorldInformationPanel clientWorldInfo;
    private final WorldInformationPanel serverWorldInfo;

    public WorldsInfoPanels() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        clientWorldInfo = createInformationPanel("Currently selected world");
        serverWorldInfo = createInformationPanel("Current server world");

        add(clientWorldInfo.infoPanel);
        add(Box.createVerticalStrut(30));
        add(serverWorldInfo.infoPanel);
    }


    public void updateClientWorldInformation(File worldPath) {
        updatePanel(clientWorldInfo, worldPath);
    }

    public void updateServerWorldInformation(File serverWorldPath) {
        updatePanel(serverWorldInfo, serverWorldPath);
    }



    private void updatePanel(WorldInformationPanel worldInformationPanel, File worldPath) {
        MinecraftWorld clientMCWorld;
        try {
            clientMCWorld = new MinecraftWorld(worldPath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if(clientMCWorld.getLevelNameNoColors() == null) {
            worldInformationPanel.worldInformationLabel.setText("<html><font size=4>Level.dat file not found!</font></html>");
            worldInformationPanel.worldInformationLabel.setIcon(DEFAULT_WORLD_ICON_PACK_PNG);
            return;
        }


        String AM_PM = "AM";
        if(clientMCWorld.getLastPlayedDate().get(Calendar.AM_PM) == Calendar.PM)
            AM_PM = "PM";

        String formattedDate = clientMCWorld.getLastPlayedDate().get(Calendar.YEAR) + "∕" +
                (clientMCWorld.getLastPlayedDate().get(Calendar.MONTH) + 1) + "∕" +
                clientMCWorld.getLastPlayedDate().get(Calendar.DAY_OF_MONTH) + ", " +
                clientMCWorld.getLastPlayedDate().get(Calendar.HOUR) + ":" + clientMCWorld.getLastPlayedDate().get(Calendar.MINUTE) + " " + AM_PM;

        String details = "<html><font size=4><b>" + clientMCWorld.getLevelNameColors() +"</b>" +
                "<br>"+ clientMCWorld.getFolderName() + " (" + formattedDate + ")" +
                "<br>" + clientMCWorld.getGamemode() + " Mode, " + (clientMCWorld.isUsingCheats() ? "Cheats, " : "") + "Version: " + clientMCWorld.getGameVersion() + "</font></html>";
        worldInformationPanel.worldInformationLabel.setText(details);
        worldInformationPanel.worldInformationLabel.setIcon(clientMCWorld.getWorldIcon());
    }
    private WorldInformationPanel createInformationPanel(String title) {
        JPanel worldPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel(title);
        JLabel iconAndWorldInfo = new JLabel("<html><font size=4>World details will appear here.</font></html>");

        worldPanel.setBorder(new FlatLineBorder(new Insets(10, 10, 10, 0), new Color(44, 44, 44), 1, 32));
        worldPanel.setBackground(new Color(60, 63, 65));
        worldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        iconAndWorldInfo.setIcon(DEFAULT_WORLD_ICON_PACK_PNG);

        worldPanel.add(titleLabel, BorderLayout.PAGE_START);
        worldPanel.add(iconAndWorldInfo, BorderLayout.CENTER);

        return new WorldInformationPanel(worldPanel, iconAndWorldInfo);
    }

    private record WorldInformationPanel(JPanel infoPanel, JLabel worldInformationLabel){

    }
}
