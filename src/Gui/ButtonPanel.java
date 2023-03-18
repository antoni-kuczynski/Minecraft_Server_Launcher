package Gui;

import Servers.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class ButtonPanel extends JPanel implements ActionListener {
    private final Config config = new Config();

    public void setButtonIcon(JButton button, String iconPath) {
        ImageIcon icon = new ImageIcon(iconPath);
        Image image = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(image);
        button.setIcon(scaledIcon);
    }

    public JButton createButton(String label) {
        JButton button = new JButton(label);
        FontMetrics metrics = button.getFontMetrics(button.getFont());
        int width = metrics.stringWidth(label) + 20; // Add padding to the width
        int height = metrics.getHeight() + 10; // Add padding to the height
        button.setPreferredSize(new Dimension(width, height));
        return button;
    }



    public ButtonPanel() throws IOException {
        setLayout(new GridLayout(10, 5, 10, 10));
        // Add i JButtons to the panel
        for (int i = 0; i < config.getData().size(); i++) {
                JButton button = createButton(config.getData().get(i).getButtonText());

                button.setEnabled(false);
                if(new File(config.getData().get(i).getPathToServerFolder()).exists()) {
                    button.setEnabled(true);
                    button.setToolTipText("Server path: " + config.getData().get(i).getPathToServerFolder() + "\nServer executable: " +
                            config.getData().get(i).getPathToServerJarFile() + "\nJava executable: " + config.getData().get(i).getPathToJavaRuntime() + "\nLaunch arguments: " +
                            config.getData().get(i).getServerLaunchArguments());
                } else {
                    button.setToolTipText("Server files not found." + "\nServer path: " + config.getData().get(i).getPathToServerFolder() + "\nServer executable: " +
                            config.getData().get(i).getPathToServerJarFile() + "\nJava executable: " + config.getData().get(i).getPathToJavaRuntime() + "\nLaunch arguments: " +
                            config.getData().get(i).getServerLaunchArguments());
                }
                setButtonIcon(button, config.getData().get(i).getPathToButtonIcon());


                button.setPreferredSize(new Dimension(100, 40)); // Set the preferred size of the button
                button.setFont(new Font("Arial", Font.PLAIN, 14)); // Set the font and size of the button text
                button.addActionListener(this); // Add the action listener to the button
                button.setActionCommand(Integer.toString(i)); // Set the action command to the index of the button
                add(button);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.setPreferredSize(new Dimension(getWidth(), getHeight() - 50));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String index = e.getActionCommand(); // Get the action command (i.e., the index of the button)

        new Runner(config.getData().get(Integer.parseInt(index)).getPathToServerJarFile(), //holy fuck
                Run.SERVER_JAR, config.getData().get(Integer.parseInt(index)).getPathToJavaRuntime(),
                config.getData().get(Integer.parseInt(index)).getServerLaunchArguments()).start();

        ConfigStuffPanel.setServerVariables(config.getData().get(Integer.parseInt(index)).getButtonText(),
                config.getData().get(Integer.parseInt(index)).getPathToServerFolder());
    }
}
