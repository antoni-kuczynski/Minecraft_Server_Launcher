package Gui;

import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

public class Frame extends JFrame implements ActionListener {

    private final String PREFS_KEY_X = "window_x";
    private final String PREFS_KEY_Y = "window_y";
    private final String PREFS_KEY_WIDTH = "window_width";
    private final String PREFS_KEY_HEIGHT = "window_height";
    private final String PREFS_KEY_LOOK_AND_FEEL = "look_and_feel";


    public static void alert(AlertType alertType, String message) {
        switch(alertType) {
            case INFO -> JOptionPane.showMessageDialog(null, message, "Information", JOptionPane.INFORMATION_MESSAGE);
            case ERROR -> JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            case WARNING -> JOptionPane.showMessageDialog(null, message, "Warning", JOptionPane.WARNING_MESSAGE);
            case FATAL -> JOptionPane.showMessageDialog(null, message, "Fatal Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private final JMenuItem darculaMenuItem;
    private final JMenuItem githubDarkMenuItem;
    private final JMenuItem oneDarkMenuItem;
    private final JMenuItem inteliijLightMenuItem;
    private final JMenuItem xCodeDarkMenuItem;
    private final JMenuItem draculaMenuItem;
    private final JMenuItem nordMenuItem;
    private static String lookAndFeel;
    public Frame() throws IOException {

        JMenuBar menuBar = new JMenuBar();

        JMenu lookAndFeelMenu = new JMenu("Change Theme");

        darculaMenuItem = new JMenuItem("Darcula (Inteliij Dark)");
        darculaMenuItem.addActionListener(this);
        lookAndFeelMenu.add(darculaMenuItem);

        nordMenuItem = new JMenuItem("Nord");
        nordMenuItem.addActionListener(this);
        lookAndFeelMenu.add(nordMenuItem);

        draculaMenuItem = new JMenuItem("Dracula");
        draculaMenuItem.addActionListener(this);
        lookAndFeelMenu.add(draculaMenuItem);

        oneDarkMenuItem = new JMenuItem("One Dark");
        oneDarkMenuItem.addActionListener(this);
        lookAndFeelMenu.add(oneDarkMenuItem);

        githubDarkMenuItem = new JMenuItem("GitHub Dark");
        githubDarkMenuItem.addActionListener(this);
        lookAndFeelMenu.add(githubDarkMenuItem);

        xCodeDarkMenuItem = new JMenuItem("Xcode Dark");
        xCodeDarkMenuItem.addActionListener(this);
        lookAndFeelMenu.add(xCodeDarkMenuItem);

        inteliijLightMenuItem = new JMenuItem("Inteliij Light");
        inteliijLightMenuItem.addActionListener(this);
        lookAndFeelMenu.add(inteliijLightMenuItem);



//        darculaMenuItem.setSelected(true);

        menuBar.add(lookAndFeelMenu);

        setJMenuBar(menuBar);

        // Set up the JFrame
//        setFocusableWindowState(false);
        setIconImage(new ImageIcon("app_icon.png").getImage());
        setTitle("Minecraft Server Server Launcher V2");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        Preferences prefs = Preferences.userNodeForPackage(getClass());

        // Create the JPanels
        TitlePanel titlePanel = new TitlePanel();
        ButtonPanel buttonPanel = new ButtonPanel(Preferences.userNodeForPackage(getClass()));
        ConfigStuffPanel configStuffPanel = new ConfigStuffPanel(prefs);
        AddWorldsPanel addWorldsPanel = new AddWorldsPanel();
        configStuffPanel.setPanel(configStuffPanel, addWorldsPanel);

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

        //Add the world add JPanel to the frame
        addWorldsPanel.add(emptyPanel2);



//        buttonPanel.setPreferredSize(new Dimension(400, 100));
        JPanel testPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                System.out.println(getWidth());
                buttonPanel.setSize(new Dimension(getWidth() / 2, getHeight()));
            }
        };
        testPanel.setLayout(new BorderLayout(10, 10));
        testPanel.add(buttonPanel, BorderLayout.LINE_START);
        testPanel.add(new JSeparator(SwingConstants.VERTICAL), BorderLayout.CENTER);
        testPanel.add(addWorldsPanel, BorderLayout.LINE_END);

        // Add the JPanel to the JFrame's BorderLayout.CENTER
        add(titlePanel, BorderLayout.PAGE_START);
        add(emptyPanel1, BorderLayout.LINE_START);
        add(testPanel, BorderLayout.CENTER);
        add(configPanel, BorderLayout.PAGE_END);
        setVisible(true);

        // Set the initial size and position of the JFrame
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.getWidth() * 0.5);
        int height = (int) (screenSize.getHeight() * 0.5);
        int x = (int) (screenSize.getWidth() * 0.25);
        int y = (int) (screenSize.getHeight() * 0.25);
        setBounds(x, y, width, height);

        // Load the window position from user preferences

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
                prefs.put(PREFS_KEY_LOOK_AND_FEEL, lookAndFeel);


                File dir = new File(".\\world_temp");
                File[] files = dir.listFiles();
                assert files != null;
                for (File file : files) {
                    try {
                        FileUtils.deleteDirectory(file);
                    } catch (IOException e) {
                        alert(AlertType.ERROR, "Cannot clear the \"world_temp\" folder.");
                    }
                }
            }
        });
    }


    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == darculaMenuItem) {
            setLookAndFeel("com.formdev.flatlaf.FlatDarculaLaf");
        } else if (e.getSource() == githubDarkMenuItem) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkIJTheme");
        } else if (e.getSource() == oneDarkMenuItem) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme");
        } else if (e.getSource() == inteliijLightMenuItem) {
            setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
        } else if (e.getSource() == xCodeDarkMenuItem) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatXcodeDarkIJTheme");
        } else if (e.getSource() == draculaMenuItem) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatDraculaIJTheme");
        } else if (e.getSource() == nordMenuItem) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatNordIJTheme");
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
        new Frame();
    }
}
