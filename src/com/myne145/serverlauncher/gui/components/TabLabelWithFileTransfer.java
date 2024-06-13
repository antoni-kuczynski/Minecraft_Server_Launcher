package com.myne145.serverlauncher.gui.components;

import com.myne145.serverlauncher.gui.window.ServerTabbedPane;
import com.myne145.serverlauncher.gui.window.ContainerPane;
import com.myne145.serverlauncher.gui.window.Window;

import javax.swing.*;

public class TabLabelWithFileTransfer extends JLabel {
    protected static ContainerPane containerPane;
    public static boolean fileEntered;


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

    private void update(int index) {
//        Thread thread1 = new Thread(() -> {
//                    System.out.println(Window.getWindow().isFocused());
            if(!fileEntered) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e); //TODO
                }
                fileEntered = true;
                return;
            }

            containerPane.setSelectedIndex(index);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e); //TODO
            }
            fileEntered = Window.getWindow().isMouseWithinWindow();
//                    fileEntered = containerPane.isTabbedPaneFocused();
//        });
//        thread1.start();
    }

    public TabLabelWithFileTransfer(String text, ContainerPane parentPane, int index) {
        super(text);
        containerPane = parentPane;
        TransferHandler transferHandler = new TransferHandler() {
            @Override
            public boolean canImport(TransferHandler.TransferSupport support) {
                Thread t = new Thread(() -> update(index));
                t.setPriority(Thread.MIN_PRIORITY);
                t.start();

                return true;
            }
        };

        this.setTransferHandler(transferHandler);
    }
//    public static void setParentPane(ContainerPane parentPane) {
//        TabLabelWithFileTransfer.parentPane = parentPane;
//    }
}
