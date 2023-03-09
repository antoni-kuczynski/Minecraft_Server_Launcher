package Gui;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.Serial;
import java.util.ArrayList;

public class AddWorldsPanel extends JPanel {
    private final ArrayList<File> worlds = new ArrayList<>();

    public AddWorldsPanel() {
//        setLayout(new BorderLayout());
        super(new BorderLayout());
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
                if(!worlds.contains(new File(folderPath)))
                    worlds.add(new File(folderPath));
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
            public boolean importData(TransferHandler.TransferSupport support) {
                try {
                    Transferable transferable = support.getTransferable();
                    DataFlavor[] flavors = transferable.getTransferDataFlavors();
                    for (DataFlavor flavor : flavors) {
                        if (flavor.isFlavorJavaFileListType()) {
                            java.util.List<File> files = (java.util.List<File>) transferable.getTransferData(flavor);
                            for (File file : files) {
                                if (file.isDirectory()) {
                                    System.out.println("Dropped folder: " + file.getAbsolutePath());
                                    return true;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
        JProgressBar progressBar = new JProgressBar();
        JPanel emptyPanel1 = new JPanel();
        JPanel emptyPanel2 = new JPanel();
        JPanel copyStuffPanel = new JPanel();
        copyStuffPanel.setLayout(new BorderLayout());

        emptyPanel1.setPreferredSize(new Dimension(50, 100));
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
        add(emptyPanel1, BorderLayout.CENTER);
        add(copyStuffPanel, BorderLayout.PAGE_END);
//        add(button, BorderLayout.LINE_START);
//        add(dragNDropInfo, BorderLayout.LINE_END);
//        add(startCopying, BorderLayout.LINE_END);
    }
}
