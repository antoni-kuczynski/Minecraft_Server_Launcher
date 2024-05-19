package com.myne145.serverlauncher.gui.window;

import com.formdev.flatlaf.ui.FlatLineBorder;
import com.myne145.serverlauncher.gui.components.OpenContextMenuItem;
import com.myne145.serverlauncher.gui.tabs.addserver.AddServerPanel;
import com.myne145.serverlauncher.server.Config;
import com.formdev.flatlaf.IntelliJTheme;
//import com.myne145.serverlauncher.utils.AlertType;
import com.myne145.serverlauncher.utils.Colors;
import com.myne145.serverlauncher.utils.DateFormat;
import com.myne145.serverlauncher.utils.DefaultIcons;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
    private final String PREFS_SERVER_ICONS_SCALE = "prefs_server_icons_scale";
    private static final Preferences userValues = Preferences.userNodeForPackage(Window.class);
    private static boolean areChartsEnabled;
    public static int SERVER_STATUS_ICON_DIMENSION;
    public static DateFormat dateFormat = DateFormat.YYYY_MM_DD;
    private static Window window;
    private final static Taskbar taskbar = Taskbar.getTaskbar();
    private static final JMenuBar menuBar = new JMenuBar();
    private final JButton addServerButton = new JButton("Add server");
    private final Container glassPane = (Container) getRootPane().getGlassPane();

    public Window() {
        // Set up the JFrame
        setIconImage(DefaultIcons.getIcon(DefaultIcons.APP_ICON).getImage());
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

//        JMenuItem openServerFolder = new JMenuItem("Open current server's folder");
        OpenContextMenuItem openConfigFile = new OpenContextMenuItem("Open config file");
        openConfigFile.updatePath(new File(Config.ABSOLUTE_PATH));

        fileMenu.add(new OpenContextMenuItem("Open current server's folder"));
        fileMenu.add(openConfigFile);

        buttonGroup.add(scaleSmall);
        buttonGroup.add(scaleMedium);
        buttonGroup.add(scaleLarge);
        serverButtonsScale.add(scaleSmall);
        serverButtonsScale.add(scaleMedium);
        serverButtonsScale.add(scaleLarge);

        viewMenu.add(showCharts);
        viewMenu.add(serverButtonsScale);


        menuBar.setBorder(new MatteBorder(0,0,1,0, Colors.BORDER_COLOR));
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
//        menuBar.add(debugShit1);
        setJMenuBar(menuBar);

        ContainerPane containerPane = new ContainerPane();



        glassPane.setVisible(true);
        glassPane.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(130, 0, 0, 15);
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        glassPane.add(addServerButton, gbc);

        addServerButton.setPreferredSize(new Dimension(220, 50));
        addServerButton.setBorder(new FlatLineBorder(new Insets(5,5,5,5), Colors.BORDER_COLOR));
        addServerButton.setBackground(Colors.TABBEDPANE_BACKGROUND_COLOR);


        addServerButton.addActionListener(e -> {
            JDialog dialog = new JDialog((Dialog) null);

            dialog.setTitle("Add server");
            dialog.setIconImage(DefaultIcons.getIcon(DefaultIcons.APP_ICON).getImage());
            dialog.getRootPane().putClientProperty("JRootPane.titleBarBackground", Colors.TABBEDPANE_BACKGROUND_COLOR);
            dialog.getRootPane().putClientProperty("JRootPane.titleBarForeground", Colors.TEXT_COLOR);

//            dialog.setBounds((this.getWidth() - this.getX()) / 2 - 380, (this.getHeight() - this.getY()) / 2 + 225, 760, 450);
            dialog.setBounds((this.getWidth() - this.getX()) / 2 - 225, (this.getHeight() - this.getY()) / 2 + 225, 450, 450);
            dialog.add(new AddServerPanel(containerPane, dialog));
            dialog.setResizable(false);

//            dialog.setSize(300, 400);
//            dialog.setSize(new Dimension(760, 450));
//            dialog.setMinimumSize(new Dimension(800, 550));
            dialog.setVisible(true);
        });

        add(containerPane, BorderLayout.CENTER);


        setVisible(true);
        containerPane.setChartsVisibility(areChartsEnabled);


        //TEMP
//        JDialog dialog = new JDialog();
//        dialog.setTitle("Add server");
//        dialog.add(new AddServerPanel(containerPane));
//        dialog.setSize(800, 500);
//        dialog.setVisible(true);


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
//        openServerFolder.addActionListener(e -> DesktopOpener.openServerFolder(containerPane.getSelectedIndex()));

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



//        openConfigFile.addActionListener(e -> DesktopOpener.openConfigFile());



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

//    protected void updateAddServerButtonsSize(int width) {
//        Dimension d = new Dimension(width, addServerButton.getPreferredSize().height);
//        addServerButton.setPreferredSize(d);
//        addServerButton.setSize(d);
//    }

//    public static String getErrorDialogMessage(Exception e) {
//        Toolkit.getDefaultToolkit().beep();
//        taskbar.setWindowProgressState(Window.getWindow(), Taskbar.State.ERROR);
//        taskbar.setWindowProgressValue(Window.getWindow(), 100);
//        StringBuilder errorMessage = new StringBuilder();
//        errorMessage.append(e).append("\n");
//        errorMessage.append("Caused by:\n");
//        StackTraceElement[] errorStackTrace = e.getStackTrace();
//        for (StackTraceElement element : errorStackTrace) {
//            errorMessage.append(element.toString());
//            errorMessage.append("\n");
//        }
//        return errorMessage.toString();
//    }

//    public static void alert(AlertType alertType, String message) {
//        switch(alertType) {
//            case INFO -> JOptionPane.showMessageDialog(null, message, "Information", JOptionPane.INFORMATION_MESSAGE);
//            case ERROR -> JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
//            case WARNING -> JOptionPane.showMessageDialog(null, message, "Warning", JOptionPane.WARNING_MESSAGE);
//            case FATAL -> JOptionPane.showMessageDialog(null, message, "Fatal Error", JOptionPane.ERROR_MESSAGE);
//        }
//        taskbar.setWindowProgressState(Window.getWindow(), Taskbar.State.OFF);
//    }

    public static void showErrorMessage(String basicInfo, Exception e) {
        new ErrorDialog(basicInfo, e).setVisible(true);
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
            } catch (IOException e) { //TODO
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

        System.out.println(Config.getData());
        InputStream inputStream = Config.getResource(Config.RESOURCES_PATH + "/DarkFlatTheme/DarkFlatTheme.json");
        IntelliJTheme.setup(inputStream);

        //            try {
        //            } catch (Exception e) {
        //                alert(, getErrorDialogMessage(e));
        //                throw new RuntimeException(e);
        //            }
        SwingUtilities.invokeLater(Window::new);
    }
}
