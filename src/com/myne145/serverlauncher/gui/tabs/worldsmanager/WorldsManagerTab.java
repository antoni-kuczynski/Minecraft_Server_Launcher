package com.myne145.serverlauncher.gui.tabs.worldsmanager;

import com.myne145.serverlauncher.utils.ButtonWarning;
import com.myne145.serverlauncher.gui.components.PickFileButton;
import com.myne145.serverlauncher.server.MinecraftWorld;
import com.myne145.serverlauncher.gui.window.ContainerPane;
import com.myne145.serverlauncher.gui.window.Window;
import com.myne145.serverlauncher.server.Config;
import com.myne145.serverlauncher.server.MinecraftServer;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import static com.myne145.serverlauncher.utils.ZipUtils.isArchive;

public class WorldsManagerTab extends JPanel {
    private final JProgressBar progressBar = new JProgressBar();
    private final JButton startCopying = new JButton("Start importing");
    private final MinecraftWorld userAddedWorld = new MinecraftWorld(progressBar);
    private boolean isInArchiveMode;
    private final int index;
    private final WorldsInfoPanels worldsInfoPanels;
    private final PickFileButton pickFileButton = new PickFileButton("Import existing world", new Dimension(130, 40), new Dimension(130, 40), this::setUserAddedWorld);
    private File selectedWorld;

    public WorldsManagerTab(ContainerPane parentPane, MinecraftServer server) {
        super(new BorderLayout());

        index = server.getServerId();
        worldsInfoPanels = new WorldsInfoPanels(server);

        if(!server.getServerPath().exists())
            return;

        startCopying.setEnabled(false);

        TransferHandler transferHandler = pickFileButton.getCustomTransferHandler(this::setUserAddedWorld);
        this.setTransferHandler(transferHandler);

        startCopying.addActionListener(e -> {
            try {
                userAddedWorld.copyToServer(Config.getData().get(index - 1));
            } catch (IOException e1) {
                Window.showErrorMessage("I/O error copying the world files.", e1);
            }
            worldsInfoPanels.updateServerWorldInformation();
            setIcons();
        });

        JButton refreshButton = new JButton("Refresh worlds");
        refreshButton.setToolTipText("Refreshes the server world, if it was replaced meanwhile.\nNote: This isn't for refreshing the server list.");

        refreshButton.addActionListener(e -> parentPane.onTabSwitched(index));

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

        JLabel title = new JLabel( "<html>Worlds - " + server.getName(50) + "</html>");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        if(server.getName().length() > 50)
            title.setToolTipText(server.getName());


        titlePanel.add(Box.createRigidArea(new Dimension(5,5)), BorderLayout.PAGE_START);
        titlePanel.add(Box.createRigidArea(new Dimension(5,10)), BorderLayout.LINE_START);
        titlePanel.add(title, BorderLayout.CENTER);
        titlePanel.add(Box.createRigidArea(new Dimension(5,10)), BorderLayout.PAGE_END);

        openButtonInCorrectPlacement.add(pickFileButton, BorderLayout.LINE_START);


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
//        final File worldBs = world;
        double ONE_GIGABYTE = 1073741824;
        if (isArchive(world)) {
            if (FileUtils.sizeOf(world) >= ONE_GIGABYTE) {
                pickFileButton.setImportButtonWarning(ButtonWarning.LARGER_THAN_1GIB);
            }

            File finalWorld = world;
            Thread thread = new Thread(() -> {
                try {
                    userAddedWorld.updateFromArchive(finalWorld, this);
                } catch (IOException e) {
                    Window.showErrorMessage("I/O error extracting the archive.", e);
                }
                setIcons();
            });
            thread.start();
            this.selectedWorld = world;
        } else {
            if (FileUtils.sizeOfDirectory(world.getParentFile()) >= ONE_GIGABYTE) {
                pickFileButton.setImportButtonWarning(ButtonWarning.LARGER_THAN_1GIB);
            }
            isInArchiveMode = false;
            if(world.isFile()) {
                world = new File(world.getParent());
            }
            File levelDat = new File(world.getAbsolutePath() + "/level.dat");
            if(!levelDat.exists()) {
                getPickDirectoryButton().setImportButtonWarning(ButtonWarning.NOT_A_MINECRAFT_WORLD);
//                return;
            }
//            if(userAddedWorld == null)
//                userAddedWorld = new MinecraftWorld(levelDat);
//            else
            userAddedWorld.updateFromLevelDat(levelDat);
            startCopying.setEnabled(true);

//            userAddedWorld = world;
            getWorldsInfoPanels().updateClientWorldInformation(userAddedWorld);
//            WorldCopyHandler.createWorldCopyHandler(worldsManagerTab).start();
            setIcons();
        }
    }

    public void setIcons() {
        if(userAddedWorld == null || userAddedWorld.getWorldPath() == null)
            return;

        isInArchiveMode = isArchive(userAddedWorld.getWorldPath());
        worldsInfoPanels.updateClientWorldInformation(userAddedWorld);
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

    public int getIndex() {
        return index;
    }

    public PickFileButton getPickDirectoryButton() {
        return pickFileButton;
    }

    public File getSelectedWorld() {
        return selectedWorld;
    }
}