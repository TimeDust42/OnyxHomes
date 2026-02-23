package com.timedust.onyxHomes.data;

import com.timedust.onyxHomes.homes.Home;
import com.timedust.onyxHomes.homes.HomeManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class ConfigManager {

    private final JavaPlugin plugin;
    private final HomeManager manager;
    private final File file;

    public ConfigManager(JavaPlugin plugin, HomeManager manager) {
        this.plugin = plugin;
        this.manager = manager;
        file = new File(plugin.getDataFolder(), "homes.yml");
    }

    public void saveHomes() {
        Map<UUID, Map<String, Home>> snapshot = manager.getSnapshot();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            FileConfiguration config = new YamlConfiguration();
            snapshot.forEach((uuid, homes) -> {
                homes.forEach((name, home) -> {
                    config.set("homes." + uuid + "." + name, home.getLocation());
                });
            });
            try {
                config.save(file);
            } catch (IOException e) {
                plugin.getLogger().severe("Could not save homes.yml!");
            }
        });
    }


    public void loadHomes() {
        var homeList = manager.getHomeList();

        homeList.clear();

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        ConfigurationSection homeSection = config.getConfigurationSection("homes");
        if (homeSection == null) return;

        for (String rawUuid : homeSection.getKeys(false)) {
            ConfigurationSection userSection = homeSection.getConfigurationSection(rawUuid);
            if (userSection == null) continue;

            for (String homeName : userSection.getKeys(false)) {
                Location loc = userSection.getLocation(homeName);

                if (loc != null) {
                    UUID uuid = UUID.fromString(rawUuid);
                    manager.addHome(uuid, homeName, loc);
                }
            }
        }
    }
}
