package Gui;

import SelectedServer.NBTParser;
import SelectedServer.ServerDetails;
import SelectedServer.ServerPropertiesFile;
import Server.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static Gui.ServerSelectionPanel.addWorldsPanel;

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
        repaint();
    }
    public void clearAllButtons() throws IOException {
        for(JButton button : buttons)
            remove(button);
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        initialize();
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
    public void actionPerformed(ActionEvent e) {
        Config config;
        try {
            config = new Config();
        } catch (IOException ex) {
            Frame.alert(AlertType.ERROR, Frame.getErrorDialogMessage(ex));
            return;
        }
        int index = Integer.parseInt(e.getActionCommand());
        ButtonData serverConfig = config.getData().get(index);
        try {
            ServerSelectionPanel.setServerVariables(serverConfig.getButtonText(), serverConfig.getPathToServerFolder(), serverConfig.getServerId());
            new ServerPropertiesFile(); //this needs a refactor - makes level-name actually update TODO
            NBTParser nbtParser = new NBTParser(); //reading NBT level.dat file for level name
//            nbtParser.setLaunchingServer(true);
            nbtParser.start();
            nbtParser.join();
            ServerDetails.serverLevelName = nbtParser.getLevelName();
//            nbtParser.setLaunchingServer(true);
        } catch (Exception ex) {
//            Frame.alert(AlertType.ERROR, Frame.getErrorDialogMessage(ex)); //shut the fuck up, it always throws this exception NO MATTER FUCKING WHAT
        }

        ServerSelectionPanel.getServerSelection().setSelectedIndex(index);

        addWorldsPanel.setIcons();

        new Runner(serverConfig.getPathToServerJarFile(), RunMode.SERVER_JAR, serverConfig.getPathToJavaRuntime(),
                serverConfig.getServerLaunchArguments()).start();
    }
}
