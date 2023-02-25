package Gui;

import Servers.Config;
import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Frame extends JFrame {

    public Frame() {


        // Set up the JFrame
        setIconImage(new ImageIcon("app_icon.png").getImage());
        setTitle("Minecraft Server Server.Launcher V2");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the JPanels
        TitlePanel titlePanel = new TitlePanel();
        ButtonPanel buttonPanel = new ButtonPanel();
        ConfigStuffPanel configStuffPanel = new ConfigStuffPanel();

        // Add the JPanel to the JFrame's BorderLayout.CENTER
        add(titlePanel, BorderLayout.PAGE_START);
        add(buttonPanel, BorderLayout.CENTER);
        add(configStuffPanel, BorderLayout.PAGE_END);

        // Set the JFrame size and make it visible
        setSize(500, 700);
        setVisible(true);
    }

    public static void main(String[] args) throws IOException {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf() );
        } catch( Exception ex ) {
            System.err.println( "Failed to initialize LaF" );
        }
        new Frame();
        new Config();
    }
}
