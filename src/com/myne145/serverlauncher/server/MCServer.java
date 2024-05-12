package com.myne145.serverlauncher.server;

import com.myne145.serverlauncher.gui.window.Window;
import com.myne145.serverlauncher.utils.AlertType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import static com.myne145.serverlauncher.gui.window.Window.alert;
import static com.myne145.serverlauncher.gui.window.Window.getErrorDialogMessage;

public class MCServer {
    private String serverName;
    private File serverPath;
    private File serverJarPath;
    private File javaRuntimePath;
    private String serverLaunchArgs;
    private int serverId;
    private File worldPath;
    private final LinkedHashMap<String, String> properties = new LinkedHashMap<>();
    private ServerPlatform platform;

    public boolean isComplete() {
        return serverName != null && serverPath != null
                && serverJarPath != null && javaRuntimePath != null
                && serverLaunchArgs != null;
    }

    public MCServer() {
        this.serverId = Config.getData().size() + 1;
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
        } catch (Exception e) {
            alert(AlertType.ERROR, getErrorDialogMessage(e));
        }

    }

    public void setJavaRuntimePath(File javaRuntimePath) {
        this.javaRuntimePath = javaRuntimePath;
    }

    public void setServerLaunchArgs(String serverLaunchArgs) {

        this.serverLaunchArgs = "nogui" + serverLaunchArgs.replace("nogui", "");
    }


    public MCServer(String serverName, File serverPath, File serverJarPath, String javaRuntimePath, String serverLaunchArgs, int serverId) {
        this.serverName = serverName;
        this.serverPath = serverPath;
        this.serverJarPath = serverJarPath;

        if(javaRuntimePath.equalsIgnoreCase("java")) {
            this.javaRuntimePath = Config.getDefaultJava();
        } else {
            this.javaRuntimePath = new File(javaRuntimePath);
        }

        this.serverLaunchArgs = serverLaunchArgs;
        this.serverId = serverId;

        updateWorldPath();
        try {
            updateProperties();
        } catch (Exception e) {
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
            alert(AlertType.ERROR, getErrorDialogMessage(e));
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

    private String getJavaVersion() throws IOException {
        File javaPath = getJavaRuntimePath();
        String javaVersion = "Unknown";
        if(javaPath == null || javaPath.getParentFile().list() == null || javaPath.getParentFile().list().length == 0)
            return javaVersion;

        if(javaPath.isFile())
            javaPath = javaPath.getParentFile();

        List<String> files = Arrays.asList(javaPath.list());
        while(!files.contains("release")) {
            files = Arrays.asList(javaPath.getParentFile().list());
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

    public String getServerName() {
        return serverName;
    }

    public File getServerPath() {
        return serverPath;
    }

    public File getServerJarPath() {
        return serverJarPath;
    }

    public File getJavaRuntimePath() {
        return javaRuntimePath;
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
}
