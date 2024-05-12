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
                return "PaperMC";
            }
            case FORGE -> {
                return "Forge";
            }
            case BUKKIT -> {
                return "CraftBukkit";
            }
            case SPIGOT -> {
                return "Spigot";
            }
            case VANILLA -> {
                return "Vanilla";
            }
            case FABRIC -> {
                return "Fabric";
            }
        }
        return "Unknown platform";
    }
}
