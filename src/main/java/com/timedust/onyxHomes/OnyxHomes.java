package com.timedust.onyxHomes;

import com.timedust.onyxHomes.commands.ConfigurationCommand;
import com.timedust.onyxHomes.commands.DeleteHomeCommand;
import com.timedust.onyxHomes.commands.HomeCommand;
import com.timedust.onyxHomes.commands.SetHomeCommand;
import com.timedust.onyxHomes.data.PluginConfig;
import com.timedust.onyxHomes.data.ConfigManager;
import com.timedust.onyxHomes.homes.HomeManager;
import com.timedust.onyxHomes.teleport.TeleportManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.Objects;

public final class OnyxHomes extends JavaPlugin {

    private HomeManager homeManager;
    private PluginConfig pluginConfig;
    private ConfigManager configManager;
    private TeleportManager teleportManager;
    private File homesConfig;

    private BukkitTask autoSaveTask;

    @Override
    public void onEnable() {
        homesConfig = new File(getDataFolder(), "homes.yml");

        saveDefaultConfig();
        saveDefaultHomeList();

        homeManager = new HomeManager();
        pluginConfig = new PluginConfig(this);
        configManager = new ConfigManager(this, homeManager);
        teleportManager = new TeleportManager(this, pluginConfig);

        pluginConfig.loadConfig();
        configManager.loadHomes();

        getServer().getPluginManager().registerEvents(teleportManager, this);

        Objects.requireNonNull(getCommand("onyxhomes")).setExecutor(new ConfigurationCommand(this, pluginConfig));
        Objects.requireNonNull(getCommand("home")).setExecutor(new HomeCommand(homeManager, teleportManager));
        Objects.requireNonNull(getCommand("sethome")).setExecutor(new SetHomeCommand(homeManager));
        Objects.requireNonNull(getCommand("delhome")).setExecutor(new DeleteHomeCommand(homeManager));

        startAutoSaveTask();
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Сохранение домов...");
        pluginConfig.saveConfig();
        configManager.saveHomes();

        if (autoSaveTask != null) {
            autoSaveTask.cancel();
        }
    }

    private void saveDefaultHomeList() {
        if (!homesConfig.exists()) {
            saveResource("homes.yml", false);
        }
    }

    public void reloadPluginConfig() {
        if (autoSaveTask != null) {
            autoSaveTask.cancel();
        }

        this.reloadConfig();
        pluginConfig.loadConfig();

        startAutoSaveTask();
    }

    public void startAutoSaveTask() {
        if (pluginConfig.isAutoSaveEnabled()) {
            this.getLogger().info("Auto-save started");
            int interval = pluginConfig.getInterval();
            autoSaveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
                configManager.saveHomes();
                this.getLogger().info("Бэкап домов");
            }, 20L * interval, 20L * interval);
        }
    }
}
