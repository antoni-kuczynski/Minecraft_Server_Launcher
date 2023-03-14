package Servers;

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

    public static String readFileString(File f) throws IOException {
        StringBuilder bobTheBuilder = new StringBuilder();
        for(String s : Files.readAllLines(f.toPath())) {
            bobTheBuilder.append(s);
        }
        return bobTheBuilder.toString();
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
                        "buttonText": "Put your values here",
                        "pathToButtonIcon": "",
                        "pathToServerFolder": "",
                        "pathToServerJarFile": "",
                        "pathToJavaExecutable": ""
                      },
                      {
                        "buttonText": "If you want to add more servers, just copy paste this whole block",
                        "pathToButtonIcon": "",
                        "pathToServerFolder": "",
                        "pathToServerJarFile": "",
                        "pathToJavaExecutable": ""
                      }
                    ]""");
            writer.close();
        }
        JSONArray jsonArray = new JSONArray(readFileString(new File("servers.json")));
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String buttonText = jsonObject.getString("buttonText");
            String pathToButtonIcon = jsonObject.getString("pathToButtonIcon");
            String pathToServerFolder = jsonObject.getString("pathToServerFolder");
            String pathToServerJarFile = jsonObject.getString("pathToServerJarFile");
            String pathToJavaRuntime = jsonObject.getString("pathToJavaExecutable");
            data.add(new ButtonData(buttonText, pathToButtonIcon, pathToServerFolder, pathToServerJarFile, pathToJavaRuntime));
        }
    }
}