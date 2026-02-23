package com.timedust.onyxHomes.commands;

import com.timedust.onyxHomes.OnyxHomes;
import com.timedust.onyxHomes.data.PluginConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ConfigurationCommand implements CommandExecutor, TabCompleter {

    private final OnyxHomes plugin;
    private final PluginConfig config;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public ConfigurationCommand(OnyxHomes plugin, PluginConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        switch (args[0]) {
            case "reload" -> {
                plugin.reloadPluginConfig();
                sender.sendMessage("Конфигурация плагина успешно перезагружена!");
            }
            case "interval" -> {
                if (args[1].equalsIgnoreCase("get")) {
                    sender.sendMessage(mm.deserialize("Текущий интервал авто-сохранения: <interval>.",
                            Placeholder.parsed("interval", Integer.toString(config.getInterval()))));
                    return true;
                }
                if (args[1].equalsIgnoreCase("set")) {
                    try {
                        int newValue = Integer.parseInt(args[2]);
                        if (config.getInterval() == newValue) {
                            sender.sendMessage("Текущее значение параметра не отличается от нового");
                            return true;
                        }
                        config.setInterval(newValue);
                        plugin.startAutoSaveTask();
                        sender.sendMessage(mm.deserialize("Новый интервал авто-сохранения: <new-value>.",
                                Placeholder.parsed("new-value", Integer.toString(newValue))));
                        return true;
                    } catch (NumberFormatException e) {
                        sender.sendMessage(mm.deserialize("<bad-input> не является числом!",
                                Placeholder.parsed("bad-input", args[2])));
                        return true;
                    }
                }
            }
            case "auto-save" -> {
                if (args[1].equalsIgnoreCase("get")) {
                    sender.sendMessage(mm.deserialize("Текущее состояние авто-сохранения: <auto-save>.",
                            Placeholder.parsed("auto-save", Boolean.toString(config.isAutoSaveEnabled()))));
                    return true;
                }
                if (args[1].equalsIgnoreCase("set")) {
                    try {
                        boolean newValue = Boolean.parseBoolean(args[2]);
                        if (config.isAutoSaveEnabled() == newValue) {
                            sender.sendMessage("Текущее значение параметра не отличается от нового");
                            return true;
                        }
                        config.setAutoSaveEnabled(newValue);
                        plugin.startAutoSaveTask();
                        sender.sendMessage(mm.deserialize("Новое состояние авто-сохранения: <new-auto-save>",
                                Placeholder.parsed("new-auto-save", Boolean.toString(newValue))));
                    } catch (Exception e) {
                        sender.sendMessage(mm.deserialize("<bad-input> не является булевым значением!",
                                Placeholder.parsed("bad-input", args[2])));
                        return true;
                    }

                }
            }
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("reload", "interval", "auto-save");
        }

        switch (args[0]) {
            case "interval" -> {
                if (args.length == 2) {
                    return List.of("get", "set");
                }
            }
            case "auto-save" -> {
                if (args.length == 2) {
                    return List.of("get", "set");
                }
                if (args.length == 3 && args[1].equalsIgnoreCase("set")) {
                    return List.of("true", "false");
                }
            }
        }
        return List.of();
    }
}
