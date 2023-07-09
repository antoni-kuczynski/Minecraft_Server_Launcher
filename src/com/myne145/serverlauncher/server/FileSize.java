package com.myne145.serverlauncher.server;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.text.DecimalFormat;

public class FileSize {
    private final String size;
    private final String unit;
    private static final DecimalFormat unitRound = new DecimalFormat("###.##");

    private FileSize(String size, String unit) {
        this.size = size;
        this.unit = unit;
    }

    public static FileSize directorySizeWithConverion(File directory) {
        long SIZE_IN_BYTES = FileUtils.sizeOfDirectory(directory);
        double ONE_KILOBYTE = 1024;
        double ONE_MEGABYTE = 1048576;

        double finalSize = SIZE_IN_BYTES;
        String unitSymbol = "b";
        double ONE_GIGABYTE = 1073741824;
        if (SIZE_IN_BYTES >= ONE_KILOBYTE && SIZE_IN_BYTES < ONE_MEGABYTE) {
            finalSize = SIZE_IN_BYTES / ONE_KILOBYTE;
            unitSymbol = "KiB";
        } else if (SIZE_IN_BYTES >= ONE_MEGABYTE && SIZE_IN_BYTES < ONE_GIGABYTE) {
            finalSize = SIZE_IN_BYTES / ONE_MEGABYTE;
            unitSymbol = "MiB";
        } else if (SIZE_IN_BYTES >= ONE_GIGABYTE) {
            finalSize = SIZE_IN_BYTES / ONE_GIGABYTE;
            unitSymbol = "GiB";
        }
        return new FileSize(unitRound.format(finalSize), unitSymbol);
    }

    public String getSize() {
        return size;
    }

    public String getUnit() {
        return unit;
    }

    public String getText() {
        return size + unit;
    }
}
