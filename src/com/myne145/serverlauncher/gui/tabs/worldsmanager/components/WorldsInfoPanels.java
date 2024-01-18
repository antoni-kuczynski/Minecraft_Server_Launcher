package com.myne145.serverlauncher.gui.tabs.worldsmanager.components;

import com.formdev.flatlaf.ui.FlatLineBorder;
import com.myne145.serverlauncher.gui.tabs.worldsmanager.nbt.ClientMinecraftWorld;
import com.myne145.serverlauncher.gui.tabs.worldsmanager.nbt.MinecraftWorld;
import com.myne145.serverlauncher.gui.tabs.worldsmanager.nbt.ServerMinecraftWorld;
import com.myne145.serverlauncher.gui.window.Window;
import com.myne145.serverlauncher.server.Config;
import com.myne145.serverlauncher.utils.AlertType;
import com.myne145.serverlauncher.utils.Colors;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class WorldsInfoPanels extends JPanel {
    private ImageIcon DEFAULT_WORLD_ICON_PACK_PNG;
    private final WorldInformationPanel clientWorldInfo;
    private final WorldInformationPanel serverWorldInfo;
    private final int tabIndex;

    public WorldsInfoPanels(int tabIndex) {
        this.tabIndex = tabIndex;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        try {
            DEFAULT_WORLD_ICON_PACK_PNG = new ImageIcon(ImageIO.read(Window.getClassLoader().getResourceAsStream(Config.RESOURCES_PATH + "/default_world_icon.png")).getScaledInstance(96, 96, Image.SCALE_SMOOTH));
        } catch (IOException e) {
            Window.alert(AlertType.ERROR, Window.getErrorDialogMessage(e));
        }

        clientWorldInfo = createInformationPanel("World to import");
        serverWorldInfo = createInformationPanel("Server world (to be replaced)");

        add(clientWorldInfo.infoPanel);
        add(Box.createVerticalStrut(30));
        add(serverWorldInfo.infoPanel);
    }


    public void updateClientWorldInformation(File worldPath) {
        updatePanel(clientWorldInfo, worldPath, "CLIENT");
    }

    public void updateServerWorldInformation(File serverWorldPath) {
        updatePanel(serverWorldInfo, serverWorldPath, "SERVER");
    }



    private void updatePanel(WorldInformationPanel worldInformationPanel, File worldPath, String type) {
        MinecraftWorld minecraftWorld;
        try {
            if(type.equals("CLIENT"))
                minecraftWorld = new ClientMinecraftWorld(worldPath);
            else if(type.equals("SERVER"))
                minecraftWorld = new ServerMinecraftWorld(worldPath, tabIndex);
            else
                return;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if(minecraftWorld.getLevelNameNoColors() == null) {
            worldInformationPanel.worldInformationLabel.setText("<html><font size=4>Level.dat file not found!</font></html>");
            worldInformationPanel.worldInformationLabel.setIcon(DEFAULT_WORLD_ICON_PACK_PNG);
            return;
        }


        String AM_PM = "AM";
        if(minecraftWorld.getLastPlayedDate().get(Calendar.AM_PM) == Calendar.PM)
            AM_PM = "PM";

        String formattedDate = minecraftWorld.getLastPlayedDate().get(Calendar.YEAR) + "∕" +
                (minecraftWorld.getLastPlayedDate().get(Calendar.MONTH) + 1) + "∕" +
                minecraftWorld.getLastPlayedDate().get(Calendar.DAY_OF_MONTH) + ", " +
                minecraftWorld.getLastPlayedDate().get(Calendar.HOUR) + ":" + minecraftWorld.getLastPlayedDate().get(Calendar.MINUTE) + " " + AM_PM;

        String details = "<html><font size=4><b>" + minecraftWorld.getLevelNameColors() +"</b>" +
                "<br>"+ minecraftWorld.getFolderName() + " (" + formattedDate + ")" +
                "<br>" + minecraftWorld.getGamemode() + " Mode, " + (minecraftWorld.isUsingCheats() ? "Cheats, " : "") + "Version: " + minecraftWorld.getGameVersion() + "</font></html>";
        worldInformationPanel.worldInformationLabel.setText(details);
        worldInformationPanel.worldInformationLabel.setIcon(minecraftWorld.getWorldIcon());
    }
    private WorldInformationPanel createInformationPanel(String title) {
        JPanel worldPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel(title);
        JLabel iconAndWorldInfo = new JLabel("<html><font size=4>World details will appear here.</font></html>");

        worldPanel.setBorder(new FlatLineBorder(new Insets(10, 10, 10, 0), Colors.BORDER_COLOR, 1, 16));
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
