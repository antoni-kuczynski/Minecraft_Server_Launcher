package com.myne145.serverlauncher.server;

import com.myne145.serverlauncher.utils.AlertType;
import com.myne145.serverlauncher.gui.window.Window;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;

import static com.myne145.serverlauncher.gui.window.Window.getErrorDialogMessage;

public class Config extends ArrayList<MCServer> {
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

        boolean isConfigAValidJSON = true;
        try {
            new JSONArray(readFileString(new File("servers.json")));
        } catch (Exception e) {
            isConfigAValidJSON = false;
        }

        if(!serverConfigFile.exists() || !isConfigAValidJSON) {
            if(!serverConfigFile.exists() && !serverConfigFile.createNewFile()) {
                Window.alert(AlertType.FATAL, "Cannot create config file");
                System.exit(1);
            }
            FileWriter configWriter = getConfigWriter(serverConfigFile);
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
            data.add(new MCServer(serverName, new File(pathToServerFolder), new File(pathToServerJarFile), pathToJavaRuntime,
                serverLaunchArgs, serverId));
            serverId++;
        }
    }

    private static FileWriter getConfigWriter(File serverConfigFile) throws IOException {
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