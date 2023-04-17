package Gui;

import SelectedServer.NBTParser;
import SelectedServer.ServerDetails;
import SelectedServer.ServerPropertiesFile;
import Server.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.prefs.Preferences;

import static Gui.Frame.alert;
import static Gui.Frame.getErrorDialogMessage;

public class ServerSelectionPanel extends JPanel {
    private final JButton openServerFolder;
    private static ServerSelectionPanel panel;
    public static AddWorldsPanel addWorldsPanel;
    private final Preferences userValues;
    private int selectedIndexInComboBox;
    private static final DefaultComboBoxModel<String> serverSelectionModel = new DefaultComboBoxModel<>();
    private static final JComboBox<String> serverSelection = new JComboBox<>(serverSelectionModel);
    private final ArrayList<Integer> disabledComboBoxIndexes = new ArrayList<>();
    private int previouslySelectedComboBoxIndex;


    public ServerSelectionPanel(Preferences userValues) throws IOException, InterruptedException {
        this.userValues = userValues;
        setLayout(new BorderLayout(10, 10));
        JButton openCfg = new JButton("Open App's Config File");
        ServerDetails.serverName = userValues.get("SELECTED_SERVER_NAME", "ERROR");
        ServerDetails.serverPath = userValues.get("SELECTED_SERVER_PATH", "ERROR");
        openServerFolder = new JButton("Open " + userValues.get("SELECTED_SERVER_NAME", "ERROR") + "'s Server Folder");

        openCfg.addActionListener(e -> new Runner(RunMode.CONFIG_FILE).start());

        openServerFolder.addActionListener(e -> new Runner(RunMode.SERVER_FOLDER, ServerDetails.serverPath).start());
        Config config = null;
        try {
            config = new Config();
        } catch(IOException e) {
            Frame.alert(AlertType.FATAL, getErrorDialogMessage(e));
        }
        JLabel selServerTitle = new JLabel(" or select server here:");


        JPanel selServerManually = new JPanel();


        for(int i = 0; i < Objects.requireNonNull(config).getData().size(); i++) {
            if(new File(config.getData().get(i).getPathToServerFolder()).exists()) {
                serverSelection.addItem(config.getData().get(i).getButtonText());
            } else {
                serverSelection.addItem(config.getData().get(i).getButtonText() + " (MISSING FILES)");

                disabledComboBoxIndexes.add(i);
            }
        }

        int SELECTED_COMBO_INDEX = userValues.getInt("SELECTED_COMBO_INDEX", 0);
        if(serverSelection.getItemCount() > SELECTED_COMBO_INDEX)
            serverSelection.setSelectedIndex(SELECTED_COMBO_INDEX);
        else
            serverSelection.setSelectedIndex(0);

        Config finalConfig = config;
        serverSelection.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.DESELECTED) { //part of issue #49's fix
                previouslySelectedComboBoxIndex = serverSelectionModel.getIndexOf(e.getItem());
            }
            if(e.getStateChange() == ItemEvent.SELECTED) {
                ServerDetails.serverId = serverSelection.getSelectedIndex() + 1; //ids start at 1
                ServerDetails.serverName = (String) e.getItem();
                ServerDetails.serverPath = finalConfig.getData().get(serverSelection.getSelectedIndex()).getPathToServerFolder(); //This is a very awful solution - if SOMEHOW indexes of the buttons won't correspond to the JComboBoxes's indexes, this code is fucked
                NBTParser nbtParserComboBox = new NBTParser(); //added reading NBT level.dat file for level name to jcombobox
                try {
                    new ServerPropertiesFile();
                    nbtParserComboBox.start();
                    nbtParserComboBox.join();
                } catch (Exception ex) {
                    alert(AlertType.ERROR, getErrorDialogMessage(ex));
                }
                ServerDetails.serverLevelName = nbtParserComboBox.getLevelName();
                panel.reloadButtonText();
                selectedIndexInComboBox = serverSelection.getSelectedIndex();
                userValues.putInt("SELECTED_COMBO_INDEX", selectedIndexInComboBox);
                addWorldsPanel.setIcons();
            }
        });
        DefaultListSelectionModel model = new DefaultListSelectionModel();
        for(int i = 0; i < config.getData().size(); i++) {
            if(!disabledComboBoxIndexes.contains(i)) {
                model.addSelectionInterval(i, i);
            }
        } //issue #50 fix - custom (totaly mine) renderer to gray out the items
        EnabledComboBoxRenderer comboBoxRendererWithDisabledItems = new EnabledComboBoxRenderer();
        comboBoxRendererWithDisabledItems.setDisabledColor(Color.GRAY);
        comboBoxRendererWithDisabledItems.setEnabledItems(model);
        serverSelection.setRenderer(comboBoxRendererWithDisabledItems);

        selServerManually.setLayout(new BorderLayout());
        selServerManually.add(openServerFolder, BorderLayout.LINE_START);
        selServerManually.add(selServerTitle, BorderLayout.CENTER);
        selServerManually.add(serverSelection, BorderLayout.LINE_END);

        Dimension dimension = new Dimension(10, 1);
        add(Box.createRigidArea(dimension), BorderLayout.PAGE_START);
        add(openCfg, BorderLayout.LINE_START);
        add(selServerManually, BorderLayout.LINE_END);
        add(Box.createRigidArea(dimension), BorderLayout.PAGE_END);

        new ServerPropertiesFile(); //this needs a refactor - makes level-name actually update TODO
        NBTParser nbtParser = new NBTParser(); //reading NBT level.dat file for level name
        nbtParser.start();
        nbtParser.join();
        ServerDetails.serverLevelName = nbtParser.getLevelName(); //issue #64 fix
//        addWorldsPanel.setIcons();
    }

    public static void setServerVariables(String text, String serverPath, int serverId) throws InterruptedException, IOException {
        ServerDetails.serverName = text;
        ServerDetails.serverPath = serverPath;
        ServerDetails.serverId = serverId;
        System.out.println("Server id: " + serverId);
        panel.reloadButtonText(); //removed redundant addWorldPanel.repaint() calls and replaces panel.repaint() to decrease RAM usage
    }

    public void setPanels(ServerSelectionPanel panel, AddWorldsPanel addWorldsPanel) {
        ServerSelectionPanel.panel = panel;
        ServerSelectionPanel.addWorldsPanel = addWorldsPanel;
        addWorldsPanel.setIcons();
    }

    private void reloadButtonText() {
        userValues.put("SELECTED_SERVER_NAME", ServerDetails.serverName);
        userValues.put("SELECTED_SERVER_PATH", ServerDetails.serverPath);
        openServerFolder.setText("Open " + ServerDetails.serverName + "'s Server Folder");
        if(disabledComboBoxIndexes.contains(serverSelection.getSelectedIndex())) { //just select previous index
            serverSelection.setSelectedIndex(previouslySelectedComboBoxIndex);
        }
    }


    public static JComboBox<String> getServerSelection() {
        return serverSelection;
    }
}