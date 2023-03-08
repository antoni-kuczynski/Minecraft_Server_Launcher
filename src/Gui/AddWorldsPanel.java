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
    private final JButton button;
    public AddWorldsPanel() {
        //super(new BorderLayout());
        JLabel selServerTitle = new JLabel("Select server here:");
        JComboBox<String> serverSelection = new JComboBox<>();


        button = new JButton("Open Folder");
        button.addActionListener(e -> {
            FileDialog fileDialog = new FileDialog((Frame)null, "Select Folder");
            fileDialog.setMode(FileDialog.LOAD);
            fileDialog.setFile("*.dat");
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
        add(selServerTitle);
        add(serverSelection);
        add(button);
    }
}
