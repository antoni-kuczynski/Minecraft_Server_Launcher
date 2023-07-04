package com.myne145.serverlauncher.Gui;

import com.myne145.serverlauncher.Server.Current.NBTParser;
import com.myne145.serverlauncher.Server.Current.CurrentServerInfo;
import com.myne145.serverlauncher.Server.Current.ServerPropertiesFile;
import com.myne145.serverlauncher.Server.Config;
import com.formdev.flatlaf.IntelliJTheme;
import com.myne145.serverlauncher.Server.FileOpener;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.prefs.Preferences;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Window extends JFrame {
    private final String PREFS_KEY_X = "window_x";
    private final String PREFS_KEY_Y = "window_y";
    private final String PREFS_KEY_WIDTH = "window_width";
    private final String PREFS_KEY_HEIGHT = "window_height";
    private final String PREFS_ARE_CHARTS_ENABLED = "are_charts_enabled";
    private final String PREFS_SERVER_ID = "prefs_server_id";
    private final String PREFS_SERVER_ICONS_SCALE = "prefs_server_icons_scale";
    public static Preferences userValues = Preferences.userNodeForPackage(Window.class);
    public static boolean areChartsEnabled;
    public static int SERVER_STATUS_ICON_DIMENSION;

    public Window() throws Exception {
        // Set up the JFrame
        setIconImage(new ImageIcon("resources/app_icon.png").getImage());
        setTitle("Minecraft Server Launcher V2");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SERVER_STATUS_ICON_DIMENSION = userValues.getInt(PREFS_SERVER_ICONS_SCALE,  32); //this needs to be here cuz size needs to be loaded before actually loading the icons
        JPanel buttonAndWorldsPanel = new JPanel(new BorderLayout(10,10));
        buttonAndWorldsPanel.setBackground(new Color(51, 51, 52));


        if(userValues.getInt(PREFS_SERVER_ID, 1) - 1 <= Config.getData().size()) {
            CurrentServerInfo.serverId = userValues.getInt(PREFS_SERVER_ID, 1);
            CurrentServerInfo.serverName = Config.getData().get(CurrentServerInfo.serverId - 1).serverName();
            CurrentServerInfo.serverPath = Config.getData().get(CurrentServerInfo.serverId - 1).serverPath();
        } else {
            CurrentServerInfo.serverId = 1;
            CurrentServerInfo.serverName = Config.getData().get(0).serverName();
            CurrentServerInfo.serverPath = Config.getData().get(0).serverPath();
        }

        JFrame.setDefaultLookAndFeelDecorated(true);
        this.getRootPane().putClientProperty("JRootPane.titleBarBackground", new Color(51, 51, 52));
        this.getRootPane().putClientProperty("JRootPane.titleBarForeground", new Color(204, 204, 204));

        new ServerPropertiesFile();
        NBTParser nbtParser = new NBTParser(); //reading NBT level.dat file for level name
        nbtParser.start();

        ContainerPane containerPane = new ContainerPane();
        buttonAndWorldsPanel.add(containerPane, BorderLayout.CENTER);
        add(buttonAndWorldsPanel, BorderLayout.CENTER);
        setVisible(true);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu viewMenu = new JMenu("View");
        JCheckBoxMenuItem showCharts = new JCheckBoxMenuItem("Show usage graphs");

        JMenu serverButtonsScale = new JMenu("Server buttons scale");
        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButtonMenuItem scaleSmall = new JRadioButtonMenuItem("Small");
        JRadioButtonMenuItem scaleMedium = new JRadioButtonMenuItem("Medium");
        JRadioButtonMenuItem scaleLarge = new JRadioButtonMenuItem("Large");

        JMenuItem openServerFolder = new JMenuItem("Open current server's folder");
        JMenuItem openConfigFile = new JMenuItem("Open config file");

        fileMenu.add(openServerFolder);
        fileMenu.add(openConfigFile);

        buttonGroup.add(scaleSmall);
        buttonGroup.add(scaleMedium);
        buttonGroup.add(scaleLarge);
        serverButtonsScale.add(scaleSmall);
        serverButtonsScale.add(scaleMedium);
        serverButtonsScale.add(scaleLarge);

        viewMenu.add(showCharts);
        viewMenu.add(serverButtonsScale);

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        setJMenuBar(menuBar);
        areChartsEnabled = userValues.getBoolean(PREFS_ARE_CHARTS_ENABLED, true);
        showCharts.setSelected(areChartsEnabled);
        containerPane.setChartsVisibility(areChartsEnabled);

        switch(SERVER_STATUS_ICON_DIMENSION) {
            case 16 -> scaleSmall.setSelected(true);
            case 32 -> scaleMedium.setSelected(true);
            case 48 -> scaleLarge.setSelected(true);
        }

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

        showCharts.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED) {
                containerPane.setChartsVisibility(true);
                areChartsEnabled = true;
            } else {
                containerPane.setChartsVisibility(false);
                areChartsEnabled = false;
            }
        });

        scaleSmall.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED) {
                SERVER_STATUS_ICON_DIMENSION = 16;
                containerPane.updateServerButtonsSizes();
            }
        });

        scaleMedium.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED) {
                SERVER_STATUS_ICON_DIMENSION = 32;
                containerPane.updateServerButtonsSizes();
            }
        });

        scaleLarge.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED) {
                SERVER_STATUS_ICON_DIMENSION = 48;
                containerPane.updateServerButtonsSizes();
            }
        });

        openConfigFile.addActionListener(e -> FileOpener.openConfigFile());

        openServerFolder.addActionListener(e -> FileOpener.openServerFolder());

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
                userSavedValues.putInt(PREFS_SERVER_ID, CurrentServerInfo.serverId);
                userSavedValues.putInt(PREFS_SERVER_ICONS_SCALE, SERVER_STATUS_ICON_DIMENSION);
                userSavedValues.putBoolean(PREFS_ARE_CHARTS_ENABLED, areChartsEnabled);

                containerPane.killAllServerProcesses();

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

    public static void main(String[] args) throws Exception {
        Config.createConfig();
        InputStream inputStream = new FileInputStream("arc-theme.theme.json");
        IntelliJTheme.setup(inputStream);

        SwingUtilities.invokeLater(() -> {
            try {
                new Window();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
