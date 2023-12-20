package com.myne145.serverlauncher.gui.addserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AddServerPanel extends javax.swing.JPanel {
    private final JButton browseJavaPath = new JButton("...");
    private final JButton browseServerJar = new JButton("...");
    private final JButton confirmButton = new JButton("Add server");

    private final JTextField javaPathInput = new JTextField();
    private final JTextField launchArgsInput = new JTextField();
    private final JTextField serverJarInput = new JTextField();
    private final JTextField serverNameInput = new JTextField();

    public AddServerPanel() {
        setPreferredSize(new Dimension(428, 324));
        setLayout(new BorderLayout());
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new javax.swing.BoxLayout(fieldsPanel, javax.swing.BoxLayout.Y_AXIS));

        JLabel titleText = new JLabel("Add a Server");
        titleText.setFont(new Font("Arial", Font.BOLD, 18));
//        titleText.setText("Add a Server");
        titleText.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(titleText, BorderLayout.CENTER);

        add(titlePanel, BorderLayout.PAGE_START);


//        launchArgsInput.setDisabledTextColor(new Color(140, 140, 140));
//        launchArgsInput.setPlaceholder("nogui");

        new TextPrompt("nogui", launchArgsInput);
        new TextPrompt("java", javaPathInput);

        confirmButton.setEnabled(false);

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

//        browseServerJar.setText("...");

        serverJarPanel.add(browseServerJar);

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

//        browseJavaPath.setText("...");
        javaPathPanel.add(browseJavaPath);

        fieldsPanel.add(javaPathPanel);

//        launchArgsPanel.setMaximumSize(new Dimension(32767, 40));

//        launchArgsLabel.setText("Launch args:");
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


        browseJavaPath.addActionListener(evt -> {

        });

        browseServerJar.addActionListener(evt -> {

        });

        confirmButton.addActionListener(evt -> {

        });

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
                System.out.println(textField);
//                System.out.println(e.getSource());
            }
        };

        serverNameInput.addKeyListener(keyAdapter);
        serverJarInput.addKeyListener(keyAdapter);
        javaPathInput.addKeyListener(keyAdapter);
        launchArgsInput.addKeyListener(keyAdapter);

        javaPathInput.addMouseListener(mouseListener);
        launchArgsInput.addMouseListener(mouseListener);
    }

    private static boolean isEmpty(JTextField t) {
        return t.getText().isEmpty() || t.getText() == null;
    }

    private void handleKeyTyping(KeyEvent e) {
        confirmButton.setEnabled(!isEmpty(serverNameInput) && !isEmpty(serverJarInput));
    }
}
