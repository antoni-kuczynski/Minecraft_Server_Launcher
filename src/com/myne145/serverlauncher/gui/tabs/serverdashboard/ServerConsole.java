package com.myne145.serverlauncher.gui.tabs.serverdashboard;

import com.myne145.serverlauncher.gui.tabs.serverdashboard.components.ServerConsoleContextMenu;
import com.myne145.serverlauncher.gui.window.ContainerPane;
import com.myne145.serverlauncher.server.MCServer;
import com.myne145.serverlauncher.server.Config;
//import com.myne145.serverlauncher.utils.AlertType;
import com.myne145.serverlauncher.utils.Colors;
import com.myne145.serverlauncher.utils.DefaultIcons;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

import static com.myne145.serverlauncher.gui.window.Window.*;

public class ServerConsole extends JPanel {
    private final JTextArea consoleOutput = new JTextArea();
    private final ArrayList<Process> processes = new ArrayList<>();
    private boolean isServerRunning;
    private boolean wasServerStopCausedByUser = false;
    private final JLabel serverPIDText = new JLabel();
    private ContainerPane parentPane;
    private int index;
    private ServerDashboardTab parentConsoleTab;
    public boolean isVisible = false;
    private final ArrayList<String> commandHistory = new ArrayList<>();
    private int commandIndex;
    private final JLabel serverConsoleTitle = new JLabel( "<html>Console - " + Config.getData().get(index).getServerName() + "</html>");
    private final Runnable consoleRunner = () -> {
//        try {
            while (true) {
                synchronized (this) {
                    while (!isVisible || !isServerRunning) {
                        try {
                            wait(500);
                        } catch (InterruptedException e) {
                            showErrorMessage("Console thread for " + Config.getData().get(index).getServerName() + " was interrupted.", e);
                        }
                    }
                }

                // Get the input stream of the server process
                InputStream inputStream = processes.get(processes.size() - 1).getInputStream();

                // Create a reader to read the input stream
                InputStreamReader reader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(reader);

                String line = null;
                int howManyTimesLineWasNull = 0;

                while (isServerRunning && isVisible) {
                    try {
                        line = bufferedReader.readLine();
                    } catch (IOException e) {
                        showErrorMessage("I/O error in " + Config.getData().get(index).getServerName() + " thread - reading line.", e);
                    }

                    if(line != null) {
                        String finalLine = line;
                        SwingUtilities.invokeLater(() -> consoleOutput.append(finalLine + "\n"));
                        System.out.println(line + "\n");
                        continue;
                    }

                    inputStream = processes.get(processes.size() - 1).getInputStream();
                    reader = new InputStreamReader(inputStream);
                    bufferedReader = new BufferedReader(reader);
                    howManyTimesLineWasNull++;

                    if(howManyTimesLineWasNull < 50)
                        continue;

                    //assuming that a server has been stopped (at least 50 lines were null)
                    isServerRunning = false;
                    howManyTimesLineWasNull = 0;
                    processes.get(processes.size() - 1).destroy();
                    if (wasServerStopCausedByUser) {
                        parentPane.setIconAt(index, DefaultIcons.getIcon(DefaultIcons.SERVER_OFFLINE));
                        parentConsoleTab.changeServerActionButtonsVisibility(false);
                        parentConsoleTab.setWaitingStop(false);
                        parentPane.setToolTipTextAt(index, "Offline");
                    } else {
                        parentPane.setIconAt(index, DefaultIcons.getIcon(DefaultIcons.SERVER_ERRORED));
                        parentConsoleTab.changeServerActionButtonsVisibility(false);
                        parentPane.setToolTipTextAt(index, "Errored");

                    }
                    parentConsoleTab.setWaitingStop(false);
                    wasServerStopCausedByUser = true;
                }
            }
//        } catch (IOException | InterruptedException e) {
//            alert(AlertType.ERROR, "Error in server console thread:\n" + getErrorDialogMessage(e));
//        }
    };
    private final Thread consoleMainThread = new Thread(consoleRunner);

    protected ServerConsole(ContainerPane parentPane, int index, ServerDashboardTab tab) {
        setLayout(new BorderLayout());
        this.index = index;
        this.parentPane = parentPane;
        this.parentConsoleTab = tab;
        JScrollPane scrollPane = new JScrollPane(consoleOutput);
        ServerConsoleContextMenu.addDefaultContextMenu(consoleOutput);

        consoleMainThread.setName("SERVER_" + Config.getData().get(index).getServerId() + "_CONSOLE");
        setBackground(Colors.BACKGROUND_PRIMARY_COLOR);
        consoleOutput.setBorder(null);

        JTextField commandField = new JTextField();
        JButton executeButton = new JButton("Execute");


        serverConsoleTitle.setText("<html>Console - " + Config.getData().get(index).getServerName() + "</html>");

        //.console_history file loading
        File consoleHistory = new File(Config.getData().get(index).getServerPath() + "/.console_history");
        if(consoleHistory.exists()) {
            String commands;
            try {
                commands = readFileString(consoleHistory);
            } catch (IOException e) { //TODO
                throw new RuntimeException();
            }
            for(String s : commands.split("\n")) {
                commandHistory.add(s.substring(14)); //this's gonna break on 20nov 2286
            }
            commandIndex = commandHistory.size();
        }

        executeButton.addActionListener(e -> {
            String command = commandField.getText();
            executeCommand(command);
            commandField.setText("");
        });

        commandField.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String command = commandField.getText();
                    executeCommand(command);
                    commandField.setText("");
                } else if(e.getKeyCode() == KeyEvent.VK_UP) {
                    if(commandIndex == 0)
                        return;
                    commandField.setText(commandHistory.get(commandIndex - 1));
                    commandIndex--;
                } else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (commandIndex == commandHistory.size())
                        return;
                    commandField.setText(commandHistory.get(commandIndex));
                    commandIndex++;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }

            @Override
            public void keyTyped(KeyEvent e) {

            }
        });
        consoleOutput.setEditable(false);

        JPanel optionsPanel = new JPanel(new BorderLayout());
        JPanel options = new JPanel();
        JButton clearAll = new JButton("Clear all");
        JCheckBox wrapLines = new JCheckBox("Wrap lines");

        serverPIDText.setVisible(false);
        options.add(serverPIDText);
        options.add(wrapLines);
        options.add(clearAll);
        options.setOpaque(true);
        options.setBackground(Colors.BACKGROUND_PRIMARY_COLOR);

        serverConsoleTitle.setFont(new Font("Arial", Font.BOLD, 18));
        optionsPanel.add(options, BorderLayout.LINE_END);
        optionsPanel.add(serverConsoleTitle, BorderLayout.LINE_START);


        clearAll.addActionListener(e -> consoleOutput.setText(""));

        wrapLines.setSelected(getUserValues().getBoolean("is_wrap_lines_enabled", false));

        wrapLines.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                consoleOutput.setLineWrap(true);
                getUserValues().putBoolean("is_wrap_lines_enabled", true);
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                consoleOutput.setLineWrap(false);
                getUserValues().putBoolean("is_wrap_lines_enabled", false);
            }

        });

        DefaultCaret caret = (DefaultCaret) consoleOutput.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JPanel commandPanel = new JPanel();
        commandPanel.setLayout(new BorderLayout());
        commandPanel.add(commandField, BorderLayout.CENTER);
        commandPanel.add(executeButton, BorderLayout.LINE_END);

        add(optionsPanel, BorderLayout.PAGE_START);
        add(scrollPane, BorderLayout.CENTER);
        add(commandPanel, BorderLayout.PAGE_END);
    }

    protected void killServer() {
        if (!processes.isEmpty())
            processes.get(processes.size() - 1).destroy();
        parentPane.setToolTipTextAt(index, "Offline");
        wasServerStopCausedByUser = true;
        isVisible = false;
        parentConsoleTab.setWaitingStop(false);
    }


    protected void startServerWithoutChangingTheButtons(MCServer MCServer) {
        serverConsoleTitle.setIcon(null);
        isVisible = true;
        boolean isSelectedJavaTheDefaultOne = MCServer.getJavaExecutablePath().getAbsolutePath().contains(new File("").getAbsolutePath()) &&
                MCServer.getJavaExecutablePath().getAbsolutePath().endsWith("java");
        String tempJavaPath = isSelectedJavaTheDefaultOne ? "java" : MCServer.getJavaExecutablePath().getAbsolutePath();
        ArrayList<String> command = new ArrayList<>(Arrays.asList(tempJavaPath, "-jar", MCServer.getServerJarPath().getAbsolutePath(), "nogui"));
        consoleOutput.setText("");

        ProcessBuilder processBuilder;
        try {
            processBuilder = new ProcessBuilder(command);
            processBuilder.directory(MCServer.getServerPath());
            processBuilder.redirectErrorStream(true);
            isServerRunning = true;
            Process serverProcess = processBuilder.start();


            serverPIDText.setText("Process's PID: " + serverProcess.pid());
            processes.add(serverProcess);
            if (processes.size() == 1)
                consoleMainThread.start();
            parentPane.setToolTipTextAt(index, "Running");
            parentPane.setIconAt(index, DefaultIcons.getIcon(DefaultIcons.SERVER_ONLINE));
        } catch (Exception e) { //TODO
//            System.out.println(Window.getErrorDialogMessage(e));

            parentPane.setIconAt(index, DefaultIcons.getIcon(DefaultIcons.SERVER_ERRORED));
            parentPane.setToolTipTextAt(index, "Errored");


            serverConsoleTitle.setIcon(DefaultIcons.getSVGIcon(DefaultIcons.ERROR).derive(16,16));


            consoleOutput.append(e.getMessage() +
                    "\n(You probably specified a java executable that is not valid in the config file.)");
        }


        if (consoleMainThread.isAlive()) {
            processBuilder = new ProcessBuilder(command);
            processBuilder.directory(MCServer.getServerPath());
            processBuilder.redirectErrorStream(true);
            isServerRunning = true;
        }
    }

    protected void executeCommand(String command) {
        if (!isServerRunning && command.equalsIgnoreCase("start"))
            parentConsoleTab.startServer();

        if(processes.isEmpty())
            return;

        if (isServerRunning && command.equalsIgnoreCase("stop")) {
            wasServerStopCausedByUser = true;
            parentConsoleTab.setWaitingStop(true);
        }

        PrintWriter writer = new PrintWriter(processes.get(processes.size() - 1).getOutputStream());
        writer.println(command);
        writer.flush();
    }

    public void killAllProcesses() {
        for (Process p : processes)
            p.destroy();
    }

    private static String readFileString(File fileToRead) throws IOException {
        StringBuilder fileToReadReader = new StringBuilder();
        for (String fileLine : Files.readAllLines(fileToRead.toPath())) {
            fileToReadReader.append(fileLine).append("\n");
        }
        return fileToReadReader.toString();
    }

    public void setTextFromLatestLogFile() throws IOException {
        if(!isServerRunning)
            return;
        File latestLog = new File(Config.getData().get(index).getServerPath().getAbsolutePath() + "\\logs\\latest.log");
        if(!latestLog.exists()) {
//            System.out.println(ServerIcon.getServerIconSVG(ServerIcon.WARNING));
//            serverConsoleTitle.setIcon(ServerIcon.getServerIconSVG(ServerIcon.WARNING));
            consoleOutput.append("\n[LAUNCHER WARNING]: Console won't update when unfocused (latest.log file not found)\n");
            isVisible = true;
            return;
        }
        consoleOutput.setText("");
        consoleOutput.append(readFileString(latestLog));
        isVisible = true;
    }

    public boolean isServerRunning() {
        return isServerRunning;
    }

    protected void setPIDTextVisible(boolean b) {
        serverPIDText.setVisible(b);
    }
}
