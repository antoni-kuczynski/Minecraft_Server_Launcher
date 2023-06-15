package Server;

import Enums.AlertType;
import Gui.Frame;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class Config {

    public ArrayList<ButtonData> getData() {
        return data;
    }

    private final ArrayList<ButtonData> data = new ArrayList<>();
    public static String globalServerFolder;

    public static String readFileString(File fileToRead) throws IOException {
        StringBuilder fileToReadReader = new StringBuilder();
        for(String fileLine : Files.readAllLines(fileToRead.toPath())) {
            fileToReadReader.append(fileLine);
        }
        return fileToReadReader.toString();
    }

    public Config() throws IOException {
        File serverConfigFile = new File("servers.json");
        if(!serverConfigFile.exists()) {
            if(!serverConfigFile.createNewFile()) {
                Frame.alert(AlertType.FATAL, "Cannot create config file");
                System.exit(1);
            }
            FileWriter configWriter = new FileWriter(serverConfigFile);
            configWriter.write("""
                    [
                    {
                        "globalLaunchArgs": "-Xmx16G -Xms2G -XX:+UseG1GC -XX:+UseThreadPriorities -XX:ThreadPriorityPolicy=1 -XX:ParallelGCThreads=4 -XX:+OptimizeStringConcat",
                        "globalServerFolder": ""
                    },
                    {
                        "serverName": "Put your values here",
                        "pathToServerButtonIcon": "",
                        "serverPath": "",
                        "serverJarPath": "",
                        "pathToJavaExecutable": "",
                        "overrideDefaultLaunchArgs": false,
                        "launchArgs": "-Xmx4G -Xms256M"
                      },
                      {
                        "serverName": "If you want to add more servers, just copy paste this whole block",
                        "pathToServerButtonIcon": "",
                        "serverPath": "",
                        "serverJarPath": "",
                        "pathToJavaExecutable": "",
                        "overrideDefaultLaunchArgs": false,
                        "launchArgs": "-Xmx4G -Xms256M"
                      }
                    ]""");
            configWriter.close();
        }

        JSONArray configJSONObjects = new JSONArray(readFileString(new File("servers.json")));
        JSONObject globalVariables = configJSONObjects.getJSONObject(0);
        String javaArguments = globalVariables.getString("globalLaunchArgs");
        globalServerFolder = globalVariables.getString("globalServerFolder");

        for (int jsonIndex = 1; jsonIndex < configJSONObjects.length(); jsonIndex++) { //start on index 1 because index 0 are global variables
            JSONObject jsonObject = configJSONObjects.getJSONObject(jsonIndex);
            String buttonText = jsonObject.getString("serverName");
            String pathToButtonIcon = jsonObject.getString("pathToServerIcon");
            String pathToServerFolder = jsonObject.getString("pathToServer");
            String pathToServerJarFile = jsonObject.getString("pathToServerJarFile");
            String pathToJavaRuntime = jsonObject.getString("pathToJavaRuntimeExecutable");
            boolean overrideGloballaunchArgs = jsonObject.getBoolean("overrideDefaultLaunchArgs");
            String serverLaunchArgs;
            if(overrideGloballaunchArgs)
                serverLaunchArgs = jsonObject.getString("launchArgs");
            else
                serverLaunchArgs = javaArguments;


            data.add(new ButtonData(buttonText, new File(pathToButtonIcon), new File(pathToServerFolder), new File(pathToServerJarFile), new File(pathToJavaRuntime), serverLaunchArgs, jsonIndex));
        }
    }
}