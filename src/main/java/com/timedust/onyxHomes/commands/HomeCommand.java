package com.timedust.onyxHomes.commands;

import com.timedust.onyxHomes.homes.HomeManager;
import com.timedust.onyxHomes.teleport.TeleportManager;
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
    private final HomeManager homeManager;
    private final TeleportManager teleportManager;

    public HomeCommand(HomeManager homeManager, TeleportManager teleportManager) {
        this.homeManager = homeManager;
        this.teleportManager = teleportManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NonNull @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only for Players!");
            return true;
        }

        if (args.length == 0) {
            teleport(player, null);
        }

        if (args.length == 1) {
            teleport(player, args[0]);
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NonNull @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 1) {
                return homeManager.getHomeList(player.getUniqueId(), args[0]);
            }
        }
        return List.of();
    }

    private void teleport(Player player, String homeName) {
        UUID uuid = player.getUniqueId();

        if (homeName == null) {
            homeName = "home";
        }

        if (!homeManager.getHomeList(uuid).contains(homeName)) {
            player.sendMessage(mm.deserialize("<red>Дом " + homeName + " не найден"));
            return;
        }

        teleportManager.teleportWithDelay(player, homeManager.getHomeList().get(uuid).get(homeName).getLocation());
    }
}
