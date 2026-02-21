package com.timedust.onyxHomes.commands;

import com.timedust.onyxHomes.homes.HomeManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.UUID;

public class HomeCommand implements CommandExecutor, TabCompleter {

    private final MiniMessage mm = MiniMessage.miniMessage();
    private final HomeManager manager;

    public HomeCommand(HomeManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NonNull @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only for Players!");
        } else {
            if (args.length == 1) {
                UUID uuid = player.getUniqueId();
                String homeName = args[0];
                if (!manager.getHomeList(uuid).contains(homeName)) {
                    player.sendMessage(mm.deserialize("<red>Дом " + homeName + " не найден"));
                    return true;
                }
                player.teleport(manager.getLocationByName(player.getUniqueId(), homeName));
                return true;
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NonNull @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 1) {
                return manager.getHomeList(player.getUniqueId(), args[0]);
            }
        }
        return List.of();
    }
}
