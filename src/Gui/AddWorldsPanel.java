package Gui;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class AddWorldsPanel extends JPanel {
    private JButton button;
    public AddWorldsPanel() {
        //super(new BorderLayout());

        button = new JButton("Open Folder");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                FileDialog fileDialog = new FileDialog((Frame)null, "Select Folder");
                fileDialog.setMode(FileDialog.LOAD);
                fileDialog.setFile("*.txt");
                fileDialog.setVisible(true);

                String folderPath = fileDialog.getDirectory();
                if (folderPath != null) {
                    System.out.println("Selected folder: " + folderPath);
                }
            }
        });
        button.setTransferHandler(new TransferHandler() {
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
                    for (int i = 0; i < flavors.length; i++) {
                        if (flavors[i].isFlavorJavaFileListType()) {
                            java.util.List<File> files = (java.util.List<File>) transferable.getTransferData(flavors[i]);
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
        add(button);
    }
}
