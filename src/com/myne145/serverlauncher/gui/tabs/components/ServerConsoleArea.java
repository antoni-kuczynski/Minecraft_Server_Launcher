package com.myne145.serverlauncher.gui.tabs.components;

import com.myne145.serverlauncher.gui.ContainerPane;
import com.myne145.serverlauncher.gui.tabs.components.contextmenus.ServerConsoleContextMenu;
import com.myne145.serverlauncher.gui.Window;
import com.myne145.serverlauncher.gui.tabs.ServerConsoleTab;
import com.myne145.serverlauncher.server.current.CurrentServerInfo;
import com.myne145.serverlauncher.server.MCServer;
import com.myne145.serverlauncher.server.Config;

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

import static com.myne145.serverlauncher.gui.Window.SERVER_STATUS_ICON_DIMENSION;

public class ServerConsoleArea extends JPanel {
    public JTextArea consoleOutput = new JTextArea();
    private final ArrayList<Process> processes = new ArrayList<>();
    private boolean isServerRunning;
    public boolean isServerStopCausedByAButton = false;
    public final JLabel serverPIDText = new JLabel("Process's PID:");
    private ContainerPane parentPane;
    private int index;
    private ServerConsoleTab tab;
    public boolean isVisible = false;
    private final ImageIcon ERRORED = new ImageIcon(new ImageIcon("resources/server_errored.png").getImage().getScaledInstance(SERVER_STATUS_ICON_DIMENSION, SERVER_STATUS_ICON_DIMENSION, Image.SCALE_SMOOTH));
    private final ImageIcon OFFLINE = new ImageIcon(new ImageIcon("resources/server_offline.png").getImage().getScaledInstance(SERVER_STATUS_ICON_DIMENSION, SERVER_STATUS_ICON_DIMENSION, Image.SCALE_SMOOTH));
    private final ImageIcon ONLINE = new ImageIcon(new ImageIcon("resources/server_online.png").getImage().getScaledInstance(SERVER_STATUS_ICON_DIMENSION, SERVER_STATUS_ICON_DIMENSION, Image.SCALE_SMOOTH));
    private final Runnable consoleRunner = () -> {
        try {
            synchronized (this) {
                while (true) {
                    // Wait until isVisible is true
                    while (!isVisible) {
                        wait();
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
                        line = bufferedReader.readLine();
                        if (line == null) {
                            if(isServerRunning) {
                                inputStream = processes.get(processes.size() - 1).getInputStream();
                                reader = new InputStreamReader(inputStream);
                                bufferedReader = new BufferedReader(reader);
                                howManyTimesLineWasNull++;
                                if(howManyTimesLineWasNull > 50) { //assuming that a server has been stopped
                                    isServerRunning = false;
                                    howManyTimesLineWasNull = 0;
                                    processes.get(processes.size() - 1).destroy();
                                }
                            }
                        } else {
                            consoleOutput.append(line + "\n");
                        }
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    };

    private final Thread consoleMainThread = new Thread(consoleRunner);
    private ProcessBuilder processBuilder;


    public ServerConsoleArea(ContainerPane parentPane, int index, ServerConsoleTab tab) {
        setBackground(Color.GREEN);
        setLayout(new BorderLayout());
        this.index = index;
        this.parentPane = parentPane;
        this.tab = tab;
        JScrollPane scrollPane = new JScrollPane(consoleOutput);
        ServerConsoleContextMenu.addDefaultContextMenu(consoleOutput);
//        scrollPane.setPreferredSize(new Dimension(500,500));

        setBackground(new Color(56, 56, 56));
        consoleOutput.setBorder(null);
//        scrollPane.setBorder(null);


        JTextField commandField = new JTextField();
        JButton executeButton = new JButton("Execute");

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
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String command = commandField.getText();
                    executeCommand(command);
                    commandField.setText("");
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        consoleOutput.setEditable(false);

        JPanel optionsPanel = new JPanel(new BorderLayout());
        JPanel options = new JPanel();
        JButton clearAll = new JButton("Clear All");
        JCheckBox wrapLines = new JCheckBox("Wrap lines");
        JLabel serverConsoleTitle = new JLabel("Server Console");
        serverPIDText.setVisible(false);
        options.add(serverPIDText);
//        options.add(Box.createRigidArea(new Dimension(50,10)));
        options.add(wrapLines);
        options.add(clearAll);

        serverConsoleTitle.setFont(new Font("Arial", Font.BOLD, 18));
        optionsPanel.add(serverConsoleTitle, BorderLayout.LINE_START);
//        optionsPanel.add(Box.createRigidArea(new Dimension(100,1)));
        optionsPanel.add(options, BorderLayout.LINE_END);

        clearAll.addActionListener(e -> consoleOutput.setText(""));

        wrapLines.addItemListener(e -> {
            if(e.getStateChange() == ItemEvent.SELECTED) {
                consoleOutput.setLineWrap(true);
            } else if(e.getStateChange() == ItemEvent.DESELECTED) {
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
//        add(killServer, BorderLayout.PAGE_START);
        add(commandPanel, BorderLayout.PAGE_END);
    }

    public void killServer() {
        try {
            if (processes.size() > 0)
                processes.get(processes.size() - 1).destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        parentPane.setToolTipTextAt(index, "Offline");
        isVisible = false;
    }

    public void startServer(MCServer MCServer) {
        isVisible = true;
        ArrayList<String> command = new ArrayList<>(Arrays.asList("java",
                 "-jar", MCServer.serverJarPath().getAbsolutePath(), "nogui"));
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
            parentPane.setIconAt(index, ONLINE);
        } catch (Exception e) {
//            appendToPane(console, Frame.getErrorDialogMessage(e), Color.RED);
            consoleOutput.append(Window.getErrorDialogMessage(e));
//            consoleOutput.setForeground(Color.RED);
        }
        if(consoleMainThread.isAlive()) { //that is the most braindead code that I've ever written TO DATE (seriously)
            try {
                processBuilder = new ProcessBuilder(command);
                processBuilder.directory(MCServer.serverPath());
                processBuilder.redirectErrorStream(true);
                isServerRunning = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void executeCommand(String command) {
        if(!isServerRunning && command.equalsIgnoreCase("start"))
            startServer(Config.getData().get(CurrentServerInfo.serverId - 1));
        if (processes.size() > 0) {
            if(command.equals("stop")) {
                isServerStopCausedByAButton = true;
                parentPane.setToolTipTextAt(index, "Offline");
                serverPIDText.setVisible(false);
            }
            PrintWriter writer = new PrintWriter(processes.get(processes.size() - 1).getOutputStream());
            writer.flush();
        }
    }

    public void killAllProcesses() {
        for(Process p : processes)
            p.destroy();
    }

    private String readFileString(File fileToRead) throws IOException {
        StringBuilder fileToReadReader = new StringBuilder();
        for(String fileLine : Files.readAllLines(fileToRead.toPath())) {
            fileToReadReader.append(fileLine).append("\n");
        }
        return fileToReadReader.toString();
    }

    public void setTextFromLatestLogFile() throws IOException {
        if(isServerRunning) {
            File latestLog = new File(CurrentServerInfo.serverPath.getAbsolutePath() + "\\logs\\latest.log");
            consoleOutput.setText("");
            consoleOutput.append(readFileString(latestLog));
            isVisible = true;
        }
    }
}
