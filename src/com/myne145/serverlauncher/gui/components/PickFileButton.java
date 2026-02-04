package com.myne145.serverlauncher.gui.components;

import com.formdev.flatlaf.util.SystemInfo;
import com.myne145.serverlauncher.gui.window.Window;
import com.myne145.serverlauncher.server.Config;
import com.myne145.serverlauncher.utils.ButtonWarning;
import com.myne145.serverlauncher.utils.DefaultIcons;
import com.myne145.serverlauncher.utils.FilePickerButtonAction;
import jnafilechooser.api.JnaFileChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.myne145.serverlauncher.gui.window.Window.getScaledSize;
import static com.myne145.serverlauncher.gui.window.Window.showErrorMessage;

public class PickFileButton extends JButton {
    private static final JnaFileChooser FILE_CHOOSER_WINDOWS = new JnaFileChooser();
    private static final FileDialog FILE_CHOOSER = new FileDialog(Window.getWindow());
    private final Dimension defaultSize;
    private String customText;
    private String defaultFile = "";
    private final ArrayList<ButtonWarning> currentWarnings = new ArrayList<>();
    private String tooltipTextWithoutWarnings = "";
    private File previousFile;

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(super.getPreferredSize().width, defaultSize.height);
    }

    public PickFileButton(String defaultTitle, Dimension defaultSize, Dimension maximumSize, FilePickerButtonAction afterFileIsSelected) {
        this.defaultSize = new Dimension(defaultSize.width, defaultSize.height + 10);
        this.setText("<html>" + defaultTitle + "</html>");
        this.setMinimumSize(defaultSize);
        this.setMaximumSize(maximumSize);
        this.setToolTipText("");

        FILE_CHOOSER_WINDOWS.setMode(JnaFileChooser.Mode.Files);
        FILE_CHOOSER.setMode(FileDialog.LOAD);

        this.addActionListener(e -> getActionListener(afterFileIsSelected));
    }

    private void getActionListener(FilePickerButtonAction afterFileIsSelected) {
        Runnable runnable = () -> {
            if(SystemInfo.isWindows) {
                FILE_CHOOSER_WINDOWS.setDefaultFileName(defaultFile);
                FILE_CHOOSER_WINDOWS.showOpenDialog(Window.getWindow());
                updateFileRelatedStuff(FILE_CHOOSER_WINDOWS.getSelectedFile(), afterFileIsSelected);
                return;
            }
            if(previousFile != null) {
                FILE_CHOOSER.setDirectory(previousFile.getPath());
            }
            FILE_CHOOSER.setFile(defaultFile);
            FILE_CHOOSER.setVisible(true);
            if(FILE_CHOOSER.getFile() != null) {
                File f = new File(FILE_CHOOSER.getDirectory() + FILE_CHOOSER.getFile());
                updateFileRelatedStuff(f, afterFileIsSelected);
                previousFile = f.getParentFile();
            }
        };
        Thread thread = new Thread(runnable);
        thread.setName("FILECHOOSER");
        thread.start();
    }

    public void setFileChooserFileExtension(String extension) {
        defaultFile = ("*." + extension);
    }
    public void setCustomButtonText(String s) {
        customText = "<html><b>Currently selected:</b><br><p style=\"font-size:" + getScaledSize(9) + "px\">" + s + "</p></html>";
        this.setText(customText);
    }
    private void removeImportButtonWarning() {
        this.setIcon(null);
        this.setToolTipText(null);
        currentWarnings.clear();
    }


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
            this.setText("<html><b>Currently selected:</b><br><p style=\"font-size:" + getScaledSize(9) + "px\">" + Config.abbreviateFilePath(filePath, 60) + "</p></html>");
        }
        else {
            this.setText(customText);
        }
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

    public boolean hasWarnings() {
        return !currentWarnings.isEmpty();
    }
}

