package com.myne145.serverlauncher.server;

//import com.myne145.serverlauncher.utils.AlertType;

import com.myne145.serverlauncher.gui.window.Window;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import static com.myne145.serverlauncher.gui.window.Window.showErrorMessage;
//import static com.myne145.serverlauncher.gui.window.Window.getErrorDialogMessage;

public class MinecraftServer {
    private String serverName;
    private File serverPath;
    private File serverJarPath;
    private File javaExecutablePath;
    private String serverLaunchArgs;
    private int serverId;
    private File worldPath;
    private final LinkedHashMap<String, String> properties = new LinkedHashMap<>();
    private ServerPlatform platform;
    private String javaVersion;
    private String minecraftVersion;
    private MinecraftWorld serverWorld;

    public MinecraftServer(String serverName, File serverPath, File serverJarPath, String javaExecutablePath, String serverLaunchArgs, int serverId) {
        this.serverName = serverName;
        this.serverPath = serverPath;
        this.serverJarPath = serverJarPath;

        if (javaExecutablePath.equalsIgnoreCase("java")) {
            this.javaExecutablePath = Config.getDefaultJava();
        } else {
            this.javaExecutablePath = new File(javaExecutablePath);
        }

        this.serverLaunchArgs = serverLaunchArgs;
        this.serverId = serverId;

        updateWorldPath();
        try {
            updateProperties();
        } catch (IOException e) {
            showErrorMessage("I/O error reading server.properties file.", e);
        }
        platform = getPlatformFromManifestFile();
        minecraftVersion = getMinecraftServerVersion();
        serverWorld = new MinecraftWorld(this);
    }

    public MinecraftServer() {
        if (Config.getData().isEmpty())
            this.serverId = 1;
        else
            this.serverId = Config.getData().size() + 1; //top tier code right here
//            this.serverId = Config.getData().get(Config.getData().size() - 1).getServerId() + 1;
    }

    private String getVersionFromVersionJSONFile() {
        try (JarFile jarFile = new JarFile(this.serverJarPath.getAbsolutePath())) {
            ZipEntry entry = jarFile.getEntry("version.json");

            if (entry == null) {
                return "Unknown";
            }

            try (InputStream inputStream = jarFile.getInputStream(entry);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

                StringBuilder jsonContent = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonContent.append(line);
                }

                JSONObject jsonObject = new JSONObject(jsonContent.toString());
                return jsonObject.getString("id"); // assuming "id" contains the version
            }
        } catch (IOException e) {
            return "Unknown";
        }
    }

    private String getVersionFromLatestLog() throws FileNotFoundException {
        File logPath = new File(serverPath.getAbsolutePath() + "/logs/latest.log");
        if (!logPath.exists())
            return "Unknown";

        Scanner sc = new Scanner(logPath);
        String line;
        int index = 0;
        while (sc.hasNextLine() && index < 50) {
            index++;
            line = sc.nextLine();
            if (!line.contains("Starting minecraft server version"))
                continue;

            Pattern versionPattern = Pattern.compile("version\\s+([0-9]+\\.[0-9]+(\\.[0-9]+)?)");
            Matcher matcher = versionPattern.matcher(line);

            if (matcher.find()) {
                return matcher.group(1);
            } else {
                return "Unknown";
            }
        }
        return "Unknown";
    }

    private String getMinecraftServerVersion() {
        String version;
        //check for jar' version.json file if there isn't one skip
        version = getVersionFromVersionJSONFile();

        //check for latest.log file
        try {
            version = getVersionFromLatestLog();
        } catch (FileNotFoundException e) {
            version = "Unknown";
        }

        return version;
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

                if (key.equalsIgnoreCase("Main-Class")) {
                    mainClass = value.toLowerCase();
                } else if (key.equalsIgnoreCase("Implementation-Version")) {
                    implementationVersion = value.toLowerCase();
                }
            }
            jarFile.close();
        } catch (IOException e) {
            showErrorMessage("I/O error reading " + serverJarPath.getName() + " file, when trying to obtain server's version.", e);
        }

        if (mainClass.contains("papermc")) {
            return ServerPlatform.PAPER_MC;
        } else if (mainClass.contains("fml")) {
            return ServerPlatform.FORGE;
        } else if (mainClass.contains("bukkit") &&
                (implementationVersion.isEmpty() || implementationVersion.contains("bukkit"))) {
            return ServerPlatform.BUKKIT;
        } else if (mainClass.contains("bukkit") &&
                implementationVersion.contains("spigot")) {
            return ServerPlatform.SPIGOT;
        } else if (mainClass.contains("minecraftserver")) {
            return ServerPlatform.VANILLA;
        } else if (mainClass.contains("fabricmc")) {
            return ServerPlatform.FABRIC;
        }

        return ServerPlatform.UNKNOWN;
    }


    public void updateWorldPath() {
        String worldName;
        if (!properties.containsKey("level-name")) {
            worldName = "world";
        } else {
            worldName = getProperty("level-name");
        }

        worldPath = new File(serverPath.getAbsolutePath() + "/" + worldName);
        if (!worldPath.exists() && !worldPath.mkdirs()) {
            showErrorMessage("Cannot create " + worldPath.getAbsolutePath() + " world directory.", new FileSystemException(worldPath.getAbsolutePath()));
        }
    }

    private String getJavaVersionFromReleaseFile() throws IOException {
        File javaPath = getJavaExecutablePath();
        String javaVersion = "Unknown";
        if (javaPath == null || javaPath.getParentFile().list() == null || javaPath.getParentFile().list().length == 0)
            return javaVersion;

        if (javaPath.isFile())
            javaPath = javaPath.getParentFile();

        if (javaPath.list() == null)
            return javaVersion;

        List<String> files = Arrays.asList(javaPath.list());
        if (!files.contains("release") && javaPath.getParentFile() != null && javaPath.getParentFile().list() != null) {
            files = Arrays.asList(javaPath.getParentFile().list()); //one folder back if user selected the "bin" folder
            javaPath = javaPath.getParentFile();
        }


        if (!files.contains("release"))
            return javaVersion;


        ArrayList<String> content = (ArrayList<String>) Files.readAllLines(new File(javaPath.getAbsolutePath() + "/release").toPath());
        for (String s : content) {
            if (s.split("=")[0].equals("JAVA_VERSION")) {
                javaVersion = s.split("=")[1].replace("\"", "");
            }
        }

        return javaVersion;
    }

    private void updateProperties() throws IOException {
        File propertiesFile = new File(serverPath.getAbsolutePath() + "/server.properties");
        if (!propertiesFile.exists())
            return;
        ArrayList<String> content = (ArrayList<String>) Files.readAllLines(propertiesFile.toPath());
        if (content.isEmpty())
            return;

        for (String s : content) {
            if (s.startsWith("#"))
                continue;
            String[] lineSplitted = s.split("=");
            String value = getValue(lineSplitted);
            properties.put(lineSplitted[0], value);

        }
    }

    private static String getValue(String[] lineSplitted) {
        StringBuilder value = new StringBuilder();
        if (lineSplitted.length == 2) {
            value.append(lineSplitted[1]);
        } else if (lineSplitted.length > 2) {
            for (int i = 1; i < lineSplitted.length; i++) {
                value.append("=").append(lineSplitted[i]);
            }
        }
        return value.toString();
    }

    public String getProperty(String key) {
        if (!properties.containsKey(key))
            return "";

        return properties.get(key);
    }

    private static JSONObject getJSONObject(MinecraftServer server) {
        JSONObject object = new JSONObject();
        object.put("serverName", server.serverName);
        object.put("pathToServerJarFile", server.serverJarPath);
        object.put("pathToJavaRuntimeExecutable", server.javaExecutablePath);
        object.put("launchArgs", server.serverLaunchArgs);
        object.put("tabIndex", server.serverId);
        return object;
    }

    public static void writeAllToConfig() {
        JSONArray array = new JSONArray();
        for (MinecraftServer server : Config.getData()) {
            JSONObject object = getJSONObject(server);
            array.put(object);
        }

        try (FileWriter writer = new FileWriter(Config.ABSOLUTE_PATH)) {
            writer.write("");
            writer.write(array.toString(4));
        } catch (IOException e) {
            Window.showErrorMessage("I/O error writing the server to config. Server will not be added.", e);
        }
    }

    public boolean isComplete() {
        return (serverName != null && serverPath != null
                && serverJarPath != null && javaExecutablePath != null
                && serverLaunchArgs != null) &&
                !serverName.isEmpty();
    }

    public boolean hasServerProperties() {
        return !properties.isEmpty();
    }

    public String getAbbreviatedName(int maxChars) {
        if (serverName.length() < maxChars)
            return serverName;
        return serverName.substring(0, maxChars - 3) + "...";
    }

    public String getName() {
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

    public String getMinecraftVersion() {
        return minecraftVersion;
    }

    public void setServerId(int id) {
        this.serverId = id;
    }

    public void setServerLaunchArgs(String serverLaunchArgs) {
        this.serverLaunchArgs = "nogui " + serverLaunchArgs.replace("nogui", "");
    }

    public MinecraftWorld getServerWorld() {
        return serverWorld;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public void setServerJarPath(File serverJarPath) {
        this.serverJarPath = serverJarPath;
        this.serverPath = serverJarPath.getParentFile();
        this.platform = getPlatformFromManifestFile();
        this.minecraftVersion = getMinecraftServerVersion();
        try {
            updateProperties();
        } catch (IOException e) {
            showErrorMessage("Cannot read " + serverName + " server.properties file.", e);
        }
        updateWorldPath();
        serverWorld = new MinecraftWorld(this);
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

    public File getTempLevelDat() {
        File levelDat = new File(this.getWorldPath().getAbsolutePath() + "/level.dat");
//        File tempLevelDatFile = new File("world_temp/level_" + "server_id_" + this.getServerId() + ".dat");
//
//        try {
//            try (FileChannel sourceChannel = FileChannel.open(levelDat.toPath(), StandardOpenOption.READ);
//                 FileChannel destChannel = FileChannel.open(tempLevelDatFile.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
//                sourceChannel.transferTo(0, sourceChannel.size(), destChannel);
//            }
//
//            return tempLevelDatFile;
//        } catch (IOException e) {
//            return levelDat;
//        }
//    }
        return levelDat;
    }
}
