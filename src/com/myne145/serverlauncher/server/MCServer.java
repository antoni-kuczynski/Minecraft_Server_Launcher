package com.myne145.serverlauncher.server;

import java.io.File;

public record MCServer(String serverName,
                       File serverPath, File serverJarPath,
                       File javaRuntimePath, String serverLaunchArgs,
                       int serverId, File worldPath) {
}
