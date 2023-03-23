package Gui;

import Servers.WorldCopyHandler;
import com.formdev.flatlaf.ui.FlatRoundBorder;
import dev.dewy.nbt.Nbt;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.io.Serial;

import static Gui.Frame.alert;
import static Gui.Frame.exStackTraceToString;

public class AddWorldsPanel extends JPanel {
    private static File worldToAdd;
    private final JProgressBar progressBar = new JProgressBar();
    private static String extractedWorldDir;
    private final JLabel worldIcon = new JLabel();
    private final JLabel serverWorldIcon = new JLabel();
    private final JButton startCopying = new JButton("Start Copying");
    private final JTextArea worldNameAndStuffText = new JTextArea();
    private final JTextArea serverWorldNameAndStuff = new JTextArea();
    private final JPanel worldPanelUpper = new JPanel(new BorderLayout());
    private final JPanel serverPanelBottom = new JPanel(new BorderLayout());
    private boolean isArchiveMode; //issue #8 fixed by adding a boolean to check the content's type
    private final ImageIcon defaultWorldIcon = new ImageIcon(new ImageIcon("defaultworld.jpg").getImage().getScaledInstance(96, 96, Image.SCALE_SMOOTH));
    public AddWorldsPanel() throws IOException {
        super(new BorderLayout());
        JLabel dragNDropInfo = new JLabel(" or drag and drop it here.");
        JLabel selectedServerTxt = new JLabel();
        String selServPrefix = "Selected server: ";
        selectedServerTxt.setText(selServPrefix + ConfigStuffPanel.getServName());
        startCopying.setEnabled(false);
        JButton openButton = new JButton("Open Folder");
        openButton.addActionListener(e -> {
            FileDialog fileDialog = new FileDialog((Frame)null, "Select Folder");
            fileDialog.setMode(FileDialog.LOAD);
            fileDialog.setFile("level.dat");
            fileDialog.setVisible(true);

            File[] filePaths = fileDialog.getFiles();
            String folderPath = fileDialog.getDirectory();

            if(fileDialog.getFiles().length > 0 && filePaths != null && folderPath != null) {
                File filePath = filePaths[0];
                String fileExtension = filePath.toString().split("\\.")[filePath.toString().split("\\.").length - 1];

                if (fileExtension.equals("zip") || fileExtension.equals("rar") || fileExtension.equals("7z") || fileExtension.equals("tar")) {
                    worldToAdd = filePath;
                    isArchiveMode = true;
                    try {
                        new WorldCopyHandler(this, progressBar, worldToAdd, false).start();
                    } catch (IOException ex) {
                        alert(AlertType.ERROR, exStackTraceToString(ex.getStackTrace()));
                    }
                } else {
                    isArchiveMode = false;
                    File folder = new File(folderPath);
                    //issue #16 fix adding a warning to check for folder's size
                    if(FileUtils.sizeOfDirectory(folder) > 1000000000) { //greater than 1GB
                        if (JOptionPane.showConfirmDialog(null, "Folder that you're trying to copy's size is greater than 1GB. Do you still want to prooced?", "Warning",
                                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                            worldToAdd = folder; //yes option
                        }
                    } else { //if file is less than 1gb
                        worldToAdd = folder;
                    }
                }
            }

            repaint();
        });

        final JPanel tempPanel = this;
        this.setTransferHandler(new TransferHandler() {
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
                        String fileExtension = fileToAdd.toString().split("\\.")[fileToAdd.toString().split("\\.").length - 1];

                        if (fileExtension.equals("zip") || fileExtension.equals("rar") || fileExtension.equals("7z") || fileExtension.equals("tar")) {
                            isArchiveMode = true;
                            worldToAdd = fileToAdd;
                            new WorldCopyHandler(tempPanel, progressBar, worldToAdd, false).start();
                        } else {
                            isArchiveMode = false;
                            //issue #16 fix adding a warning to check for folder's size
                            if(FileUtils.sizeOfDirectory(new File(fileToAdd.getParent())) > 1000000000) { //greater than 1GB
                                if (JOptionPane.showConfirmDialog(null, "Folder that you're trying to copy's size is greater than 1GB. Do you still want to prooced?", "Warning",
                                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                                    worldToAdd = new File(fileToAdd.getParent()); //yes option
                                }
                            } else { //if file is less than 1gb
                                worldToAdd = new File(fileToAdd.getParent());
                            }

                        }
                        System.out.println(extractedWorldDir);
                        repaint();
                    } catch (UnsupportedFlavorException | IOException e) {
                            alert(AlertType.ERROR, exStackTraceToString(e.getStackTrace()));
                        return false;
                    }
                return true;
            }
        });


        startCopying.addActionListener(e -> {
            WorldCopyHandler worldCopyHandler;
            try {
                worldCopyHandler = new WorldCopyHandler(this, progressBar, worldToAdd, true);
            } catch (IOException ex) {
                alert(AlertType.ERROR, exStackTraceToString(ex.getStackTrace()));
                throw new RuntimeException(); //idk why but this line needs to stay here or i need to deal with another nullpointerexception
            }
            worldCopyHandler.start();
        });

        worldIcon.setIcon(defaultWorldIcon);
//        worldNameAndStuffText.setLineWrap(true);

        //Empty Panels
        JPanel emptyPanel7 = new JPanel();
        JPanel emptyPanel8 = new JPanel(); //lmao im definetely doing this wrong, surely no one could be this fucking dumb to want to become a frontend dev
        JPanel emptyPanel9 = new JPanel();

        //Set sizes
        emptyPanel7.setPreferredSize(new Dimension(10,5));
        emptyPanel8.setPreferredSize(new Dimension(10,5));
        emptyPanel9.setPreferredSize(new Dimension(10,100));

        ArrayList<JPanel> emptyPanels = new ArrayList<>(); //lmfao
        for(int i = 0; i < 25; i++) {
            JPanel panel = new JPanel();
            panel.setPreferredSize(new Dimension(10,10));
            emptyPanels.add(panel);
        }

        //Panels
        JPanel buttonAndText = new JPanel(new BorderLayout());
        JPanel separatorBtnTextKinda = new JPanel(new BorderLayout());
        JPanel startCopyingPanel = new JPanel(new BorderLayout());
        JPanel copyingProgress = new JPanel(new BorderLayout());
        JPanel startCopyingBtnPanel = new JPanel(new BorderLayout());


        openButton.setPreferredSize(new Dimension(130, 40));
        separatorBtnTextKinda.add(openButton, BorderLayout.LINE_START);
        separatorBtnTextKinda.add(dragNDropInfo, BorderLayout.CENTER);
        separatorBtnTextKinda.add(emptyPanels.get(0), BorderLayout.LINE_END);

        buttonAndText.add(emptyPanels.get(1), BorderLayout.PAGE_START);
        buttonAndText.add(emptyPanels.get(2), BorderLayout.LINE_START);
        buttonAndText.add(separatorBtnTextKinda, BorderLayout.CENTER);

        copyingProgress.add(emptyPanels.get(3), BorderLayout.PAGE_START);
        copyingProgress.add(emptyPanel7, BorderLayout.LINE_START);
        copyingProgress.add(progressBar, BorderLayout.CENTER);
        copyingProgress.add(emptyPanel8, BorderLayout.LINE_END);
        copyingProgress.add(emptyPanels.get(4), BorderLayout.PAGE_END);


        startCopyingBtnPanel.add(startCopying, BorderLayout.CENTER);
        startCopyingBtnPanel.add(emptyPanels.get(5), BorderLayout.LINE_END);

        startCopyingPanel.add(emptyPanels.get(6), BorderLayout.LINE_START);
        startCopyingPanel.add(startCopyingBtnPanel, BorderLayout.LINE_END);
        startCopyingPanel.add(copyingProgress, BorderLayout.PAGE_END);


        JPanel addingWorld = new JPanel(new BorderLayout());


        worldNameAndStuffText.setEditable(false);
        worldNameAndStuffText.setText("World File name will appear here.");

        worldPanelUpper.add(emptyPanels.get(7), BorderLayout.PAGE_START);
        worldPanelUpper.add(emptyPanels.get(8), BorderLayout.LINE_START);
        worldPanelUpper.add(worldIcon, BorderLayout.CENTER);
        worldPanelUpper.add(worldNameAndStuffText, BorderLayout.LINE_END);

        JPanel serverNameAndStuff = new JPanel(new BorderLayout());

        serverWorldNameAndStuff.setEditable(false);

        serverNameAndStuff.add(emptyPanels.get(9), BorderLayout.LINE_START);
        serverNameAndStuff.add(serverWorldIcon, BorderLayout.CENTER);
        serverNameAndStuff.add(serverWorldNameAndStuff, BorderLayout.LINE_END);

        serverPanelBottom.add(serverNameAndStuff, BorderLayout.LINE_START);

        addingWorld.add(worldPanelUpper, BorderLayout.PAGE_START);
        addingWorld.add(serverPanelBottom, BorderLayout.PAGE_END);

        add(buttonAndText, BorderLayout.PAGE_START);
        add(addingWorld, BorderLayout.LINE_START);
        add(startCopyingPanel, BorderLayout.PAGE_END);
    }

    WorldCopyHandler worldCopyText = new WorldCopyHandler();
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        worldPanelUpper.setBorder(new FlatRoundBorder()); //issue #5 fixed
        serverPanelBottom.setBorder(new FlatRoundBorder());

        if(worldToAdd != null && isArchiveMode) { //issue #7 fix
            worldNameAndStuffText.setText("File: " + worldToAdd.getAbsolutePath() + "\nWorld Name: " + "TODO");
        } else if(!isArchiveMode && worldToAdd != null) {
            worldNameAndStuffText.setText("Folder: " + worldToAdd.getAbsolutePath() + "\nWorld Name: " + "TODO");
        }

        if(isArchiveMode && extractedWorldDir != null) {
            startCopying.setEnabled(true);
            //this is the worst fucking solution ever lol
            File extractedDir = new File(extractedWorldDir);
            if(!new File(extractedWorldDir + "\\icon.png").exists()) {
                boolean doesIconInParentExist = new File(extractedDir.getParent() + "\\icon.png").exists();
                ImageIcon parentImg = new ImageIcon(new ImageIcon(extractedDir.getParent() + "\\icon.png").getImage().getScaledInstance(96, 96, Image.SCALE_SMOOTH));
                worldIcon.setIcon(doesIconInParentExist ? parentImg : defaultWorldIcon);
            } else {
                if(new File(extractedDir + "\\icon.png").exists()) //issue #22 fixed by adding another check
                    worldIcon.setIcon(new ImageIcon(new ImageIcon(extractedDir + "\\icon.png").getImage().getScaledInstance(96, 96, Image.SCALE_SMOOTH)));
                else
                    worldIcon.setIcon(defaultWorldIcon);
            }
        } else if(worldToAdd != null && worldToAdd.exists()) { //issue #8 fix
            startCopying.setEnabled(true);
            if(new File(worldToAdd + "\\icon.png").exists()) //issue #24 fix
                worldIcon.setIcon(new ImageIcon(new ImageIcon(worldToAdd + "\\icon.png").getImage().getScaledInstance(96,96, Image.SCALE_SMOOTH)));
            else worldIcon.setIcon(defaultWorldIcon);
        } else if(extractedWorldDir == null) {
            worldIcon.setIcon(defaultWorldIcon);
        }





        if(!new File(ConfigStuffPanel.getServPath() + "\\" + worldCopyText.getServerWorldName() + "\\icon.png").exists()) {
            serverWorldIcon.setIcon(defaultWorldIcon);
        } else {
            serverWorldIcon.setIcon(new ImageIcon(new ImageIcon(ConfigStuffPanel.getServPath() + "\\" + worldCopyText.getServerWorldName() + "\\icon.png").getImage().getScaledInstance(96, 96, Image.SCALE_SMOOTH)));
        }

        //size is in bytes
        if(new File(ConfigStuffPanel.getServPath() + "\\" + worldCopyText.getServerWorldName()).exists()) {
            Nbt nbt = new Nbt();
            serverWorldNameAndStuff.setText("Folder: " + worldCopyText.getServerWorldName() + "\nWorld Name: " + "TODO" + "\nSize: " + FileUtils.sizeOfDirectory(new File(ConfigStuffPanel.getServPath() + "\\" + worldCopyText.getServerWorldName())));
        } else {
            serverWorldNameAndStuff.setText("Server world folder does not exist.");
        }
    }

    public static void setExtractedWorldDir(String extractedWorldDir) {
        AddWorldsPanel.extractedWorldDir = extractedWorldDir;
    }

    public static String getExtractedWorldDir() {
        return extractedWorldDir;
    }
}
