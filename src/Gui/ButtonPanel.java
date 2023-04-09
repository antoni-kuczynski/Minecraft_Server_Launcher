package Gui;

import Server.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ButtonPanel extends JPanel implements ActionListener {
    private static final int BUTTON_WIDTH_PADDING = 20;
    private static final int BUTTON_HEIGHT_PADDING = 10;
    private final ArrayList<JButton> buttons = new ArrayList<>();

    public void initialize() throws IOException {
        buttons.clear();
        Config config = new Config();
        ArrayList<ButtonData> serverConfigs = config.getData();
        for (int i = 0; i < serverConfigs.size(); i++) {
            JButton button = createButton(serverConfigs.get(i).getButtonText());
            boolean doServerFilesExist = new File(serverConfigs.get(i).getPathToServerFolder()).exists();
            button.setEnabled(doServerFilesExist);
            String toolTipText = "Server path: " + serverConfigs.get(i).getPathToServerFolder()
                    + "\nServer executable: " + serverConfigs.get(i).getPathToServerJarFile()
                    + "\nJava executable: " + serverConfigs.get(i).getPathToJavaRuntime()
                    + "\nLaunch arguments: " + serverConfigs.get(i).getServerLaunchArguments();
            if (!doServerFilesExist) {
                toolTipText = "Server files not found." + "\n" + toolTipText;
            }
            button.setToolTipText(toolTipText);
            setButtonIcon(button, serverConfigs.get(i).getPathToButtonIcon());
            button.setPreferredSize(new Dimension(100, 40));
            button.setFont(new Font("Arial", Font.PLAIN, 14));
            button.addActionListener(this);
            button.setActionCommand(Integer.toString(i));
            buttons.add(button);
            add(button);
        }
    }
    public void clearAllButtons() throws IOException {
        for(JButton button : buttons)
            remove(button);
//        repaint();
        initialize();
        repaint();

    }

    public ButtonPanel() throws IOException {
        setLayout(new GridLayout(10, 5, 10, 10));
        initialize();
    }

    public void setButtonIcon(JButton button, String iconPath) {
        ImageIcon icon = new ImageIcon(iconPath);
        Image image = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(image);
        button.setIcon(scaledIcon);
    }

    public JButton createButton(String label) {
        JButton button = new JButton(label);
        FontMetrics metrics = button.getFontMetrics(button.getFont());
        int width = metrics.stringWidth(label) + BUTTON_WIDTH_PADDING;
        int height = metrics.getHeight() + BUTTON_HEIGHT_PADDING;
        button.setPreferredSize(new Dimension(width, height));
        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.setPreferredSize(new Dimension(getWidth(), getHeight() - 50));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Config config;
        try {
            config = new Config();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        int index = Integer.parseInt(e.getActionCommand());
        ButtonData serverConfig = config.getData().get(index);
        new Runner(serverConfig.getPathToServerJarFile(), RunMode.SERVER_JAR, serverConfig.getPathToJavaRuntime(),
                serverConfig.getServerLaunchArguments()).start();
        try {
            ServerSelectionPanel.setServerVariables(serverConfig.getButtonText(), serverConfig.getPathToServerFolder());
        } catch (InterruptedException ex) {
            Frame.alert(AlertType.ERROR, Frame.exStackTraceToString(ex.getStackTrace()));
        }
        ServerSelectionPanel.getServerSelection().setSelectedIndex(index);
    }
}
