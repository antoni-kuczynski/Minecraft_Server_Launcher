package com.myne145.serverlauncher.Gui.Tabs;

import com.myne145.serverlauncher.CustomJComponents.DirectoryTree;
import com.myne145.serverlauncher.Enums.AlertType;
import com.myne145.serverlauncher.Server.ConvertedSize;
import com.myne145.serverlauncher.SelectedServer.ServerDetails;
import com.myne145.serverlauncher.Server.WorldCopyHandler;
import com.formdev.flatlaf.ui.FlatRoundBorder;
//import jnafilechooser.api.JnaFileChooser;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.text.DecimalFormat;
import java.util.List;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.io.Serial;

import static com.myne145.serverlauncher.Gui.Frame.alert;
import static com.myne145.serverlauncher.Gui.Frame.getErrorDialogMessage;

public class WorldsTab extends JPanel {
    private final JProgressBar progressBar = new JProgressBar();
    private final JLabel selectedWorldIconLabel = new JLabel();
    private final JLabel serverWorldIconLabel = new JLabel();
    private final JLabel worldNameAndStuffText = new JLabel();
    private final JLabel serverWorldNameAndStuff = new JLabel();
    private final JPanel worldPanel = new JPanel(new BorderLayout());
    private final DirectoryTree directoryTree = new DirectoryTree();
    private final FlatRoundBorder border = new FlatRoundBorder();
    private final JButton startCopying = new JButton("Start Copying");
    private final ImageIcon defaultWorldIcon = new ImageIcon(new ImageIcon("resources/defaultworld.jpg").getImage().getScaledInstance(96, 96, Image.SCALE_SMOOTH));
    private final JPanel serverNameAndStuff = new JPanel(new BorderLayout());
    private static File userSelectedWorld;
    private static String extractedWorldDir;
    private boolean isInArchiveMode; //issue #8 fixed by adding a boolean to check the content's type
    private final DecimalFormat unitRound = new DecimalFormat("###.##");
    public static boolean wasServerPropertiesFound = true;

    private final double ONE_GIGABYTE = 1073741824;

    public WorldsTab() {
        super(new BorderLayout());
        JLabel dragNDropInfo = new JLabel(" or drag and drop it here.");
        JLabel selectedServerTxt = new JLabel();
        String selServPrefix = "Selected server: ";
        selectedServerTxt.setText(selServPrefix + ServerDetails.serverName);
        startCopying.setEnabled(false);
        JButton openButton = new JButton("Open Folder");
//        openButton.addActionListener(e -> { //jna file chooser implementation here - issue #42 fixed
//            JnaFileChooser fileDialog = new JnaFileChooser();
//            fileDialog.showOpenDialog(null);
//
//            File[] filePaths = fileDialog.getSelectedFiles();
//            String folderPath = "";
//            if(fileDialog.getCurrentDirectory() != null)
//                folderPath = fileDialog.getCurrentDirectory().getAbsolutePath();
//
//            if (fileDialog.getSelectedFiles().length == 0 || filePaths == null || filePaths[0] == null) {
//                return;
//            }
//
//            File selectedFile = filePaths[0];
//
//            if (WorldCopyHandler.isArchive(selectedFile)) {
//                userSelectedWorld = selectedFile;
//                isInArchiveMode = true;
//                try {
//                    new WorldCopyHandler(this, progressBar, userSelectedWorld, false, startCopying).start();
//                } catch (IOException ex) {
//                    alert(AlertType.ERROR, getErrorDialogMessage(ex));
//                }
//            } else {
//                isInArchiveMode = false;
//                File folder = new File(folderPath);
//                //issue #16 fix adding a warning to check for folder's size
//
//                if (FileUtils.sizeOfDirectory(folder) < ONE_GIGABYTE) {
//                    userSelectedWorld = folder;
//                }
//                if (FileUtils.sizeOfDirectory(folder) >= ONE_GIGABYTE) {
//                    if (JOptionPane.showConfirmDialog(null,
//                            "Folder that you're trying to copy's size is greater than 1GB. Do you still want to prooced?", "Warning",
//                            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
//                        userSelectedWorld = folder; //yes option
//                    }
//                }
//            }
//            setIcons();
//        });

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
                        userSelectedWorld = fileToAdd;
                        new WorldCopyHandler(tempPanel, progressBar, userSelectedWorld, false, startCopying).start();
                    } else {
                        isInArchiveMode = false;
                        //issue #16 fix adding a warning to check for folder's size
                        if(FileUtils.sizeOfDirectory(new File(fileToAdd.getParent())) > ONE_GIGABYTE) {
                            if (JOptionPane.showConfirmDialog(null,
                                    "Folder that you're trying to copy's size is greater than 1GB. Do you still want to prooced?", "Warning",
                                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                                userSelectedWorld = new File(fileToAdd.getParent()); //yes option
                            }
                        } else { //if file is less than 1gb
                            userSelectedWorld = new File(fileToAdd.getParent());
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

        directoryTree.setDirectory(ServerDetails.serverPath.getAbsolutePath());
        JScrollPane directoryTreeScroll = new JScrollPane(directoryTree);

        selectedWorldIconLabel.setIcon(defaultWorldIcon);

        //Panels
        JPanel buttonAndText = new JPanel(new BorderLayout());
        JPanel separatorBtnTextKinda = new JPanel(new BorderLayout());
        JPanel startCopyingPanel = new JPanel(new BorderLayout());
        JPanel copyingProgress = new JPanel(new BorderLayout());
        JPanel startCopyingBtnPanel = new JPanel(new BorderLayout());


        openButton.setPreferredSize(new Dimension(130, 40));
        separatorBtnTextKinda.add(openButton, BorderLayout.LINE_START);
        separatorBtnTextKinda.add(dragNDropInfo, BorderLayout.CENTER);
        Dimension dimension = new Dimension(10, 10);
        separatorBtnTextKinda.add(Box.createRigidArea(dimension), BorderLayout.LINE_END);

        buttonAndText.add(Box.createRigidArea(dimension), BorderLayout.PAGE_START);
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

        JPanel addingWorld = new JPanel(new BorderLayout());

        worldNameAndStuffText.setText("World File name will appear here.");

        JPanel iHateFrontendPanel2 = new JPanel(new BorderLayout());
        iHateFrontendPanel2.add(Box.createRigidArea(dimension), BorderLayout.LINE_START);
        iHateFrontendPanel2.add(selectedWorldIconLabel, BorderLayout.CENTER);

        worldPanel.add(Box.createRigidArea(dimension), BorderLayout.PAGE_START);
        worldPanel.add(iHateFrontendPanel2, BorderLayout.LINE_START);
        worldPanel.add(Box.createRigidArea(dimension)); //issue #62 fix
        worldPanel.add(worldNameAndStuffText, BorderLayout.LINE_END);
        worldPanel.add(Box.createRigidArea(dimension), BorderLayout.PAGE_END);

        JPanel worldPaneUpper = new JPanel(new BorderLayout());
        worldPaneUpper.add(Box.createRigidArea(dimension), BorderLayout.LINE_START);
        worldPaneUpper.add(worldPanel, BorderLayout.CENTER);
        worldPaneUpper.add(Box.createRigidArea(dimension), BorderLayout.LINE_END);


        JPanel serverIconWithSpacing = new JPanel(new BorderLayout());
        serverIconWithSpacing.add(serverWorldIconLabel, BorderLayout.LINE_START);
        serverIconWithSpacing.add(Box.createRigidArea(dimension), BorderLayout.CENTER); //issue #62 fix for servers
        serverIconWithSpacing.add(serverWorldNameAndStuff, BorderLayout.LINE_END);

        serverNameAndStuff.add(Box.createRigidArea(dimension), BorderLayout.PAGE_START);
        serverNameAndStuff.add(Box.createRigidArea(dimension), BorderLayout.LINE_START);
        serverNameAndStuff.add(serverIconWithSpacing, BorderLayout.LINE_END);
        serverNameAndStuff.add(Box.createRigidArea(dimension), BorderLayout.PAGE_END);

        JPanel iHateFrontendPanel = new JPanel(new BorderLayout());
        iHateFrontendPanel.add(Box.createRigidArea(dimension), BorderLayout.LINE_START);
        iHateFrontendPanel.add(serverNameAndStuff, BorderLayout.CENTER);

        JPanel serverPanelBottom = new JPanel(new BorderLayout());
        serverPanelBottom.add(iHateFrontendPanel, BorderLayout.LINE_START);
        serverPanelBottom.add(Box.createRigidArea(dimension), BorderLayout.LINE_END);

        addingWorld.add(worldPaneUpper, BorderLayout.PAGE_START);
        addingWorld.add(Box.createRigidArea(dimension), BorderLayout.LINE_START);
        addingWorld.add(directoryTreeScroll, BorderLayout.CENTER);
        addingWorld.add(serverPanelBottom, BorderLayout.PAGE_END);

        add(buttonAndText, BorderLayout.PAGE_START);
        add(addingWorld, BorderLayout.LINE_START);
        add(startCopyingPanel, BorderLayout.PAGE_END);
    }

    private ConvertedSize directorySizeWithConverion(File directory) {
        long SIZE_IN_BYTES = FileUtils.sizeOfDirectory(directory);
        double ONE_KILOBYTE = 1024;
        double ONE_MEGABYTE = 1048576;

        double finalSize = SIZE_IN_BYTES;
        String unitSymbol = "b";
        if (SIZE_IN_BYTES >= ONE_KILOBYTE && SIZE_IN_BYTES < ONE_MEGABYTE) {
            finalSize = SIZE_IN_BYTES / ONE_KILOBYTE;
            unitSymbol = "kb";
        } else if (SIZE_IN_BYTES >= ONE_MEGABYTE && SIZE_IN_BYTES < ONE_GIGABYTE) {
            finalSize = SIZE_IN_BYTES / ONE_MEGABYTE;
            unitSymbol = "mb";
        } else if (SIZE_IN_BYTES >= ONE_GIGABYTE) {
            finalSize = SIZE_IN_BYTES / ONE_GIGABYTE;
            unitSymbol = "gb";
        }
        return new ConvertedSize(unitRound.format(finalSize), unitSymbol);
    }
    public void setIcons() {
        directoryTree.setDirectory(ServerDetails.serverPath.getAbsolutePath());
        if(userSelectedWorld != null && isInArchiveMode) { //issue #7 fix
            worldNameAndStuffText.setText("<html>File: " + userSelectedWorld.getAbsolutePath() +
                    "<br>File size: TODO<br>" + "Extracted size: TODO" + "</html>");
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
                    "<br>Folder size: " + directorySizeWithConverion(userSelectedWorld).getText() + "</html>");
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


        if(!new File(ServerDetails.serverWorldPath + "\\icon.png").exists()) {
            serverWorldIconLabel.setIcon(defaultWorldIcon);
        } else {
            serverWorldIconLabel.setIcon(new ImageIcon(new ImageIcon(ServerDetails.serverWorldPath + "\\icon.png")
                    .getImage().getScaledInstance(96, 96, Image.SCALE_SMOOTH)));
        }

        //size is in bytes
        if(ServerDetails.serverWorldPath.exists()) {
            ConvertedSize serverWorldConvertedSize = directorySizeWithConverion(ServerDetails.serverWorldPath);
//            LevelNameColorConverter.convertColors(ServerDetails.serverLevelName);
            if(ServerDetails.serverLevelName == null)
                ServerDetails.serverLevelName = "Level.dat file not found.";

            String folderNameTemp = ServerDetails.serverWorldPath.getName();
            if(!wasServerPropertiesFound)
                folderNameTemp = "server.properties file does not exist";
            serverWorldNameAndStuff.setText("<html> Folder Name: " + folderNameTemp +"<br> Level name: " + ServerDetails.serverLevelName + "<br> Size: " + serverWorldConvertedSize.getText() + "</html>"); //world name todo here
        } else {
            serverWorldNameAndStuff.setText("Server world folder does not exist.");
        }

    }

    public void setBorders() {
        worldPanel.setBorder(border); //issue #5 fixed
        serverNameAndStuff.setBorder(border);
    }

    public static void setExtractedWorldDir(String extractedWorldDir) {
        WorldsTab.extractedWorldDir = extractedWorldDir;
    }

    public static String getExtractedWorldDir() {
        return extractedWorldDir;
    }
}