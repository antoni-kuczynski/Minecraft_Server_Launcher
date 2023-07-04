package com.myne145.serverlauncher.Server;

public record FileSize(String size, String unit) {

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
