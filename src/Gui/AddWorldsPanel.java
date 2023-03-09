package Gui;

import javax.swing.*;
import java.util.List;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.io.Serial;
import java.util.ArrayList;

public class AddWorldsPanel extends JPanel {

    public static ArrayList<File> getWorlds() {
        return worlds;
    }

    private static final ArrayList<File> worlds = new ArrayList<>(); //lol that makes no fucking sense u cant add more worlds than one to a sever
    private final JList<String> pathList = new JList<>();
    private final DefaultListModel<String> pathListModel = new DefaultListModel<>();

    public AddWorldsPanel() {
//        setLayout(new BorderLayout());
        super(new BorderLayout());
        pathList.setModel(pathListModel);
        JLabel dragNDropInfo = new JLabel(" or drag and drop it into the button.");
//        JComboBox<String> serverSelection = new JComboBox<>();

        JButton startCopying = new JButton("Start Copying");
        JButton button = new JButton("Open Folder");
        button.addActionListener(e -> {
            FileDialog fileDialog = new FileDialog((Frame)null, "Select Folder");
            fileDialog.setMode(FileDialog.LOAD);
            fileDialog.setFile("level.dat");
            fileDialog.setVisible(true);

            String folderPath = fileDialog.getDirectory();
            if (folderPath != null) {
                if(!worlds.contains(new File(folderPath))) {
                    worlds.add(new File(folderPath));
                    pathListModel.addElement(folderPath);
                }
                System.out.println(worlds);
            }
        });
        button.setTransferHandler(new TransferHandler() {
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
//                        worlds.addAll(l);
                        for(File f : l) {
                            if(f.isDirectory() && !worlds.contains(f)) {
                                worlds.add(f);
                                pathListModel.add(pathListModel.getSize(), f.toPath().toString());
                            }
                        }
                    } catch (UnsupportedFlavorException | IOException e) {
                        return false;
                    }
                return true;
            }
        });
        JProgressBar progressBar = new JProgressBar();

        pathList.setPreferredSize(new Dimension(400, 10));
        JPanel emptyPanel2 = new JPanel();
        JPanel copyStuffPanel = new JPanel();
        copyStuffPanel.setLayout(new BorderLayout());

//        emptyPanel1.setPreferredSize(new Dimension(50, 100));
//        pathListPanel.add(pathList);
        emptyPanel2.setPreferredSize(new Dimension(50, 10));
        JPanel dragAndDropBtnPanel = new JPanel();
        button.setPreferredSize(new Dimension(130, 50));
        dragAndDropBtnPanel.add(button);
        dragAndDropBtnPanel.add(dragNDropInfo);

        copyStuffPanel.add(startCopying, BorderLayout.PAGE_START);
        copyStuffPanel.add(emptyPanel2, BorderLayout.CENTER);
        copyStuffPanel.add(progressBar, BorderLayout.PAGE_END);

        add(dragAndDropBtnPanel, BorderLayout.PAGE_START);
//        add(emptyPanel1, BorderLayout.LINE_END);
        add(pathList, BorderLayout.LINE_START);
        add(copyStuffPanel, BorderLayout.PAGE_END);
//        add(button, BorderLayout.LINE_START);
//        add(dragNDropInfo, BorderLayout.LINE_END);
//        add(startCopying, BorderLayout.LINE_END);
    }
}
