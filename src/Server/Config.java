package Server;

import Gui.AlertType;
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

    public static String readFileString(File fileToRead) throws IOException {
        StringBuilder fileToReadReader = new StringBuilder();
        for(String s : Files.readAllLines(fileToRead.toPath())) {
            fileToReadReader.append(s);
        }
        return fileToReadReader.toString();
    }

    public Config() throws IOException {
        File serverConfig = new File("servers.json");
        if(!serverConfig.exists()) {
            if(!serverConfig.createNewFile()) {
                Frame.alert(AlertType.FATAL, "Cannot create config file");
                System.exit(1);
            }
            FileWriter writer = new FileWriter(serverConfig);
            writer.write("""
                    [
                    {
                        "globalLaunchArgs": "-Xmx16G -Xms2G -XX:+UseG1GC -XX:+UseThreadPriorities -XX:ThreadPriorityPolicy=1 -XX:ParallelGCThreads=4 -XX:+OptimizeStringConcat"
                    },
                    {
                        "buttonText": "Put your values here",
                        "pathToButtonIcon": "",
                        "pathToServerFolder": "",
                        "pathToServerJarFile": "",
                        "pathToJavaExecutable": "",
                        "overrideDefaultLaunchArgs": false,
                        "launchArgs": "-Xmx4G -Xms256M"
                      },
                      {
                        "buttonText": "If you want to add more servers, just copy paste this whole block",
                        "pathToButtonIcon": "",
                        "pathToServerFolder": "",
                        "pathToServerJarFile": "",
                        "pathToJavaExecutable": "",
                        "overrideDefaultLaunchArgs": false,
                        "launchArgs": "-Xmx4G -Xms256M"
                      }
                    ]""");
            writer.close();
        }

        JSONArray configJSONObjects = new JSONArray(readFileString(new File("servers.json")));
        JSONObject globalVariables = configJSONObjects.getJSONObject(0);
        String javaArguments = globalVariables.getString("globalLaunchArgs");

        for (int i = 1; i < configJSONObjects.length(); i++) { //start on index 1 because index 0 are global variables
            JSONObject jsonObject = configJSONObjects.getJSONObject(i);
            String buttonText = jsonObject.getString("buttonText");
            String pathToButtonIcon = jsonObject.getString("pathToButtonIcon");
            String pathToServerFolder = jsonObject.getString("pathToServerFolder");
            String pathToServerJarFile = jsonObject.getString("pathToServerJarFile");
            String pathToJavaRuntime = jsonObject.getString("pathToJavaExecutable");
            boolean overrideGloballaunchArgs = jsonObject.getBoolean("overrideDefaultLaunchArgs");
            String serverLaunchArgs;
            if(overrideGloballaunchArgs)
                serverLaunchArgs = jsonObject.getString("launchArgs");
            else
                serverLaunchArgs = javaArguments;
            data.add(new ButtonData(buttonText, pathToButtonIcon, pathToServerFolder, pathToServerJarFile, pathToJavaRuntime, serverLaunchArgs));
        }
    }
}