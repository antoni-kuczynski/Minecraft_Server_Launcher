package com.myne145.serverlauncher.gui.window;

import com.formdev.flatlaf.icons.FlatOptionPaneErrorIcon;
import com.formdev.flatlaf.ui.FlatLineBorder;
//import com.myne145.serverlauncher.utils.AlertType;
import com.myne145.serverlauncher.utils.Colors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ErrorDialog extends JDialog {
    private static final FlatOptionPaneErrorIcon icon = new FlatOptionPaneErrorIcon();
//    private final static Taskbar taskbar = Taskbar.getTaskbar();

    protected ErrorDialog(String text, Exception e) {
        Dimension dimension = new Dimension((int) (400 * Window.getDisplayScale()), (int) (160 * Window.getDisplayScale()));
        setPreferredSize(dimension);
        setMinimumSize(dimension);
        setBounds(dimension.width, dimension.height, (int) (Window.getCenter().x - dimension.getWidth() / 2), (int) (Window.getCenter().y - dimension.getHeight() / 2));
        setTitle("Error");
        getRootPane().putClientProperty("JRootPane.titleBarBackground", Colors.TABBEDPANE_BACKGROUND_COLOR);
        getRootPane().putClientProperty("JRootPane.titleBarForeground", Colors.TEXT_COLOR);
//        Window.getTaskbar().setWindowProgressState(Window.getWindow(), Taskbar.State.ERROR);
//        Window.getTaskbar().setWindowProgressValue(Window.getWindow(), 100);


//        JLabel basicText = new JLabel("12345678912345678912345678912345678912345678912345");
        JLabel basicText = new JLabel("<html>" + text + "</html>");
        basicText.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        basicText.setIcon(icon);

        JPanel panel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new BorderLayout());
        JPanel stackTracePanel = new JPanel(new BorderLayout());

        JButton ok = new JButton("OK");
        JButton viewStackTraceButton = new JButton("View stack trace");
        JTextArea stackTraceArea = new JTextArea();
        JScrollPane stackTraceScrollPane = new JScrollPane(stackTraceArea);

        stackTraceArea.setText(getErrorDialogMessage(e));
        stackTraceArea.setEditable(false);
        stackTraceArea.setBorder(new FlatLineBorder(new Insets(10,10,10,10), Colors.BORDER_COLOR, 1, 16));
        stackTraceArea.setBackground(Colors.COMPONENT_PRIMARY_COLOR);
        stackTraceArea.setLineWrap(true);

        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        buttonPanel.add(ok, BorderLayout.LINE_END);
        buttonPanel.add(viewStackTraceButton, BorderLayout.LINE_START);

        stackTracePanel.add(Box.createRigidArea(new Dimension(10,10)), BorderLayout.LINE_START);
        stackTracePanel.add(stackTraceScrollPane, BorderLayout.CENTER);
        stackTracePanel.add(Box.createRigidArea(new Dimension(10,10)), BorderLayout.LINE_END);
        stackTracePanel.setVisible(false);

        panel.add(basicText, BorderLayout.PAGE_START);
        panel.add(stackTracePanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.PAGE_END);

        add(panel);
        pack();

        getRootPane().setDefaultButton(ok);
        SwingUtilities.invokeLater(ok::requestFocusInWindow);
        System.out.println(stackTraceArea.getText());

        viewStackTraceButton.addActionListener(e1 -> {
            if(!stackTracePanel.isVisible()) {
                stackTracePanel.setVisible(true);
                this.setSize(getWidth() + 100, getHeight() + 300);
            } else {
                stackTracePanel.setVisible(false);
                this.setSize(getWidth() - 100, getHeight() - 300);
            }
        });

        ok.addActionListener(e1 -> {
            dispose();
//            Window.getTaskbar().setWindowProgressState(Window.getWindow(), Taskbar.State.OFF);
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
//                Window.getTaskbar().setWindowProgressState(Window.getWindow(), Taskbar.State.OFF);
            }

            @Override
            public void windowClosed(WindowEvent e) {
//                Window.getTaskbar().setWindowProgressState(Window.getWindow(), Taskbar.State.OFF);
            }
        });
    }

    private static String getErrorDialogMessage(Exception e) {
        Toolkit.getDefaultToolkit().beep();
//        taskbar.setWindowProgressState(Window.getWindow(), Taskbar.State.ERROR);
//        taskbar.setWindowProgressValue(Window.getWindow(), 100);
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append(e).append("\n");
        errorMessage.append("Caused by:\n");
        StackTraceElement[] errorStackTrace = e.getStackTrace();
        for (StackTraceElement element : errorStackTrace) {
            errorMessage.append(element.toString());
            errorMessage.append("\n");
        }
        return errorMessage.toString();
    }
}
