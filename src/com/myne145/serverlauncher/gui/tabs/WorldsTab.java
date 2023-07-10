package com.myne145.serverlauncher.gui.tabs;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.myne145.serverlauncher.gui.AlertType;
import com.myne145.serverlauncher.gui.Window;
import com.myne145.serverlauncher.server.Config;
import com.myne145.serverlauncher.server.FileSize;
import com.myne145.serverlauncher.server.current.CurrentServerInfo;
import com.myne145.serverlauncher.server.WorldCopyHandler;
import com.formdev.flatlaf.ui.FlatRoundBorder;
import jnafilechooser.api.JnaFileChooser;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.util.List;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.io.Serial;

import static com.myne145.serverlauncher.gui.Window.alert;
import static com.myne145.serverlauncher.gui.Window.getErrorDialogMessage;

public class WorldsTab extends JPanel {
    private final JProgressBar progressBar = new JProgressBar();
    private final JLabel selectedWorldIconLabel = new JLabel();
    private final JLabel serverWorldIconLabel = new JLabel();
    private final JLabel worldNameAndStuffText = new JLabel("World File name will appear here.");
    private final JLabel serverWorldNameAndStuff = new JLabel();
    //    private final DirectoryTree directoryTree = new DirectoryTree();
    private final JButton startCopying = new JButton("Start Copying");
    private final ImageIcon defaultWorldIcon = new ImageIcon(new ImageIcon("resources/defaultworld.jpg").getImage().getScaledInstance(96, 96, Image.SCALE_SMOOTH));
    private File userSelectedWorld;
    private String extractedWorldDir;
    private boolean isInArchiveMode; //issue #8 fixed by adding a boolean to check the content's type
    public FileSize extractedWorldSize;
    private final WorldsTab worldsTab;

    private final double ONE_GIGABYTE = 1073741824;

    public WorldsTab(int index) {
        super(new BorderLayout());
        worldsTab = this;
        if(!Config.getData().get(index).serverPath().exists())
            return;
        JLabel dragNDropInfo = new JLabel(" or drag and drop it here.");
        JLabel selectedServerTxt = new JLabel();
        String selServPrefix = "Selected server: ";
        selectedServerTxt.setText(selServPrefix + CurrentServerInfo.serverName);
        startCopying.setEnabled(false);
        JButton openButton = new JButton("Open Folder");
        openButton.addActionListener(e -> {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    JnaFileChooser fileDialog = new JnaFileChooser();
                    fileDialog.showOpenDialog(Window.getWindow());

                    File[] filePaths = fileDialog.getSelectedFiles();
                    String folderPath = "";
                    if(fileDialog.getCurrentDirectory() != null)
                        folderPath = fileDialog.getCurrentDirectory().getAbsolutePath();

                    if (fileDialog.getSelectedFiles().length == 0 || filePaths == null || filePaths[0] == null) {
                        return;
                    }

                    File selectedFile = filePaths[0];

                    if (WorldCopyHandler.isArchive(selectedFile)) {
                        userSelectedWorld = selectedFile;
                        isInArchiveMode = true;
                        try {
                            new WorldCopyHandler(worldsTab, progressBar, userSelectedWorld, false, startCopying).start();
                        } catch (IOException ex) {
                            alert(AlertType.ERROR, getErrorDialogMessage(ex));
                        }
                    } else {
                        isInArchiveMode = false;
                        File folder = new File(folderPath);
                        //issue #16 fix adding a warning to check for folder's size

                        if (FileUtils.sizeOfDirectory(folder) < ONE_GIGABYTE) {
                            userSelectedWorld = folder;
                        }
                        if (FileUtils.sizeOfDirectory(folder) >= ONE_GIGABYTE) {
                            if (JOptionPane.showConfirmDialog(null,
                                    "Folder that you're trying to copy's size is greater than 1GiB. Do you still want to prooced?", "Warning",
                                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                                userSelectedWorld = folder; //yes option
                            }
                        }
                    }
                    setIcons();
                }
            };
            new Thread(runnable).start();

        });
        setIcons();
        final WorldsTab tempPanel = this;
        TransferHandler transferHandler = new TransferHandler() {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public boolean canImport(TransferHandler.TransferSupport support) {
                return true;
            }

            @Override
            public boolean importData(TransferSupport support) {
                if (!canImport(support)) {
                    return false;
                }
                Transferable t = support.getTransferable();
                try {
                    List<File> l = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
                    File fileToAdd = l.get(l.size() - 1);

                    if (WorldCopyHandler.isArchive(fileToAdd)) {
                        isInArchiveMode = true;
                        userSelectedWorld = fileToAdd; //TODO: proper directory checks not stupid file.getParent()
                        new WorldCopyHandler(tempPanel, progressBar, userSelectedWorld, false, startCopying).start();
                    } else {
                        isInArchiveMode = false;
                        if(fileToAdd.isFile()) {
                            fileToAdd = new File(fileToAdd.getParent());
                        }
                        if(FileUtils.sizeOfDirectory(fileToAdd) > ONE_GIGABYTE) {
                            if (JOptionPane.showConfirmDialog(null,
                                    "Folder that you're trying to copy's size is greater than 1GiB. Do you still want to prooced?", "Warning",
                                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                                userSelectedWorld = fileToAdd; //yes option
                            }
                        } else { //if file is less than 1gb
                            userSelectedWorld = fileToAdd;
                        }

                    }
                    setIcons();
                } catch (UnsupportedFlavorException | IOException e) {
                    alert(AlertType.ERROR, getErrorDialogMessage(e));
                    return false;
                }
                return true;
            }
        };

        this.setTransferHandler(transferHandler);
        serverWorldNameAndStuff.setTransferHandler(transferHandler);
        worldNameAndStuffText.setTransferHandler(transferHandler);

        startCopying.addActionListener(e -> {
            WorldCopyHandler worldCopyHandler;
            try {
                worldCopyHandler = new WorldCopyHandler(this, progressBar, userSelectedWorld, true, startCopying);
            } catch (IOException ex) {
                alert(AlertType.ERROR, getErrorDialogMessage(ex));
                return;
            }
            worldCopyHandler.start();
        });

//        directoryTree.setDirectory(ServerDetails.serverPath.getAbsolutePath());
//        JScrollPane directoryTreeScroll = new JScrollPane(directoryTree);

        selectedWorldIconLabel.setIcon(defaultWorldIcon);
        openButton.setPreferredSize(new Dimension(130, 40));



        //Panels
        JPanel buttonAndText = new JPanel(new BorderLayout());
        JPanel separatorBtnTextKinda = new JPanel(new BorderLayout());
        JPanel startCopyingPanel = new JPanel(new BorderLayout());
        JPanel copyingProgress = new JPanel(new BorderLayout());
        JPanel startCopyingBtnPanel = new JPanel(new BorderLayout());
        JPanel titlePanel = new JPanel(new BorderLayout());
        JPanel addingWorld = new JPanel(new BorderLayout());
        JPanel arrowPanel = new JPanel(new BorderLayout());


        Dimension dimension = new Dimension(10, 10);


        JLabel arrow = new JLabel();
        JLabel modeInfo = new JLabel("Copy and replace   ");
        modeInfo.setFont(new Font("Arial", Font.BOLD, 14));
        arrow.setIcon(new FlatSVGIcon(new File("resources/arrow.svg")).derive(80,116));
        arrowPanel.add(modeInfo, BorderLayout.LINE_START);
        arrowPanel.add(arrow, BorderLayout.CENTER);

        JLabel title = new JLabel("World Manager");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(Box.createRigidArea(new Dimension(5,5)), BorderLayout.PAGE_START);
        titlePanel.add(Box.createRigidArea(new Dimension(5,10)), BorderLayout.LINE_START);
        titlePanel.add(title, BorderLayout.CENTER);
        titlePanel.add(Box.createRigidArea(new Dimension(5,10)), BorderLayout.PAGE_END);

        separatorBtnTextKinda.add(openButton, BorderLayout.LINE_START);
        separatorBtnTextKinda.add(dragNDropInfo, BorderLayout.CENTER);
        separatorBtnTextKinda.add(Box.createRigidArea(dimension), BorderLayout.LINE_END);

        buttonAndText.add(titlePanel, BorderLayout.PAGE_START);
        buttonAndText.add(Box.createRigidArea(dimension), BorderLayout.LINE_START);
        buttonAndText.add(separatorBtnTextKinda, BorderLayout.CENTER);

        copyingProgress.add(Box.createRigidArea(dimension), BorderLayout.PAGE_START);
        copyingProgress.add(Box.createRigidArea(new Dimension(10,5)), BorderLayout.LINE_START);
        copyingProgress.add(progressBar, BorderLayout.CENTER);
        copyingProgress.add(Box.createRigidArea(new Dimension(10,5)), BorderLayout.LINE_END);
        copyingProgress.add(Box.createRigidArea(dimension), BorderLayout.PAGE_END);


        startCopyingBtnPanel.add(startCopying, BorderLayout.CENTER);
        startCopyingBtnPanel.add(Box.createRigidArea(dimension), BorderLayout.LINE_END);

        startCopyingPanel.add(Box.createRigidArea(dimension), BorderLayout.LINE_START);
        startCopyingPanel.add(startCopyingBtnPanel, BorderLayout.LINE_END);
        startCopyingPanel.add(copyingProgress, BorderLayout.PAGE_END);


        JPanel worldIconWithSpacing = new JPanel(new BorderLayout());
        worldIconWithSpacing.add(Box.createRigidArea(dimension), BorderLayout.LINE_START);
        worldIconWithSpacing.add(selectedWorldIconLabel, BorderLayout.CENTER);
        worldIconWithSpacing.add(Box.createRigidArea(dimension), BorderLayout.LINE_END);

        JPanel worldPanel = new JPanel(new BorderLayout());
        worldPanel.add(Box.createRigidArea(dimension), BorderLayout.PAGE_START);
        worldPanel.add(worldIconWithSpacing, BorderLayout.LINE_START);
        worldPanel.add(worldNameAndStuffText, BorderLayout.CENTER);
        worldPanel.add(Box.createRigidArea(dimension), BorderLayout.LINE_END);
        worldPanel.add(Box.createRigidArea(dimension), BorderLayout.PAGE_END);
        worldPanel.setBorder(new FlatRoundBorder());

        JPanel worldPaneUpper = new JPanel(new BorderLayout());
        worldPaneUpper.add(Box.createRigidArea(dimension), BorderLayout.LINE_START);
        worldPaneUpper.add(worldPanel, BorderLayout.CENTER);
        worldPaneUpper.add(Box.createRigidArea(dimension), BorderLayout.LINE_END);


        JPanel serverIconWithSpacing = new JPanel(new BorderLayout());
        serverIconWithSpacing.add(serverWorldIconLabel, BorderLayout.LINE_START);
        serverIconWithSpacing.add(Box.createRigidArea(dimension), BorderLayout.CENTER); //issue #62 fix for servers
        serverIconWithSpacing.add(serverWorldNameAndStuff, BorderLayout.LINE_END);

        JPanel serverNameAndStuff = new JPanel(new BorderLayout());
        serverNameAndStuff.add(Box.createRigidArea(dimension), BorderLayout.PAGE_START);
        serverNameAndStuff.add(Box.createRigidArea(dimension), BorderLayout.LINE_START);
        serverNameAndStuff.add(serverIconWithSpacing, BorderLayout.CENTER);
        serverNameAndStuff.add(Box.createRigidArea(dimension), BorderLayout.LINE_END);
        serverNameAndStuff.add(Box.createRigidArea(dimension), BorderLayout.PAGE_END);
        serverNameAndStuff.setBorder(new FlatRoundBorder());

        JPanel serverWorldNameWithSpacing = new JPanel(new BorderLayout());
        serverWorldNameWithSpacing.add(Box.createRigidArea(dimension), BorderLayout.LINE_START);
        serverWorldNameWithSpacing.add(serverNameAndStuff, BorderLayout.CENTER);

        JPanel serverPanelBottom = new JPanel(new BorderLayout());


        serverPanelBottom.add(serverWorldNameWithSpacing, BorderLayout.LINE_START);
        serverPanelBottom.add(Box.createRigidArea(dimension), BorderLayout.LINE_END);

        addingWorld.add(worldPaneUpper, BorderLayout.PAGE_START);
        addingWorld.add(Box.createRigidArea(dimension), BorderLayout.LINE_START);
        addingWorld.add(arrowPanel, BorderLayout.CENTER);
        addingWorld.add(serverPanelBottom, BorderLayout.PAGE_END);

        add(buttonAndText, BorderLayout.PAGE_START);
        add(addingWorld, BorderLayout.LINE_START);
        add(startCopyingPanel, BorderLayout.PAGE_END);
    }


    public void setIcons() {
        String extractedWorldSizeText = "Can't obtain world's size";
        if(extractedWorldSize != null)
            extractedWorldSizeText = extractedWorldSize.getText();
        if(userSelectedWorld != null)
            isInArchiveMode = WorldCopyHandler.isArchive(userSelectedWorld);
//        directoryTree.setDirectory(CurrentServerInfo.serverPath.getAbsolutePath());
        if(userSelectedWorld != null && isInArchiveMode) { //issue #7 fix
            worldNameAndStuffText.setText("<html>File: " + userSelectedWorld.getAbsolutePath() +
                    "<br>Size: " + FileSize.fileSizeWithConversion(userSelectedWorld).getText() + "<br>Extracted size: " + extractedWorldSizeText + "</html>");
        } else if(!isInArchiveMode && userSelectedWorld != null) {
            String worldToAddTempText = userSelectedWorld.getAbsolutePath();
            if(worldToAddTempText.length() > 50 && userSelectedWorld.getName().length() < 25) { //issue #77 fix by adding ... to the path
                worldToAddTempText = worldToAddTempText.substring(0, 9) + "...\\" + userSelectedWorld.getName();
            }
            if(worldToAddTempText.length() > 50 && userSelectedWorld.getName().length() >= 25) {
                String tempWorldName = userSelectedWorld.getName();
                worldToAddTempText = worldToAddTempText.substring(0, 9) + "...\\" + tempWorldName.substring(0,20) +
                        "..." + tempWorldName.substring(tempWorldName.length() - 9, tempWorldName.length() - 1);
            }
            worldNameAndStuffText.setText("<html>Folder: " + worldToAddTempText +
                    "<br>Size: " + FileSize.directorySizeWithConversion(userSelectedWorld).getText() + "</html>");
        }

        if(isInArchiveMode && extractedWorldDir != null) {
            //this is the worst fucking solution ever lol
            File extractedDir = new File(extractedWorldDir);
            if(!new File(extractedWorldDir + "\\icon.png").exists()) {
                boolean doesIconInParentExist = new File(extractedDir.getParent() + "\\icon.png").exists();
                ImageIcon parentImg = new ImageIcon(new ImageIcon(extractedDir.getParent() +
                        "\\icon.png").getImage().getScaledInstance(96, 96, Image.SCALE_SMOOTH));
                selectedWorldIconLabel.setIcon(doesIconInParentExist ? parentImg : defaultWorldIcon);
            } else {
                if(new File(extractedDir + "\\icon.png").exists()) //issue #22 fixed by adding another check
                    selectedWorldIconLabel.setIcon(new ImageIcon(new ImageIcon(extractedDir + "\\icon.png").getImage().getScaledInstance(96, 96, Image.SCALE_SMOOTH)));
                else
                    selectedWorldIconLabel.setIcon(defaultWorldIcon);
            }
        } else if(userSelectedWorld != null && userSelectedWorld.exists()) { //issue #8 fix
            startCopying.setEnabled(true);
            if(new File(userSelectedWorld + "\\icon.png").exists()) //issue #24 fix
                selectedWorldIconLabel.setIcon(new ImageIcon(new ImageIcon(userSelectedWorld + "\\icon.png").getImage().getScaledInstance(96,96, Image.SCALE_SMOOTH)));
            else
                selectedWorldIconLabel.setIcon(defaultWorldIcon);
        } else if(extractedWorldDir == null) {
            selectedWorldIconLabel.setIcon(defaultWorldIcon);
        }


        if(!new File(CurrentServerInfo.serverWorldPath + "\\icon.png").exists()) {
            serverWorldIconLabel.setIcon(defaultWorldIcon);
        } else {
            serverWorldIconLabel.setIcon(new ImageIcon(new ImageIcon(CurrentServerInfo.serverWorldPath + "\\icon.png")
                    .getImage().getScaledInstance(96, 96, Image.SCALE_SMOOTH)));
        }

        if(CurrentServerInfo.serverWorldPath.exists()) {
            FileSize serverWorldFileSizeBytes = FileSize.directorySizeWithConversion(CurrentServerInfo.serverWorldPath);
//            LevelNameColorConverter.convertColors(ServerDetails.serverLevelName);
            if(CurrentServerInfo.serverLevelName == null)
                CurrentServerInfo.serverLevelName = "Level.dat file not found.";

            String folderNameTemp = CurrentServerInfo.serverWorldPath.getName();
//            if(!wasServerPropertiesFound)
//                folderNameTemp = "server.properties file does not exist";
            serverWorldNameAndStuff.setText("<html> Folder Name: " + folderNameTemp +"<br> Level name: " + CurrentServerInfo.serverLevelName + "<br>Size: " + serverWorldFileSizeBytes.getText() + "</html>");
        } else {
            serverWorldNameAndStuff.setText("Server world folder does not exist.");
        }

    }
    public void setExtractedWorldDir(String extractedWorldDir) {
        this.extractedWorldDir = extractedWorldDir;
    }

    public String getExtractedWorldDir() {
        return extractedWorldDir;
    }
}