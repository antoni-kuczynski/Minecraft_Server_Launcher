package com.myne145.serverlauncher.server;

import com.myne145.serverlauncher.utils.AlertType;
import com.myne145.serverlauncher.gui.window.Window;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import static com.myne145.serverlauncher.gui.window.Window.getErrorDialogMessage;

public class Config extends ArrayList<MCServer> {
    private static final ArrayList<MCServer> data = new ArrayList<>();
    public static String RESOURCES_PATH = "com/myne145/serverlauncher/resources";
    public static String ABSOLUTE_PATH;

    private static String readFileString(File fileToRead) throws IOException {
        StringBuilder fileToReadReader = new StringBuilder();
        for(String fileLine : Files.readAllLines(fileToRead.toPath())) {
            fileToReadReader.append(fileLine);
        }
        return fileToReadReader.toString();
    }

    public static void reloadServersWorldPath(MCServer server) {
        String path = getServerWorldPath(server.serverPath().getAbsolutePath());
        if(!path.equals(server.worldPath().getAbsolutePath())) {
            data.set(server.serverId() - 1, new MCServer(server.serverName(), server.serverPath(),
                    server.serverJarPath(), server.javaRuntimePath(), server.serverLaunchArgs(), server.serverId(), new File(path)));
        }
    }

    private static String getServerWorldPath(String pathToServer) {
        File serverProperties = new File(pathToServer + "/server.properties");
        String worldName = "world";
        if(!serverProperties.exists()) {
            return pathToServer + "/" + worldName;
        }

        ArrayList<String> serverPropertiesContent;
        try {
            serverPropertiesContent = (ArrayList<String>) Files.readAllLines(serverProperties.toPath());
        } catch (IOException e) {
            Window.alert(AlertType.FATAL, "Cannot read server.properties content.\n" + getErrorDialogMessage(e));
            throw new RuntimeException(e);
        }

        for(String s : serverPropertiesContent) {
            if(s.contains("level-name")) {
                worldName = s.split("=")[1];
                break;
            }
        }
        return pathToServer + "/" + worldName;
    }

    public static void createConfig() throws Exception {
        File serverConfigFile = new File("servers.json");
        ABSOLUTE_PATH = serverConfigFile.getAbsolutePath();
        if(!serverConfigFile.exists()) {
            if(!serverConfigFile.createNewFile()) {
                Window.alert(AlertType.FATAL, "Cannot create config file");
                System.exit(1);
            }
            FileWriter configWriter = new FileWriter(serverConfigFile);
            configWriter.write("""
                    [
                     {
                         "globalLaunchArgs": ""
                     },
                     {
                         "serverName": "YOUR SERVER NAME",
                         "pathToServerJarFile": "PATH TO SERVER JAR",
                         "pathToJavaRuntimeExecutable": "PATH TO JAVA RUNTIME EXECUTABLE",
                         "overrideDefaultLaunchArgs": false
                       }
                     ]""");
            configWriter.close();
        }

        JSONArray configJSONObjects = new JSONArray(readFileString(new File("servers.json")));
        JSONObject globalVariables = configJSONObjects.getJSONObject(0);
        String javaArguments = globalVariables.getString("globalLaunchArgs");


        int serverId = 1;
        for (int jsonIndex = 1; jsonIndex < configJSONObjects.length(); jsonIndex++) { //start on index 1 because index 0 are global variables
            JSONObject jsonObject = configJSONObjects.getJSONObject(jsonIndex);
            String serverName = jsonObject.getString("serverName");
            String pathToServerFolder = new File(jsonObject.getString("pathToServerJarFile")).getParent();
            String pathToServerJarFile = jsonObject.getString("pathToServerJarFile");
            String pathToJavaRuntime = jsonObject.getString("pathToJavaRuntimeExecutable");
            boolean overrideGloballaunchArgs = jsonObject.getBoolean("overrideDefaultLaunchArgs");
//            boolean isEmpty = serverName.isEmpty() && pathToServerFolder.isEmpty() && pathToServerJarFile.isEmpty() && pathToJavaRuntime.isEmpty();

            String serverLaunchArgs;
            if(overrideGloballaunchArgs)
                serverLaunchArgs = jsonObject.getString("launchArgs");
            else
                serverLaunchArgs = javaArguments;

            if(!new File(pathToServerJarFile).exists()) {
                continue;
            }
            data.add(new MCServer(serverName, new File(pathToServerFolder), new File(pathToServerJarFile), new File(pathToJavaRuntime),
                serverLaunchArgs, serverId, new File(getServerWorldPath(pathToServerFolder))));
            serverId++;
        }
    }
    public static ArrayList<MCServer> getData() {
        return data;
    }
}