package com.myne145.serverlauncher.server;

public enum ServerPlatform {
    PAPER_MC,
    FORGE,
    BUKKIT,
    SPIGOT,
    VANILLA,
    FABRIC,
    UNKNOWN;


    @Override
    public String toString() {
        switch (this) {
            case PAPER_MC -> {
                return "PaperMC server";
            }
            case FORGE -> {
                return "Forge server";
            }
            case BUKKIT -> {
                return "CraftBukkit server";
            }
            case SPIGOT -> {
                return "Spigot server";
            }
            case VANILLA -> {
                return "Vanilla server";
            }
            case FABRIC -> {
                return "Fabric server";
            }
        }
        return "Unknown platform";
    }
}
