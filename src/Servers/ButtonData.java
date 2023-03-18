package Servers;

public class ButtonData {
    private final String buttonText;
    private final String pathToButtonIcon;
    private final String pathToServerFolder;
    private final String pathToServerJarFile;
    private final String pathToJavaRuntime;
    private final String serverLaunchArguments;

    public ButtonData(String buttonText, String pathToButtonIcon, String pathToServerFolder, String pathToServerJarFile, String pathToJavaRuntime, String serverLaunchArguments) {
        this.buttonText = buttonText;
        this.pathToButtonIcon = pathToButtonIcon;
        this.pathToServerFolder = pathToServerFolder;
        this.pathToServerJarFile = pathToServerJarFile;
        this.pathToJavaRuntime = pathToJavaRuntime;
        this.serverLaunchArguments = serverLaunchArguments;
    }

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
}
