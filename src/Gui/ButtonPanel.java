package Gui;

import Enums.AlertType;
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

import static Gui.ServerSelectionPanel.worldsTab;

public class ButtonPanel extends JPanel implements ActionListener {
    private final Font BUTTON_FONT = new Font("Arial", Font.PLAIN, 14);
    private final ArrayList<JButton> buttons = new ArrayList<>();

    public void initializeServerButtons(JPanel buttonContainer) throws IOException {
        buttons.clear();
        Config config = new Config();
        ArrayList<ButtonData> serverConfig = config.getData();
        for (int serverIndex = 0; serverIndex < serverConfig.size(); serverIndex++) {
            JButton serverLaunchButton = createButton(serverConfig.get(serverIndex).getButtonText());
            boolean doServerFilesExist = new File(serverConfig.get(serverIndex).getPathToServerFolder()).exists();
            serverLaunchButton.setEnabled(doServerFilesExist);
            String buttonToolTipText = "Server path: " + serverConfig.get(serverIndex).getPathToServerFolder()
                    + "\nServer executable: " + serverConfig.get(serverIndex).getPathToServerJarFile()
                    + "\nJava executable: " + serverConfig.get(serverIndex).getPathToJavaRuntime()
                    + "\nLaunch arguments: " + serverConfig.get(serverIndex).getServerLaunchArguments();
            if (!doServerFilesExist) {
                buttonToolTipText = "Server files not found." + "\n" + buttonToolTipText;
            }
            serverLaunchButton.setToolTipText(buttonToolTipText);
            setButtonIcon(serverLaunchButton, serverConfig.get(serverIndex).getPathToButtonIcon());
            serverLaunchButton.setPreferredSize(new Dimension(100, 40));
            serverLaunchButton.setFont(BUTTON_FONT);
            serverLaunchButton.addActionListener(this);
            serverLaunchButton.setActionCommand(Integer.toString(serverIndex));
            buttons.add(serverLaunchButton);
            buttonContainer.add(serverLaunchButton);
        }
        repaint();
    }

    public ButtonPanel() throws IOException {
//        setBackground(new Color(26, 26, 26));
        setPreferredSize(new Dimension(250, getHeight()));
        // Create the container panel
        JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.PAGE_AXIS));

        // Set the container panel as the content of the JScrollPane
        JScrollPane scrollPane = new JScrollPane(buttonContainer);

        // Call the method to initialize the server buttons
        initializeServerButtons(buttonContainer);

        // Add the scroll pane to the ButtonPanel
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
    }

    public void setButtonIcon(JButton button, String iconPath) {
        ImageIcon icon = new ImageIcon(iconPath);
        Image image = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(image);
        button.setIcon(scaledIcon);
    }

    public void clearAllButtons() throws IOException {
        for(JButton button : buttons)
            remove(button);
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            Frame.alert(AlertType.ERROR, Frame.getErrorDialogMessage(e));
        }
//        initializeServerButtons();
    }

    public JButton createButton(String label) {
        JButton button = new JButton(label);

        // Set the desired width and height for the button
        Dimension buttonSize = new Dimension(300, 50);
        button.setPreferredSize(buttonSize);
        button.setMinimumSize(buttonSize);
        button.setMaximumSize(buttonSize);

        button.setBorderPainted(false);
        button.setBorder(null);
//        button.setBackground(new Color(13, 17, 23));
        return button;
    }

    public void onButtonClicked(int index) {
        Config config;
        try {
            config = new Config();
        } catch (IOException ex) {
            Frame.alert(AlertType.ERROR, Frame.getErrorDialogMessage(ex));
            return;
        }
//        int index = Integer.parseInt(e.getActionCommand());
        ButtonData serverConfig = config.getData().get(index);
        try {
            ServerSelectionPanel.setServerVariables(serverConfig.getButtonText(), serverConfig.getPathToServerFolder(), serverConfig.getServerId());
            new ServerPropertiesFile(); //this needs a refactor - makes level-name actually update TODO
            NBTParser nbtParser = new NBTParser(); //reading NBT level.dat file for level name
            nbtParser.start();
            nbtParser.join();
            ServerDetails.serverLevelName = nbtParser.getLevelName();
        } catch (Exception ex) {
            // Frame.alert(AlertType.ERROR, Frame.getErrorDialogMessage(ex));
        }
//        ServerSelectionPanel.getServerSelection().setSelectedIndex(index);
        worldsTab.setIcons();

        buttons.get(ServerDetails.serverId - 1).setBackground(Color.RED);
        for(JButton b : buttons) {
            if(b.getBackground() == Color.RED && buttons.indexOf(b) != ServerDetails.serverId - 1) {
//                b.setContentAreaFilled(false);
//                b.setContentAreaFilled(true);
                Color bgColor = UIManager.getColor("Button.background");
                b.setBackground(bgColor);
////                b.setBackground(new Color(bgColor.getRed() + 25, bgColor.getBlue() + 25, bgColor.getGreen() + 25));
            }
            //ij darcula theme
            //button color rgb(85, 88, 90)
            //bg color rgb(60, 63, 65)
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        onButtonClicked(Integer.parseInt(e.getActionCommand()));
    }

    public void setBorders() {
        for(JButton b : buttons) {
            b.setBorderPainted(false);
            b.setBorder(null);
        }
    }
}
