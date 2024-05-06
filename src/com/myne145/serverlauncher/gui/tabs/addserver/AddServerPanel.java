package com.myne145.serverlauncher.gui.tabs.addserver;

import com.formdev.flatlaf.ui.FlatLineBorder;
import com.myne145.serverlauncher.gui.components.PickDirectoryButton;
import com.myne145.serverlauncher.gui.window.ContainerPane;
import com.myne145.serverlauncher.gui.window.Window;
import com.myne145.serverlauncher.utils.Colors;
import jnafilechooser.api.JnaFileChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class AddServerPanel extends JPanel {
    private static File serverJarPath;
    private static File javaBinPath;

    public AddServerPanel(ContainerPane parentPane) {
        setLayout(new BorderLayout());

        PickDirectoryButton serverJarPathButton = new PickDirectoryButton("Open directory", new Dimension(130, 40), new Dimension(300, 40), fileToAdd -> serverJarPath = fileToAdd);
        PickDirectoryButton javaBinPathButton = new PickDirectoryButton("Open directory", new Dimension(130, 40), new Dimension(300, 40), fileToAdd -> javaBinPath = fileToAdd);
//        serverJarPathButton.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
//        javaBinPathButton.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Add server");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        titlePanel.add(title, BorderLayout.LINE_START);

        JPanel bottomPanel = new JPanel(new BorderLayout());


        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new FlatLineBorder(new Insets(10, 100, 10, 100), Colors.BORDER_COLOR, 1, 16));
        mainPanel.setBackground(Colors.COMPONENT_PRIMARY_COLOR);

        JPanel serverJar = new JPanel();
//        serverJar.setBorder(new FlatLineBorder(new Insets(10, 10, 10, 0), Colors.BORDER_COLOR, 1, 16));
        serverJar.setBackground(Colors.COMPONENT_PRIMARY_COLOR);

        JPanel javaBin = new JPanel();
//        javaBin.setBorder(new FlatLineBorder(new Insets(10, 10, 10, 0), Colors.BORDER_COLOR, 1, 16));
        javaBin.setBackground(Colors.COMPONENT_PRIMARY_COLOR);

        serverJar.add(serverJarPathButton);
        javaBin.add(javaBinPathButton);

        mainPanel.add(serverJar);
        mainPanel.add(javaBin);

        bottomPanel.add(new JButton("fosdaosdjfojisdf"));

        add(titlePanel, BorderLayout.PAGE_START);
        add(Box.createRigidArea(new Dimension(10, 10)), BorderLayout.LINE_START);
        add(mainPanel, BorderLayout.CENTER);
        add(Box.createRigidArea(new Dimension(10, 10)), BorderLayout.LINE_END);
        add(bottomPanel, BorderLayout.PAGE_END);
//        confirmButton.addActionListener(e -> {
//            MCServer currentServer = new MCServer(
//                    serverNameInput.getText(),
//                    serverJarPath.getParentFile(),
//                    serverJarPath,
//                    javaBinPath,
//                    launchArgsInput.getText(),
//                    Config.getData().size() + 1,
//                    new File(Config.getServerWorldPath(serverJarPath.getParent()))
//            );
//            Config.getData().add(currentServer);
//            parentPane.addServer(currentServer);
//        });


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
        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                handleKeyTyping(e);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyTyping(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                handleKeyTyping(e);
            }
        };


//        serverNameInput.addKeyListener(keyAdapter);
//        serverJarInput.addKeyListener(keyAdapter);
    }

    private void handleKeyTyping(KeyEvent e) {

    }
}
