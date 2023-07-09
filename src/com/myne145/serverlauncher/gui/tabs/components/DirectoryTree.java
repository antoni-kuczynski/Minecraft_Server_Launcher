package com.myne145.serverlauncher.gui.tabs.components;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DirectoryTree extends JTree {

    public DirectoryTree() {
        super();

        setModel(new DefaultTreeModel(null));
    }

    public void setDirectory(String path) {
        File file = new File(path);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(file);
        setModel(new DefaultTreeModel(root));
        addNodes(root, file);
        expandAllNodes();

        TreePath pathToSelect = findPathToNode(path);
        if (pathToSelect != null) {
            setSelectionPath(pathToSelect);
        }
    }

    private void addNodes(DefaultMutableTreeNode parentNode, File parentFile) {
        File[] files = parentFile.listFiles();
        if (files == null) {
            return;
        }

        // create two separate lists for directories and files
        List<File> directories = new ArrayList<>();
        List<File> filesList = new ArrayList<>();
        for (File file : files) {
            if (file.isDirectory()) {
                directories.add(file);
            } else {
                filesList.add(file);
            }
        }

        // sort directories alphabetically
        directories.sort(Comparator.comparing(File::getName));
        // sort files alphabetically
        filesList.sort(Comparator.comparing(File::getName));

        // add directories first
        for (File dir : directories) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(dir.getName());
            parentNode.add(node);
            addNodes(node, dir);
        }

        // then add files
        for (File file : filesList) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(file.getName());
            parentNode.add(node);
        }
    }


    private void expandAllNodes() {
        expandRow(0);
    }

    private TreePath findPathToNode(String nodePath) {
        String[] pathElements = nodePath.split("\\\\");
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) getModel().getRoot();

        if (pathElements.length == 0) {
            return new TreePath(node);
        }

        for (String pathElement : pathElements) {
            boolean found = false;
            for (int i = 0; i < node.getChildCount(); i++) {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
                if (child.getUserObject().toString().equals(pathElement)) {
                    node = child;
                    found = true;
                    break;
                }
            }
            if (!found) {
                return null;
            }
        }

        return new TreePath(node.getPath());
    }

}