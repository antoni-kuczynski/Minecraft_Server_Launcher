package Gui;

import javax.swing.*;
import java.awt.*;

public class TitlePanel extends JPanel {
    public TitlePanel() {
        setLayout(new BorderLayout());
        JLabel title = new JLabel("Select server below:");
        JLabel titleAddWorlds = new JLabel("Or add a world to existing server:");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        titleAddWorlds.setFont(new Font("Arial", Font.BOLD, 18));
        JPanel emptyPanel1 = new JPanel();
        JPanel emptyPanel2 = new JPanel();
        JPanel addWorldTitlePanel = new JPanel();
        emptyPanel1.setPreferredSize(new Dimension(10,10));
        emptyPanel2.setPreferredSize(new Dimension(10,10));

        addWorldTitlePanel.add(titleAddWorlds, BorderLayout.CENTER);
        addWorldTitlePanel.add(emptyPanel2, BorderLayout.LINE_END);

        add(emptyPanel1, BorderLayout.LINE_START);
        add(title, BorderLayout.CENTER);
        add(addWorldTitlePanel, BorderLayout.LINE_END);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(getWidth(), 50);
    }
}
