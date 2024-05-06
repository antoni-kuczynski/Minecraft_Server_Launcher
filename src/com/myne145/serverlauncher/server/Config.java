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

    public static String getServerWorldPath(String pathToServer) {
        File serverProperties = new File(pathToServer + "/server.properties");
        String worldName = "world";
        if(!serverProperties.exists()) {
            return pathToServer + "/" + worldName;
        }

        ArrayList<String> serverPropertiesContent;
        try {
            serverPropertiesContent = (ArrayList<String>) Files.readAllLines(serverProperties.toPath());
        } catch (IOException e) {
            Window.alert(AlertType.ERROR, "Cannot read server.properties content.\n" + getErrorDialogMessage(e));
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
            data.add(new MCServer(serverName, new File(pathToServerFolder), new File(pathToServerJarFile), new File(pathToJavaRuntime),
                serverLaunchArgs, serverId, new File(getServerWorldPath(pathToServerFolder))));
            serverId++;
        }
    }

    public static void clearConfig() {
        data.clear();
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

    public static String abbreviateConfigPath() {
        return abbreviateFile(Config.ABSOLUTE_PATH, 27);
    }

    public static String abbreviateServerPath(int serverIndex) {
        return abbreviateFile(Config.getData().get(serverIndex).serverPath().getAbsolutePath(), 27);
    }

    public static String abbreviateFile(String fileName, int maxLen) {
        if (fileName.length() <= maxLen)
            return fileName;

        File f = new File(fileName);
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
            return fileName;

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
        for (b = true; !coll.isEmpty() && len <= maxLen; b = !b) {
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

        return (begBuf.append(endBuf).toString());
    }

    public static ArrayList<MCServer> getData() {
        return data;
    }
}