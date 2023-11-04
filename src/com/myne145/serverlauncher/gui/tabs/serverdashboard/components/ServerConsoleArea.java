package com.myne145.serverlauncher.gui.tabs.serverdashboard.components;

import com.myne145.serverlauncher.gui.window.ContainerPane;
import com.myne145.serverlauncher.gui.window.Window;
import com.myne145.serverlauncher.gui.tabs.serverdashboard.ServerConsoleTab;
import com.myne145.serverlauncher.server.MCServer;
import com.myne145.serverlauncher.server.Config;
import com.myne145.serverlauncher.utils.AlertType;
import com.myne145.serverlauncher.utils.ServerIcon;

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

public class ServerConsoleArea extends JPanel {
    public JTextArea consoleOutput = new JTextArea();
    private final ArrayList<Process> processes = new ArrayList<>();
    private boolean isServerRunning;
    public boolean wasServerStopCausedByAButton = false;
    public final JLabel serverPIDText = new JLabel("Process's PID:");
    private ContainerPane parentPane;
    private int index;
    private final ServerConsoleTab tab;
    public boolean isVisible = false;
    private final Runnable consoleRunner = () -> {
        try {
            while (true) {
                synchronized (this) {
                    // Wait until isVisible is true
                    while (!isVisible) {
                        wait();
                    }
                }

                // Get the input stream of the server process
                InputStream inputStream = processes.get(processes.size() - 1).getInputStream();

                // Create a reader to read the input stream
                InputStreamReader reader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(reader);

                // Read the output from the server and append it to the JTextArea
                String line;

                int howManyTimesLineWasNull = 0;
                while (true) {
                    if (!isServerRunning) {
                        break;
                    }
                    line = bufferedReader.readLine();
                    if (line == null) {
                        inputStream = processes.get(processes.size() - 1).getInputStream();
                        reader = new InputStreamReader(inputStream);
                        bufferedReader = new BufferedReader(reader);
                        howManyTimesLineWasNull++;
                        if (howManyTimesLineWasNull > 50) { //assuming that a server has been stopped
                            isServerRunning = false;
                            howManyTimesLineWasNull = 0;
                            processes.get(processes.size() - 1).destroy();
                            if(wasServerStopCausedByAButton) {
                                parentPane.setIconAt(index, ServerIcon.getServerIcon(ServerIcon.OFFLINE));
                                parentPane.setToolTipTextAt(index, "Offline");
                            } else {
                                parentPane.setIconAt(index, ServerIcon.getServerIcon(ServerIcon.ERRORED));
                                parentPane.setToolTipTextAt(index, "Errored");
                            }
                        }
                    } else {
                        String finalLine = line;
                        SwingUtilities.invokeLater(() -> consoleOutput.append(finalLine + "\n"));
                    }
                    System.out.println("Line: " + line);
                }
            }
        } catch (IOException | InterruptedException e) {
            alert(AlertType.ERROR, "Error in server console thread:\n" + getErrorDialogMessage(e));
        }
    };
    private final Thread consoleMainThread = new Thread(consoleRunner);
    private ProcessBuilder processBuilder;
    private final ArrayList<String> commandHistory = new ArrayList<>();
    private int commandIndex;

    public ServerConsoleArea(ContainerPane parentPane, int index, ServerConsoleTab tab) {
        setLayout(new BorderLayout());
        this.index = index;
        this.parentPane = parentPane;
        this.tab = tab;
        JScrollPane scrollPane = new JScrollPane(consoleOutput);
        ServerConsoleContextMenu.addDefaultContextMenu(consoleOutput);

        consoleMainThread.setName("SERVER_" + Config.getData().get(index).serverId() + "_CONSOLE");
        setBackground(new Color(56, 56, 56));
        consoleOutput.setBorder(null);

        JTextField commandField = new JTextField();
        JButton executeButton = new JButton("Execute");

        //.console_history file loading
        File consoleHistory = new File(Config.getData().get(index).serverPath() + "/.console_history");
        if(consoleHistory.exists()) {
            String commands;
            try {
                commands = readFileString(consoleHistory);
            } catch (IOException e) {
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
            public void keyTyped(KeyEvent e) {

            }

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
        });
        consoleOutput.setEditable(false);

        JPanel optionsPanel = new JPanel(new BorderLayout());
        JPanel options = new JPanel();
        JLabel serverConsoleTitle = new JLabel( "<html>Console - " + Config.getData().get(index).serverName() + "</html>");
        JButton clearAll = new JButton("Clear All");
        JCheckBox wrapLines = new JCheckBox("Wrap Lines");
        serverPIDText.setVisible(false);
        options.add(serverPIDText);
        options.add(wrapLines);
        options.add(clearAll);
        options.setOpaque(true);
        options.setBackground(new Color(56, 56, 56));

        serverConsoleTitle.setFont(new Font("Arial", Font.BOLD, 18));
        optionsPanel.add(options, BorderLayout.LINE_END);
        optionsPanel.add(serverConsoleTitle, BorderLayout.LINE_START);


        clearAll.addActionListener(e -> consoleOutput.setText(""));

        wrapLines.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                consoleOutput.setLineWrap(true);
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                consoleOutput.setLineWrap(false);
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

    public void killServer() {
        if (!processes.isEmpty())
            processes.get(processes.size() - 1).destroy();
//        parentPane.setToolTipTextAt(index, "Offline");
        wasServerStopCausedByAButton = true;
        isVisible = false;
    }


    public void startServer(MCServer MCServer) {
        isVisible = true;
        boolean isSelectedJavaTheDefaultOne = MCServer.javaRuntimePath().getAbsolutePath().contains(new File("").getAbsolutePath()) &&
                MCServer.javaRuntimePath().getAbsolutePath().endsWith("java");
        String tempJavaPath = isSelectedJavaTheDefaultOne ? "java" : MCServer.javaRuntimePath().getAbsolutePath();
        ArrayList<String> command = new ArrayList<>(Arrays.asList(tempJavaPath, "-jar", MCServer.serverJarPath().getAbsolutePath(), "nogui"));

        try {
            processBuilder = new ProcessBuilder(command);
            processBuilder.directory(MCServer.serverPath());
            processBuilder.redirectErrorStream(true);
            isServerRunning = true;
            Process process1 = processBuilder.start();
            serverPIDText.setText("Process's PID: " + process1.pid());
            processes.add(process1);
            if (processes.size() == 1)
                consoleMainThread.start();
            parentPane.setToolTipTextAt(index, "Running");
            parentPane.setIconAt(index, ServerIcon.getServerIcon(ServerIcon.ONLINE));
        } catch (Exception e) {
            consoleOutput.append(Window.getErrorDialogMessage(e));
            consoleOutput.append("(You probably specified a java executable that is not valid in the config file.)");
        }
        if (consoleMainThread.isAlive()) {
            processBuilder = new ProcessBuilder(command);
            processBuilder.directory(MCServer.serverPath());
            processBuilder.redirectErrorStream(true);
            isServerRunning = true;
        }
    }

    public void executeCommand(String command) {
        if (!isServerRunning && command.equalsIgnoreCase("start")) {
            startServer(Config.getData().get(index));
            tab.startServer.setVisible(false);
            tab.stopServer.setVisible(true);
            tab.killServer.setEnabled(true);
            tab.getServerConsoleArea().serverPIDText.setVisible(true);
            parentPane.setToolTipTextAt(index, "Running");
        }
        
        if (!processes.isEmpty()) {
            if (command.equals("stop")) {
                wasServerStopCausedByAButton = true;
//                parentPane.setToolTipTextAt(index, "Offline");
//                parentPane.setIconAt  (index, ServerIcon.getServerIcon(ServerIcon.OFFLINE));
                serverPIDText.setVisible(false);
            }
            PrintWriter writer = new PrintWriter(processes.get(processes.size() - 1).getOutputStream());
            writer.println(command);
            writer.flush();
        }
    }

    public void killAllProcesses() {
        for (Process p : processes)
            p.destroy();
    }

    private String readFileString(File fileToRead) throws IOException {
        StringBuilder fileToReadReader = new StringBuilder();
        for (String fileLine : Files.readAllLines(fileToRead.toPath())) {
            fileToReadReader.append(fileLine).append("\n");
        }
        return fileToReadReader.toString();
    }

    public void setTextFromLatestLogFile() throws IOException {
        if (isServerRunning) {
            File latestLog = new File(Config.getData().get(index).serverPath().getAbsolutePath() + "\\logs\\latest.log");
            consoleOutput.setText("");
            consoleOutput.append(readFileString(latestLog));
            isVisible = true;
        }
    }
}
