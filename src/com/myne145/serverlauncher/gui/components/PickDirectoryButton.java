package com.myne145.serverlauncher.gui.components;

import com.myne145.serverlauncher.server.Config;
import com.myne145.serverlauncher.utils.DefaultIcons;
import com.myne145.serverlauncher.utils.DirectoryPickerButtonAction;
import com.myne145.serverlauncher.utils.ZipUtils;
import jnafilechooser.api.JnaFileChooser;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class PickDirectoryButton extends JButton {
    private final JnaFileChooser FILE_CHOOSER = new JnaFileChooser();
    private final Dimension defaultSize;


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

    public PickDirectoryButton(String defaultTitle, Dimension defaultSize, Dimension maximumSize, DirectoryPickerButtonAction afterFileIsSelected) {
        this.defaultSize = defaultSize;

        this.setText("<html>" + defaultTitle + "</html>");
        this.setMinimumSize(defaultSize);
        this.setMaximumSize(maximumSize);
        this.setToolTipText("");

        FILE_CHOOSER.setMode(JnaFileChooser.Mode.Files);

        this.addActionListener(e -> {
            Runnable runnable = () -> {
                FILE_CHOOSER.showOpenDialog(null);
                removeImportButtonWarning();

                File[] filePaths = FILE_CHOOSER.getSelectedFiles();

                if (FILE_CHOOSER.getSelectedFiles().length == 0 || filePaths == null || filePaths[0] == null) {
                    return;
                }

                File fileToAdd = filePaths[0];
                afterFileIsSelected.run(fileToAdd);

                this.setText("<html><b>Currently selected:</b><br><small>" + Config.abbreviateFile(fileToAdd.getAbsolutePath(), 60) + "</small></html>");

                double ONE_GIGABYTE = 1073741824;
                if (FileUtils.sizeOf(fileToAdd) >= ONE_GIGABYTE) {
                    setImportButtonWarning("File larger than 1GiB");
                } else if (!ZipUtils.isArchive(fileToAdd) && FileUtils.sizeOfDirectory(fileToAdd.getParentFile()) >= ONE_GIGABYTE) {
                    setImportButtonWarning("Folder larger than 1GiB");
                }

            };
            Thread thread = new Thread(runnable);
            thread.setName("FILECHOOSER");
            thread.start();
        });
    }
}

