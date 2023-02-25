package Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonPanel extends JPanel implements ActionListener {

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



    public ButtonPanel() {
        setLayout(new GridLayout(10, 5, 10, 10));
        // Add 10 JButtons to the panel
        for (int i = 1; i <= 100; i++) {
            JButton button = createButton("Button " + i);
            setButtonIcon(button, "app_icon.png");

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
        System.out.println("Button " + index + " was clicked!"); // Print the index of the clicked button
    }
}
