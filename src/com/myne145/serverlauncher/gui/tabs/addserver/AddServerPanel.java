package com.myne145.serverlauncher.gui.tabs.addserver;

import com.formdev.flatlaf.ui.FlatLineBorder;
import com.myne145.serverlauncher.gui.components.PickFileButton;
import com.myne145.serverlauncher.gui.window.ContainerPane;
import com.myne145.serverlauncher.server.Config;
import com.myne145.serverlauncher.server.MCServer;
import com.myne145.serverlauncher.utils.Colors;
import com.myne145.serverlauncher.utils.FilePickerButtonAction;
import com.myne145.serverlauncher.utils.ZipUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class AddServerPanel extends JPanel {
    private final JButton confirmButton = new JButton("Add server");
    private final Pair<JPanel, PickFileButton> openServerJarPanel = getOpenDirButtonPanel("Server jar", this::setServerJarPath);
    private final Pair<JPanel, PickFileButton> openJavaBinPanel = getOpenDirButtonPanel("Java bin", this::setJavaBinPath);
    private final ServerInfoPanel serverInfoPanel = new ServerInfoPanel();
    private final MCServer currentServer = new MCServer();

    private Pair<JPanel, PickFileButton> getOpenDirButtonPanel(String titleText, FilePickerButtonAction action) {
        PickFileButton pickFileButton = new PickFileButton("Open directory", new Dimension(130, 40), new Dimension(300, 40), action);
        JLabel titleLabel = new JLabel(titleText);
        JPanel result = new JPanel();

        titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        pickFileButton.setAlignmentX(LEFT_ALIGNMENT);

        result.setBackground(Colors.COMPONENT_PRIMARY_COLOR);
        result.add(titleLabel);
        result.add(pickFileButton);

        return Pair.of(result, pickFileButton);
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
        if(!path.isFile()) {
            openServerJarPanel.getValue().setImportButtonWarning("Not a file");
            return;
        }
        if(ZipUtils.getFileExtension(path).equals("jar")) {
//            serverJarPath = path;
            currentServer.setServerJarPath(path);
        } else {
            openServerJarPanel.getValue().setImportButtonWarning("Not a jar file");
        }
        if(currentServer.isComplete())
            confirmButton.setEnabled(true);

        openServerJarPanel.getValue().setCustomButtonText(path.getName());
        serverInfoPanel.updateText(currentServer);
    }

    private void setJavaBinPath(File path) {
        currentServer.setJavaRuntimePath(path);
        if(currentServer.isComplete())
            confirmButton.setEnabled(true);

        openJavaBinPanel.getValue().setCustomButtonText(path.getName() + ", Version: " + currentServer.getJavaVersion());
        serverInfoPanel.updateText(currentServer);
    }

    public AddServerPanel(ContainerPane parentPane) {
        setLayout(new BorderLayout());

        if (Config.getDefaultJava() == null) {
            openJavaBinPanel.getValue().setImportButtonWarning("No default java installation found");
        }

        currentServer.setJavaRuntimePath(Config.getDefaultJava());
        currentServer.setServerLaunchArgs("nogui");

        confirmButton.setEnabled(false);
        confirmButton.setAlignmentX(LEFT_ALIGNMENT);


        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleText = new JLabel("Add server");

        JPanel mainPanel = new JPanel();
        JPanel serverNameInputPanel = getTextInputPanel("Server name");
        JPanel launchArgsInputPanel = getTextInputPanel("Launch args");
        JPanel mainPanelWithSpacing = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JTextField serverNameInput = (JTextField) serverNameInputPanel.getComponent(1);
        JTextField launchArgsInput = (JTextField) launchArgsInputPanel.getComponent(1);


        JPanel serverInfoPanelWithSpacing = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JPanel bottomPanel = new JPanel(new BorderLayout());


        titleText.setFont(new Font("Arial", Font.BOLD, 18));
        titleText.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        titlePanel.add(titleText, BorderLayout.LINE_START);


        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new FlatLineBorder(new Insets(10, 100, 10, 100), Colors.BORDER_COLOR, 1, 16));
        mainPanel.setBackground(Colors.COMPONENT_PRIMARY_COLOR);
        mainPanel.setAlignmentX(LEFT_ALIGNMENT);


        mainPanel.add(new JLabel("<html><b><font size=4>Required</font></b></html>"));
        mainPanel.add(serverNameInputPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        mainPanel.add(openServerJarPanel.getKey());

        mainPanel.add(Box.createRigidArea(new Dimension(10, 50)));
        mainPanel.add(new JLabel("<html><b><font size=4>Optional</font></b></html>"));
        mainPanel.add(launchArgsInputPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        mainPanel.add(openJavaBinPanel.getKey());


        mainPanelWithSpacing.add(Box.createRigidArea(new Dimension(10, 10)));
        mainPanelWithSpacing.add(mainPanel);


        serverInfoPanelWithSpacing.add(serverInfoPanel);
        serverInfoPanelWithSpacing.add(Box.createRigidArea(new Dimension(10, 10)));


        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.add(confirmButton, BorderLayout.LINE_END);

        add(titlePanel, BorderLayout.PAGE_START);
        add(mainPanelWithSpacing, BorderLayout.LINE_START);
        add(Box.createRigidArea(new Dimension(10, 10)), BorderLayout.CENTER);
        add(serverInfoPanelWithSpacing, BorderLayout.LINE_END);
        add(bottomPanel, BorderLayout.PAGE_END);


        serverNameInput.putClientProperty("type", "server_name");
        serverNameInput.addKeyListener(getKeyAdapter(serverNameInput));

        launchArgsInput.putClientProperty("type", "launch_args");
        launchArgsInput.addKeyListener(getKeyAdapter(launchArgsInput));
        new TextPrompt("nogui", launchArgsInput);

        confirmButton.addActionListener(e -> {
            handleKeyTyping(serverNameInput);
            handleKeyTyping(launchArgsInput);

            Config.getData().add(currentServer);
            parentPane.addServer(currentServer);
        });
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
        serverInfoPanel.updateText(currentServer);
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
