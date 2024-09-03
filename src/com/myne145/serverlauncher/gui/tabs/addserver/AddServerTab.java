package com.myne145.serverlauncher.gui.tabs.addserver;

import com.formdev.flatlaf.ui.FlatLineBorder;
import com.myne145.serverlauncher.utils.ButtonWarning;
import com.myne145.serverlauncher.gui.components.PickFileButton;
import com.myne145.serverlauncher.gui.window.ServerTabbedPane;
import com.myne145.serverlauncher.gui.window.ContainerPane;
import com.myne145.serverlauncher.gui.window.Window;
import com.myne145.serverlauncher.server.Config;
import com.myne145.serverlauncher.server.MinecraftServer;
import com.myne145.serverlauncher.utils.Colors;
import com.myne145.serverlauncher.utils.FilePickerButtonAction;
import com.myne145.serverlauncher.utils.TextPrompt;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

import static com.myne145.serverlauncher.gui.window.Window.getScaledSize;

public class AddServerTab extends JPanel {
    private final JButton confirmButton = new JButton("Add server");
    private Pair<JPanel, PickFileButton> openServerJarPanel = getOpenDirButtonPanel("Open server jar file", "Server jar", this::setServerJarPath);
    private Pair<JPanel, PickFileButton> openJavaBinPanel = getOpenDirButtonPanel("Open java bin file", "Java bin", this::setJavaBinPath);
    private final ServerInfoPanel serverInfoPanel = new ServerInfoPanel();
    private MinecraftServer currentServer = new MinecraftServer();

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(com.myne145.serverlauncher.gui.window.Window.getWindow() == null || currentServer.getServerJarPath() == null || openServerJarPanel.getValue().hasWarnings()) {
            return;
        }

        serverInfoPanel.setVisible(Window.getWindow().getWidth() >= 986);
    }

    public AddServerTab(ContainerPane parentPane) {
        setLayout(new BorderLayout());
        setFocusable(true);
        requestFocusInWindow();

        if (Config.getDefaultJava() == null) {
            openJavaBinPanel.getValue().setImportButtonWarning(ButtonWarning.NO_DEFAULT_JAVA);
        } else {
            setJavaBinPath(Config.getDefaultJava());
        }

        serverInfoPanel.setVisible(false);

        openServerJarPanel.getValue().setFileChooserFileExtension("jar");
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

        titleText.setFont(new Font("Arial", Font.BOLD, getScaledSize(18)));
        titleText.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        titlePanel.add(titleText, BorderLayout.LINE_START);

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new FlatLineBorder(new Insets(10, 100, 10, 100), Colors.BORDER_COLOR, 1, 16));
        mainPanel.setBackground(Colors.COMPONENT_PRIMARY_COLOR);
        mainPanel.setAlignmentX(LEFT_ALIGNMENT);


        mainPanel.add(new JLabel("<html><b><font size=4>Required</font></b></html>"));
        mainPanel.add(serverNameInputPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(10, 20)));
        mainPanel.add(openServerJarPanel.getKey());

        mainPanel.add(Box.createRigidArea(new Dimension(10, 80)));
        mainPanel.add(new JLabel("<html><b><font size=4>Optional</font></b></html>"));
        mainPanel.add(launchArgsInputPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(10, 20)));
        mainPanel.add(openJavaBinPanel.getKey());


        mainPanelWithSpacing.add(Box.createRigidArea(new Dimension(10, 10)));
        mainPanelWithSpacing.add(mainPanel);

        serverInfoPanelWithSpacing.add(serverInfoPanel);
        serverInfoPanelWithSpacing.add(Box.createRigidArea(new Dimension(10, 10)));

        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        bottomPanel.add(confirmButton, BorderLayout.LINE_END);


        serverInfoPanel.setPreferredSize(new Dimension(250, mainPanel.getPreferredSize().height));

        add(titlePanel, BorderLayout.PAGE_START);
        add(mainPanelWithSpacing, BorderLayout.LINE_START);
        add(Box.createRigidArea(new Dimension(10, 10)), BorderLayout.CENTER);
        add(serverInfoPanelWithSpacing, BorderLayout.LINE_END);
        add(bottomPanel, BorderLayout.PAGE_END);

        serverNameInput.putClientProperty("type", "server_name");
        serverNameInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                handleKeyTyping(serverNameInput);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyTyping(serverNameInput);
            }
        });

        launchArgsInput.putClientProperty("type", "launch_args");
        launchArgsInput.addKeyListener(getKeyAdapter(launchArgsInput));
        new TextPrompt("nogui", launchArgsInput);

        confirmButton.addActionListener(e -> {
            handleKeyTyping(serverNameInput);
            handleKeyTyping(launchArgsInput);

            Config.getData().add(currentServer);
            parentPane.addServer(currentServer);
            MinecraftServer.writeAllToConfig();


            parentPane.setComponentAt(0, new ServerTabbedPane(
                    new AddServerTab(parentPane)
            ));
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                requestFocusInWindow();
            }
        });

        System.out.println(serverInfoPanel.getPreferredSize());
        System.out.println(mainPanel.getPreferredSize());
    }

    private Pair<JPanel, PickFileButton> getOpenDirButtonPanel(String buttonText, String titleText, FilePickerButtonAction action) {
        PickFileButton pickFileButton = new PickFileButton(buttonText, new Dimension(130, 40), new Dimension(300, 40), action);
        JLabel titleLabel = new JLabel(titleText);
        JPanel result = new JPanel();

        pickFileButton.setTransferHandler(pickFileButton.getCustomTransferHandler(action));

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
        field.setPreferredSize(new Dimension(130, 25));


        result.setBackground(Colors.COMPONENT_PRIMARY_COLOR);
        result.add(titleLabel);
        result.add(field);

        return result;
    }

    private void setServerJarPath(File path) {
        boolean isValid = false;
//        if(!path.isFile()) {
//            openServerJarPanel.getValue().setImportButtonWarning("Not a file");
//        }
        if(FilenameUtils.getExtension(path.getName()).equals("jar")) {
            currentServer.setServerJarPath(path);
            isValid = true;
        } else {
            openServerJarPanel.getValue().setImportButtonWarning(ButtonWarning.NOT_A_JAR_FILE);
        }

        if(currentServer.isComplete())
            confirmButton.setEnabled(isValid);

        String s = path.getName();
        if(s.length() > 27) { //max 27chars
            s = s.substring(0, s.length() / 2 - (s.length() - 27) / 2) + "..." + s.substring(s.length() / 2 + (s.length() - 27) / 2);
        }

        openServerJarPanel.getValue().setCustomButtonText(s);
        openServerJarPanel.getValue().setToolTipText(path.getAbsolutePath());
        if(!isValid) {
            serverInfoPanel.setVisible(false);
            return;
        }

        serverInfoPanel.updateText(currentServer);
        serverInfoPanel.setVisible(true);
    }

    private void setJavaBinPath(File path) {
        currentServer.setJavaExecutablePath(path);
        if(currentServer.isComplete())
            confirmButton.setEnabled(true);

        openJavaBinPanel.getValue().setCustomButtonText(path.getName() + ", Version: " + currentServer.getJavaVersion());
        openJavaBinPanel.getValue().setToolTipText(path.getAbsolutePath());
        serverInfoPanel.updateText(currentServer);
    }

    private void handleKeyTyping(JTextField field) {
        if(field.getClientProperty("type") == null)
            return;
        if(field.getClientProperty("type").equals("server_name")) {
            currentServer.setServerName(field.getText());
        } else if(field.getClientProperty("type").equals("launch_args")) {
            currentServer.setServerLaunchArgs(field.getText());
        }

        confirmButton.setEnabled(currentServer.isComplete());
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
