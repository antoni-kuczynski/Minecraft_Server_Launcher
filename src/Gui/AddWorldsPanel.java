package Gui;

import Servers.WorldCopyHandler;

import javax.swing.*;
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
    private final JLabel selectedWorld = new JLabel("testWorld");
    private final JLabel arrow = new JLabel();
    private final JLabel selectedServer = new JLabel("testServer");
    private final JProgressBar progressBar = new JProgressBar();

    public AddWorldsPanel() throws IOException {
        super(new BorderLayout());
        JLabel dragNDropInfo = new JLabel(" or drag and drop it here.");
        selectedServerTxt.setText(selServPrefix + ConfigStuffPanel.getServName());
        JButton startCopying = new JButton("Start Copying");
        startCopying.setEnabled(false);
        JButton button = new JButton("Open Folder");
        button.addActionListener(e -> {
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
                    } else {
                        worldToAdd = new File(folderPath);
                    }
                    startCopying.setEnabled(true);
                }
            }

            repaint();
        });
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
                        } else {
                            worldToAdd = new File(fileToAdd.getParent());
                        }
                        startCopying.setEnabled(true);
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
                worldCopyHandler = new WorldCopyHandler(progressBar, worldToAdd);
            } catch (IOException ex) {
                alert(AlertType.ERROR, exStackTraceToString(ex.getStackTrace()));
                throw new RuntimeException(); //idk why but this line needs to stay here or i need to deal with another nullpointerexception
            }
            worldCopyHandler.start();
        });

        JPanel emptyPanel2 = new JPanel();
        JPanel copyStuffPanel = new JPanel();
        copyStuffPanel.setLayout(new BorderLayout());

        emptyPanel2.setPreferredSize(new Dimension(50, 10));
        JPanel dragAndDropBtnPanel = new JPanel();
        button.setPreferredSize(new Dimension(130, 50));
        dragAndDropBtnPanel.add(button);
        dragAndDropBtnPanel.add(dragNDropInfo);

        JPanel emptyPanel4 = new JPanel();
        emptyPanel4.setPreferredSize(new Dimension(10, 10));
        JPanel emptyPanel5 = new JPanel();
        emptyPanel5.setPreferredSize(new Dimension(10, 5));
        JPanel emptyPanel6 = new JPanel();
        emptyPanel6.setPreferredSize(new Dimension(10, 5));

        JPanel copyPanelBottom = new JPanel();

        copyPanelBottom.setLayout(new BorderLayout());
        copyPanelBottom.add(emptyPanel5, BorderLayout.LINE_START);
        copyPanelBottom.add(progressBar, BorderLayout.CENTER);
        copyPanelBottom.add(emptyPanel6, BorderLayout.LINE_END);
        copyPanelBottom.add(emptyPanel4, BorderLayout.PAGE_END);


        JPanel emptyPanel7 = new JPanel();
        emptyPanel7.setPreferredSize(new Dimension(10, 10));
        JPanel emptyPanel8 = new JPanel();
        emptyPanel8.setPreferredSize(new Dimension(10, 5));

        startCopying.setPreferredSize(new Dimension(75, 35));
        JPanel copyPanelUp = new JPanel(new BorderLayout());
        copyPanelUp.add(emptyPanel7, BorderLayout.LINE_START);
        copyPanelUp.add(startCopying, BorderLayout.CENTER);
        copyPanelUp.add(emptyPanel8, BorderLayout.LINE_END);


        copyStuffPanel.add(copyPanelUp, BorderLayout.PAGE_START);
        copyStuffPanel.add(emptyPanel2, BorderLayout.CENTER);
        copyStuffPanel.add(copyPanelBottom, BorderLayout.PAGE_END);

        JPanel separatorPanel = new JPanel();
        separatorPanel.setLayout(new BorderLayout());
        separatorPanel.setPreferredSize(new Dimension(50, 100));
        separatorPanel.add(new JSeparator(SwingConstants.VERTICAL), BorderLayout.LINE_START);

        JPanel selectedStuffPanel = new JPanel();

        arrow.setIcon(new ImageIcon("arrow.png"));

        JPanel emptyPanel1 = new JPanel();
        emptyPanel1.setPreferredSize(new Dimension(10,10));

        selectedStuffPanel.setLayout(new GridLayout(7,3));

        selectedStuffPanel.add(selectedWorld);
        selectedStuffPanel.add(arrow);
        selectedStuffPanel.add(selectedServer);

        JPanel selectedStuffAndSpacePanel = new JPanel();
        JPanel emptyPanel3 = new JPanel();
        emptyPanel3.setPreferredSize(new Dimension(50, 10));
        selectedStuffAndSpacePanel.setLayout(new BorderLayout());
        selectedStuffAndSpacePanel.add(emptyPanel3, BorderLayout.LINE_START);
        selectedStuffAndSpacePanel.add(selectedStuffPanel, BorderLayout.CENTER);


        add(dragAndDropBtnPanel, BorderLayout.PAGE_START);
        add(emptyPanel1, BorderLayout.LINE_START);
        add(selectedStuffAndSpacePanel, BorderLayout.LINE_START);
        add(copyStuffPanel, BorderLayout.PAGE_END);
    }

    WorldCopyHandler worldCopyText = new WorldCopyHandler();
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(worldToAdd != null)
            selectedServer.setText(worldToAdd.getName() + " ⟶ " + ConfigStuffPanel.getServName() +"\\" + worldCopyText.getServerWorldName());
        else
            selectedServer.setText("Select world to copy it to " + ConfigStuffPanel.getServName() +"\\" + worldCopyText.getServerWorldName());
    }
}
