package com.myne145.serverlauncher.gui.components;

import com.myne145.serverlauncher.gui.window.Window;
import com.myne145.serverlauncher.server.Config;
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
import java.util.ArrayList;
import java.util.List;

import static com.myne145.serverlauncher.gui.window.Window.showErrorMessage;

public class PickFileButton extends JButton {
    private static final JnaFileChooser FILE_CHOOSER = new JnaFileChooser();
    private final Dimension defaultSize;
    private String customText;
    private String defaultFile = "";
    private final ArrayList<ButtonWarning> currentWarnings = new ArrayList<>();
    private String tooltipTextWithoutWarnings = "";

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(super.getPreferredSize().width, defaultSize.height);
    }

    public void setFileChooserFileExtension(String extension) {
        defaultFile = ("*." + extension);
    }
    public void setCustomButtonText(String s) {
        customText = "<html><b>Currently selected:</b><br><small>" + s + "</small></html>";
        this.setText(customText);
    }
    private void removeImportButtonWarning() {
        this.setIcon(null);
        this.setToolTipText(null);
        currentWarnings.clear();
    }

//    public void setImportButtonWarning(String message) {
//        this.setIcon(DefaultIcons.getSVGIcon(DefaultIcons.ERROR).derive(16,16));
//
//        if(this.getToolTipText() == null)
//            this.setToolTipText("");
//
//        if(!this.getToolTipText().isEmpty()) {
//            this.setToolTipText(this.getToolTipText() + "\n- " + message);
//        } else {
//            this.setToolTipText("- " + message);
//        }
//    }


    @Override
    public void setToolTipText(String text) {
        if((text == null || text.isEmpty()) && currentWarnings.isEmpty())
            return;

        tooltipTextWithoutWarnings = text;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < currentWarnings.size(); i++) {
            builder.append("- ").append(currentWarnings.get(i).toString());
            if(i != currentWarnings.size() - 1)
                builder.append("\n");
        }

        if(currentWarnings.isEmpty()) {
            super.setToolTipText(tooltipTextWithoutWarnings);
            return;
        }

        if(tooltipTextWithoutWarnings == null || tooltipTextWithoutWarnings.isEmpty()) {
            super.setToolTipText(builder.toString());
            return;
        }
        builder.append("\n");

        super.setToolTipText(builder + tooltipTextWithoutWarnings);
    }

    public void setImportButtonWarning(ButtonWarning warning) {
        this.setIcon(DefaultIcons.getSVGIcon(DefaultIcons.ERROR).derive(16,16));

        if(!currentWarnings.contains(warning))
            currentWarnings.add(warning);

        setToolTipText("");
    }

    private void updateFileRelatedStuff(File filePath, FilePickerButtonAction afterFileIsSelected) {
        removeImportButtonWarning();
        if(filePath == null) {
            return;
        }

        afterFileIsSelected.run(filePath);

        if(customText == null) {
            this.setText("<html><b>Currently selected:</b><br><small>" + Config.abbreviateFilePathOld(filePath.getAbsolutePath(), 60) + "</small></html>");
        }
        else {
            this.setText(customText);
        }
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
                FILE_CHOOSER.setDefaultFileName(defaultFile);
                FILE_CHOOSER.showOpenDialog(Window.getWindow());
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
                } catch (UnsupportedFlavorException e) {
                    showErrorMessage("Requested data not supported in this flavor.", e);
                    return false;
                } catch (IOException ee) {
                    showErrorMessage("I/O error.", ee);
                }
                return true;
            }
        };
    }
}

