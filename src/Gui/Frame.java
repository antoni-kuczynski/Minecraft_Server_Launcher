package Gui;

import Enums.AlertType;
import SelectedServer.ServerDetails;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.prefs.Preferences;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

public class Frame extends JFrame implements ActionListener {
    private final JMenuItem darculaTheme;
    private final JMenuItem githubDarkTheme;
    private final JMenuItem oneDarkTheme;
    private final JMenuItem inteliijLightMenuItem;
    private final JMenuItem xCodeDarkTheme;
    private final JMenuItem draculaTheme;
    private final JMenuItem nordTheme;
    private static String lookAndFeel;
    private final WorldsTab worldsTab;
    private final ButtonPanel buttonPanel;
    private final String PREFS_KEY_X = "window_x";
    private final String PREFS_KEY_Y = "window_y";
    private final String PREFS_KEY_WIDTH = "window_width";
    private final String PREFS_KEY_HEIGHT = "window_height";
    private static final String PREFS_KEY_LOOK_AND_FEEL = "look_and_feel";

    public Frame() throws IOException, InterruptedException {
        JMenuBar optionsBar = new JMenuBar();
        JMenu changeTheme = new JMenu("Change Theme");
        JMenu refreshServerList = new JMenu("Refresh Server List");
        JButton openServer = new JButton("Debug");
        refreshServerList.setBorderPainted(false);
        refreshServerList.setRequestFocusEnabled(false);
        refreshServerList.setContentAreaFilled(false);
        refreshServerList.setVisible(true);

        AtomicInteger themeIndex = new AtomicInteger(1);
        openServer.setVisible(true);
        openServer.addActionListener(e -> {
            debugColorSchemes(themeIndex.get());
            System.out.println(themeIndex.get());
            themeIndex.getAndIncrement();
        });

        darculaTheme = new JMenuItem("Darcula (Inteliij Dark)");
        darculaTheme.addActionListener(this);
        changeTheme.add(darculaTheme);

        nordTheme = new JMenuItem("Nord");
        nordTheme.addActionListener(this);
        changeTheme.add(nordTheme);

        draculaTheme = new JMenuItem("Dracula");
        draculaTheme.addActionListener(this);
        changeTheme.add(draculaTheme);

        oneDarkTheme = new JMenuItem("One Dark");
        oneDarkTheme.addActionListener(this);
        changeTheme.add(oneDarkTheme);

        githubDarkTheme = new JMenuItem("GitHub Dark");
        githubDarkTheme.addActionListener(this);
        changeTheme.add(githubDarkTheme);

        xCodeDarkTheme = new JMenuItem("Xcode Dark");
        xCodeDarkTheme.addActionListener(this);
        changeTheme.add(xCodeDarkTheme);

        inteliijLightMenuItem = new JMenuItem("Inteliij Light");
        inteliijLightMenuItem.addActionListener(this);
        changeTheme.add(inteliijLightMenuItem);

        optionsBar.add(changeTheme);
        optionsBar.add(refreshServerList);
        optionsBar.add(openServer);
        setJMenuBar(optionsBar);


        // Set up the JFrame
        setIconImage(new ImageIcon("resources/app_icon.png").getImage());
        setTitle("Minecraft Server Server Launcher V2");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        Preferences userValues = Preferences.userNodeForPackage(getClass());
        ServerDetails.serverId = userValues.getInt("PREFS_SERVER_ID", 1);
        // Create the JPanels
        TitlePanel titlePanel = new TitlePanel();
        buttonPanel = new ButtonPanel();
        ServerSelectionPanel serverSelectionPanel = new ServerSelectionPanel(userValues);
        WorldsTab worldsTab = new WorldsTab();
        ServerConsoleTab serverConsoleTab = new ServerConsoleTab();

        serverSelectionPanel.setPanels(serverSelectionPanel, worldsTab);

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
        Dimension dimension = new Dimension(10, 10);
        configPanel.add(Box.createRigidArea(dimension), BorderLayout.LINE_START);
        configPanel.add(serverSelectionPanel, BorderLayout.CENTER);
        configPanel.add(Box.createRigidArea(dimension), BorderLayout.LINE_END);
        configPanel.add(Box.createRigidArea(dimension), BorderLayout.PAGE_END);

        JPanel worldsPanelSpacingAnotherLayer = new JPanel(new BorderLayout());
        JPanel worldsPanelAndSpacing = new JPanel(new BorderLayout());

        worldsPanelSpacingAnotherLayer.add(worldsTab, BorderLayout.LINE_START);
//        worldsPanelSpacingAnotherLayer.add(Box.createRigidArea(new Dimension(100, 50)), BorderLayout.LINE_END);

        worldsPanelAndSpacing.add(new JSeparator(SwingConstants.VERTICAL), BorderLayout.LINE_START);
        worldsPanelAndSpacing.add(worldsPanelSpacingAnotherLayer, BorderLayout.LINE_END);
//        worldsPanelAndSpacing.add(Box.createRigidArea(new Dimension(200, 10)), BorderLayout.LINE_END);

        JPanel buttonAndWorldsPanel = new JPanel(new BorderLayout(10,10));
        JTabbedPane serverPageSwitcher = new JTabbedPane(JTabbedPane.RIGHT);
        serverPageSwitcher.addTab("Console", serverConsoleTab);
        serverPageSwitcher.addTab("Worlds", worldsTab);

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                buttonAndWorldsPanel.add(buttonPanel, BorderLayout.LINE_START);
//                buttonAndWorldsPanel.add(worldsPanelAndSpacing, BorderLayout.CENTER);
//                buttonAndWorldsPanel.add(serverPageSwitcher, BorderLayout.LINE_END);
                add(titlePanel, BorderLayout.PAGE_START);
                add(buttonAndWorldsPanel, BorderLayout.LINE_START);
                add(serverPageSwitcher, BorderLayout.CENTER);
                setVisible(true);
                return null;
            }
        }.execute();


        // Add the JPanel to the JFrame's BorderLayout.CENTER
        add(Box.createRigidArea(dimension), BorderLayout.LINE_START);
        add(configPanel, BorderLayout.PAGE_END);


        // Set the initial size and position of the JFrame
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int windowWidth = (int) (screenSize.getWidth() * 0.5);
        int windowHeight = (int) (screenSize.getHeight() * 0.5);
        int xWindowPosiotion = (int) (screenSize.getWidth() * 0.25);
        int yWindowPosiotion = (int) (screenSize.getHeight() * 0.25);
        setBounds(xWindowPosiotion, yWindowPosiotion, windowWidth, windowHeight);

        // Load the window position from user preferences
        int savedXPosition = userValues.getInt(PREFS_KEY_X, Integer.MIN_VALUE);
        int savedYPosition = userValues.getInt(PREFS_KEY_Y, Integer.MIN_VALUE);
        int savedWidth = userValues.getInt(PREFS_KEY_WIDTH, Integer.MIN_VALUE);
        int savedHeight = userValues.getInt(PREFS_KEY_HEIGHT, Integer.MIN_VALUE);
        if (savedXPosition != Integer.MIN_VALUE && savedYPosition != Integer.MIN_VALUE && savedWidth != Integer.MIN_VALUE && savedHeight != Integer.MIN_VALUE) {
            setBounds(savedXPosition, savedYPosition, savedWidth, savedHeight);
        }


        // Save the window position to user preferences when the JFrame is closed
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                Preferences userSavedValues = Preferences.userNodeForPackage(getClass());
                Rectangle screenDimensions = getBounds();
                userSavedValues.putInt(PREFS_KEY_X, screenDimensions.x);
                userSavedValues.putInt(PREFS_KEY_Y, screenDimensions.y);
                userSavedValues.putInt(PREFS_KEY_WIDTH, screenDimensions.width);
                userSavedValues.putInt(PREFS_KEY_HEIGHT, screenDimensions.height);
                userSavedValues.put(PREFS_KEY_LOOK_AND_FEEL, lookAndFeel);
                userSavedValues.putInt("PREFS_SERVER_ID", ServerDetails.serverId);

                File temporaryFilesDirectory = new File("world_temp");
                if(!temporaryFilesDirectory.exists()) //issue #55 fix by checking if the folder exitst and creating it (if somehow it doesn't exist here)
                    temporaryFilesDirectory.mkdirs();

                if(temporaryFilesDirectory.exists()) //another issue #55 check for some reason
                    clearTempDirectory();
                else
                    System.exit(1);
            }

            @Override
            public void windowOpened(WindowEvent e) {
                super.windowOpened(e);
                File temporaryFilesDirectory = new File("world_temp");
                if(!temporaryFilesDirectory.exists()) //issue #55 fix by checking if the folder exitst and creating it
                    temporaryFilesDirectory.mkdirs();
                clearTempDirectory();
            }
        });

        this.worldsTab = worldsTab;
        worldsTab.setBorders();

        refreshServerList.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                try {
                    buttonPanel.clearAllButtons();
                } catch (IOException ex) {
                    alert(AlertType.ERROR, getErrorDialogMessage(ex));
                }
                refreshServerList.setSelected(false);
            }

            @Override
            public void menuDeselected(MenuEvent e) {

            }

            @Override
            public void menuCanceled(MenuEvent e) {

            }
        });
//        System.out.println(getWidth());
    }

    public static String getErrorDialogMessage(Exception e) {
        Toolkit.getDefaultToolkit().beep();
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append(e).append("\n");
        errorMessage.append("Caused by:\n");
        StackTraceElement[] errorStackTrace = e.getStackTrace();
        for (StackTraceElement element : errorStackTrace) {
            errorMessage.append(element.toString());
            errorMessage.append("\n");
        }
        return errorMessage.toString();
    }

    public static void alert(AlertType alertType, String message) {
        switch(alertType) {
            case INFO -> JOptionPane.showMessageDialog(null, message, "Information", JOptionPane.INFORMATION_MESSAGE);
            case ERROR -> JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
            case WARNING -> JOptionPane.showMessageDialog(null, message, "Warning", JOptionPane.WARNING_MESSAGE);
            case FATAL -> JOptionPane.showMessageDialog(null, message, "Fatal Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void clearTempDirectory() {
        File tempFilesDirectory = new File(".\\world_temp");
        File[] filesInTempDir = tempFilesDirectory.listFiles();
        if(filesInTempDir == null)
            return;
        for (File tempFile : filesInTempDir) {
            try {
                if(tempFile.isDirectory())
                    FileUtils.deleteDirectory(tempFile);
                else
                    tempFile.delete();
            } catch (IOException e) {
                alert(AlertType.ERROR, "Cannot clear the \"world_temp\" folder." + getErrorDialogMessage(e));
            }
        }
    }

    private void setLookAndFeel(String className) {
        lookAndFeel = className;
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException | InstantiationException |
                IllegalAccessException | UnsupportedLookAndFeelException ex) {
            alert(AlertType.ERROR, "Cannot set look and feel.\n" + getErrorDialogMessage(ex));
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == darculaTheme) {
            setLookAndFeel("com.formdev.flatlaf.FlatDarculaLaf");
        } else if (e.getSource() == githubDarkTheme) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkIJTheme");
        } else if (e.getSource() == oneDarkTheme) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme");
        } else if (e.getSource() == inteliijLightMenuItem) {
            setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
        } else if (e.getSource() == xCodeDarkTheme) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatXcodeDarkIJTheme");
        } else if (e.getSource() == draculaTheme) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatDraculaIJTheme");
        } else if (e.getSource() == nordTheme) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatNordIJTheme");
        }
        worldsTab.setBorders();
        buttonPanel.setBorders();
    }

    public void debugColorSchemes(int theme) {
        if (theme == 1) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatDarculaIJTheme");
        } else if (theme == 2) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatArcIJTheme");
        } else if (theme == 3) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatArcOrangeIJTheme");
        } else if (theme == 4) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatArcDarkIJTheme");
        } else if (theme == 5) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatArcDarkOrangeIJTheme");
        } else if (theme == 6) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatCarbonIJTheme");
        } else if (theme == 7) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatCobalt2IJTheme");
        } else if (theme == 8) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatCyanLightIJTheme");
        } else if (theme == 9) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatDarkFlatIJTheme");
        } else if (theme == 10) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatDarkPurpleIJTheme");
        } else if (theme == 11) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme");
        } else if (theme == 12) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatGradiantoDarkFuchsiaIJTheme");
        } else if (theme == 13) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatGradiantoDeepOceanIJTheme");
        } else if (theme == 14) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatGradiantoMidnightBlueIJTheme");
        } else if (theme == 15) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatGradiantoNatureGreenIJTheme");
        } else if (theme == 16) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatGrayIJTheme");
        } else if (theme == 17) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatGruvboxDarkHardIJTheme");
        } else if (theme == 18) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatGruvboxDarkMediumIJTheme");
        } else if (theme == 19) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatGruvboxDarkSoftIJTheme");
        } else if (theme == 20) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatHiberbeeDarkIJTheme");
        } else if (theme == 21) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatHighContrastIJTheme");
        } else if (theme == 22) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatMaterialDesignDarkIJTheme");
        } else if (theme == 23) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatMonokaiIJTheme");
        } else if (theme == 24) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatMonokaiProIJTheme");
        } else if (theme == 25) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatNordIJTheme");
        } else if (theme == 26) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme");
        } else if (theme == 27) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatSolarizedDarkIJTheme");
        } else if (theme == 28) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatSolarizedLightIJTheme");
        } else if (theme == 29) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatSpacegrayIJTheme");
        } else if (theme == 30) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatVuesionIJTheme");
        } else if (theme == 33) {
            setLookAndFeel("com.formdev.flatlaf.intellijthemes.FlatXcodeDarkIJTheme");
        }
        worldsTab.setBorders();
        buttonPanel.setBorders();
        System.out.println(UIManager.getColor("Button.background"));
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Preferences userValues = Preferences.userNodeForPackage(Frame.class);
        lookAndFeel = userValues.get(PREFS_KEY_LOOK_AND_FEEL, "com.formdev.flatlaf.FlatDarculaLaf");
        try {
            UIManager.setLookAndFeel(lookAndFeel);
        } catch( Exception ex ) {
            alert(AlertType.ERROR, "Cannot initialize look and feel\n" + getErrorDialogMessage(ex));
        }
        new Frame();
    }
}
