package com.myne145.serverlauncher.gui.components;

import com.myne145.serverlauncher.gui.window.ContainerPane;

import javax.swing.*;

public class TabLabelWithFileTransfer extends JLabel {
    public TabLabelWithFileTransfer(String text, JTabbedPane parentPane, int index) {
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

    public TabLabelWithFileTransfer(String text, int index) {
        super(text);
        TransferHandler transferHandler = new TransferHandler() {
            @Override
            public boolean canImport(TransferHandler.TransferSupport support) {
                ContainerPane.getCurrentPane().setSelectedIndex(index);
                return true;
            }
        };

        this.setTransferHandler(transferHandler);
    }
}
