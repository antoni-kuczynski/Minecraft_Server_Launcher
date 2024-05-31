package com.myne145.serverlauncher.gui.components;

import com.myne145.serverlauncher.gui.tabs.ServerTabbedPane;
import com.myne145.serverlauncher.gui.window.ContainerPane;

import javax.swing.*;

public class TabLabelWithFileTransfer extends JLabel {
    protected static ContainerPane containerPane;
    public TabLabelWithFileTransfer(String text, ServerTabbedPane parentPane, int index) {
        super(text);
        TransferHandler transferHandler = new TransferHandler() {
            @Override
            public boolean canImport(TransferHandler.TransferSupport support) {
                parentPane.setSelectedIndex(index);
                return true;
            }
        };

        this.setTransferHandler(transferHandler);
    }

    public TabLabelWithFileTransfer(String text, ContainerPane parentPane, int index) {
        super(text);
        containerPane = parentPane;
        TransferHandler transferHandler = new TransferHandler() {
            @Override
            public boolean canImport(TransferHandler.TransferSupport support) {
                parentPane.setSelectedIndex(index);
                return true;
            }
        };

        this.setTransferHandler(transferHandler);
    }
//    public static void setParentPane(ContainerPane parentPane) {
//        TabLabelWithFileTransfer.parentPane = parentPane;
//    }
}
