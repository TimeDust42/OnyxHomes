package com.timedust.onyxHomes;

import com.timedust.onyxHomes.commands.HomeCommand;
import com.timedust.onyxHomes.commands.SetHomeCommand;
import com.timedust.onyxHomes.config.ConfigManager;
import com.timedust.onyxHomes.homes.HomeManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Objects;

public final class OnyxHomes extends JavaPlugin {

    private HomeManager homeManager;
    private ConfigManager configManager;

    private BukkitTask autoSaveTask;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResource("homes.yml", false);

        homeManager = new HomeManager();
        configManager = new ConfigManager(this, homeManager);

        configManager.load();
        configManager.loadConfig();

        Objects.requireNonNull(getCommand("home")).setExecutor(new HomeCommand(homeManager));
        Objects.requireNonNull(getCommand("sethome")).setExecutor(new SetHomeCommand(homeManager));

        startAutoSaveTask();
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Сохранение домов...");
        configManager.save();
        configManager.saveConfig();

        if (autoSaveTask != null) {
            autoSaveTask.cancel();
        }
    }

    public void reloadPluginConfig() {
        if (autoSaveTask != null) {
            autoSaveTask.cancel();
        }

        this.reloadConfig();
        configManager.loadConfig();

        startAutoSaveTask();
    }

    public void startAutoSaveTask() {
        if (configManager.isAutoSaveEnabled()) {
            int interval = configManager.getInterval();
            autoSaveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
                configManager.save();
                this.getLogger().info("Бэкап домов");
            }, 20L * interval, 20L * interval);
        }
    }
}
