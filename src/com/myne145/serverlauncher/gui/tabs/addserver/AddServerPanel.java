package com.myne145.serverlauncher.gui.tabs.addserver;

import com.myne145.serverlauncher.gui.window.ContainerPane;
import com.myne145.serverlauncher.gui.window.Window;
import jnafilechooser.api.JnaFileChooser;

import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class AddServerPanel extends javax.swing.JPanel {
    private static File serverJarPath;
    private static File javaBinPath;

    public AddServerPanel(ContainerPane parentPane) {
        setLayout(new BorderLayout());


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
