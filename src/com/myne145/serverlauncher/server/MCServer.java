package com.myne145.serverlauncher.server;

//import com.myne145.serverlauncher.utils.AlertType;

import com.myne145.serverlauncher.gui.window.Window;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import static com.myne145.serverlauncher.gui.window.Window.showErrorMessage;
//import static com.myne145.serverlauncher.gui.window.Window.getErrorDialogMessage;

public class MCServer {
    private String serverName;
    private File serverPath;
    private File serverJarPath;
    private File javaExecutablePath;
    private String serverLaunchArgs;
    private final int serverId;
    private File worldPath;
    private final LinkedHashMap<String, String> properties = new LinkedHashMap<>();
    private ServerPlatform platform;
    private String javaVersion;

    public boolean isComplete() {
        return serverName != null && serverPath != null
                && serverJarPath != null && javaExecutablePath != null
                && serverLaunchArgs != null;
    }

    public MCServer() {
        this.serverId = Config.getData().get(Config.getData().size() - 1).getServerId() + 1;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void setServerJarPath(File serverJarPath) {
        this.serverJarPath = serverJarPath;
        this.serverPath = serverJarPath.getParentFile();
        this.platform = getPlatformFromManifestFile();
        try {
            updateProperties();
        } catch (IOException e) {
            showErrorMessage("Cannot read " + serverName + " server.properties file.", e);
        }

    }

    public void setJavaExecutablePath(File javaExecutablePath) {
        this.javaExecutablePath = javaExecutablePath;
        try {
            javaVersion = getJavaVersionFromReleaseFile();
        } catch (Exception e) {
            showErrorMessage("Cannot read java release file.", e);
            javaVersion = "Unknown";
        }
    }

    public void setServerLaunchArgs(String serverLaunchArgs) {
        this.serverLaunchArgs = "nogui " + serverLaunchArgs.replace("nogui", "");
    }


    public MCServer(String serverName, File serverPath, File serverJarPath, String javaExecutablePath, String serverLaunchArgs, int serverId) {
        this.serverName = serverName;
        this.serverPath = serverPath;
        this.serverJarPath = serverJarPath;

        if(javaExecutablePath.equalsIgnoreCase("java")) {
            this.javaExecutablePath = Config.getDefaultJava();
        } else {
            this.javaExecutablePath = new File(javaExecutablePath);
        }

        this.serverLaunchArgs = serverLaunchArgs;
        this.serverId = serverId;

        updateWorldPath();
        try {
            updateProperties();
        } catch (Exception e) { //TODO
            throw new RuntimeException(e);
        }
        platform = getPlatformFromManifestFile();
    }


    private ServerPlatform getPlatformFromManifestFile() {
        String jarFilePath = serverJarPath.getAbsolutePath();
        String mainClass = "";
        String implementationVersion = "";
        try {
            JarFile jarFile = new JarFile(jarFilePath);
            Manifest manifest = jarFile.getManifest();
            Attributes mainAttributes = manifest.getMainAttributes();

            for (Map.Entry<Object, Object> entry : mainAttributes.entrySet()) {
                String key = String.valueOf(entry.getKey());
                String value = String.valueOf(entry.getValue());

                if(key.equalsIgnoreCase("Main-Class")) {
                    mainClass = value.toLowerCase();
                } else if(key.equalsIgnoreCase("Implementation-Version")) {
                    implementationVersion = value.toLowerCase();
                }
            }
            jarFile.close();
        } catch (IOException e) {
            showErrorMessage("I/O error reading " + serverJarPath.getName() + " file, when trying to obtain server's version.", e);
        }

        if(mainClass.contains("papermc")) {
            return ServerPlatform.PAPER_MC;
        } else if(mainClass.contains("fml")) {
            return ServerPlatform.FORGE;
        } else if(mainClass.contains("bukkit") &&
                (implementationVersion.isEmpty() || implementationVersion.contains("bukkit"))) {
            return ServerPlatform.BUKKIT;
        } else if(mainClass.contains("bukkit") &&
                implementationVersion.contains("spigot")) {
            return ServerPlatform.SPIGOT;
        } else if(mainClass.contains("minecraftserver")) {
            return ServerPlatform.VANILLA;
        } else if(mainClass.contains("fabricmc")) {
            return ServerPlatform.FABRIC;
        }

        return ServerPlatform.UNKNOWN;
    }


    public void updateWorldPath() {
        String worldName;
        if(!properties.containsKey("level-name")) {
            worldName = "world";
        } else {
            worldName = getProperty("level-name");
        }

        worldPath = new File(serverPath.getAbsolutePath() + "/" + worldName);
    }

    private String getJavaVersionFromReleaseFile() throws IOException {
        File javaPath = getJavaExecutablePath();
        String javaVersion = "Unknown";
        if(javaPath == null || javaPath.getParentFile().list() == null || javaPath.getParentFile().list().length == 0)
            return javaVersion;

        if(javaPath.isFile())
            javaPath = javaPath.getParentFile();

        if(javaPath.list() == null)
            return javaVersion;

        List<String> files = Arrays.asList(javaPath.list());
        if(!files.contains("release") && javaPath.getParentFile() != null && javaPath.getParentFile().list() != null) {
            files = Arrays.asList(javaPath.getParentFile().list()); //one folder back if user selected the "bin" folder
            javaPath = javaPath.getParentFile();
        }


        if(!files.contains("release"))
            return javaVersion;


        ArrayList<String> content = (ArrayList<String>) Files.readAllLines(new File(javaPath.getAbsolutePath() + "/release").toPath());
        for(String s : content) {
            if(s.split("=")[0].equals("JAVA_VERSION")) {
                javaVersion = s.split("=")[1].replace("\"", "");
            }
        }

        return javaVersion;
    }

    private void updateProperties() throws IOException {
        File propertiesFile = new File(serverPath.getAbsolutePath() + "/server.properties");
        if(!propertiesFile.exists())
            return;
        ArrayList<String> content = (ArrayList<String>) Files.readAllLines(propertiesFile.toPath());
        if(content.isEmpty())
            return;

        for(String s : content) {
            if(s.startsWith("#"))
                continue;
            String[] lineSplitted = s.split("=");
            String value = getValue(lineSplitted);
            properties.put(lineSplitted[0], value);

        }
    }

    private static String getValue(String[] lineSplitted) {
        StringBuilder value = new StringBuilder();
        if(lineSplitted.length == 2) {
            value.append(lineSplitted[1]);
        }
        else if(lineSplitted.length > 2) {
            for(int i = 1; i < lineSplitted.length; i++) {
                value.append("=").append(lineSplitted[i]);
            }
        }
        return value.toString();
    }

    public String getProperty(String key) {
        if(!properties.containsKey(key))
            return "";

        return properties.get(key);
    }

    public void writeToConfig() {
        Config.getData().add(this);
        JSONObject object = new JSONObject();
        object.put("serverName", serverName);
        object.put("pathToServerJarFile", serverJarPath);
        object.put("pathToJavaRuntimeExecutable", javaExecutablePath);
        object.put("launchArgs", serverLaunchArgs);
        object.put("tabIndex", serverId);

        JSONArray array = Config.getJSONArray();
        array.put(object);
        System.out.println(array.toString(4));

        try(FileWriter writer = new FileWriter(Config.ABSOLUTE_PATH)) {
            writer.write("");
            writer.write(array.toString(4));
        } catch (IOException e) {
            Window.showErrorMessage("I/O error writing the server to config. Server will not be added.", e);
        }
    }

    public String getServerName() {
        return serverName;
    }

    public File getServerPath() {
        return serverPath;
    }

    public File getServerJarPath() {
        return serverJarPath;
    }

    public File getJavaExecutablePath() {
        return javaExecutablePath;
    }

    public String getServerLaunchArgs() {
        return serverLaunchArgs;
    }

    public int getServerId() {
        return serverId;
    }

    public File getWorldPath() {
        return worldPath;
    }

    public ServerPlatform getPlatform() {
        return platform;
    }

    public String getJavaVersion() {
        return javaVersion;
    }
}
