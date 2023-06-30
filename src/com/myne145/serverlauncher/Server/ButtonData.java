package com.myne145.serverlauncher.Server;

import java.io.File;

public record ButtonData(String serverName, File pathToServerButtonIcon,
                         File serverPath, File serverJarPath,
                         File javaRuntimePath, String serverLaunchArgs,
                         int serverId) {

//    public String getServerName() {
//        return serverName;
//    }
//
//    public String getServerIcon() {
//        return pathToServerButtonIcon;
//    }
//
//    public String getServerPath() {
//        return serverPath;
//    }
//
//    public String getPathToServerJarFile() {
//        return serverJarPath;
//    }
//
//    public String getPathToJavaRuntime() {
//        return javaRuntimePath;
//    }
//
//    public String getServerLaunchArguments() {
//        return serverLaunchArgs;
//    }
//
//    public int getServerId() {
//        return serverId;
//    }
}
