package com.timedust.onyxHomes.config;

import com.timedust.onyxHomes.homes.HomeManager;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class ConfigManager {

    private final JavaPlugin plugin;
    private final HomeManager manager;
    private final File file;

    private boolean isAutoSaveEnabled;
    private int interval;

    public ConfigManager(JavaPlugin plugin, HomeManager manager) {
        this.plugin = plugin;
        this.manager = manager;
        file = new File(plugin.getDataFolder(), "homes.yml");
    }

    public void save() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        var homeList = manager.getHomeList();

        config.set("homes", null);

        for (UUID uuid : homeList.keySet()) {
            for (String homeName : homeList.get(uuid).keySet()) {
                String path = "homes." + uuid.toString() + "." + homeName;
                config.set(path, homeList.get(uuid).get(homeName).getLocation());
            }
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        var homeList = manager.getHomeList();

        homeList.clear();

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        ConfigurationSection homeSection = config.getConfigurationSection("homes");
        if (homeSection == null) return;

        for (String rawUuid : homeSection.getKeys(false)) {
            ConfigurationSection userSection = homeSection.getConfigurationSection(rawUuid);
            if (userSection == null) continue;

            for (String homeName : userSection.getKeys(false)) {
                String path = "homes." + rawUuid + "." + homeName;
                Location loc = config.getLocation(path);

                if (loc != null) {
                    UUID uuid = UUID.fromString(rawUuid);
                    manager.addHome(uuid, homeName, loc);
                }
            }
        }
    }

    public void loadConfig() {
        isAutoSaveEnabled = plugin.getConfig().getBoolean("auto-save.enable");
        interval = plugin.getConfig().getInt("auto-save.interval");
    }

    public void saveConfig() {
        plugin.getConfig().set("auto-save.enable", isAutoSaveEnabled);
        plugin.getConfig().set("auto-save.interval", interval);
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
