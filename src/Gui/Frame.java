package Gui;

import RightClickMode.ServerSelector;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Frame extends JFrame implements ActionListener {

    private final String PREFS_KEY_X = "window_x";
    private final String PREFS_KEY_Y = "window_y";
    private final String PREFS_KEY_WIDTH = "window_width";
    private final String PREFS_KEY_HEIGHT = "window_height";
    private final String PREFS_KEY_LOOK_AND_FEEL = "look_and_feel";

    private final Dimension dimension = new Dimension(10,10);


    public static String exStackTraceToString(StackTraceElement[] elements) {
        StringBuilder sb = new StringBuilder();
        sb.append("Caused by:\n");
        for (StackTraceElement e : elements) {
            sb.append(e.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    public static void alert(AlertType alertType, String message) {
        switch(alertType) {
            case INFO -> JOptionPane.showMessageDialog(null, message, "Information", JOptionPane.INFORMATION_MESSAGE);
            case ERROR -> JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            case WARNING -> JOptionPane.showMessageDialog(null, message, "Warning", JOptionPane.WARNING_MESSAGE);
            case FATAL -> JOptionPane.showMessageDialog(null, message, "Fatal Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void clearTempDir() {
        File dir = new File(".\\world_temp");
        File[] files = dir.listFiles();
        assert files != null;
        for (File file : files) {
            try {
                FileUtils.deleteDirectory(file);
            } catch (IOException e) {
                alert(AlertType.ERROR, "Cannot clear the \"world_temp\" folder." + exStackTraceToString(e.getStackTrace()));
            }
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
        ButtonPanel buttonPanel = new ButtonPanel();
        ConfigStuffPanel configStuffPanel = new ConfigStuffPanel(prefs);
        AddWorldsPanel addWorldsPanel = new AddWorldsPanel();
        configStuffPanel.setPanel(configStuffPanel, addWorldsPanel);


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
        configPanel.add(Box.createRigidArea(dimension), BorderLayout.LINE_START);
        configPanel.add(configStuffPanel, BorderLayout.CENTER);
        configPanel.add(Box.createRigidArea(dimension), BorderLayout.LINE_END);
        configPanel.add(Box.createRigidArea(dimension), BorderLayout.PAGE_END);

        //Add the world add JPanel to the frame
        addWorldsPanel.add(Box.createRigidArea(dimension));


        JPanel testPanel2 = new JPanel(new BorderLayout());
        testPanel2.add(new JSeparator(SwingConstants.VERTICAL), BorderLayout.LINE_START);
        testPanel2.add(addWorldsPanel, BorderLayout.CENTER);

        JPanel testPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                buttonPanel.setSize(new Dimension(getWidth() / 2, getHeight()));
//                addWorldsPanel.setSize(new Dimension(getWidth() / 2, getHeight()));
            }
        };

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                testPanel.setLayout(new BorderLayout(10, 10));
                testPanel.add(buttonPanel, BorderLayout.LINE_START);
                testPanel.add(testPanel2, BorderLayout.CENTER);
                add(testPanel, BorderLayout.CENTER);
                add(titlePanel, BorderLayout.PAGE_START);
                setVisible(true);
                return null;
            }
        }.execute();


        // Add the JPanel to the JFrame's BorderLayout.CENTER
        add(Box.createRigidArea(dimension), BorderLayout.LINE_START);
        add(configPanel, BorderLayout.PAGE_END);


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

                clearTempDir();
            }

            @Override
            public void windowOpened(WindowEvent e) {
                super.windowOpened(e);
                clearTempDir();
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
            alert(AlertType.ERROR, "Cannot set look and feel.\n" + exStackTraceToString(ex.getStackTrace()));
        }
    }

    public static void main(String[] args) throws IOException {
        Preferences prefs = Preferences.userNodeForPackage(Frame.class);
        lookAndFeel = prefs.get("look_and_feel", "com.formdev.flatlaf.FlatDarculaLaf");
        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch( Exception ex ) {
            alert(AlertType.ERROR, "Cannot initialize look and feel\n" + exStackTraceToString(ex.getStackTrace()));
        }
        if(args.length == 0) {
            SwingUtilities.invokeLater(() -> {
                try {
                    new Frame().setVisible(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
//            new Frame();
        } else {
            new ServerSelector(args);
        }
    }
}
