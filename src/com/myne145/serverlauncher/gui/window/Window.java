package com.myne145.serverlauncher.gui.window;

import com.formdev.flatlaf.util.SystemInfo;
import com.myne145.serverlauncher.gui.components.OpenContextMenuItem;
import com.myne145.serverlauncher.gui.tabs.serverdashboard.charts.BasicChart;
import com.myne145.serverlauncher.server.Config;
import com.formdev.flatlaf.IntelliJTheme;
import com.myne145.serverlauncher.server.MinecraftServer;
import com.myne145.serverlauncher.utils.Colors;
import com.myne145.serverlauncher.utils.DateFormat;
import com.myne145.serverlauncher.utils.DefaultIcons;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.prefs.Preferences;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.border.MatteBorder;

public class Window extends JFrame {
    private final String PREFS_KEY_X = "window_x";
    private final String PREFS_KEY_Y = "window_y";
    private final String PREFS_KEY_WIDTH = "window_width";
    private final String PREFS_KEY_HEIGHT = "window_height";
    private final String PREFS_ARE_CHARTS_ENABLED = "are_charts_enabled";
    private final String PREFS_ICON_SCALE_MODE = "prefs_server_icons_scale";
    private static final Preferences userValues = Preferences.userNodeForPackage(Window.class);
    private static boolean areChartsEnabled;
    public static int serverStatusIconScaleMode;
    public static DateFormat dateFormat = DateFormat.YYYY_MM_DD;
    private static Window window;
    private static Taskbar taskbar;
    private static final JMenuBar menuBar = new JMenuBar();
    private static double scale = -1;

    public Window() {
        // Set up the JFrame
        setIconImage(DefaultIcons.getIcon(DefaultIcons.APP_ICON).getImage());
        setTitle("Minecraft Server Launcher");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension((int) (750 * getDisplayScale()), (int) (490 * getDisplayScale())));

        if(Taskbar.isTaskbarSupported())
            taskbar = Taskbar.getTaskbar();

        //1 small, 2 medium, 3 large
        serverStatusIconScaleMode = getUserValues().getInt(PREFS_ICON_SCALE_MODE,  2);
        
        this.getRootPane().putClientProperty("JRootPane.titleBarBackground", Colors.TABBEDPANE_BACKGROUND_COLOR);
        this.getRootPane().putClientProperty("JRootPane.titleBarForeground", Colors.TEXT_COLOR);

        JMenu fileMenu = new JMenu("File");
        OpenContextMenuItem openConfigFile = new OpenContextMenuItem("Open config file");
        openConfigFile.updatePath(new File(Config.ABSOLUTE_PATH));
        OpenContextMenuItem openCurrentServer = new OpenContextMenuItem("Open current server's folder");
        fileMenu.add(openCurrentServer);
        fileMenu.add(openConfigFile);

        JMenu viewMenu = new JMenu("View");
        JCheckBoxMenuItem showCharts = new JCheckBoxMenuItem("Show CPU & RAM usage graphs");
        JMenu serverButtonsScale = new JMenu("Server buttons scale");

        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButtonMenuItem scaleSmall = new JRadioButtonMenuItem("Small");
        JRadioButtonMenuItem scaleMedium = new JRadioButtonMenuItem("Medium");
        JRadioButtonMenuItem scaleLarge = new JRadioButtonMenuItem("Large");
        JMenuItem refreshAllServers = new JMenuItem("Refresh servers");
        buttonGroup.add(scaleSmall);
        buttonGroup.add(scaleMedium);
        buttonGroup.add(scaleLarge);
        serverButtonsScale.add(scaleSmall);
        serverButtonsScale.add(scaleMedium);
        serverButtonsScale.add(scaleLarge);

        viewMenu.add(showCharts);
        viewMenu.add(serverButtonsScale);
        viewMenu.add(refreshAllServers);


        menuBar.setBorder(new MatteBorder(0,0,1,0, Colors.BORDER_COLOR));
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        setJMenuBar(menuBar);

        ContainerPane containerPane = new ContainerPane();
        add(containerPane, BorderLayout.CENTER);
        BasicChart.startResourceMonitoringTimer();

        setVisible(true);
        containerPane.setChartsVisibility(areChartsEnabled);

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
                serverStatusIconScaleMode = 1;
                containerPane.updateServerButtonsSizes(getScaledSize(16));
            }
        });

        scaleMedium.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED) {
                serverStatusIconScaleMode = 2;
                containerPane.updateServerButtonsSizes(getScaledSize(32));
            }
        });

        scaleLarge.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED) {
                serverStatusIconScaleMode = 3;
                containerPane.updateServerButtonsSizes(getScaledSize(48));
            }
        });

        refreshAllServers.addActionListener(e -> {
            try {
                Config.createConfig();
            } catch (IOException ex) {
                showErrorMessage("Cannot refresh servers from config file.", ex);
            }

            int tabCount = containerPane.getTabCount();
            for(int i = 1; i < tabCount; i++) {
                containerPane.remove(1);
            }
            Config.getData().forEach(containerPane::addServer);
            MinecraftServer.writeAllToConfig();
            containerPane.setSelectedIndex(0);
        });

        areChartsEnabled = getUserValues().getBoolean(PREFS_ARE_CHARTS_ENABLED, true);
        showCharts.setSelected(areChartsEnabled);


        switch(serverStatusIconScaleMode) {
            case 1 -> scaleSmall.setSelected(true);
            case 2 -> scaleMedium.setSelected(true);
            case 3 -> scaleLarge.setSelected(true);
        }

        int savedXPosition = getUserValues().getInt(PREFS_KEY_X, 0);
        int savedYPosition = getUserValues().getInt(PREFS_KEY_Y, 0);
        int savedWidth = getUserValues().getInt(PREFS_KEY_WIDTH, 1000);
        int savedHeight = getUserValues().getInt(PREFS_KEY_HEIGHT, 550);
        setBounds(savedXPosition, savedYPosition, savedWidth, savedHeight); //doesnt work on linux

        for(WindowListener windowListener : getWindowListeners())
            removeWindowListener(windowListener);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                Preferences userSavedValues = Preferences.userNodeForPackage(getClass());
                Rectangle screenDimensions = getBounds();
                userSavedValues.putInt(PREFS_KEY_X, screenDimensions.x);
                userSavedValues.putInt(PREFS_KEY_Y, screenDimensions.y);
                userSavedValues.putInt(PREFS_KEY_WIDTH, screenDimensions.width);
                userSavedValues.putInt(PREFS_KEY_HEIGHT, screenDimensions.height);
                userSavedValues.putInt(PREFS_ICON_SCALE_MODE, serverStatusIconScaleMode);
                userSavedValues.putBoolean(PREFS_ARE_CHARTS_ENABLED, areChartsEnabled);

                containerPane.killAllServerProcesses();
                MinecraftServer.writeAllToConfig();

                File temporaryFilesDirectory = new File("world_temp");
                if(!temporaryFilesDirectory.exists())
                    temporaryFilesDirectory.mkdirs();

                if(temporaryFilesDirectory.exists())
                    clearTempDirectory();
                else
                    System.exit(1);
            }

            @Override
            public void windowOpened(WindowEvent e) {
//                File temporaryWorldLevelDatFiles = new File("world_temp/worlds_level_dat");
//                if(!temporaryWorldLevelDatFiles.exists())
//                    temporaryWorldLevelDatFiles.mkdirs();
                clearTempDirectory();
            }

            @Override
            public void windowIconified(WindowEvent e) {
                if(Taskbar.isTaskbarSupported())
                    taskbar.setWindowProgressState(Window.getWindow(), Taskbar.State.OFF);
                Window.getWindow().setState(Window.ICONIFIED);
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
                if(Taskbar.isTaskbarSupported())
                    taskbar.setWindowProgressState(Window.getWindow(), Taskbar.State.OFF);
                Window.getWindow().setState(Window.NORMAL);
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                if(Taskbar.isTaskbarSupported())
                    taskbar.setWindowProgressState(Window.getWindow(), Taskbar.State.OFF);
                Window.getWindow().setState(Window.ICONIFIED);
            }

            @Override
            public void windowGainedFocus(WindowEvent e) {
                if(Taskbar.isTaskbarSupported())
                    taskbar.setWindowProgressState(Window.getWindow(), Taskbar.State.OFF);
                Window.getWindow().setState(Window.NORMAL);
            }

        });
        window = this;
    }


    public boolean isMouseWithinWindow() {
        Point mousePos = MouseInfo.getPointerInfo().getLocation();
        Rectangle bounds = getBounds();
        bounds.setLocation(getLocationOnScreen());
        return bounds.contains(mousePos);
    }

    public static void showErrorMessage(String basicInfo, Exception e) {
        if(!Taskbar.isTaskbarSupported()) {
            new ErrorDialog(basicInfo, e).setVisible(true);
            return;
        }
        getTaskbar().setWindowProgressState(Window.getWindow(), Taskbar.State.ERROR);
        getTaskbar().setWindowProgressValue(Window.getWindow(), 100);
        new ErrorDialog(basicInfo, e).setVisible(true);
        getTaskbar().setWindowProgressState(Window.getWindow(), Taskbar.State.OFF);
    }

    private static void clearTempDirectory() {
        File tempFilesDirectory = new File("world_temp");
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
//                alert(AlertType.ERROR, "Cannot clear the \"world_temp\" folder." + getErrorDialogMessage(e));
            }
        }
//        new File(tempFilesDirectory.getAbsolutePath() + "/worlds_level_dat").mkdirs();
    }

    public static Point getCenter() {
        if(window == null)
            return new Point(0,0);
        return new Point(
                window.getX() + (window.getWidth() / 2),
                window.getY() + (window.getHeight() / 2)
                );
    }

    public static double getDisplayScale() {
        if(scale != -1)
            return scale;
        GraphicsConfiguration asdf = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        AffineTransform asfd2 = asdf.getDefaultTransform();
        return asfd2.getScaleX();
    }

    public static int getScaledSize(int fontSize) {
        if(SystemInfo.isLinux)
            return (int) (fontSize * getDisplayScale());
        return fontSize;
    }

    public static Window getWindow() {
        return window;
    }

    public static JMenuBar getMenu() {
        return menuBar;
    }

    public static Preferences getUserValues() {
        return userValues;
    }

    protected static boolean areChartsEnabled() {
        return areChartsEnabled;
    }

    public static Taskbar getTaskbar() {
        return taskbar;
    }

    public static DateFormat getDateFormat() {
        return dateFormat;
    }

    public static void main(String[] args) throws Exception {
        InputStream inputStream = Config.getResource(Config.RESOURCES_PATH + "/DarkFlatTheme/DarkFlatTheme.json");
        IntelliJTheme.setup(inputStream);
//        if( SystemInfo.isLinux ) {
//            // enable custom window decorations
//            JFrame.setDefaultLookAndFeelDecorated( true );
//            JDialog.setDefaultLookAndFeelDecorated( true );
//        }

        for(int i = 0; i < args.length; i++) {
            if(args[i].equals("--scale") && args.length >= i + 1) {
                scale = Double.parseDouble(args[i + 1]);
            }
        }

        Config.createConfig();
        SwingUtilities.invokeLater(Window::new);
    }
}
