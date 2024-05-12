package com.myne145.serverlauncher.gui.components;

import com.myne145.serverlauncher.server.Config;
import com.myne145.serverlauncher.utils.DefaultIcons;
import com.myne145.serverlauncher.utils.DirectoryPickerButtonAction;
import com.myne145.serverlauncher.utils.ZipUtils;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class PickDirectoryButton extends JButton {
//    private static final FileDialog FILE_CHOOSER = new FileDialog((Frame) null);

    private static final jnafilechooser.api.JnaFileChooser FILE_CHOOSER = new jnafilechooser.api.JnaFileChooser();
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

    public void click(File filePaths, DirectoryPickerButtonAction afterFileIsSelected) {
//        if (FILE_CHOOSER.getSelectedFiles().length == 0 || filePaths == null || filePaths[0] == null) {
//            return;
//        }
        if(filePaths == null)
            return;

//        File fileToAdd = filePaths[0];
        afterFileIsSelected.run(filePaths);

        this.setText("<html><b>Currently selected:</b><br><small>" + Config.abbreviateFilePath(filePaths.getAbsolutePath(), 60) + "</small></html>");
    }

    public PickDirectoryButton(String defaultTitle, Dimension defaultSize, Dimension maximumSize, DirectoryPickerButtonAction afterFileIsSelected) {
        this.defaultSize = defaultSize;

        this.setText("<html>" + defaultTitle + "</html>");
        this.setMinimumSize(defaultSize);
        this.setMaximumSize(maximumSize);
        this.setToolTipText("");

        FILE_CHOOSER.setMode(jnafilechooser.api.JnaFileChooser.Mode.Files);
//            FILE_CHOOSER.setMultipleMode(false);

        this.addActionListener(e -> {

            FILE_CHOOSER.showOpenDialog(null);
            removeImportButtonWarning();

//                File[] filePaths = FILE_CHOOSER.getSelectedFiles();
//                click(FILE_CHOOSER.getFiles()[0], afterFileIsSelected);
            click(FILE_CHOOSER.getSelectedFile(), afterFileIsSelected);


//            Runnable runnable = () -> {
////                FILE_CHOOSER.setVisible(true);
//                FILE_CHOOSER.showOpenDialog(null);
//                removeImportButtonWarning();
//
////                File[] filePaths = FILE_CHOOSER.getSelectedFiles();
////                click(FILE_CHOOSER.getFiles()[0], afterFileIsSelected);
//                click(FILE_CHOOSER.getSelectedFile(), afterFileIsSelected);
//            };
//            Thread thread = new Thread(runnable);
//            thread.setName("FILECHOOSER");
//            thread.start();
        });
    }
}

