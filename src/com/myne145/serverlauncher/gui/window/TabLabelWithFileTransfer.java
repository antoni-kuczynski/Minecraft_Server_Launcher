package com.myne145.serverlauncher.gui.window;

import javax.swing.*;

public class TabLabelWithFileTransfer extends JLabel {

    public TabLabelWithFileTransfer(String text, JTabbedPane parentPane, int index) {
        super(text);
//        setIconTextGap(100);
        TransferHandler transferHandler = new TransferHandler() {
            @Override
            public boolean canImport(TransferHandler.TransferSupport support) {
                parentPane.setSelectedIndex(index);
                return true;
            }
        };

        this.setTransferHandler(transferHandler);
    }
}
