package com.myne145.serverlauncher.gui.components;

import com.myne145.serverlauncher.server.Config;
import com.myne145.serverlauncher.utils.AlertType;
import com.myne145.serverlauncher.utils.DefaultIcons;
import com.myne145.serverlauncher.utils.FilePickerButtonAction;
import jnafilechooser.api.JnaFileChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.myne145.serverlauncher.gui.window.Window.alert;
import static com.myne145.serverlauncher.gui.window.Window.getErrorDialogMessage;

public class PickFileButton extends JButton {
    private static final JnaFileChooser FILE_CHOOSER = new JnaFileChooser();
    private final Dimension defaultSize;
    private String customText;

    public void setCustomButtonText(String s) {
        customText = "<html><b>Currently selected:</b><br><small>" + s + "</small></html>";
        this.setText(customText);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(super.getPreferredSize().width, defaultSize.height);
    }

    private void removeImportButtonWarning() {
        this.setIcon(null);
        this.setToolTipText(null);
    }

    public void setImportButtonWarning(String message) {
        this.setIcon(DefaultIcons.getSVGIcon(DefaultIcons.ERROR).derive(16,16));

        if(this.getToolTipText() == null)
            this.setToolTipText("");

        if(!this.getToolTipText().isEmpty()) {
            this.setToolTipText(this.getToolTipText() + "\n- " + message);
        } else {
            this.setToolTipText("- " + message);
        }
    }

    private void updateFileRelatedStuff(File filePaths, FilePickerButtonAction afterFileIsSelected) {
        removeImportButtonWarning();
        if(filePaths == null)
            return;

        afterFileIsSelected.run(filePaths);

        if(customText == null)
            this.setText("<html><b>Currently selected:</b><br><small>" + Config.abbreviateFilePath(filePaths.getAbsolutePath(), 60) + "</small></html>");
        else
            this.setText(customText);
    }

    public PickFileButton(String defaultTitle, Dimension defaultSize, Dimension maximumSize, FilePickerButtonAction afterFileIsSelected) {
        this.defaultSize = defaultSize;
        this.setText("<html>" + defaultTitle + "</html>");
        this.setMinimumSize(defaultSize);
        this.setMaximumSize(maximumSize);
        this.setToolTipText("");

        FILE_CHOOSER.setMode(JnaFileChooser.Mode.Files);

        this.addActionListener(e -> {
            Runnable runnable = () -> {
                FILE_CHOOSER.showOpenDialog(null);
                updateFileRelatedStuff(FILE_CHOOSER.getSelectedFile(), afterFileIsSelected);
            };
            Thread thread = new Thread(runnable);
            thread.setName("FILECHOOSER");
            thread.start();
        });
    }



    public TransferHandler getCustomTransferHandler(FilePickerButtonAction action) {
        return new TransferHandler() {
            @Override
            public boolean canImport(TransferSupport support) {
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
                    updateFileRelatedStuff(fileToAdd, action);
                } catch (UnsupportedFlavorException | IOException e) {
                    alert(AlertType.ERROR, getErrorDialogMessage(e));
                    return false;
                }
                return true;
            }
        };
    }
}

