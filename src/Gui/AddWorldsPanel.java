package Gui;

import Servers.DirectoryTree;
import Servers.WorldCopyHandler;
import com.formdev.flatlaf.ui.FlatRoundBorder;
import jnafilechooser.api.JnaFileChooser;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
    private final ImageIcon defaultWorldIcon = new ImageIcon(new ImageIcon("resources/defaultworld.jpg").getImage().getScaledInstance(96, 96, Image.SCALE_SMOOTH));
    private final DirectoryTree directoryTree = new DirectoryTree();

    private final DecimalFormat unitRound = new DecimalFormat("###.##");
    private final FlatRoundBorder border = new FlatRoundBorder();

    private ArrayList<String> sizeOfDirectory(File directory) {
        long BYTE_SIZE = FileUtils.sizeOfDirectory(directory);
        double finalSize = BYTE_SIZE;
        String unit = "b";
        double ONE_KILOBYTE = 1024;
        double ONE_MEGABYTE = 1048576;
        double ONE_GIGABYTE = 1073741824;
        if (BYTE_SIZE >= ONE_KILOBYTE && BYTE_SIZE < ONE_MEGABYTE) {
            finalSize = BYTE_SIZE / ONE_KILOBYTE;
            unit = "kb";
        } else if (BYTE_SIZE >= ONE_MEGABYTE && BYTE_SIZE < ONE_GIGABYTE) {
            finalSize = BYTE_SIZE / ONE_MEGABYTE;
            unit = "mb";
        } else if (BYTE_SIZE >= ONE_GIGABYTE) {
            finalSize = BYTE_SIZE / ONE_GIGABYTE;
            unit = "gb";
        }
        return new ArrayList<>(Arrays.asList(unitRound.format(finalSize), unit));
    }

    public AddWorldsPanel() throws IOException {
        super(new BorderLayout());
        JLabel dragNDropInfo = new JLabel(" or drag and drop it here.");
        JLabel selectedServerTxt = new JLabel();
        String selServPrefix = "Selected server: ";
        selectedServerTxt.setText(selServPrefix + ConfigStuffPanel.getServName());
        startCopying.setEnabled(false);
        JButton openButton = new JButton("Open Folder");
        openButton.addActionListener(e -> { //jna file chooser implementation here - issue #42 fixed
                JnaFileChooser fileDialog = new JnaFileChooser();
//                fileDialog.addFilter("Archive files", "*.7z", "*.zip", "*.rar", "*.tar"); its bugged
                fileDialog.showOpenDialog(null);

                File[] filePaths = fileDialog.getSelectedFiles();
                String folderPath = "";
                if(fileDialog.getCurrentDirectory() != null)
                    folderPath = fileDialog.getCurrentDirectory().getAbsolutePath();

                if (fileDialog.getSelectedFiles().length > 0 && filePaths != null && filePaths[0] != null) { //issue #43 fixed
                    File filePath = filePaths[0];
                    String fileExtension = filePath.toString().split("\\.")[filePath.toString().split("\\.").length - 1];

                    if (fileExtension.equals("zip") || fileExtension.equals("rar") || fileExtension.equals("7z") || fileExtension.equals("tar")) {
                        worldToAdd = filePath;
                        isArchiveMode = true;
                        try {
                            new WorldCopyHandler(this, progressBar, worldToAdd, false, startCopying, ConfigStuffPanel.getServerSelection().getSelectedIndex()).start();
                        } catch (IOException ex) {
                            alert(AlertType.ERROR, exStackTraceToString(ex.getStackTrace()));
                        }
                    } else {
                        isArchiveMode = false;
                        File folder = new File(folderPath);
                        //issue #16 fix adding a warning to check for folder's size
                        if (FileUtils.sizeOfDirectory(folder) > 1000000000) { //greater than 1GB
                            if (JOptionPane.showConfirmDialog(null,
                                    "Folder that you're trying to copy's size is greater than 1GB. Do you still want to prooced?", "Warning",
                                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                                worldToAdd = folder; //yes option
                            }
                        } else { //if file is less than 1gb
                            worldToAdd = folder;
                        }
                    }
                repaint();
            }
        });

        final JPanel tempPanel = this;
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
                    String fileExtension = fileToAdd.toString().split("\\.")[fileToAdd.toString().split("\\.").length - 1];

                    if (fileExtension.equals("zip") || fileExtension.equals("rar") || fileExtension.equals("7z") || fileExtension.equals("tar")) {
                        isArchiveMode = true;
                        worldToAdd = fileToAdd;
                        new WorldCopyHandler(tempPanel, progressBar, worldToAdd, false, startCopying, ConfigStuffPanel.getServerSelection().getSelectedIndex()).start();
                    } else {
                        isArchiveMode = false;
                        //issue #16 fix adding a warning to check for folder's size
                        if(FileUtils.sizeOfDirectory(new File(fileToAdd.getParent())) > 1000000000) { //greater than 1GB
                            if (JOptionPane.showConfirmDialog(null,
                                    "Folder that you're trying to copy's size is greater than 1GB. Do you still want to prooced?", "Warning",
                                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                                worldToAdd = new File(fileToAdd.getParent()); //yes option
                            }
                        } else { //if file is less than 1gb
                            worldToAdd = new File(fileToAdd.getParent());
                        }

                    }
                    repaint();
                } catch (UnsupportedFlavorException | IOException e) {
                    alert(AlertType.ERROR, exStackTraceToString(e.getStackTrace()));
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
                worldCopyHandler = new WorldCopyHandler(this, progressBar, worldToAdd, true, startCopying, ConfigStuffPanel.getServerSelection().getSelectedIndex());
            } catch (IOException ex) {
                alert(AlertType.ERROR, exStackTraceToString(ex.getStackTrace()));
                throw new RuntimeException(); //idk why but this line needs to stay here or i need to deal with another nullpointerexception
            }
            worldCopyHandler.start();
        });

        directoryTree.setDirectory(ConfigStuffPanel.getServPath(), ConfigStuffPanel.getServPath());
        JScrollPane directoryTreeScroll = new JScrollPane(directoryTree);

        worldIcon.setIcon(defaultWorldIcon);
//        worldNameAndStuffText.setLineWrap(true);


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


        worldNameAndStuffText.setEditable(false);
        worldNameAndStuffText.setText("World File name will appear here.");

        worldPanelUpper.add(Box.createRigidArea(dimension), BorderLayout.PAGE_START);
        worldPanelUpper.add(Box.createRigidArea(dimension), BorderLayout.LINE_START);
        worldPanelUpper.add(worldIcon, BorderLayout.CENTER);
        worldPanelUpper.add(worldNameAndStuffText, BorderLayout.LINE_END);

        JPanel serverNameAndStuff = new JPanel(new BorderLayout());

        serverWorldNameAndStuff.setEditable(false);


        serverNameAndStuff.add(Box.createRigidArea(dimension), BorderLayout.LINE_START);
        serverNameAndStuff.add(serverWorldIcon, BorderLayout.CENTER);
        serverNameAndStuff.add(serverWorldNameAndStuff, BorderLayout.LINE_END);

        serverPanelBottom.add(serverNameAndStuff, BorderLayout.LINE_START);

        addingWorld.add(worldPanelUpper, BorderLayout.PAGE_START);
        addingWorld.add(directoryTreeScroll, BorderLayout.CENTER);
        addingWorld.add(serverPanelBottom, BorderLayout.PAGE_END);

        add(buttonAndText, BorderLayout.PAGE_START);
        add(addingWorld, BorderLayout.LINE_START);
        add(startCopyingPanel, BorderLayout.PAGE_END);
    }

    WorldCopyHandler worldCopyText = new WorldCopyHandler(ConfigStuffPanel.getServerSelection().getSelectedIndex());
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        worldPanelUpper.setBorder(border); //issue #5 fixed
        serverPanelBottom.setBorder(border);

        directoryTree.setDirectory(ConfigStuffPanel.getServPath(), ConfigStuffPanel.getServPath());
        if(worldToAdd != null && isArchiveMode) { //issue #7 fix
            worldNameAndStuffText.setText("File: " + worldToAdd.getAbsolutePath()); //world name todo here
        } else if(!isArchiveMode && worldToAdd != null) {
            worldNameAndStuffText.setText("Folder: " + worldToAdd.getAbsolutePath()); //world name todo here
        }

        if(isArchiveMode && extractedWorldDir != null) {
            System.out.println(extractedWorldDir);
//            startCopying.setEnabled(true);
            //this is the worst fucking solution ever lol
            File extractedDir = new File(extractedWorldDir);
            if(!new File(extractedWorldDir + "\\icon.png").exists()) {
                boolean doesIconInParentExist = new File(extractedDir.getParent() + "\\icon.png").exists();
                ImageIcon parentImg = new ImageIcon(new ImageIcon(extractedDir.getParent() +
                        "\\icon.png").getImage().getScaledInstance(96, 96, Image.SCALE_SMOOTH));
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
            serverWorldIcon.setIcon(new ImageIcon(new ImageIcon(ConfigStuffPanel.getServPath() + "\\" + worldCopyText.getServerWorldName() + "\\icon.png")
                    .getImage().getScaledInstance(96, 96, Image.SCALE_SMOOTH)));
        }

        //size is in bytes
        if(new File(ConfigStuffPanel.getServPath() + "\\" + worldCopyText.getServerWorldName()).exists()) {
            ArrayList<String> arr = sizeOfDirectory(new File(ConfigStuffPanel.getServPath() + "\\" + worldCopyText.getServerWorldName()));
            serverWorldNameAndStuff.setText("Folder Name: " + worldCopyText.getServerWorldName() + "\nSize: " + arr.get(0) + arr.get(1)); //world name todo here
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
