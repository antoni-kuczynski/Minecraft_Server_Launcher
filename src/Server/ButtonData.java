package Server;

public record ButtonData(String buttonText, String pathToButtonIcon,
                         String pathToServerFolder, String pathToServerJarFile,
                         String pathToJavaRuntime, String serverLaunchArguments,
                         int serverId) {

    public String getButtonText() {
        return buttonText;
    }

    public String getPathToButtonIcon() {
        return pathToButtonIcon;
    }

    public String getPathToServerFolder() {
        return pathToServerFolder;
    }

    public String getPathToServerJarFile() {
        return pathToServerJarFile;
    }

    public String getPathToJavaRuntime() {
        return pathToJavaRuntime;
    }

    public String getServerLaunchArguments() {
        return serverLaunchArguments;
    }

    public int getServerId() {
        return serverId;
    }
}
