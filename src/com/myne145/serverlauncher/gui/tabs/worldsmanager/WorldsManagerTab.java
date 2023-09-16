package com.myne145.serverlauncher.gui.tabs.worldsmanager;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.ui.FlatLineBorder;
import com.myne145.serverlauncher.gui.tabs.worldsmanager.components.WorldsInfoPanels;
import com.myne145.serverlauncher.utils.AlertType;
import com.myne145.serverlauncher.gui.window.components.ContainerPane;
import com.myne145.serverlauncher.gui.window.Window;
import com.myne145.serverlauncher.server.Config;
import com.myne145.serverlauncher.utils.FileSize;
import com.myne145.serverlauncher.server.current.CurrentServerInfo;
import com.myne145.serverlauncher.server.WorldCopyHandler;
import com.formdev.flatlaf.ui.FlatRoundBorder;
import com.myne145.serverlauncher.server.current.NBTParser;
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

import static com.myne145.serverlauncher.gui.window.Window.alert;
import static com.myne145.serverlauncher.gui.window.Window.getErrorDialogMessage;
import static com.myne145.serverlauncher.utils.ZipUtils.isArchive;

public class WorldsManagerTab extends JPanel {
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
    private final WorldsManagerTab worldsManagerTab;

    private final double ONE_GIGABYTE = 1073741824;
    private final JButton openButton =  new JButton("<html><sub>\u200E </sub>Import existing world<sup>\u200E </sup></html>");
    private final WorldsInfoPanels worldsInfoPanels = new WorldsInfoPanels();

    public WorldsManagerTab(ContainerPane parentPane, int tabSwitchingToIndex) {
        super(new BorderLayout());
        worldsManagerTab = this;
        if(!Config.getData().get(tabSwitchingToIndex).serverPath().exists())
            return;

        startCopying.setEnabled(false);
        openButton.setMaximumSize(new Dimension(300, 40));
        openButton.addActionListener(e -> {
            Runnable runnable = () -> {
                JnaFileChooser fileDialog = new JnaFileChooser();
                fileDialog.showOpenDialog(Window.getWindow());

                removeImportButtonWarning();
                File[] filePaths = fileDialog.getSelectedFiles();

                if (fileDialog.getSelectedFiles().length == 0 || filePaths == null || filePaths[0] == null) {
                    return;
                }

                File fileToAdd = filePaths[0];
                setUserAddedWorld(fileToAdd);
            };
            new Thread(runnable).start();

        });
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

        Dimension EMPTY_BOX_DIMENSION = new Dimension(10, 10);




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
        JPanel refreshButtonWithSpacing = new JPanel(new BorderLayout());


        JPanel worldPaneUpper = new JPanel(new BorderLayout());
        JPanel serverInfoPanelWithIcon = new JPanel(new BorderLayout());
        JPanel serverInfoPanelWithSpacing = new JPanel(new BorderLayout());
        JPanel serverWorldNameWithSpacing = new JPanel(new BorderLayout());
        JPanel serverPanelBottom = new JPanel(new BorderLayout());
        JPanel worldAndArrowPanel = new JPanel();

        worldAndArrowPanel.setLayout(new BoxLayout(worldAndArrowPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("World Manager");
        title.setFont(new Font("Arial", Font.BOLD, 18));


        titlePanel.add(Box.createRigidArea(new Dimension(5,5)), BorderLayout.PAGE_START);
        titlePanel.add(Box.createRigidArea(new Dimension(5,10)), BorderLayout.LINE_START);
        titlePanel.add(title, BorderLayout.CENTER);
        titlePanel.add(Box.createRigidArea(new Dimension(5,10)), BorderLayout.PAGE_END);

        openButtonInCorrectPlacement.add(openButton, BorderLayout.LINE_START);

        buttonAndText.add(titlePanel, BorderLayout.PAGE_START);
        buttonAndText.add(Box.createRigidArea(EMPTY_BOX_DIMENSION), BorderLayout.LINE_START);
        buttonAndText.add(openButtonInCorrectPlacement, BorderLayout.CENTER);
        buttonAndText.add(Box.createRigidArea(new Dimension(10,10)), BorderLayout.PAGE_END);




        worldIconWithSpacing.add(Box.createRigidArea(EMPTY_BOX_DIMENSION), BorderLayout.LINE_START);
        worldIconWithSpacing.add(userAddedWorldIconOnly, BorderLayout.CENTER);
        worldIconWithSpacing.add(Box.createRigidArea(EMPTY_BOX_DIMENSION), BorderLayout.LINE_END);

        worldPanel.setBorder(new FlatRoundBorder());
        worldPanel.add(Box.createRigidArea(EMPTY_BOX_DIMENSION), BorderLayout.PAGE_START);
        worldPanel.add(worldIconWithSpacing, BorderLayout.LINE_START);
        worldPanel.add(userAddedWorldDetailsWithoutIcon, BorderLayout.CENTER);
        worldPanel.add(Box.createRigidArea(EMPTY_BOX_DIMENSION), BorderLayout.LINE_END);
        worldPanel.add(Box.createRigidArea(EMPTY_BOX_DIMENSION), BorderLayout.PAGE_END);

        worldPanel.setBackground(new Color(60, 63, 65));
        worldPanel.setBackground(new Color(60, 63, 65));
        worldPanel.setBorder(new FlatLineBorder(new Insets(0, 0, 0, 0), new Color(44, 44, 44), 1, 32));

        JPanel worldInfoPanelWithIcon = new JPanel();

        worldInfoPanelWithIcon.add(Box.createRigidArea(EMPTY_BOX_DIMENSION));
        worldInfoPanelWithIcon.add(userAddedWorldIconOnly);
        worldInfoPanelWithIcon.add(Box.createRigidArea(EMPTY_BOX_DIMENSION));
        worldInfoPanelWithIcon.add(userAddedWorldDetailsWithoutIcon);

        worldPanel.add(Box.createRigidArea(EMPTY_BOX_DIMENSION), BorderLayout.PAGE_START);
//        worldPanel.add(Box.createRigidArea(emptyBoxDimension), BorderLayout.LINE_START);
        worldPanel.add(worldInfoPanelWithIcon, BorderLayout.LINE_START);
        worldPanel.add(Box.createRigidArea(EMPTY_BOX_DIMENSION), BorderLayout.LINE_END);
        worldPanel.add(Box.createRigidArea(EMPTY_BOX_DIMENSION), BorderLayout.PAGE_END);


        worldInfoPanelWithIcon.setBackground(new Color(60, 63, 65));
        worldPanel.setBackground(new Color(60, 63, 65));
        worldPanel.setBorder(new FlatLineBorder(new Insets(0, 0, 0, 0), new Color(44, 44, 44), 1, 32));


        copyingProgress.add(Box.createRigidArea(EMPTY_BOX_DIMENSION), BorderLayout.PAGE_START);
        copyingProgress.add(Box.createRigidArea(new Dimension(10,5)), BorderLayout.LINE_START);
        copyingProgress.add(progressBar, BorderLayout.CENTER);
        copyingProgress.add(Box.createRigidArea(new Dimension(10,5)), BorderLayout.LINE_END);
        copyingProgress.add(Box.createRigidArea(EMPTY_BOX_DIMENSION), BorderLayout.PAGE_END);


        startCopyingBtnPanel.add(startCopying, BorderLayout.CENTER);
        startCopyingBtnPanel.add(Box.createRigidArea(EMPTY_BOX_DIMENSION), BorderLayout.LINE_END);

        refreshButtonWithSpacing.add(Box.createRigidArea(new Dimension(10,10)), BorderLayout.LINE_START);
        refreshButtonWithSpacing.add(refreshButton, BorderLayout.CENTER);


        startCopyingPanel.add(refreshButtonWithSpacing, BorderLayout.LINE_START);
        startCopyingPanel.add(startCopyingBtnPanel, BorderLayout.LINE_END);
        startCopyingPanel.add(copyingProgress, BorderLayout.PAGE_END);

//
        worldPaneUpper.add(Box.createRigidArea(EMPTY_BOX_DIMENSION), BorderLayout.LINE_START);
        worldPaneUpper.add(worldPanel, BorderLayout.CENTER);
        worldPaneUpper.add(Box.createRigidArea(EMPTY_BOX_DIMENSION), BorderLayout.LINE_END);


        serverInfoPanelWithIcon.add(serverWorldIconLabel, BorderLayout.LINE_START);
        serverInfoPanelWithIcon.add(Box.createRigidArea(EMPTY_BOX_DIMENSION), BorderLayout.CENTER);
        serverInfoPanelWithIcon.add(serverWorldDetailsWithoutIcon, BorderLayout.LINE_END);


        serverInfoPanelWithSpacing.add(Box.createRigidArea(EMPTY_BOX_DIMENSION), BorderLayout.PAGE_START);
        serverInfoPanelWithSpacing.add(Box.createRigidArea(EMPTY_BOX_DIMENSION), BorderLayout.LINE_START);
        serverInfoPanelWithSpacing.add(serverInfoPanelWithIcon, BorderLayout.CENTER);
        serverInfoPanelWithSpacing.add(Box.createRigidArea(EMPTY_BOX_DIMENSION), BorderLayout.LINE_END);
        serverInfoPanelWithSpacing.add(Box.createRigidArea(EMPTY_BOX_DIMENSION), BorderLayout.PAGE_END);


        serverInfoPanelWithIcon.setBackground(new Color(60, 63, 65));
        serverInfoPanelWithSpacing.setBackground(new Color(60, 63, 65));
        serverInfoPanelWithSpacing.setBorder(new FlatLineBorder(new Insets(0, 0, 0, 0), new Color(44, 44, 44), 1, 32));


        serverWorldNameWithSpacing.add(Box.createRigidArea(EMPTY_BOX_DIMENSION), BorderLayout.LINE_START);
        serverWorldNameWithSpacing.add(serverInfoPanelWithSpacing, BorderLayout.CENTER);


        serverPanelBottom.add(serverWorldNameWithSpacing, BorderLayout.LINE_START);
        serverPanelBottom.add(Box.createRigidArea(EMPTY_BOX_DIMENSION), BorderLayout.LINE_END);


//        worldAndArrowPanel.add(worldPaneUpper);
//        worldAndArrowPanel.add(serverPanelBottom);
        worldAndArrowPanel.add(worldsInfoPanels);


        addingWorld.add(buttonAndText, BorderLayout.PAGE_START);
        addingWorld.add(Box.createRigidArea(EMPTY_BOX_DIMENSION), BorderLayout.LINE_START);
        addingWorld.add(worldsInfoPanels, BorderLayout.CENTER);
        addingWorld.add(Box.createRigidArea(EMPTY_BOX_DIMENSION), BorderLayout.LINE_END);
//        addingWorld.add(startCopyingPanel, BorderLayout.PAGE_END);

//        add(buttonAndText, BorderLayout.PAGE_START);
        add(addingWorld, BorderLayout.PAGE_START);
        add(startCopyingPanel, BorderLayout.PAGE_END);

        setIcons(); //non-removable
    }

    private void setUserAddedWorld(File world) {
        removeImportButtonWarning();
        if (isArchive(world)) {
            userAddedWorld = world;
            isInArchiveMode = true;
            WorldCopyHandler.createWorldCopyHandler(worldsManagerTab).start();
        } else {
            isInArchiveMode = false;
            if(world.isFile()) {
                world = new File(world.getParent());
            }

            if (FileUtils.sizeOfDirectory(world) >= ONE_GIGABYTE) {
                setImportButtonWarning("Larger than 1GiB!");
            }
            userAddedWorld = world;
            WorldCopyHandler.createWorldCopyHandler(worldsManagerTab).start();
            setIcons(); //non-removable
        }
    }

    public void setImportButtonWarning(String message) {
        openButton.setIcon(new FlatSVGIcon(new File("src/com/myne145/serverlauncher/resources/error.svg")).derive(16, 16));
        if(openButton.getToolTipText() != null) {
            openButton.setToolTipText(openButton.getToolTipText() + "\n" + message);
        } else
            openButton.setToolTipText(message);
    }
    public void removeImportButtonWarning() {
        openButton.setIcon(null);
        openButton.setToolTipText(null);
    }

    public void setIcons() {
        System.out.println(userAddedWorld);
        String extractedWorldSizeText = "Can't obtain world's size";
        if(userAddedWorld != null) {
            openButton.setText("<html><b>Currently selected:</b><br><small>" + userAddedWorld.getAbsolutePath() + "</small></html>");
        } else {
            openButton.setText("<html><sub>\u200E </sub>Import existing world<sup>\u200E </sup></html>");
        }
        if(extractedWorldSize != null)
            extractedWorldSizeText = extractedWorldSize.getText();
        if(userAddedWorld != null)
            isInArchiveMode = isArchive(userAddedWorld);
//        directoryTree.setDirectory(CurrentServerInfo.serverPath.getAbsolutePath());
        if(userAddedWorld != null && isInArchiveMode) {
//            NBTParser nbtParser = NBTParser.createAddedWorldNBTParser(extractedWorldDir);
//            userAddedWorldDetailsWithoutIcon.setText(
//                    "<html>Level name: " + extractedWorldDir + //can be like that: FOLDER_NAME / level.dat NAME
//                    "<br>Last played: " + "TODO" +
//                    "<br>Folder size: " + extractedWorldSizeText + "</html>"
//            );
            worldsInfoPanels.updateClientWorldInformation(new File(extractedWorldDir));
        } else if(!isInArchiveMode && userAddedWorld != null) {
//            String worldToAddTempText = userAddedWorld.getAbsolutePath();
//            if(worldToAddTempText.length() > 50 && userAddedWorld.getName().length() < 25) { //issue #77 fix by adding ... to the path
//                worldToAddTempText = worldToAddTempText.substring(0, 9) + "...\\" + userAddedWorld.getName();
//            }
//            if(worldToAddTempText.length() > 50 && userAddedWorld.getName().length() >= 25) {
//                String tempWorldName = userAddedWorld.getName();
//                worldToAddTempText = worldToAddTempText.substring(0, 9) + "...\\" + tempWorldName.substring(0,20) +
//                        "..." + tempWorldName.substring(tempWorldName.length() - 9, tempWorldName.length() - 1);
//            }
//            userAddedWorldDetailsWithoutIcon.setText(
//                    "<html>Level name: " + worldToAddTempText + //can be like that: FOLDER_NAME / level.dat NAME
//                    "<br>Other info like date modified / gamemode used: " + "TODO" +
//                    "<br>Folder size: " + FileSize.directorySizeWithConversion(userAddedWorld).getText() + "</html>"
//            );
            worldsInfoPanels.updateClientWorldInformation(userAddedWorld);

        }

//        if(isInArchiveMode && extractedWorldDir != null) {
//            File extractedDir = new File(extractedWorldDir);
//            if(!new File(extractedWorldDir + "\\icon.png").exists()) {
//                boolean doesIconInParentExist = new File(extractedDir.getParent() + "\\icon.png").exists();
//                ImageIcon parentImg = new ImageIcon(new ImageIcon(extractedDir.getParent() +
//                        "\\icon.png").getImage().getScaledInstance(96, 96, Image.SCALE_SMOOTH));
//                userAddedWorldIconOnly.setIcon(doesIconInParentExist ? parentImg : DEFAULT_WORLD_ICON_PACK_PNG);
//            } else {
//                if(new File(extractedDir + "\\icon.png").exists()) //issue #22 fixed by adding another check
//                    userAddedWorldIconOnly.setIcon(new ImageIcon(new ImageIcon(extractedDir + "\\icon.png").getImage().getScaledInstance(96, 96, Image.SCALE_SMOOTH)));
//                else
//                    userAddedWorldIconOnly.setIcon(DEFAULT_WORLD_ICON_PACK_PNG);
//            }
//        } else if(userAddedWorld != null && userAddedWorld.exists()) { //issue #8 fix
//            startCopying.setEnabled(true);
//            if(new File(userAddedWorld + "\\icon.png").exists()) //issue #24 fix
//                userAddedWorldIconOnly.setIcon(new ImageIcon(new ImageIcon(userAddedWorld + "\\icon.png").getImage().getScaledInstance(96,96, Image.SCALE_SMOOTH)));
//            else
//                userAddedWorldIconOnly.setIcon(DEFAULT_WORLD_ICON_PACK_PNG);
//        } else if(extractedWorldDir == null) {
//            userAddedWorldIconOnly.setIcon(DEFAULT_WORLD_ICON_PACK_PNG);
//        }


//        if(!new File(CurrentServerInfo.world.getPath() + "\\icon.png").exists()) {
//            serverWorldIconLabel.setIcon(DEFAULT_WORLD_ICON_PACK_PNG);
//        } else {
//            serverWorldIconLabel.setIcon(new ImageIcon(new ImageIcon(CurrentServerInfo.world.getPath() + "\\icon.png")
//                    .getImage().getScaledInstance(96, 96, Image.SCALE_SMOOTH)));
//        }

        if(CurrentServerInfo.world.getPath().exists()) {
//            FileSize serverWorldFileSizeBytes = FileSize.directorySizeWithConversion(CurrentServerInfo.world.getPath());
////            LevelNameColorConverter.convertColors(ServerDetails.serverLevelName);
//            if(CurrentServerInfo.world.getLevelName() == null)
//                CurrentServerInfo.world.levelName = "Level.dat file not found.";
//
//            String folderNameTemp = CurrentServerInfo.world.getPath().getName();
////            if(!wasServerPropertiesFound)
////                folderNameTemp = "server.properties file does not exist";
//            serverWorldDetailsWithoutIcon.setText(
//                    "<html>Level name: " + CurrentServerInfo.world.getLevelName() + " / " + folderNameTemp +
//                    "<br>Other info like date modified / gamemode used: " + "TODO" +
//                    "<br>Folder size: " + serverWorldFileSizeBytes.getText() + "</html>"
//            );
            worldsInfoPanels.updateServerWorldInformation(CurrentServerInfo.world.path);
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

    public JButton getStartCopying() {
        return startCopying;
    }
}