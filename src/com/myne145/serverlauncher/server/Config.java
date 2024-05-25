package com.myne145.serverlauncher.server;

import com.myne145.serverlauncher.gui.window.Window;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public abstract class Config extends ArrayList<MCServer> {
    private static final ArrayList<MCServer> data = new ArrayList<>();
    public static final ClassLoader classLoader = Config.class.getClassLoader();
    public static String RESOURCES_PATH = "com/myne145/serverlauncher/resources";
    public static String ABSOLUTE_PATH;


    public static File getDefaultJava() {
        if(System.getProperty("java.home") == null)
            return null;
        return new File(System.getProperty("java.home") + "/bin/javaw.exe");
    }

    private static String readFileString(File fileToRead) throws IOException {
        StringBuilder fileToReadReader = new StringBuilder();
        for(String fileLine : Files.readAllLines(fileToRead.toPath())) {
            fileToReadReader.append(fileLine);
        }
        return fileToReadReader.toString();
    }

    public static void createConfig() throws Exception {
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

            data.add(new MCServer(serverName, new File(pathToServerFolder), new File(pathToServerJarFile), pathToJavaRuntime,
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
        MCServer.writeAllToConfig();
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

//    public static String abbreviateFilePath(String filePath, int maxLength) {
//        if (filePath.length() <= maxLength) {
//            return filePath;
//        }
//
//        String separator = "/";
//        int separatorIndex = filePath.lastIndexOf(separator);
//
//        if (separatorIndex == -1) {
//            // Handle case where there is no separator in the path
//            return filePath.substring(0, maxLength);
//        }
//
//        String filename = filePath.substring(separatorIndex + 1);
//        int filenameLength = filename.length();
//
//        int prefixLength = maxLength - (filenameLength + 3); // 3 for "..."
//
//        if (prefixLength <= 0) {
//            return "..." + filename;
//        }
//
//        String prefix = filePath.substring(0, separatorIndex);
//        return prefix.substring(0, Math.min(prefix.length(), prefixLength)) + "..." + filename;
//    }


    public static String abbreviateFilePath(String path, int maxLength) {

        if (path.length() <= maxLength)
            return path;

        File f = new File(path);
        ArrayList<String> coll = new ArrayList<>();
        String name;
        StringBuilder begBuf = new StringBuilder();
        StringBuilder endBuf = new StringBuilder();
        int len;
        boolean b;

        while ((f != null) && (name = f.getName()) != null) {
            coll.add(0, name);
            f = f.getParentFile();
        }
        if (coll.isEmpty())
            return path;

        len = coll.size() << 1;
        name = coll.remove(coll.size() - 1);
        endBuf.insert(0, name);
        len += name.length();
        if (!coll.isEmpty()) {
            name = coll.remove(0);
            begBuf.append(name);
            begBuf.append(File.separator);
            len += name.length() - 1;
        }
        if (!coll.isEmpty()) {
            name = coll.remove(0);
            if (name.equals("Volumes")) {
                begBuf.append('…');
                begBuf.append(File.separator);
            } else {
                begBuf.append(name);
                begBuf.append(File.separator);
                len += name.length() - 1;
            }
        }
        for (b = true; !coll.isEmpty() && len <= maxLength; b = !b) {
            if (b) {
                name = coll.remove(coll.size() - 1);
                endBuf.insert(0, File.separator);
                endBuf.insert(0, name);
            } else {
                name = coll.remove(0);
                begBuf.append(name);
                begBuf.append(File.separator);
            }
            len += name.length() - 1;
        }

        while (!coll.isEmpty()) {
            coll.remove(0);
            begBuf.append('…');
            begBuf.append(File.separator);
        }

        StringBuilder result = begBuf.append(endBuf);
        if(result.length() > maxLength) {
            result.delete(0, result.length() - maxLength);
        }

        return result.toString();
//        return (begBuf.append(endBuf).toString());
    }

    public static ArrayList<MCServer> getData() {
        return data;
    }
}