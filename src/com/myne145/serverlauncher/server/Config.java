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

public class Config {
    private static final ArrayList<MCServer> data = new ArrayList<>();
    public static String RESOURCES_PATH = "src/com/myne145/serverlauncher/resources";

    private static String readFileString(File fileToRead) throws IOException {
        StringBuilder fileToReadReader = new StringBuilder();
        for(String fileLine : Files.readAllLines(fileToRead.toPath())) {
            fileToReadReader.append(fileLine);
        }
        return fileToReadReader.toString();
    }

    public static void createConfig() throws Exception {
        File serverConfigFile = new File("servers.json");
        if(!serverConfigFile.exists()) {
            if(!serverConfigFile.createNewFile()) {
                Window.alert(AlertType.FATAL, "Cannot create config file");
                System.exit(1);
            }
            FileWriter configWriter = new FileWriter(serverConfigFile);
            configWriter.write("""
                    [
                     {
                         "globalLaunchArgs": "",
                         "globalServerFolder": ""
                     },
                     {
                         "serverName": "YOUR SERVER NAME",
                         "pathToServerIcon": "PATH TO ICON",
                         "pathToServerJarFile": "PATH TO SERVER JAR",
                         "pathToJavaRuntimeExecutable": "PATH TO JAVA RUNTIME EXECUTABLE",
                         "overrideDefaultLaunchArgs": false,
                         "launchArgs": ""
                       }
                     ]""");
            configWriter.close();
        }

        JSONArray configJSONObjects = new JSONArray(readFileString(new File("servers.json")));
        JSONObject globalVariables = configJSONObjects.getJSONObject(0);
        String javaArguments = globalVariables.getString("globalLaunchArgs");


        for (int jsonIndex = 1; jsonIndex < configJSONObjects.length(); jsonIndex++) { //start on index 1 because index 0 are global variables
            JSONObject jsonObject = configJSONObjects.getJSONObject(jsonIndex);
            String serverName = jsonObject.getString("serverName");
            String pathToServerIcon = jsonObject.getString("pathToServerIcon");
            String pathToServerFolder = new File(jsonObject.getString("pathToServerJarFile")).getParent();
            String pathToServerJarFile = jsonObject.getString("pathToServerJarFile");
            String pathToJavaRuntime = jsonObject.getString("pathToJavaRuntimeExecutable");
            boolean overrideGloballaunchArgs = jsonObject.getBoolean("overrideDefaultLaunchArgs");
            boolean isEmpty = serverName.equals("") && pathToServerIcon.equals("") && pathToServerFolder.equals("") && pathToServerJarFile.equals("") && pathToJavaRuntime.equals("");

            String serverLaunchArgs;
            if(overrideGloballaunchArgs)
                serverLaunchArgs = jsonObject.getString("launchArgs");
            else
                serverLaunchArgs = javaArguments;

            data.add(new MCServer(serverName, new File(pathToServerIcon), new File(pathToServerFolder), new File(pathToServerJarFile), new File(pathToJavaRuntime), serverLaunchArgs, jsonIndex, isEmpty));
        }
    }
    public static ArrayList<MCServer> getData() {
        return data;
    }
}