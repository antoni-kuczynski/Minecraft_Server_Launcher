package com.myne145.serverlauncher.utils;

public enum ButtonWarning {
    NOT_A_MINECRAFT_WORLD("Not a Minecraft world"),
    LARGER_THAN_1GIB("Larger than 1GiB"),
    NO_DEFAULT_JAVA("No default Java installation found"),
    NOT_A_JAR_FILE("Not a jar file");
    private final String text;

    ButtonWarning(String text) {
        this.text = text;
    }


    @Override
    public String toString() {
        return text;
    }
}
