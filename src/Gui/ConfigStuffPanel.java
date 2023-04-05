package Gui;

import Servers.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.prefs.Preferences;

import static Gui.Frame.exStackTraceToString;

public class ConfigStuffPanel extends JPanel {
    private final JButton openServerFolder;
    private static String servName;
    private static String servPath;
    private static ConfigStuffPanel panel;
    private static AddWorldsPanel addWorldsPanel;
    private final Preferences preferences;
    private int comboBoxSelectedIndex;
    private static final DefaultComboBoxModel<String> serverSelectionModel = new DefaultComboBoxModel<>();
    private static final JComboBox<String> serverSelection = new JComboBox<>(serverSelectionModel);
    private final ArrayList<Integer> disabledIndexes = new ArrayList<>();
    private int previouslySelectedIndex;


    public ConfigStuffPanel(Preferences preferences) {
        this.preferences = preferences;
        setLayout(new BorderLayout(10, 10));
        JButton openCfg = new JButton("Open App's Config File");
        servName = preferences.get("SELECTED_SERVER_NAME", "ERROR");
        servPath = preferences.get("SELECTED_SERVER_PATH", "ERROR");
        openServerFolder = new JButton("Open " + preferences.get("SELECTED_SERVER_NAME", "ERROR") + "'s Server Folder");

        openCfg.addActionListener(e -> new Runner(Run.CONFIG_FILE).start());

        openServerFolder.addActionListener(e -> new Runner(Run.SERVER_FOLDER, servPath).start());
        Config config = null;
        try {
            config = new Config();
        } catch(IOException e) {
            Frame.alert(AlertType.FATAL, exStackTraceToString(e.getStackTrace()));
        }
        JLabel selServerTitle = new JLabel(" or select server here:");


        JPanel selServerManually = new JPanel();


        for(int i = 0; i < Objects.requireNonNull(config).getData().size(); i++) {
            if(new File(config.getData().get(i).getPathToServerFolder()).exists()) {
                serverSelection.addItem(config.getData().get(i).getButtonText());
            } else {
                serverSelection.addItem(config.getData().get(i).getButtonText() + " (MISSING FILES)");

                disabledIndexes.add(i);
            }
        }

        if(serverSelection.getItemCount() > preferences.getInt("SELECTED_COMBO_INDEX", 0))
            serverSelection.setSelectedIndex(preferences.getInt("SELECTED_COMBO_INDEX", 0));
        else
            serverSelection.setSelectedIndex(0);

        Config finalConfig = config;
        serverSelection.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.DESELECTED) { //part of issue #49's fix
                previouslySelectedIndex = serverSelectionModel.getIndexOf(e.getItem());
            }
            if(e.getStateChange() == ItemEvent.SELECTED) {
                servName = (String) e.getItem();
                servPath = finalConfig.getData().get(serverSelection.getSelectedIndex()).getPathToServerFolder(); //This is a very awful solution - if SOMEHOW indexes of the buttons won't correspond to the JComboBoxes's indexes, this code is fucked
                panel.repaint();
                addWorldsPanel.repaint();
                comboBoxSelectedIndex = serverSelection.getSelectedIndex();
                preferences.putInt("SELECTED_COMBO_INDEX", comboBoxSelectedIndex);
            }
        });
        DefaultListSelectionModel model = new DefaultListSelectionModel();
        for(int i = 0; i < config.getData().size(); i++) {
            if(!disabledIndexes.contains(i)) {
                model.addSelectionInterval(i, i);
            }
        } //issue #50 fix - custom (totaly mine) renderer to gray out the items
        EnabledComboBoxRenderer enabledComboBoxRenderer = new EnabledComboBoxRenderer();
        enabledComboBoxRenderer.setDisabledColor(Color.GRAY);
        enabledComboBoxRenderer.setEnabledItems(model);
        serverSelection.setRenderer(enabledComboBoxRenderer);

        selServerManually.setLayout(new BorderLayout());
        selServerManually.add(openServerFolder, BorderLayout.LINE_START);
        selServerManually.add(selServerTitle, BorderLayout.CENTER);
        selServerManually.add(serverSelection, BorderLayout.LINE_END);

        Dimension dimension = new Dimension(10, 1);
        add(Box.createRigidArea(dimension), BorderLayout.PAGE_START);
        add(openCfg, BorderLayout.LINE_START);
        add(selServerManually, BorderLayout.LINE_END);
        add(Box.createRigidArea(dimension), BorderLayout.PAGE_END);
    }

    public static void setServerVariables(String text, String serverPath) {
        servName = text;
        servPath = serverPath;
        panel.repaint();
        addWorldsPanel.repaint();
    }

    public void setPanel(ConfigStuffPanel panel, AddWorldsPanel addWorldsPanel) {
        ConfigStuffPanel.panel = panel;
        ConfigStuffPanel.addWorldsPanel = addWorldsPanel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        preferences.put("SELECTED_SERVER_NAME", servName);
        preferences.put("SELECTED_SERVER_PATH", servPath);
        openServerFolder.setText("Open " + servName + "'s Server Folder");
        if(disabledIndexes.contains(serverSelection.getSelectedIndex())) { //just select previous index
            serverSelection.setSelectedIndex(previouslySelectedIndex);
        }
    }

    public static String getServName() {
        return servName;
    }

    public static String getServPath() {
        return servPath;
    }

    public static JComboBox<String> getServerSelection() {
        return serverSelection;
    }
}