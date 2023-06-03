package Gui;

import Server.ButtonData;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class ServerConsoleArea extends JPanel {
    public static JTextArea consoleOutput = new JTextArea();
    private final ArrayList<Process> processes = new ArrayList<>();
    private boolean isServerRunning;

    private final Runnable consoleRunner = () -> {
//        if(isServerRunning) {
            try {
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
                            if(howManyTimesLineWasNull > 50) {
                                isServerRunning = false;
                                howManyTimesLineWasNull = 0;
                                processes.get(processes.size() - 1).destroy();
                            }
                        }
                    } else {
                        ServerConsoleArea.consoleOutput.append(line + "\n");
                    }
                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
//        }
//        System.out.println("I am not in the fucking if statement!!!");
    };
    private final Thread consoleMainThread = new Thread(consoleRunner);
    private ProcessBuilder processBuilder;

    public ServerConsoleArea(Dimension size) {
        JScrollPane scrollPane = new JScrollPane(consoleOutput);
        scrollPane.setPreferredSize(new Dimension(size.width, size.height - 100));

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

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(optionsPanel, BorderLayout.PAGE_START);
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
    }

    public void startServer(ButtonData buttonData) {
        ArrayList<String> command = new ArrayList<>(Arrays.asList("java",
                 "-jar", buttonData.getPathToServerJarFile(), "nogui"));
        System.out.println("Command: " + command);
        try {
            processBuilder = new ProcessBuilder(command);
            processBuilder.directory(new File(buttonData.getPathToServerFolder()));
            processBuilder.redirectErrorStream(true);
            isServerRunning = true;
            Process process1 = processBuilder.start();
            processes.add(process1);
            if (processes.size() == 1)
                consoleMainThread.start();
            System.out.println("Console main thread state: " + consoleMainThread.getState());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(consoleMainThread.isAlive()) { //that is the most braindead code that I've ever written TO DATE (seriously)
            System.out.println("Command: " + command);
            try {
                processBuilder = new ProcessBuilder(command);
                processBuilder.directory(new File(buttonData.getPathToServerFolder()));
                processBuilder.redirectErrorStream(true);
                isServerRunning = true;
//                Process process1 = processBuilder.start();
//                processes.add(process1);
//                if (processes.size() == 1)
//                    consoleMainThread.start();
                System.out.println("Console main thread state: " + consoleMainThread.getState());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void executeCommand(String command) {
        if (processes.size() > 0) {
            PrintWriter writer = new PrintWriter(processes.get(processes.size() - 1).getOutputStream());
            writer.println(command);
            writer.flush();
        }
    }
}
