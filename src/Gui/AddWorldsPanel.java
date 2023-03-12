package Gui;

import Servers.ServerProperties;
import Servers.WorldCopyHandler;

import javax.swing.*;
import java.util.List;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.io.Serial;

public class AddWorldsPanel extends JPanel {

    public JPanel getPanel() {
        return this;
    }
    public static File getworldToAdd() {
        return worldToAdd;
    }
    private static File worldToAdd;
//    private final JList<String> pathList = new JList<>();
//    private final DefaultListModel<String> pathListModel = new DefaultListModel<>();
    private JLabel selectedServerTxt = new JLabel();
    private final String selServPrefix = "Selected server: ";
    private final JLabel selectedWorld = new JLabel("testWorld");
    private final JLabel arrow = new JLabel();
    private final JLabel selectedServer = new JLabel("testServer");
    private final JProgressBar progressBar = new JProgressBar();
    private WorldCopyHandler worldCopyHandler = new WorldCopyHandler(progressBar);

    private static String getFileExtension(File file) {
        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        } else {
            return fileName.substring(lastDotIndex + 1);
        }
    }

    public AddWorldsPanel() throws IOException {
//        setLayout(new BorderLayout());
        super(new BorderLayout());
//        pathList.setModel(pathListModel);
        JLabel dragNDropInfo = new JLabel(" or drag and drop it into the button.");
//        JComboBox<String> serverSelection = new JComboBox<>();
        selectedServerTxt.setText(selServPrefix + ConfigStuffPanel.getServName());
        JButton startCopying = new JButton("Start Copying");
        JButton button = new JButton("Open Folder");
        button.addActionListener(e -> {
            FileDialog fileDialog = new FileDialog((Frame)null, "Select Folder");
            fileDialog.setMode(FileDialog.LOAD);
            fileDialog.setFile("level.dat");
            fileDialog.setVisible(true);

            if(fileDialog.getFiles().length > 0) {
                File filePath = fileDialog.getFiles()[0];
                String folderPath = fileDialog.getDirectory();

                if (filePath != null && folderPath != null) { //null pointer prevention
                    String fileExtension = filePath.toString().split("\\.")[filePath.toString().split("\\.").length - 1];
                    if (fileExtension.equals("zip") || fileExtension.equals("rar") || fileExtension.equals("7z") || fileExtension.equals("tar")) {
                        worldToAdd = filePath;
//                    pathListModel.addElement(filePath.toString());
                    } else {
                        worldToAdd = new File(folderPath);
//                    pathListModel.addElement(folderPath);
                    }
                }
            }
            repaint();
            System.out.println(worldToAdd);
        });
        button.setTransferHandler(new TransferHandler() {
            @Serial
            private static final long serialVersionUID = 1L;

            @Override
            public boolean canImport(TransferHandler.TransferSupport support) {
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
//                        worldToAdd.addAll(l);
                        File fileToAdd = l.get(l.size() - 1);
                        String fileExtension = fileToAdd.toString().split("\\.")[fileToAdd.toString().split("\\.").length - 1];
                        System.out.println(fileToAdd);

                        if (fileExtension.equals("zip") || fileExtension.equals("rar") || fileExtension.equals("7z") || fileExtension.equals("tar")) {
                            worldToAdd = fileToAdd;
//                            pathListModel.addElement(fileToAdd.toString());
                        } else {
                            worldToAdd = new File(fileToAdd.getParent());
//                            pathListModel.addElement(fileToAdd.getParent());
                        }
                        repaint();
                    } catch (UnsupportedFlavorException | IOException e) {
                        return false;
                    }
                return true;
            }
        });





        startCopying.addActionListener(e -> {
            worldCopyHandler.start();
        });

//        pathList.setPreferredSize(new Dimension(400, 10));
        JPanel emptyPanel2 = new JPanel();
        JPanel copyStuffPanel = new JPanel();
        copyStuffPanel.setLayout(new BorderLayout());

        emptyPanel2.setPreferredSize(new Dimension(50, 10));
        JPanel dragAndDropBtnPanel = new JPanel();
        button.setPreferredSize(new Dimension(130, 50));
        dragAndDropBtnPanel.add(button);
        dragAndDropBtnPanel.add(dragNDropInfo);

        copyStuffPanel.add(startCopying, BorderLayout.PAGE_START);
        copyStuffPanel.add(emptyPanel2, BorderLayout.CENTER);
        copyStuffPanel.add(progressBar, BorderLayout.PAGE_END);

        JPanel separatorPanel = new JPanel();
        separatorPanel.setLayout(new BorderLayout());
        separatorPanel.setPreferredSize(new Dimension(50, 100));
        separatorPanel.add(new JSeparator(SwingConstants.VERTICAL), BorderLayout.LINE_START);

        JPanel selectedStuffPanel = new JPanel();

        arrow.setIcon(new ImageIcon("arrow.png"));

        JPanel emptyPanel1 = new JPanel();
        emptyPanel1.setPreferredSize(new Dimension(10,10));

        selectedStuffPanel.setLayout(new GridLayout(7,3));
//        selectedStuffPanel.setPreferredSize(new Dimension(100, 50));

        selectedStuffPanel.add(selectedWorld);
        selectedStuffPanel.add(arrow);
        selectedStuffPanel.add(selectedServer);

        JPanel selectedStuffAndSpacePanel = new JPanel();
        JPanel emptyPanel3 = new JPanel();
        emptyPanel3.setPreferredSize(new Dimension(50, 10));
        selectedStuffAndSpacePanel.setLayout(new BorderLayout());
        selectedStuffAndSpacePanel.add(emptyPanel3, BorderLayout.LINE_START);
        selectedStuffAndSpacePanel.add(selectedStuffPanel, BorderLayout.CENTER);


        add(dragAndDropBtnPanel, BorderLayout.PAGE_START);
//        add(separatorPanel, BorderLayout.LINE_START);
        add(emptyPanel1, BorderLayout.LINE_START);
        add(selectedStuffAndSpacePanel, BorderLayout.LINE_START);
//        add(pathList, BorderLayout.LINE_START);
        add(copyStuffPanel, BorderLayout.PAGE_END);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(worldToAdd != null)
            selectedWorld.setText(worldToAdd.getName());
        selectedServer.setText(ConfigStuffPanel.getServName() +"\\" + worldCopyHandler.getServerWorldName());
    }
}
