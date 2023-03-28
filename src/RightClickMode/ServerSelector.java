package RightClickMode;

import Servers.ButtonData;
import Servers.Config;
import Servers.WorldCopyHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

public class ServerSelector extends JFrame {
    private ButtonData selectedServer;
    public ServerSelector(String[] args) throws IOException {
        Preferences preferences = Preferences.userNodeForPackage(ServerSelector.class);
        StringBuilder worldpath = new StringBuilder();
        for(String s : args)
            worldpath.append(s + " ");
        System.out.println(worldpath);
        setTitle("Select Server");
        setIconImage(new ImageIcon("app_icon.png").getImage());
        JPanel parentPanel = new JPanel(new BorderLayout());
        JPanel serverSelPanel = new JPanel(new BorderLayout());
        JPanel bottomPanel = new JPanel();
        JPanel whatIsSelectedPanel = new JPanel(new BorderLayout());
        JLabel selectServer = new JLabel("Select server:");
        JComboBox<String> selectorServer = new JComboBox<>();
        JButton startCopying = new JButton("Start copying");
        JButton cancel = new JButton("Cancel");
        JLabel selectedWorld = new JLabel();
        JProgressBar progressBar = new JProgressBar();


        whatIsSelectedPanel.add(selectedWorld, BorderLayout.CENTER);
        whatIsSelectedPanel.add(progressBar, BorderLayout.PAGE_END);
        bottomPanel.add(cancel);
        bottomPanel.add(startCopying);

        serverSelPanel.add(selectServer, BorderLayout.LINE_START);
        serverSelPanel.add(selectorServer, BorderLayout.LINE_END);


        parentPanel.add(serverSelPanel, BorderLayout.PAGE_START);
        parentPanel.add(whatIsSelectedPanel, BorderLayout.CENTER);
        parentPanel.add(bottomPanel, BorderLayout.PAGE_END);


        Config config = new Config();
        for(ButtonData data : config.getData()) {
            selectorServer.addItem(data.getButtonText());
        }
        int lastSelectedServerIndex = preferences.getInt("LAST_SELECTED_SERVER", 0);
        selectorServer.setSelectedIndex(lastSelectedServerIndex);
        selectedServer = config.getData().get(lastSelectedServerIndex);
        selectedWorld.setText(args[0] + " ------> " + selectedServer.getButtonText());
        selectorServer.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED) {
                selectedServer = config.getData().get(selectorServer.getSelectedIndex());
                preferences.putInt("LAST_SELECTED_SERVER", selectorServer.getSelectedIndex());
                System.out.println(selectedServer.getButtonText());
                selectedWorld.setText(args[0] + " ------> " + selectedServer.getButtonText());
            }
        });
        //TODO: handle file names with spaces
        startCopying.addActionListener(e -> {
            try {
                WorldCopyHandler worldCopyHandler =
                        new WorldCopyHandler(parentPanel, progressBar, new File(worldpath.toString()), true, startCopying);
                worldCopyHandler.start();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        add(parentPanel);


        //JFRAME STUFF

//        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(500, 200));
        setVisible(true);
        pack();
        setLocation(preferences.getInt("LAST_LAUNCHED_POS_X", 0), preferences.getInt("LAST_LAUNCHED_POS_Y", 0));

        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                preferences.putInt("LAST_LAUNCHED_POS_X", getX());
                preferences.putInt("LAST_LAUNCHED_POS_Y", getY());
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
    }
}
