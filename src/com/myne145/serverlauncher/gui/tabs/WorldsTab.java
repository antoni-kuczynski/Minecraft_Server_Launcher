package com.myne145.serverlauncher.gui.tabs;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.myne145.serverlauncher.gui.AlertType;
import com.myne145.serverlauncher.gui.ContainerPane;
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
    private final JLabel userAddedWorldIconOnly = new JLabel();
    private final JLabel serverWorldIconLabel = new JLabel();
    private final JLabel userAddedWorldDetailsWithoutIcon = new JLabel("World File name will appear here.");
    private final JLabel serverWorldDetailsWithoutIcon = new JLabel();
    //    private final DirectoryTree directoryTree = new DirectoryTree();
    private final JButton startCopying = new JButton("Start importing");
    private final ImageIcon DEFAULT_WORLD_ICON_PACK_PNG = new ImageIcon(new ImageIcon(Config.RESOURCES_PATH + "/defaultworld.jpg").getImage().getScaledInstance(96, 96, Image.SCALE_SMOOTH));
    private File userAddedWorld;
    private String extractedWorldDir;
    private boolean isInArchiveMode; //issue #8 fixed by adding a boolean to check the content's type
    public FileSize extractedWorldSize;
    private final WorldsTab worldsTab;

    private final double ONE_GIGABYTE = 1073741824;
    private JButton openButton;

    public WorldsTab(ContainerPane parentPane, int tabSwitchingToIndex) {
        super(new BorderLayout());
        worldsTab = this;
        if(!Config.getData().get(tabSwitchingToIndex).serverPath().exists())
            return;
        JLabel selectedServerTxt = new JLabel();
        String selServPrefix = "Selected server: ";
        selectedServerTxt.setText(selServPrefix + CurrentServerInfo.serverName);
        startCopying.setEnabled(false);
        openButton = new JButton("<html><sub>\u200E </sub>Import existing world<sup>\u200E </sup></html>");
        openButton.setMaximumSize(new Dimension(300, 40));
        openButton.addActionListener(e -> {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    JnaFileChooser fileDialog = new JnaFileChooser();
                    fileDialog.showOpenDialog(Window.getWindow());

                    removeImportButtonWarning();
                    File[] filePaths = fileDialog.getSelectedFiles();

                    if (fileDialog.getSelectedFiles().length == 0 || filePaths == null || filePaths[0] == null) {
                        return;
                    }

                    File fileToAdd = filePaths[0];
                    setUserAddedWorld(fileToAdd);
//                    if (WorldCopyHandler.isArchive(fileToAdd)) {
//                        userSelectedWorld = fileToAdd;
//                        isInArchiveMode = true;
//                        WorldCopyHandler.createWorldCopyHandler(worldsTab).start();
//                    } else {
//                        isInArchiveMode = false;
//                        if(fileToAdd.isFile()) {
//                            fileToAdd = new File(fileToAdd.getParent());
//                        }
//                        if (FileUtils.sizeOfDirectory(fileToAdd) >= ONE_GIGABYTE) {
//                            setImportButtonWarning("Larger than 1GiB!");
//                            userSelectedWorld = fileToAdd;
//                        } else { //less than 1GiB
//                            userSelectedWorld = fileToAdd;
//                        }
//                    }
                    setIcons();
                }
            };
            new Thread(runnable).start();

        });
        setIcons();
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
                    setUserAddedWorld(fileToAdd);
                    setIcons();
                } catch (UnsupportedFlavorException | IOException e) {
                    alert(AlertType.ERROR, getErrorDialogMessage(e));
                    return false;
                }
                return true;
            }
        };

        this.setTransferHandler(transferHandler);
        serverWorldDetailsWithoutIcon.setTransferHandler(transferHandler);
        userAddedWorldDetailsWithoutIcon.setTransferHandler(transferHandler);

        startCopying.addActionListener(e -> WorldCopyHandler.createWorldCopyHandler(this).setCopyMode(true).start());

        JButton refreshButton = new JButton("Refresh");

        refreshButton.addActionListener(e -> parentPane.onTabSwitched(tabSwitchingToIndex));

        userAddedWorldIconOnly.setIcon(DEFAULT_WORLD_ICON_PACK_PNG);

        Dimension emptyBoxDimension = new Dimension(10, 10);

        //Panels
        JPanel titlePanel = new JPanel(new BorderLayout());
        JPanel openButtonInCorrectPlacement = new JPanel(new BorderLayout());
        JPanel buttonAndText = new JPanel(new BorderLayout());
        JPanel worldIconWithSpacing = new JPanel(new BorderLayout());
        JPanel worldPanel = new JPanel(new BorderLayout());



        JPanel startCopyingPanel = new JPanel(new BorderLayout());
        JPanel copyingProgress = new JPanel(new BorderLayout());
        JPanel startCopyingBtnPanel = new JPanel(new BorderLayout());

        JPanel addingWorld = new JPanel(new BorderLayout());
        JPanel arrowPanel = new JPanel(new BorderLayout());
        JPanel refreshButtonWithSpacing = new JPanel(new BorderLayout());


        JPanel worldPaneUpper = new JPanel(new BorderLayout());
        JPanel serverIconWithSpacing = new JPanel(new BorderLayout());
        JPanel serverNameAndStuff = new JPanel(new BorderLayout());
        JPanel serverWorldNameWithSpacing = new JPanel(new BorderLayout());
        JPanel serverPanelBottom = new JPanel(new BorderLayout());
        JPanel worldAndArrowPanel = new JPanel(new BorderLayout());


        JLabel title = new JLabel("World Manager");
        JLabel arrow = new JLabel();
        JLabel modeInfo = new JLabel("    Copy and replace   ");

        modeInfo.setFont(new Font("Arial", Font.BOLD, 14));
        title.setFont(new Font("Arial", Font.BOLD, 18));
        arrow.setIcon(new FlatSVGIcon(new File(Config.RESOURCES_PATH + "/arrow.svg")).derive(80,116));


        titlePanel.add(Box.createRigidArea(new Dimension(5,5)), BorderLayout.PAGE_START);
        titlePanel.add(Box.createRigidArea(new Dimension(5,10)), BorderLayout.LINE_START);
        titlePanel.add(title, BorderLayout.CENTER);
        titlePanel.add(Box.createRigidArea(new Dimension(5,10)), BorderLayout.PAGE_END);

        openButtonInCorrectPlacement.add(openButton, BorderLayout.LINE_START);

        buttonAndText.add(titlePanel, BorderLayout.PAGE_START);
        buttonAndText.add(Box.createRigidArea(emptyBoxDimension), BorderLayout.LINE_START);
        buttonAndText.add(openButtonInCorrectPlacement, BorderLayout.CENTER);
        buttonAndText.add(Box.createRigidArea(new Dimension(10,10)), BorderLayout.PAGE_END);




        worldIconWithSpacing.add(Box.createRigidArea(emptyBoxDimension), BorderLayout.LINE_START);
        worldIconWithSpacing.add(userAddedWorldIconOnly, BorderLayout.CENTER);
        worldIconWithSpacing.add(Box.createRigidArea(emptyBoxDimension), BorderLayout.LINE_END);

        worldPanel.setBorder(new FlatRoundBorder());
        worldPanel.add(Box.createRigidArea(emptyBoxDimension), BorderLayout.PAGE_START);
        worldPanel.add(worldIconWithSpacing, BorderLayout.LINE_START);
        worldPanel.add(userAddedWorldDetailsWithoutIcon, BorderLayout.CENTER);
        worldPanel.add(Box.createRigidArea(emptyBoxDimension), BorderLayout.LINE_END);
        worldPanel.add(Box.createRigidArea(emptyBoxDimension), BorderLayout.PAGE_END);


        arrowPanel.add(modeInfo, BorderLayout.LINE_START);
        arrowPanel.add(arrow, BorderLayout.CENTER);




        copyingProgress.add(Box.createRigidArea(emptyBoxDimension), BorderLayout.PAGE_START);
        copyingProgress.add(Box.createRigidArea(new Dimension(10,5)), BorderLayout.LINE_START);
        copyingProgress.add(progressBar, BorderLayout.CENTER);
        copyingProgress.add(Box.createRigidArea(new Dimension(10,5)), BorderLayout.LINE_END);
        copyingProgress.add(Box.createRigidArea(emptyBoxDimension), BorderLayout.PAGE_END);


        startCopyingBtnPanel.add(startCopying, BorderLayout.CENTER);
        startCopyingBtnPanel.add(Box.createRigidArea(emptyBoxDimension), BorderLayout.LINE_END);



        refreshButtonWithSpacing.add(Box.createRigidArea(new Dimension(10,10)), BorderLayout.LINE_START);
        refreshButtonWithSpacing.add(refreshButton, BorderLayout.CENTER);


        startCopyingPanel.add(refreshButtonWithSpacing, BorderLayout.LINE_START);
        startCopyingPanel.add(startCopyingBtnPanel, BorderLayout.LINE_END);
        startCopyingPanel.add(copyingProgress, BorderLayout.PAGE_END);


        worldPaneUpper.add(new JLabel("Selected world:"), BorderLayout.PAGE_START);
        worldPaneUpper.add(Box.createRigidArea(emptyBoxDimension), BorderLayout.LINE_START);
        worldPaneUpper.add(worldPanel, BorderLayout.CENTER);
        worldPaneUpper.add(Box.createRigidArea(emptyBoxDimension), BorderLayout.LINE_END);


        serverIconWithSpacing.add(serverWorldIconLabel, BorderLayout.LINE_START);
        serverIconWithSpacing.add(Box.createRigidArea(emptyBoxDimension), BorderLayout.CENTER); //issue #62 fix for servers
        serverIconWithSpacing.add(serverWorldDetailsWithoutIcon, BorderLayout.LINE_END);


        serverNameAndStuff.add(Box.createRigidArea(emptyBoxDimension), BorderLayout.PAGE_START);
        serverNameAndStuff.add(Box.createRigidArea(emptyBoxDimension), BorderLayout.LINE_START);
        serverNameAndStuff.add(serverIconWithSpacing, BorderLayout.CENTER);
        serverNameAndStuff.add(Box.createRigidArea(emptyBoxDimension), BorderLayout.LINE_END);
        serverNameAndStuff.add(Box.createRigidArea(emptyBoxDimension), BorderLayout.PAGE_END);
        serverNameAndStuff.setBorder(new FlatRoundBorder());


        serverWorldNameWithSpacing.add(Box.createRigidArea(emptyBoxDimension), BorderLayout.LINE_START);
        serverWorldNameWithSpacing.add(serverNameAndStuff, BorderLayout.CENTER);




        serverPanelBottom.add(serverWorldNameWithSpacing, BorderLayout.LINE_START);
        serverPanelBottom.add(Box.createRigidArea(emptyBoxDimension), BorderLayout.LINE_END);


        worldAndArrowPanel.add(worldPaneUpper, BorderLayout.PAGE_START);
//        worldAndArrowPanel.add(Box.createRigidArea(new Dimension(10,10)), BorderLayout.CENTER);
        worldAndArrowPanel.add(arrowPanel, BorderLayout.CENTER);
        worldAndArrowPanel.add(serverPanelBottom, BorderLayout.PAGE_END);


//        addingWorld.add(worldAndArrowPanel, BorderLayout.CENTER);
//        addingWorld.add(Box.createRigidArea(emptyBoxDimension), BorderLayout.LINE_START);
//        addingWorld.add(arrowPanel, BorderLayout.CENTER);
//        addingWorld.add(serverPanelBottom, BorderLayout.PAGE_END);

        addingWorld.add(buttonAndText, BorderLayout.PAGE_START);
        addingWorld.add(worldAndArrowPanel, BorderLayout.LINE_START);
//        addingWorld.add(startCopyingPanel, BorderLayout.PAGE_END);

//        add(buttonAndText, BorderLayout.PAGE_START);
        add(addingWorld, BorderLayout.PAGE_START);
        add(startCopyingPanel, BorderLayout.PAGE_END);
    }

    private void setUserAddedWorld(File world) {
        removeImportButtonWarning();
        if (WorldCopyHandler.isArchive(world)) {
            userAddedWorld = world;
            isInArchiveMode = true;
            WorldCopyHandler.createWorldCopyHandler(worldsTab).start();
        } else {
            isInArchiveMode = false;
            if(world.isFile()) {
                world = new File(world.getParent());
            }
            if (FileUtils.sizeOfDirectory(world) >= ONE_GIGABYTE) {
                setImportButtonWarning("Larger than 1GiB!");
            }
            userAddedWorld = world;
        }
    }

    public void setImportButtonWarning(String message) {
        openButton.setIcon(new FlatSVGIcon(new File("src/com/myne145/serverlauncher/resources/error.svg")).derive(16, 16));
        openButton.setToolTipText(message);
    }
    public void removeImportButtonWarning() {
        openButton.setIcon(null);
        openButton.setToolTipText(null);
    }

    public void setIcons() {
        String extractedWorldSizeText = "Can't obtain world's size";
        if(userAddedWorld != null) {
            openButton.setText("<html><b>Currently selected:</b><br><small>" + userAddedWorld.getAbsolutePath() + "</small></html>");
        } else {
            openButton.setText("<html><sub>\u200E </sub>Import existing world<sup>\u200E </sup></html>");
        }
        if(extractedWorldSize != null)
            extractedWorldSizeText = extractedWorldSize.getText();
        if(userAddedWorld != null)
            isInArchiveMode = WorldCopyHandler.isArchive(userAddedWorld);
//        directoryTree.setDirectory(CurrentServerInfo.serverPath.getAbsolutePath());
        if(userAddedWorld != null && isInArchiveMode) { //issue #7 fix
            userAddedWorldDetailsWithoutIcon.setText(
                    "<html>Level name: " + "TODO" + //can be like that: FOLDER_NAME / level.dat NAME
                    "<br>Last played: " + "TODO" +
                    "<br>Folder size: " + extractedWorldSizeText + "</html>"
            );
        } else if(!isInArchiveMode && userAddedWorld != null) {
            String worldToAddTempText = userAddedWorld.getAbsolutePath();
            if(worldToAddTempText.length() > 50 && userAddedWorld.getName().length() < 25) { //issue #77 fix by adding ... to the path
                worldToAddTempText = worldToAddTempText.substring(0, 9) + "...\\" + userAddedWorld.getName();
            }
            if(worldToAddTempText.length() > 50 && userAddedWorld.getName().length() >= 25) {
                String tempWorldName = userAddedWorld.getName();
                worldToAddTempText = worldToAddTempText.substring(0, 9) + "...\\" + tempWorldName.substring(0,20) +
                        "..." + tempWorldName.substring(tempWorldName.length() - 9, tempWorldName.length() - 1);
            }
            userAddedWorldDetailsWithoutIcon.setText(
                    "<html>Level name: " + worldToAddTempText + //can be like that: FOLDER_NAME / level.dat NAME
                    "<br>Other info like date modified / gamemode used: " + "TODO" +
                    "<br>Folder size: " + FileSize.directorySizeWithConversion(userAddedWorld).getText() + "</html>"
            );
        }

        if(isInArchiveMode && extractedWorldDir != null) {
            File extractedDir = new File(extractedWorldDir);
            if(!new File(extractedWorldDir + "\\icon.png").exists()) {
                boolean doesIconInParentExist = new File(extractedDir.getParent() + "\\icon.png").exists();
                ImageIcon parentImg = new ImageIcon(new ImageIcon(extractedDir.getParent() +
                        "\\icon.png").getImage().getScaledInstance(96, 96, Image.SCALE_SMOOTH));
                userAddedWorldIconOnly.setIcon(doesIconInParentExist ? parentImg : DEFAULT_WORLD_ICON_PACK_PNG);
            } else {
                if(new File(extractedDir + "\\icon.png").exists()) //issue #22 fixed by adding another check
                    userAddedWorldIconOnly.setIcon(new ImageIcon(new ImageIcon(extractedDir + "\\icon.png").getImage().getScaledInstance(96, 96, Image.SCALE_SMOOTH)));
                else
                    userAddedWorldIconOnly.setIcon(DEFAULT_WORLD_ICON_PACK_PNG);
            }
        } else if(userAddedWorld != null && userAddedWorld.exists()) { //issue #8 fix
            startCopying.setEnabled(true);
            if(new File(userAddedWorld + "\\icon.png").exists()) //issue #24 fix
                userAddedWorldIconOnly.setIcon(new ImageIcon(new ImageIcon(userAddedWorld + "\\icon.png").getImage().getScaledInstance(96,96, Image.SCALE_SMOOTH)));
            else
                userAddedWorldIconOnly.setIcon(DEFAULT_WORLD_ICON_PACK_PNG);
        } else if(extractedWorldDir == null) {
            userAddedWorldIconOnly.setIcon(DEFAULT_WORLD_ICON_PACK_PNG);
        }


        if(!new File(CurrentServerInfo.serverWorldPath + "\\icon.png").exists()) {
            serverWorldIconLabel.setIcon(DEFAULT_WORLD_ICON_PACK_PNG);
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
            serverWorldDetailsWithoutIcon.setText(
                    "<html>Level name: " + CurrentServerInfo.serverLevelName + " / " + folderNameTemp +
                    "<br>Other info like date modified / gamemode used: " + "TODO" +
                    "<br>Folder size: " + serverWorldFileSizeBytes.getText() + "</html>"
            );
        } else {
            serverWorldDetailsWithoutIcon.setText("Server world folder does not exist.");
        }

    }
    public void setExtractedWorldDir(String extractedWorldDir) {
        this.extractedWorldDir = extractedWorldDir;
    }

    public String getExtractedWorldDir() {
        return extractedWorldDir;
    }

    public File getUserAddedWorld() {
        return userAddedWorld;
    }

    public JProgressBar getProgressBar() {
        return progressBar;
    }

    public JButton getStartCopyingButton() {
        return startCopying;
    }
}