package com.myne145.serverlauncher.server;

import com.formdev.flatlaf.util.SystemInfo;
import com.myne145.serverlauncher.gui.window.Window;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;


public abstract class Config extends ArrayList<MinecraftServer> {
    private static final ArrayList<MinecraftServer> data = new ArrayList<>();
    public static final ClassLoader classLoader = Config.class.getClassLoader();
    public static String RESOURCES_PATH = "com/myne145/serverlauncher/resources";
    public static String ABSOLUTE_PATH;


    public static File getDefaultJava() {
        if(System.getProperty("java.home") == null)
            return null;
        File file = new File(System.getProperty("java.home"));
        if(System.getProperty("os.name").toLowerCase().startsWith("windows")) {
            file = new File(file.getAbsolutePath() + "/bin/java.exe");
        } else {
            file = new File(file.getAbsolutePath() + "/bin/java");
        }

        return file;
    }

    public static String readFileString(File fileToRead) throws IOException {
        StringBuilder fileToReadReader = new StringBuilder();
        for(String fileLine : Files.readAllLines(fileToRead.toPath())) {
            fileToReadReader.append(fileLine).append("\n");
        }
        return fileToReadReader.toString();
    }

    public static void createConfig() throws IOException {
        data.clear();
        File serverConfigFile = new File("servers.json");
        ABSOLUTE_PATH = serverConfigFile.getAbsolutePath();

        if(!serverConfigFile.exists() && !serverConfigFile.createNewFile()) {
            Window.showErrorMessage("Cannot create the " + serverConfigFile.getName() + " file.", new IOException());
//            System.exit(1);
        }

//        boolean isConfigAValidJSON = true;
        try {
            new JSONArray(readFileString(new File("servers.json")));
        } catch (Exception e) {
            return;
//            Window.showErrorMessage(serverConfigFile.getName() + " file is not a valid JSON.", e);
//            System.exit(1);
        }

        if(!serverConfigFile.exists()) {
            FileWriter configWriter = getConfigWriter(serverConfigFile);
            configWriter.close();
        }

        JSONArray configJSONObjects = new JSONArray(readFileString(new File("servers.json")));
//        String javaArguments = globalVariables.getString("globalLaunchArgs");


//        int serverId = 1;
        for (int jsonIndex = 0; jsonIndex < configJSONObjects.length(); jsonIndex++) {
            JSONObject jsonObject = configJSONObjects.getJSONObject(jsonIndex);
            String serverName = jsonObject.getString("serverName");
            String pathToServerFolder = new File(jsonObject.getString("pathToServerJarFile")).getParent();
            String pathToServerJarFile = jsonObject.getString("pathToServerJarFile");
            String pathToJavaRuntime = jsonObject.getString("pathToJavaRuntimeExecutable");
            String serverLaunchArgs = jsonObject.getString("launchArgs");
            int tabIndex = jsonObject.getInt("tabIndex");

            if(!serverLaunchArgs.contains("nogui"))
                serverLaunchArgs = serverLaunchArgs + " nogui";

            if(!new File(pathToServerJarFile).exists() || (pathToJavaRuntime.equals("java") && Config.getDefaultJava() == null)) {
                continue;
            }

            data.add(new MinecraftServer(serverName, new File(pathToServerFolder), new File(pathToServerJarFile), pathToJavaRuntime,
                serverLaunchArgs, tabIndex));
//            serverId++;
        }

        data.sort((o1, o2) -> {
            if (o1.getServerId() > o2.getServerId())
                return 1;
            else if (o1.getServerId() < o2.getServerId())
                return -1;
            return 0;
        });

        for(int i = 0; i < data.size(); i++) {
            data.get(i).setServerId(i + 1);
        }
        MinecraftServer.writeAllToConfig();
    }

    private static FileWriter getConfigWriter(File serverConfigFile) throws IOException {
        FileWriter configWriter = new FileWriter(serverConfigFile);
        configWriter.write("""
                [
                 {
                     "serverName": "YOUR SERVER NAME",
                     "pathToServerJarFile": "PATH TO SERVER JAR",
                     "pathToJavaRuntimeExecutable": "PATH TO JAVA RUNTIME EXECUTABLE",
                     "launchArgs": "nogui",
                     "tabIndex": 0
                   }
                 ]""");
        return configWriter;
    }

    public static InputStream getResource(String resource) {
        return classLoader.getResourceAsStream(resource);
    }

    public static String abbreviateFilePath(File filePath, int max) {
        if(filePath.getName().length() > max)
            return filePath.getName().substring(0, max - 4) + "...";

        if(filePath.getAbsolutePath().length() <= max)
            return filePath.getAbsolutePath();

        StringBuilder result = new StringBuilder();
        ArrayList<String> folders = new ArrayList<>();
        while (filePath.getParentFile() != null) {
          folders.add(filePath.getName());
          filePath = filePath.getParentFile();
        }
        System.out.println(folders);
        if(SystemInfo.isLinux && folders.get(folders.size() - 1).equals("home")) {
            folders.remove(folders.size() - 1);
            folders.set(folders.size() - 1, "~");
        }

        if(SystemInfo.isWindows) {
            String driveLetter = FilenameUtils.getPrefix(filePath.getAbsolutePath());
            folders.add(driveLetter.replace("\\", ""));
        }
        Collections.reverse(folders);

        int currentSize = folders.get(folders.size() - 1).length();
        for(int i = 0; i < folders.size() - 1; i++) {
            String s = folders.get(i);
            if(s.length() + currentSize <= max) {
                result.append(s);
                currentSize += s.length();
            } else {
                result.append("...");
            }
            result.append(File.separator);
        }
        result.append(folders.get(folders.size() - 1));

        return result.toString();
    }

    public static ArrayList<MinecraftServer> getData() {
        return data;
    }
}