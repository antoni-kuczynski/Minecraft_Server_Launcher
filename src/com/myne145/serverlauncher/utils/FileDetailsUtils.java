package com.myne145.serverlauncher.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class FileDetailsUtils {
    private final String size;
    private final String unit;
    private static final DecimalFormat unitRound = new DecimalFormat("###.##");
    private static final double ONE_KILOBYTE = 1024;
    private static final double ONE_MEGABYTE = 1048576;

    private FileDetailsUtils(String size, String unit) {
        this.size = size;
        this.unit = unit;
    }

    public static FileDetailsUtils directorySizeWithConversion(File directory) {
        long SIZE_IN_BYTES = FileUtils.sizeOfDirectory(directory);
        return convertSize(SIZE_IN_BYTES);
    }

    public static FileDetailsUtils fileSizeWithConversion(File file) {
        long SIZE_IN_BYTES = FileUtils.sizeOf(file);
        return convertSize(SIZE_IN_BYTES);
    }

    private static FileDetailsUtils convertSize(long SIZE_IN_BYTES) {
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
        return new FileDetailsUtils(unitRound.format(finalSize), unitSymbol);
    }

    public static String abbreviate(String fileName, int maxLen) {
        if (fileName.length() <= maxLen)
            return fileName;

        File f = new File(fileName);
        ArrayList<String> coll = new ArrayList<String>();
        String name;
        StringBuffer begBuf = new StringBuffer();
        StringBuffer endBuf = new StringBuffer();
        int len;
        boolean b;

        while ((f != null) && (name = f.getName()) != null) {
            coll.add(0, name);
            f = f.getParentFile();
        }
        if (coll.isEmpty())
            return fileName;

        len = coll.size() << 1; // ellipsis character per subdir and filename, separator per subdir
        name = (String) coll.remove(coll.size() - 1);
        endBuf.insert(0, name);
        len += name.length();
        if (!coll.isEmpty()) {
            name = (String) coll.remove(0);
            begBuf.append(name);
            begBuf.append(File.separator);
            len += name.length() - 1;
        }
        if (!coll.isEmpty()) {
            name = (String) coll.remove(0);
            if (name.equals("Volumes")) { // ok dis wan me don want
                begBuf.append('\u2026');
                begBuf.append(File.separator);
            } else {
                begBuf.append(name);
                begBuf.append(File.separator);
                len += name.length() - 1;
            }
        }
        for (b = true; !coll.isEmpty() && len <= maxLen; b = !b) {
            if (b) {
                name = (String) coll.remove(coll.size() - 1);
                endBuf.insert(0, File.separator);
                endBuf.insert(0, name);
            } else {
                name = (String) coll.remove(0);
                begBuf.append(name);
                begBuf.append(File.separator);
            }
            len += name.length() - 1; // full name instead of single character ellipsis
        }

        while (!coll.isEmpty()) {
            coll.remove(0);
            begBuf.append('\u2026');
            begBuf.append(File.separator);
        }

        return (begBuf.append(endBuf).toString());
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
