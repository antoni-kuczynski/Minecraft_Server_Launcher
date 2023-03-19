package Gui;

import Servers.WorldCopyHandler;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
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
    private final JLabel selectedServerTxt = new JLabel();
    private final String selServPrefix = "Selected server: ";
    private final JProgressBar progressBar = new JProgressBar();
    private static String extractedWorldDir;
    private final JLabel worldIcon = new JLabel();
    private final JLabel serverWorldIcon = new JLabel();
    private final JButton startCopying = new JButton("Start Copying");
    private final JTextArea worldNameAndStuffText = new JTextArea();
    private final JTextArea serverWorldNameAndStuff = new JTextArea();

    public AddWorldsPanel() throws IOException {
        super(new BorderLayout());
        JLabel dragNDropInfo = new JLabel(" or drag and drop it here.");
        selectedServerTxt.setText(selServPrefix + ConfigStuffPanel.getServName());
        startCopying.setEnabled(false);
        JButton openButton = new JButton("Open Folder");
        openButton.addActionListener(e -> {
            FileDialog fileDialog = new FileDialog((Frame)null, "Select Folder");
            fileDialog.setMode(FileDialog.LOAD);
            fileDialog.setFile("level.dat");
            fileDialog.setVisible(true);

            if(fileDialog.getFiles().length > 0) {
                File filePath = fileDialog.getFiles()[0];
                String folderPath = fileDialog.getDirectory();

                if (filePath != null && folderPath != null) { //null pointer prevention
                    String fileExtension = filePath.toString().split("\\.")[filePath.toString().split("\\.").length - 1];
                    if (fileExtension.equals("zip") || fileExtension.equals("rar") || fileExtension.equals("7z") || fileExtension.equals("tar")) {
                        worldToAdd = filePath;
                        try {
                            new WorldCopyHandler(this, progressBar, worldToAdd, false).start();
                        } catch (IOException ex) {
                            alert(AlertType.ERROR, exStackTraceToString(ex.getStackTrace()));
                        }
                    } else {
                        worldToAdd = new File(folderPath);
                    }
//                    startCopying.setEnabled(true);
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
                            worldToAdd = fileToAdd;
                            new WorldCopyHandler(tempPanel, progressBar, worldToAdd, false).start();
                        } else {
                            worldToAdd = new File(fileToAdd.getParent());
                        }
                        System.out.println(extractedWorldDir);
//                        startCopying.setEnabled(true);
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
//        buttonAndText.add(separatorBtnTextKinda, BorderLayout.LINE);

        copyingProgress.add(emptyPanels.get(3), BorderLayout.PAGE_START);
        copyingProgress.add(emptyPanel7, BorderLayout.LINE_START);
        copyingProgress.add(progressBar, BorderLayout.CENTER);
        copyingProgress.add(emptyPanel8, BorderLayout.LINE_END);
        copyingProgress.add(emptyPanels.get(4), BorderLayout.PAGE_END);


        startCopyingBtnPanel.add(startCopying, BorderLayout.CENTER);
        startCopyingBtnPanel.add(emptyPanels.get(5), BorderLayout.LINE_END);

        startCopyingPanel.add(emptyPanels.get(6), BorderLayout.LINE_START);
//        startCopyingPanel.add(startCopying, BorderLayout.CENTER);
        startCopyingPanel.add(startCopyingBtnPanel, BorderLayout.LINE_END);
        startCopyingPanel.add(copyingProgress, BorderLayout.PAGE_END);


        JPanel addingWorld = new JPanel(new BorderLayout());
        JPanel worldPanelUpper = new JPanel(new BorderLayout());

        worldNameAndStuffText.setEditable(false);

//        worldPanelUpper.setBorder(new MatteBorder(1,1,1,1, new Color(0,0,0)));
        worldPanelUpper.add(emptyPanels.get(7), BorderLayout.PAGE_START);
        worldPanelUpper.add(emptyPanels.get(8), BorderLayout.LINE_START);
        worldPanelUpper.add(worldIcon, BorderLayout.CENTER);
        worldPanelUpper.add(worldNameAndStuffText, BorderLayout.LINE_END);


        JPanel serverPanelBottom = new JPanel(new BorderLayout());

//        serverPanelBottom.add(emptyPanels.get(9), BorderLayout.PAGE_START);
        serverPanelBottom.add(emptyPanels.get(10), BorderLayout.LINE_START);
        serverPanelBottom.add(serverWorldIcon, BorderLayout.CENTER);
        serverPanelBottom.add(serverWorldNameAndStuff, BorderLayout.LINE_END);

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
        System.out.println("extracted dir: " + extractedWorldDir);
        if(worldToAdd != null)
            worldNameAndStuffText.setText(worldToAdd.getName());
        serverWorldNameAndStuff.setText(worldCopyText.getServerWorldName());
//        else
//            selectedServer.setText("");

        if(extractedWorldDir != null) {
            startCopying.setEnabled(true);
            if(new File(extractedWorldDir + "\\icon.png").exists())
                worldIcon.setIcon(new ImageIcon(new ImageIcon(new File(extractedWorldDir) + "\\icon.png").getImage().getScaledInstance(96, 96, Image.SCALE_SMOOTH)));
            else //this is the worst fucking solution ever lol
                worldIcon.setIcon(new ImageIcon(new ImageIcon(new File(extractedWorldDir).getParent() + "\\icon.png").getImage().getScaledInstance(96, 96, Image.SCALE_SMOOTH)));

        } else {
            worldIcon.setIcon(new ImageIcon(new ImageIcon("defaultworld.jpg").getImage().getScaledInstance(96, 96, Image.SCALE_SMOOTH)));
        }
        serverWorldIcon.setIcon(new ImageIcon(new ImageIcon(ConfigStuffPanel.getServPath() + "\\" + worldCopyText.getServerWorldName() + "\\icon.png").getImage().getScaledInstance(96, 96, Image.SCALE_SMOOTH)));

    }

    public static void setExtractedWorldDir(String extractedWorldDir) {
        AddWorldsPanel.extractedWorldDir = extractedWorldDir;
    }

    public static String getExtractedWorldDir() {
        return extractedWorldDir;
    }
}
