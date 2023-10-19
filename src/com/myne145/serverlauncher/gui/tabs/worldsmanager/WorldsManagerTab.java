package com.myne145.serverlauncher.gui.tabs.worldsmanager;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.myne145.serverlauncher.gui.tabs.worldsmanager.components.WorldsInfoPanels;
import com.myne145.serverlauncher.utils.AlertType;
import com.myne145.serverlauncher.gui.window.ContainerPane;
import com.myne145.serverlauncher.gui.window.Window;
import com.myne145.serverlauncher.server.Config;
import com.myne145.serverlauncher.utils.FileDetailsUtils;
import com.myne145.serverlauncher.server.WorldCopyHandler;
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
    private final JButton startCopying = new JButton("Start importing");
    private File userAddedWorld;
    private String extractedWorldDir;
    private boolean isInArchiveMode;
    public FileDetailsUtils extractedWorldSize;
    private final WorldsManagerTab worldsManagerTab;

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

        startCopying.addActionListener(e -> WorldCopyHandler.createWorldCopyHandler(this).setCopyMode(true).start());

        JButton refreshButton = new JButton("Refresh");

        refreshButton.addActionListener(e -> parentPane.onTabSwitched(tabSwitchingToIndex));

        Dimension EMPTY_BOX_DIMENSION = new Dimension(10, 10);


        //Panels
        JPanel titlePanel = new JPanel(new BorderLayout());
        JPanel openButtonInCorrectPlacement = new JPanel(new BorderLayout());
        JPanel buttonAndText = new JPanel(new BorderLayout());

        JPanel startCopyingPanel = new JPanel(new BorderLayout());
        JPanel copyingProgress = new JPanel(new BorderLayout());
        JPanel startCopyingBtnPanel = new JPanel(new BorderLayout());

        JPanel addingWorld = new JPanel(new BorderLayout());
        JPanel refreshButtonWithSpacing = new JPanel(new BorderLayout());

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

        addingWorld.add(buttonAndText, BorderLayout.PAGE_START);
        addingWorld.add(Box.createRigidArea(EMPTY_BOX_DIMENSION), BorderLayout.LINE_START);
        addingWorld.add(worldsInfoPanels, BorderLayout.CENTER);
        addingWorld.add(Box.createRigidArea(EMPTY_BOX_DIMENSION), BorderLayout.LINE_END);

        add(addingWorld, BorderLayout.PAGE_START);
        add(startCopyingPanel, BorderLayout.PAGE_END);

        setIcons();
    }

    private void setUserAddedWorld(File world) {
        removeImportButtonWarning();
        if (isArchive(world)) {
            userAddedWorld = world;
            isInArchiveMode = true;
            WorldCopyHandler worldCopyHandler = WorldCopyHandler.createWorldCopyHandler(worldsManagerTab);
            worldCopyHandler.start();
        } else {
            isInArchiveMode = false;
            if(world.isFile()) {
                world = new File(world.getParent());
            }

            double ONE_GIGABYTE = 1073741824;
            if (FileUtils.sizeOfDirectory(world) >= ONE_GIGABYTE) {
                setImportButtonWarning("Larger than 1GiB!");
            }
            userAddedWorld = world;
            WorldCopyHandler.createWorldCopyHandler(worldsManagerTab).start();
            setIcons();
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
        if(userAddedWorld != null) {
            openButton.setText("<html><b>Currently selected:</b><br><small>" + userAddedWorld.getAbsolutePath() + "</small></html>");
        } else {
            openButton.setText("<html><sub>\u200E </sub>Import existing world<sup>\u200E </sup></html>");
        }

        if(userAddedWorld != null)
            isInArchiveMode = isArchive(userAddedWorld);

        if(userAddedWorld != null && isInArchiveMode) {
            worldsInfoPanels.updateClientWorldInformation(new File(extractedWorldDir));
        } else if(!isInArchiveMode && userAddedWorld != null) {
            worldsInfoPanels.updateClientWorldInformation(userAddedWorld);
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

    public WorldsInfoPanels getWorldsInfoPanels() {
        return worldsInfoPanels;
    }
}