package com.myne145.serverlauncher.gui.tabs.addserver;

import com.formdev.flatlaf.ui.FlatLineBorder;
import com.myne145.serverlauncher.gui.components.PickDirectoryButton;
import com.myne145.serverlauncher.gui.window.ContainerPane;
import com.myne145.serverlauncher.server.Config;
import com.myne145.serverlauncher.server.MCServer;
import com.myne145.serverlauncher.utils.Colors;
import com.myne145.serverlauncher.utils.DirectoryPickerButtonAction;
import com.myne145.serverlauncher.utils.ZipUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class AddServerPanel extends JPanel {
    private final JButton confirmButton = new JButton("Add server");
//    private File serverJarPath;
//    private File javaBinPath;
//    private String serverName;
//    private String launchArgs;
//    private boolean isServerComplete = false;
    private final MCServer currentServer = new MCServer();

    private JPanel getOpenDirButtonPanel(String titleText, DirectoryPickerButtonAction action) {
        PickDirectoryButton pickDirectoryButton = new PickDirectoryButton("Open directory", new Dimension(130, 40), new Dimension(300, 40), action);
        JLabel titleLabel = new JLabel(titleText);
        JPanel result = new JPanel();

        titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        pickDirectoryButton.setAlignmentX(LEFT_ALIGNMENT);

        result.setBackground(Colors.COMPONENT_PRIMARY_COLOR);
        result.add(titleLabel);
        result.add(pickDirectoryButton);

        return result;
    }

    private JPanel getTextInputPanel(String titleText) {
        JTextField field = new JTextField();
        JLabel titleLabel = new JLabel(titleText);
        JPanel result = new JPanel();

        titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        field.setAlignmentX(LEFT_ALIGNMENT);
        field.setBorder(new FlatLineBorder(new Insets(1,1,1,1), Colors.COMPONENT_PRIMARY_COLOR.darker(), 1, 3));
        field.setBackground(Colors.BACKGROUND_PRIMARY_COLOR);
        field.setPreferredSize(new Dimension(130, 20));

        result.setBackground(Colors.COMPONENT_PRIMARY_COLOR);
        result.add(titleLabel);
        result.add(field);

        return result;
    }

    private void setServerJarPath(File path) {
        if(!path.isFile())
            return;
        if(ZipUtils.getFileExtension(path).equals("jar")) {
//            serverJarPath = path;
            currentServer.setServerJarPath(path);
        }
        if(currentServer.isComplete())
            confirmButton.setEnabled(true);
    }

    private void setJavaBinPath(File path) {
        if(path.isFile()) {
//            javaBinPath = path.getParentFile();
            currentServer.setJavaRuntimePath(path.getParentFile());
        } else if(path.isDirectory()) {
//            javaBinPath = path;
            currentServer.setJavaRuntimePath(path);
        }
        if(currentServer.isComplete())
            confirmButton.setEnabled(true);
    }

    public AddServerPanel(ContainerPane parentPane) {
        setLayout(new BorderLayout());

        currentServer.setJavaRuntimePath(new File(System.getProperty("java.home")));
        currentServer.setServerLaunchArgs("nogui");
        confirmButton.setEnabled(false);

        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Add server");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        titlePanel.add(title, BorderLayout.LINE_START);

//        JPanel bottomPanel = new JPanel(new BorderLayout());
//        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));



        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new FlatLineBorder(new Insets(10, 100, 10, 100), Colors.BORDER_COLOR, 1, 16));
        mainPanel.setBackground(Colors.COMPONENT_PRIMARY_COLOR);
        mainPanel.setAlignmentX(LEFT_ALIGNMENT);

        JPanel openServerJarPanel = getOpenDirButtonPanel("Server jar", this::setServerJarPath);
        JPanel openJavaBinPanel = getOpenDirButtonPanel("Java bin", this::setJavaBinPath);
        JPanel serverNamePanel = getTextInputPanel("Server name");
        JPanel launchArgsPanel = getTextInputPanel("Launch args");

        mainPanel.add(openServerJarPanel);
        mainPanel.add(openJavaBinPanel);
        mainPanel.add(serverNamePanel);
        mainPanel.add(launchArgsPanel);

//        bottomPanel.add(confirmButton, BorderLayout.LINE_END);


        ServerInfoPanel serverInfoPanel = new ServerInfoPanel();
//        serverInfoPanel.setBorder(new FlatLineBorder(new Insets(10, 20, 100, 20), Colors.BORDER_COLOR, 1, 16));
        serverInfoPanel.setBackground(Colors.COMPONENT_PRIMARY_COLOR);
        serverInfoPanel.setAlignmentX(LEFT_ALIGNMENT);


        JPanel mainPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        mainPanel2.add(Box.createRigidArea(new Dimension(10,10)));
        mainPanel2.add(mainPanel);

//        JPanel serverInfoPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JPanel serverInfoPanel2 = new JPanel();
        BoxLayout boxLayout = new BoxLayout(serverInfoPanel2, BoxLayout.Y_AXIS);


        serverInfoPanel2.setBorder(new FlatLineBorder(new Insets(10, 20, 100, 20), Colors.BORDER_COLOR, 1, 16));
        serverInfoPanel2.setBackground(Colors.COMPONENT_PRIMARY_COLOR);
        serverInfoPanel2.setLayout(boxLayout);

        confirmButton.setAlignmentX(LEFT_ALIGNMENT);
        serverInfoPanel2.add(serverInfoPanel);
        serverInfoPanel2.add(Box.createRigidArea(new Dimension(10,10)));
        serverInfoPanel2.add(confirmButton);


        add(titlePanel, BorderLayout.PAGE_START);
        add(mainPanel2, BorderLayout.LINE_START);
        add(Box.createRigidArea(new Dimension(10,10)), BorderLayout.CENTER);
        add(serverInfoPanel2, BorderLayout.LINE_END);
        add(Box.createRigidArea(new Dimension(10,50)), BorderLayout.PAGE_END);

        JTextField serverNameField = (JTextField) serverNamePanel.getComponent(1);
        JTextField launchArgsField = (JTextField) launchArgsPanel.getComponent(1);

        serverNameField.putClientProperty("type", "server_name");
        serverNameField.addKeyListener(getKeyAdapter(serverNameField));

        launchArgsField.putClientProperty("type", "launch_args");
        launchArgsField.addKeyListener(getKeyAdapter(launchArgsField));

        confirmButton.addActionListener(e -> {
//            serverName = serverNameField.getText();
//            launchArgs = launchArgsField.getText();

//            MCServer currentServer = new MCServer(
//                    serverName,
//                    serverJarPath,
//                    serverJarPath,
//                    javaBinPath,
//                    launchArgs,
//                    Config.getData().size() + 1
//            );

//            Config.getData().add(currentServer);
//            parentPane.addServer(currentServer);
        });


//        serverJarInput.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mousePressed(MouseEvent e) {
//                Runnable runnable = () -> {
//                    JnaFileChooser fileDialog = new JnaFileChooser();
//                    fileDialog.showOpenDialog(Window.getWindow());
//
//                    File[] filePaths = fileDialog.getSelectedFiles();
//
//                    if (fileDialog.getSelectedFiles().length == 0 || filePaths == null || filePaths[0] == null) {
//                        return;
//                    }
//
//                    File fileToAdd = filePaths[0];
//                    serverJarInput.setText(fileToAdd.getAbsolutePath());
//                    repaint();
//                };
//                Thread thread = new Thread(runnable);
//                thread.setName("WORLD_FILE_PICKER");
//                thread.start();
//            }
//        });








//        javaPathInput.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mousePressed(MouseEvent e) {
//                Runnable runnable = () -> {
//                    JnaFileChooser fileDialog = new JnaFileChooser();
//                    fileDialog.showOpenDialog(Window.getWindow());
//
//                    File[] filePaths = fileDialog.getSelectedFiles();
//
//                    if (fileDialog.getSelectedFiles().length == 0 || filePaths == null || filePaths[0] == null) {
//                        return;
//                    }
//
//                    File fileToAdd = filePaths[0];
//                    javaPathInput.setText(fileToAdd.getAbsolutePath());
//                    repaint();
//                };
//                Thread thread = new Thread(runnable);
//                thread.setName("WORLD_FILE_PICKER");
//                thread.start();
//            }
//        });
//        serverNameInput.addKeyListener(keyAdapter);
//        serverJarInput.addKeyListener(keyAdapter);
    }

    private void handleKeyTyping(JTextField field) {
        if(field.getClientProperty("type") == null)
            return;
        if(field.getClientProperty("type").equals("server_name")) {
            currentServer.setServerName(field.getText());
        } else if(field.getClientProperty("type").equals("launch_args")) {
            currentServer.setServerLaunchArgs(field.getText());
        }

        if(currentServer.isComplete())
            confirmButton.setEnabled(true);
    }

    private KeyAdapter getKeyAdapter(JTextField field) {
        return new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                handleKeyTyping(field);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyTyping(field);
            }
        };
    }
}
