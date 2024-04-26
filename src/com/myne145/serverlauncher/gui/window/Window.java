package com.myne145.serverlauncher.gui.window;

import com.myne145.serverlauncher.server.Config;
import com.formdev.flatlaf.IntelliJTheme;
import com.myne145.serverlauncher.server.MCServer;
import com.myne145.serverlauncher.utils.AlertType;
import com.myne145.serverlauncher.utils.Colors;
import com.myne145.serverlauncher.utils.DateFormat;
import com.myne145.serverlauncher.utils.DesktopOpener;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.prefs.Preferences;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.border.MatteBorder;

import static com.myne145.serverlauncher.server.Config.getResource;

public class Window extends JFrame {
    private final String PREFS_KEY_X = "window_x";
    private final String PREFS_KEY_Y = "window_y";
    private final String PREFS_KEY_WIDTH = "window_width";
    private final String PREFS_KEY_HEIGHT = "window_height";
    private final String PREFS_ARE_CHARTS_ENABLED = "are_charts_enabled";
    private final String PREFS_SERVER_ICONS_SCALE = "prefs_server_icons_scale";
    private static final Preferences userValues = Preferences.userNodeForPackage(Window.class);
    private static boolean areChartsEnabled;
    public static int SERVER_STATUS_ICON_DIMENSION;
    public static DateFormat dateFormat = DateFormat.YYYY_MM_DD;
    private static Window window;
    private final static Taskbar taskbar = Taskbar.getTaskbar();
    private static final JMenuBar menuBar = new JMenuBar();

    public Window() throws Exception {
        // Set up the JFrame
        setIconImage(new ImageIcon(ImageIO.read(Config.getResource(Config.RESOURCES_PATH + "/app_icon.png"))).getImage());
        setTitle("Minecraft Server Launcher");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SERVER_STATUS_ICON_DIMENSION = getUserValues().getInt(PREFS_SERVER_ICONS_SCALE,  32);


        JFrame.setDefaultLookAndFeelDecorated(true);
        this.getRootPane().putClientProperty("JRootPane.titleBarBackground", Colors.TABBEDPANE_BACKGROUND_COLOR);
        this.getRootPane().putClientProperty("JRootPane.titleBarForeground", Colors.TEXT_COLOR);

        JMenu fileMenu = new JMenu("File");
        JMenu viewMenu = new JMenu("View");
        JCheckBoxMenuItem showCharts = new JCheckBoxMenuItem("Show CPU & RAM usage graphs");

        JMenu serverButtonsScale = new JMenu("Server buttons scale");
        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButtonMenuItem scaleSmall = new JRadioButtonMenuItem("Small");
        JRadioButtonMenuItem scaleMedium = new JRadioButtonMenuItem("Medium");
        JRadioButtonMenuItem scaleLarge = new JRadioButtonMenuItem("Large");

        JMenuItem openServerFolder = new JMenuItem("Open current server's folder");
        JMenuItem openConfigFile = new JMenuItem("<html>Open config file\n<sub><center>" +Config.abbreviateConfigPath() + "</center></sub></html>"); //absolute garbage

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


        JButton debugShit1 = new JButton("Destroy config");
        JButton debugShit2 = new JButton("Create config");


        menuBar.setBorder(new MatteBorder(0,0,1,0, Colors.BORDER_COLOR));
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(debugShit1);
        menuBar.add(debugShit2);
        setJMenuBar(menuBar);

        ContainerPane containerPane = new ContainerPane();
        add(containerPane, BorderLayout.CENTER);
        setVisible(true);
        containerPane.setChartsVisibility(areChartsEnabled);


        debugShit1.addActionListener(e -> {
            Config.clearConfig();
//            remove(containerPane);
//            repaint();
        });

        debugShit2.addActionListener(e -> {
//            try {
//                Config.createConfig();
//            } catch (Exception ex) {
//                throw new RuntimeException(ex);
//            }

            ContainerPane.addServer(Config.getData().get(1));
//            ContainerPane.addServer(new MCServer("Test", new File(""), new File(""), new File(""), "", Config.getData().size() + 1, new File("")));
        });

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
        openServerFolder.addActionListener(e -> DesktopOpener.openServerFolder(containerPane.getSelectedIndex()));

        areChartsEnabled = getUserValues().getBoolean(PREFS_ARE_CHARTS_ENABLED, true);
        showCharts.setSelected(areChartsEnabled);


        switch(SERVER_STATUS_ICON_DIMENSION) {
            case 16 -> scaleSmall.setSelected(true);
            case 32 -> scaleMedium.setSelected(true);
            case 48 -> scaleLarge.setSelected(true);
        }

        int savedXPosition = getUserValues().getInt(PREFS_KEY_X, 0);
        int savedYPosition = getUserValues().getInt(PREFS_KEY_Y, 0);
        int savedWidth = getUserValues().getInt(PREFS_KEY_WIDTH, 1000);
        int savedHeight = getUserValues().getInt(PREFS_KEY_HEIGHT, 550);
        setBounds(savedXPosition, savedYPosition, savedWidth, savedHeight);



        openConfigFile.addActionListener(e -> DesktopOpener.openConfigFile());



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
                userSavedValues.putInt(PREFS_SERVER_ICONS_SCALE, SERVER_STATUS_ICON_DIMENSION);
                userSavedValues.putBoolean(PREFS_ARE_CHARTS_ENABLED, areChartsEnabled);

                containerPane.killAllServerProcesses();

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
                File temporaryWorldLevelDatFiles = new File("world_temp/worlds_level_dat");
                if(!temporaryWorldLevelDatFiles.exists())
                    temporaryWorldLevelDatFiles.mkdirs();
                clearTempDirectory();
            }

            @Override
            public void windowIconified(WindowEvent e) {
                taskbar.setWindowProgressState(Window.getWindow(), Taskbar.State.OFF);
                Window.getWindow().setState(Window.ICONIFIED);
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
                taskbar.setWindowProgressState(Window.getWindow(), Taskbar.State.OFF);
                Window.getWindow().setState(Window.NORMAL);
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                taskbar.setWindowProgressState(Window.getWindow(), Taskbar.State.OFF);
                Window.getWindow().setState(Window.ICONIFIED);
            }

            @Override
            public void windowGainedFocus(WindowEvent e) {
                taskbar.setWindowProgressState(Window.getWindow(), Taskbar.State.OFF);
                Window.getWindow().setState(Window.NORMAL);
            }
        });
        window = this;
    }

    public static String getErrorDialogMessage(Exception e) {
        Toolkit.getDefaultToolkit().beep();
        taskbar.setWindowProgressState(Window.getWindow(), Taskbar.State.ERROR);
        taskbar.setWindowProgressValue(Window.getWindow(), 100);
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
        taskbar.setWindowProgressState(Window.getWindow(), Taskbar.State.OFF);
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
        new File(tempFilesDirectory.getAbsolutePath() + "/worlds_level_dat").mkdirs();
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
        Config.createConfig();

        InputStream inputStream = Config.getResource(Config.RESOURCES_PATH + "/DarkFlatTheme/DarkFlatTheme.json");
        IntelliJTheme.setup(inputStream);

        SwingUtilities.invokeLater(() -> {
            try {
                new Window();
            } catch (Exception e) {
                alert(AlertType.ERROR, getErrorDialogMessage(e));
                throw new RuntimeException(e);
            }
        });
    }
}
