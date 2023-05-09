package Gui;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;

public class DebugWindow extends JFrame {
    private static DebugWindow window;
    public static final LinkedHashMap<String, String> debugVariables = new LinkedHashMap<>(){
        @Override
        public String put(String key, String value) {
            window.reloadText();
            return super.put(key, value);
        }
    };
    private final JLabel debugText = new JLabel();

    public DebugWindow() {
        setVisible(true);
        setTitle("Debug window");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(400,600));
        pack();

        reloadText();
        add(debugText);
    }

    public void setWindow(DebugWindow window) {
        DebugWindow.window = window;
    }

    private void reloadText() {
        StringBuilder displayedText = new StringBuilder();
        displayedText.append("<html>");
        for(int i = 0; i < debugVariables.values().size(); i++) {
            displayedText.append(debugVariables.keySet().toArray()[i]).append(": ").append(debugVariables.values().toArray()[i]).append("<br>");
        }
        displayedText.append("</html>");
        debugText.setText(displayedText.toString());
    }
}
