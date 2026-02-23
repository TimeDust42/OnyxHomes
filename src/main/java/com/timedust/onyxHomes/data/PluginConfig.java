package com.timedust.onyxHomes.data;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginConfig {

    private final JavaPlugin plugin;

    private boolean isAutoSaveEnabled;
    private int interval;

    private Component teleportSuccessOne;
    private Component teleportSuccessTwo;
    private Component teleportCancelOne;
    private Component teleportCancelTwo;
    private String teleportProcessOne;
    private String teleportProcessTwo;

    public PluginConfig(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        plugin.reloadConfig();
        FileConfiguration cfg = plugin.getConfig();

        isAutoSaveEnabled = cfg.getBoolean("auto-save.enabled", true);
        interval = cfg.getInt("auto-save.interval", 300);

        teleportSuccessOne = parse(cfg.getString("messages.teleport.success.line1", "<green>Успех!"));
        teleportSuccessTwo = parse(cfg.getString("messages.teleport.success.line2", ""));

        teleportCancelOne = parse(cfg.getString("messages.teleport.cancel.line1", "<red>Телепортация отменена"));
        teleportCancelTwo = parse(cfg.getString("messages.teleport.cancel.line2", "<gray>Вы сдвинулись"));

        teleportProcessOne = cfg.getString("messages.teleport.process.line1", "<yellow>Телепортация через <time>...");
        teleportProcessTwo = cfg.getString("messages.teleport.process.line2", "<gray>Не двигайтесь");
    }

    public void saveConfig() {
        plugin.getConfig().set("auto-save.enabled", isAutoSaveEnabled);
        plugin.getConfig().set("auto-save.interval", interval);

        plugin.saveConfig();
    }

    private Component parse(String msg) {
        return MiniMessage.miniMessage().deserialize(msg != null ? msg : "");
    }

    public boolean isAutoSaveEnabled() {
        return isAutoSaveEnabled;
    }

    public void setAutoSaveEnabled(boolean autoSaveEnabled) {
        isAutoSaveEnabled = autoSaveEnabled;
        saveConfig();
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
        saveConfig();
    }

    public Component getTeleportSuccessOne() {
        if (teleportSuccessOne == null) return Component.empty();
        return teleportSuccessOne;
    }

    public Component getTeleportSuccessTwo() {
        if (teleportSuccessTwo == null) return Component.empty();
        return teleportSuccessTwo;
    }

    public Component getTeleportCancelOne() {
        if (teleportCancelOne == null) return Component.empty();
        return teleportCancelOne;
    }

    public Component getTeleportCancelTwo() {
        if (teleportCancelTwo == null) return Component.empty();
        return teleportCancelTwo;
    }

    public Component getTeleportProcessOne(int timeToTeleport) {
        return MiniMessage.miniMessage().deserialize(teleportProcessOne,
                Placeholder.unparsed("time", String.valueOf(timeToTeleport)));
    }

    public Component getTeleportProcessTwo() {
        return MiniMessage.miniMessage().deserializeOrNull(teleportProcessTwo);
    }
}
