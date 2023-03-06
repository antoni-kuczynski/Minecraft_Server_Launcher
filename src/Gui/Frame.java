package Gui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatDraculaIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkIJTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.prefs.Preferences;

public class Frame extends JFrame implements ActionListener {

    private final String PREFS_KEY_X = "window_x";
    private final String PREFS_KEY_Y = "window_y";
    private final String PREFS_KEY_WIDTH = "window_width";
    private final String PREFS_KEY_HEIGHT = "window_height";


    public static void alert(AlertType alertType, String message) {
        switch(alertType) {
            case INFO -> JOptionPane.showMessageDialog(null, message, "Information", JOptionPane.INFORMATION_MESSAGE);
            case ERROR -> JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            case WARNING -> JOptionPane.showMessageDialog(null, message, "Warning", JOptionPane.WARNING_MESSAGE);
            case FATAL -> JOptionPane.showMessageDialog(null, message, "Fatal Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JMenuItem darculaMenuItem;
    private JMenuItem githubDarkMenuItem;
    private JMenuItem windowsMenuItem;
    private JMenuItem inteliijLightMenuItem;
    private JMenuItem githubLightMenuItem;
    private JMenuItem draculaMenuItem;
    private static String lookAndFeel;

    public Frame() throws IOException {

        JMenuBar menuBar = new JMenuBar();

        JMenu lookAndFeelMenu = new JMenu("Change Theme");

        darculaMenuItem = new JMenuItem("Darcula");
        darculaMenuItem.addActionListener(this);
        lookAndFeelMenu.add(darculaMenuItem);

        githubDarkMenuItem = new JMenuItem("GitHub Dark");
        githubDarkMenuItem.addActionListener(this);
        lookAndFeelMenu.add(githubDarkMenuItem);

        draculaMenuItem = new JMenuItem("Dracula");
        draculaMenuItem.addActionListener(this);
        lookAndFeelMenu.add(draculaMenuItem);

        inteliijLightMenuItem = new JMenuItem("Inteliij Light");
        inteliijLightMenuItem.addActionListener(this);
        lookAndFeelMenu.add(inteliijLightMenuItem);

        githubLightMenuItem = new JMenuItem("Github Light");
        githubLightMenuItem.addActionListener(this);
        lookAndFeelMenu.add(githubLightMenuItem);

        windowsMenuItem = new JMenuItem("Windows");
        windowsMenuItem.addActionListener(this);
        lookAndFeelMenu.add(windowsMenuItem);
        darculaMenuItem.setSelected(true);

        menuBar.add(lookAndFeelMenu);

        setJMenuBar(menuBar);

        // Set up the JFrame
        setIconImage(new ImageIcon("app_icon.png").getImage());
        setTitle("Minecraft Server Server Launcher V2");
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
                prefs.put("look_and_feel", lookAndFeel);
            }
        });
    }


    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == darculaMenuItem) {
            setLookAndFeel("com.formdev.flatlaf.FlatDarculaLaf");
        } else if (e.getSource() == githubDarkMenuItem) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkIJTheme");
        } else if (e.getSource() == windowsMenuItem) {
            setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } else if (e.getSource() == inteliijLightMenuItem) {
            setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
        } else if (e.getSource() == githubLightMenuItem) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubIJTheme");
        } else if (e.getSource() == draculaMenuItem) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatDraculaIJTheme");
        }
    }

    private void setLookAndFeel(String className) {
        lookAndFeel = className;
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException |
                IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Preferences prefs = Preferences.userNodeForPackage(Frame.class);
        lookAndFeel = prefs.get("look_and_feel", "com.formdev.flatlaf.FlatDarculaLaf");
        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch( Exception ex ) {
            System.err.println( "Failed to initialize LaF" );
        }
////        FlatAtomOneDarkIJTheme.setup(); //TODO: test some themes and maybe add a theme switcher
//        int x = 2;
//        if(x == 1)
//            FlatGitHubDarkIJTheme.setup();
//        if(x == 2)
//            FlatDraculaIJTheme.setup();
        new Frame();
    }
}
