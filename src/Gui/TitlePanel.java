package Gui;

import javax.swing.*;
import java.awt.*;

public class TitlePanel extends JPanel {
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 18);
    private static final Dimension PREFERRED_SIZE = new Dimension(0, 50);

    public TitlePanel() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Select server below:");
        title.setFont(TITLE_FONT);
        JPanel serversTitlePanel = new JPanel();
        serversTitlePanel.add(title, BorderLayout.CENTER);
        serversTitlePanel.add(Box.createRigidArea(new Dimension(10, 10)), BorderLayout.LINE_START);

        JLabel titleAddWorlds = new JLabel("Or add a world to existing server:");
        titleAddWorlds.setFont(TITLE_FONT);

        add(serversTitlePanel, BorderLayout.LINE_START);
        add(Box.createRigidArea(new Dimension(10, 10)), BorderLayout.CENTER);
        add(titleAddWorlds, BorderLayout.LINE_END);
        add(new JSeparator(SwingConstants.HORIZONTAL), BorderLayout.PAGE_END);

        setPreferredSize(PREFERRED_SIZE);
    }
}
