package Gui;

import Server.DirectoryTree;
import Server.ConvertedSize;
import SelectedServer.ServerDetails;
import Server.WorldCopyHandler;
import com.formdev.flatlaf.ui.FlatRoundBorder;
import jnafilechooser.api.JnaFileChooser;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.text.DecimalFormat;
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
    private final JProgressBar progressBar = new JProgressBar();
    private final JLabel selectedWorldIconLabel = new JLabel();
    private final JLabel serverWorldIconLabel = new JLabel();
    private final JTextArea worldNameAndStuffText = new JTextArea();
    private final JLabel serverWorldNameAndStuff = new JLabel();
    private final JPanel worldPanelUpper = new JPanel(new BorderLayout());
    private final JPanel serverPanelBottom = new JPanel(new BorderLayout());
    private final DirectoryTree directoryTree = new DirectoryTree();
    private final FlatRoundBorder border = new FlatRoundBorder();
    private final JButton startCopying = new JButton("Start Copying");
    private final ImageIcon defaultWorldIcon = new ImageIcon(new ImageIcon("resources/defaultworld.jpg").getImage().getScaledInstance(96, 96, Image.SCALE_SMOOTH));

    private final WorldCopyHandler serverDetails = new WorldCopyHandler();
    private static File worldToAdd;
    private static String extractedWorldDir;
    private boolean isArchiveMode; //issue #8 fixed by adding a boolean to check the content's type
    private final DecimalFormat unitRound = new DecimalFormat("###.##");

    private final double ONE_GIGABYTE = 1073741824;

    public AddWorldsPanel() throws IOException {
        super(new BorderLayout());
        JLabel dragNDropInfo = new JLabel(" or drag and drop it here.");
        JLabel selectedServerTxt = new JLabel();
        String selServPrefix = "Selected server: ";
        selectedServerTxt.setText(selServPrefix + ServerDetails.serverName);
        startCopying.setEnabled(false);
        JButton openButton = new JButton("Open Folder");
        openButton.addActionListener(e -> { //jna file chooser implementation here - issue #42 fixed
            JnaFileChooser fileDialog = new JnaFileChooser();
            fileDialog.showOpenDialog(null);

            File[] filePaths = fileDialog.getSelectedFiles();
            String folderPath = "";
            if(fileDialog.getCurrentDirectory() != null)
                folderPath = fileDialog.getCurrentDirectory().getAbsolutePath();

            if (fileDialog.getSelectedFiles().length <= 0 || filePaths == null || filePaths[0] == null) {
                return;
            }

            File selectedFile = filePaths[0];

            if (WorldCopyHandler.isArchive(selectedFile)) {
                worldToAdd = selectedFile;
                isArchiveMode = true;
                try {
                    new WorldCopyHandler(this, progressBar, worldToAdd, false, startCopying).start();
                } catch (IOException ex) {
                    alert(AlertType.ERROR, exStackTraceToString(ex.getStackTrace()));
                }
            } else {
                isArchiveMode = false;
                File folder = new File(folderPath);
                //issue #16 fix adding a warning to check for folder's size

                if (FileUtils.sizeOfDirectory(folder) < ONE_GIGABYTE) {
                    worldToAdd = folder;
                }
                if (FileUtils.sizeOfDirectory(folder) >= ONE_GIGABYTE) {
                    if (JOptionPane.showConfirmDialog(null,
                            "Folder that you're trying to copy's size is greater than 1GB. Do you still want to prooced?", "Warning",
                            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                        worldToAdd = folder; //yes option
                    }
                }
            }
            repaint();
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

                    if (WorldCopyHandler.isArchive(fileToAdd)) {
                        isArchiveMode = true;
                        worldToAdd = fileToAdd;
                        new WorldCopyHandler(tempPanel, progressBar, worldToAdd, false, startCopying).start();
                    } else {
                        isArchiveMode = false;
                        //issue #16 fix adding a warning to check for folder's size
                        if(FileUtils.sizeOfDirectory(new File(fileToAdd.getParent())) > ONE_GIGABYTE) {
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
                worldCopyHandler = new WorldCopyHandler(this, progressBar, worldToAdd, true, startCopying);
            } catch (IOException ex) {
                alert(AlertType.ERROR, exStackTraceToString(ex.getStackTrace()));
                return;
            }
            worldCopyHandler.start();
        });

        directoryTree.setDirectory(ServerDetails.serverPath, ServerDetails.serverPath);
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


        worldNameAndStuffText.setEditable(false);
        worldNameAndStuffText.setText("World File name will appear here.");

        worldPanelUpper.add(Box.createRigidArea(dimension), BorderLayout.PAGE_START);
        worldPanelUpper.add(Box.createRigidArea(dimension), BorderLayout.LINE_START);
        worldPanelUpper.add(selectedWorldIconLabel, BorderLayout.CENTER);
        worldPanelUpper.add(worldNameAndStuffText, BorderLayout.LINE_END);

        JPanel serverNameAndStuff = new JPanel(new BorderLayout());

//        serverWorldNameAndStuff.setEditable(false);
//        appendToPane(serverWorldNameAndStuff, "test", Color.BLUE);

        serverNameAndStuff.add(Box.createRigidArea(dimension), BorderLayout.LINE_START);
        serverNameAndStuff.add(serverWorldIconLabel, BorderLayout.CENTER);
        serverNameAndStuff.add(serverWorldNameAndStuff, BorderLayout.LINE_END);

        serverPanelBottom.add(serverNameAndStuff, BorderLayout.LINE_START);

        addingWorld.add(worldPanelUpper, BorderLayout.PAGE_START);
        addingWorld.add(directoryTreeScroll, BorderLayout.CENTER);
        addingWorld.add(serverPanelBottom, BorderLayout.PAGE_END);

        add(buttonAndText, BorderLayout.PAGE_START);
        add(addingWorld, BorderLayout.LINE_START);
        add(startCopyingPanel, BorderLayout.PAGE_END);
    }

    private void appendToPane(JTextPane tp, String msg, Color c) {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = tp.getDocument().getLength();
        tp.setCaretPosition(len);
        tp.setCharacterAttributes(aset, false);
        tp.replaceSelection(msg);
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


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        worldPanelUpper.setBorder(border); //issue #5 fixed
        serverPanelBottom.setBorder(border);

        directoryTree.setDirectory(ServerDetails.serverPath, ServerDetails.serverPath);
        if(worldToAdd != null && isArchiveMode) { //issue #7 fix
            worldNameAndStuffText.setText("File: " + worldToAdd.getAbsolutePath()); //world name todo here
        } else if(!isArchiveMode && worldToAdd != null) {
            worldNameAndStuffText.setText("Folder: " + worldToAdd.getAbsolutePath()); //world name todo here
        }

        if(isArchiveMode && extractedWorldDir != null) {
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
        } else if(worldToAdd != null && worldToAdd.exists()) { //issue #8 fix
            startCopying.setEnabled(true);
            if(new File(worldToAdd + "\\icon.png").exists()) //issue #24 fix
                selectedWorldIconLabel.setIcon(new ImageIcon(new ImageIcon(worldToAdd + "\\icon.png").getImage().getScaledInstance(96,96, Image.SCALE_SMOOTH)));
            else selectedWorldIconLabel.setIcon(defaultWorldIcon);
        } else if(extractedWorldDir == null) {
            selectedWorldIconLabel.setIcon(defaultWorldIcon);
        }

        if(!new File(ServerDetails.serverPath + "\\" + serverDetails.getServerWorldName() + "\\icon.png").exists()) {
            serverWorldIconLabel.setIcon(defaultWorldIcon);
        } else {
            serverWorldIconLabel.setIcon(new ImageIcon(new ImageIcon(ServerDetails.serverPath + "\\" + serverDetails.getServerWorldName() + "\\icon.png")
                    .getImage().getScaledInstance(96, 96, Image.SCALE_SMOOTH)));
        }

        //size is in bytes
        if(new File(ServerDetails.serverPath + "\\" + serverDetails.getServerWorldName()).exists()) {
            ConvertedSize serverWorldConvertedSize = directorySizeWithConverion(new File(ServerDetails.serverPath + "\\" + serverDetails.getServerWorldName()));
            serverWorldNameAndStuff.setText("<html> Folder Name: " + serverDetails.getServerWorldName() +"<br> Level name: " + "" + ServerDetails.serverLevelName + "<br> Size: " + serverWorldConvertedSize.getText() + "</html>"); //world name todo here
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