package com.timedust.onyxHomes.data;

import org.bukkit.plugin.java.JavaPlugin;

public class Config {

    private final JavaPlugin plugin;

    private boolean isAutoSaveEnabled;
    private int interval;

    public Config(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        plugin.reloadConfig();

        isAutoSaveEnabled = plugin.getConfig().getBoolean("auto-save.enabled", true);
        interval = plugin.getConfig().getInt("auto-save.interval", 300);
    }

    public void saveConfig() {
        plugin.getConfig().set("auto-save.enabled", isAutoSaveEnabled);
        plugin.getConfig().set("auto-save.interval", interval);

        plugin.saveConfig();
    }

    public boolean isAutoSaveEnabled() {
        return isAutoSaveEnabled;
    }

    public void setAutoSaveEnabled(boolean autoSaveEnabled) {
        isAutoSaveEnabled = autoSaveEnabled;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
}
