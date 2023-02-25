package Gui;

import javax.swing.*;
import java.awt.*;

public class TitlePanel extends JPanel {
    public TitlePanel() {
        JLabel title = new JLabel("Select server below:");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(getWidth(), 50);
    }
}
