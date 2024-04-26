package com.myne145.serverlauncher.gui.tabs.addserver;

import com.myne145.serverlauncher.gui.window.Window;
import com.myne145.serverlauncher.server.Config;
import com.myne145.serverlauncher.server.MCServer;
import jnafilechooser.api.JnaFileChooser;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class AddServerPanel extends javax.swing.JPanel {
    private final JButton confirmButton = new JButton("Add server");

    private final JTextField javaPathInput = new JTextField();
    private final JTextField launchArgsInput = new JTextField();
    private final JTextField serverJarInput = new JTextField();
    private final JTextField serverNameInput = new JTextField();
    private static File serverJarPath;
    private static File javaBinPath;

    public AddServerPanel() {
        setLayout(new BorderLayout());
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new javax.swing.BoxLayout(fieldsPanel, javax.swing.BoxLayout.Y_AXIS));

        setBorder(new LineBorder(Color.RED));
        setOpaque(true);

        JLabel titleText = new JLabel("Add a server");
        titleText.setFont(new Font("Arial", Font.BOLD, 18));
        titleText.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));


        add(titleText, BorderLayout.PAGE_START);



        new TextPrompt("nogui", launchArgsInput);
        new TextPrompt("java", javaPathInput);

        confirmButton.setEnabled(false);

        confirmButton.addActionListener(e -> {
            MCServer currentServer = new MCServer(
                    serverNameInput.getText(),
                    serverJarPath.getParentFile(),
                    serverJarPath,
                    javaBinPath,
                    launchArgsInput.getText(),
                    Config.getData().size() + 1,
                    getSer



            );

        });


        serverJarInput.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                setServerJarPath(new File[]{serverJarPath}, serverJarInput);
                repaint();
            }
        });

        javaPathInput.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                setServerJarPath(new File[]{javaBinPath}, javaPathInput);
                repaint();
            }
        });


        JPanel serverNamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        serverNamePanel.setMaximumSize(new Dimension(32767, 40));
        serverNamePanel.setMinimumSize(new Dimension(128, 25));
        serverNamePanel.setPreferredSize(new Dimension(944, 50));


//        serverNameLabel.setText("Name:");
        JLabel serverNameLabel = new JLabel("Name: ");
        serverNameLabel.setPreferredSize(new Dimension(68, 16));
        serverNamePanel.add(serverNameLabel);

        serverNameInput.setPreferredSize(new Dimension(250, 22));

        serverNamePanel.add(serverNameInput);

        fieldsPanel.add(serverNamePanel);

        JPanel serverJarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        serverJarPanel.setMaximumSize(new Dimension(32767, 40));
        serverJarPanel.setMinimumSize(new Dimension(178, 25));
        serverJarPanel.setPreferredSize(new Dimension(364, 50));

//        serverJarLabel.setText("Server jar:");
        JLabel serverJarLabel = new JLabel("Server jar: ");
        serverJarLabel.setPreferredSize(new Dimension(68, 16));
        serverJarPanel.add(serverJarLabel);

        serverJarInput.setPreferredSize(new Dimension(250, 22));

        serverJarPanel.add(serverJarInput);

        fieldsPanel.add(serverJarPanel);

        JPanel javaPathPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        javaPathPanel.setMaximumSize(new Dimension(32767, 40));
        javaPathPanel.setMinimumSize(new Dimension(178, 25));
        javaPathPanel.setPreferredSize(new Dimension(364, 50));

//        javaPathLabel.setText("Java path:");
        JLabel javaPathLabel = new JLabel("Java path: ");
        javaPathLabel.setMaximumSize(new Dimension(72, 16));
        javaPathLabel.setMinimumSize(new Dimension(72, 16));
        javaPathLabel.setPreferredSize(new Dimension(68, 16));
        javaPathPanel.add(javaPathLabel);

        javaPathInput.setPreferredSize(new Dimension(250, 22));

        javaPathPanel.add(javaPathInput);


        fieldsPanel.add(javaPathPanel);


        JLabel launchArgsLabel = new JLabel("Launch args: ");
        JPanel launchArgsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        launchArgsPanel.add(launchArgsLabel);

        launchArgsInput.setPreferredSize(new Dimension(250, 22));

        launchArgsPanel.add(launchArgsInput);

        fieldsPanel.add(launchArgsPanel);

        add(fieldsPanel, BorderLayout.CENTER);


//        confirmButton.setText("Add Server");
        JPanel addAServerButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addAServerButtonPanel.add(confirmButton);

        add(addAServerButtonPanel, BorderLayout.PAGE_END);


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

        MouseListener mouseListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                JTextField textField = (JTextField) e.getSource();

            }
        };

        
        serverNameInput.addKeyListener(keyAdapter);
        serverJarInput.addKeyListener(keyAdapter);
        javaPathInput.addKeyListener(keyAdapter);
        launchArgsInput.addKeyListener(keyAdapter);

        javaPathInput.addMouseListener(mouseListener);
        launchArgsInput.addMouseListener(mouseListener);
    }


    private void setServerJarPath(File[] f, JTextField field) {
        Runnable runnable = () -> {
            JnaFileChooser fileDialog = new JnaFileChooser();
            fileDialog.showOpenDialog(Window.getWindow());

            File[] filePaths = fileDialog.getSelectedFiles();

            if (fileDialog.getSelectedFiles().length == 0 || filePaths == null || filePaths[0] == null) {
                return;
            }

            f[0] = filePaths[0];
            field.setText(f[0].getAbsolutePath());
        };
        Thread thread = new Thread(runnable);
        thread.setName("FILE_PICKER");
        thread.start();
    }


    private static boolean isEmpty(JTextField t) {
        return t.getText().isEmpty() || t.getText() == null;
    }

    private void handleKeyTyping(KeyEvent e) {
        confirmButton.setEnabled(!isEmpty(serverNameInput) && !isEmpty(serverJarInput));
    }
}
