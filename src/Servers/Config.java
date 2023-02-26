package Servers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class Config {

    public ArrayList<ButtonData> getData() {
        return data;
    }

    private JSONObject serverList;
    private final ArrayList<ButtonData> data = new ArrayList<>();

    public static String readFileString(File f) throws IOException {
        StringBuilder bobTheBuilder = new StringBuilder();
        for(String s : Files.readAllLines(f.toPath())) {
            //arr.add(Integer.parseInt(s));
            bobTheBuilder.append(s);
        }
        return bobTheBuilder.toString();
    }

    private void parseJson() throws IOException {
        JSONArray jsonArray = new JSONArray(readFileString(new File("servers.json")));

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            String buttonText = jsonObject.getString("buttonText");
            String pathToButtonIcon = jsonObject.getString("pathToButtonIcon");
            String pathToServerFolder = jsonObject.getString("pathToServerFolder");
            String pathToServerJarFile = jsonObject.getString("pathToServerJarFile");

            data.add(new ButtonData(buttonText, pathToButtonIcon, pathToServerFolder, pathToServerJarFile));
//            // Do something with the parsed data
//            System.out.println("Button text: " + buttonText);
//            System.out.println("Path to button icon: " + pathToButtonIcon);
//            System.out.println("Path to server folder: " + pathToServerFolder);
//            System.out.println("Path to server jar file: " + pathToServerJarFile);
        }
    }

    public Config() throws IOException {
//        this.serverList = new JSONObject(readFileString(new File("servers.json")));
        parseJson();
    }









}
