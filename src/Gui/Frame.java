package Gui;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.prefs.Preferences;

public class Frame extends JFrame {

    private final String PREFS_KEY_X = "window_x";
    private final String PREFS_KEY_Y = "window_y";
    private final String PREFS_KEY_WIDTH = "window_width";
    private final String PREFS_KEY_HEIGHT = "window_height";

    public Frame() throws IOException {


        // Set up the JFrame
        setIconImage(new ImageIcon("app_icon.png").getImage());
        setTitle("Minecraft Server Server.Launcher V2");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the JPanels
        TitlePanel titlePanel = new TitlePanel();
        ButtonPanel buttonPanel = new ButtonPanel();
        ConfigStuffPanel configStuffPanel = new ConfigStuffPanel();
        configStuffPanel.setPanel(configStuffPanel);

        //Empty Panels (one panel doesn't work) WTF!!!
        JPanel emptyPanel1 = new JPanel();
        JPanel emptyPanel2 = new JPanel();
        JPanel emptyPanel3 = new JPanel();
        JPanel emptyPanel4 = new JPanel();
        JPanel emptyPanel5 = new JPanel();
        emptyPanel1.setPreferredSize(new Dimension(10, 50));
        emptyPanel2.setPreferredSize(new Dimension(10, 50));
        emptyPanel3.setPreferredSize(new Dimension(10, 50));
        emptyPanel4.setPreferredSize(new Dimension(10, 50));
        emptyPanel5.setPreferredSize(new Dimension(15, 5));

        //JPanel containing empty panels & config panel
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        JPanel separatorPanel = new JPanel();
        separatorPanel.setLayout(new BorderLayout());
        separatorPanel.add(separator, BorderLayout.CENTER);
        separatorPanel.setPreferredSize(new Dimension(0, 10));

        //I hate this code so much
        JPanel configPanel = new JPanel();
        configPanel.setLayout(new BorderLayout());
        configPanel.add(separatorPanel, BorderLayout.PAGE_START);
        configPanel.add(emptyPanel3, BorderLayout.LINE_START);
        configPanel.add(configStuffPanel, BorderLayout.CENTER);
        configPanel.add(emptyPanel4, BorderLayout.LINE_END);
        configPanel.add(emptyPanel5, BorderLayout.PAGE_END);

        // Add the JPanel to the JFrame's BorderLayout.CENTER
        add(titlePanel, BorderLayout.PAGE_START);
        add(emptyPanel1, BorderLayout.LINE_START);
        add(buttonPanel, BorderLayout.CENTER);
        add(emptyPanel2, BorderLayout.LINE_END);
//        add(emptyPanel3, BorderLayout.PAGE_END);
        add(configPanel, BorderLayout.PAGE_END);

        // Set the JFrame size and make it visible
        setSize(500, 700);
        setVisible(true);

        // Set the initial size and position of the JFrame
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.getWidth() * 0.5);
        int height = (int) (screenSize.getHeight() * 0.5);
        int x = (int) (screenSize.getWidth() * 0.25);
        int y = (int) (screenSize.getHeight() * 0.25);
        setBounds(x, y, width, height);

        // Load the window position from user preferences
        Preferences prefs = Preferences.userNodeForPackage(getClass());
        int savedX = prefs.getInt(PREFS_KEY_X, Integer.MIN_VALUE);
        int savedY = prefs.getInt(PREFS_KEY_Y, Integer.MIN_VALUE);
        int savedWidth = prefs.getInt(PREFS_KEY_WIDTH, Integer.MIN_VALUE);
        int savedHeight = prefs.getInt(PREFS_KEY_HEIGHT, Integer.MIN_VALUE);
        if (savedX != Integer.MIN_VALUE && savedY != Integer.MIN_VALUE && savedWidth != Integer.MIN_VALUE && savedHeight != Integer.MIN_VALUE) {
            setBounds(savedX, savedY, savedWidth, savedHeight);
        }

        // Save the window position to user preferences when the JFrame is closed
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                Preferences prefs = Preferences.userNodeForPackage(getClass());
                Rectangle bounds = getBounds();
                prefs.putInt(PREFS_KEY_X, bounds.x);
                prefs.putInt(PREFS_KEY_Y, bounds.y);
                prefs.putInt(PREFS_KEY_WIDTH, bounds.width);
                prefs.putInt(PREFS_KEY_HEIGHT, bounds.height);
            }
        });
    }

    public static void main(String[] args) throws IOException {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf() );
        } catch( Exception ex ) {
            System.err.println( "Failed to initialize LaF" );
        }
        new Frame();
    }
}
