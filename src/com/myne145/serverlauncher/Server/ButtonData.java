package com.myne145.serverlauncher.Server;

import java.io.File;

public record ButtonData(String serverName, File pathToServerButtonIcon,
                         File serverPath, File serverJarPath,
                         File javaRuntimePath, String serverLaunchArgs,
                         int serverId) {
}
