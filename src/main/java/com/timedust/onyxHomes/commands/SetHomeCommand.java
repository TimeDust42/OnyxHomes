package com.timedust.onyxHomes.commands;

import com.timedust.onyxHomes.homes.HomeManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

public class SetHomeCommand implements CommandExecutor {

    private final MiniMessage mm = MiniMessage.miniMessage();
    private final HomeManager manager;

    public SetHomeCommand(HomeManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NonNull @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Только для игроков!");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(mm.deserialize("<red> Использование: /sethome [home_name]"));
            return true;
        }

        if (args.length == 1) {
            UUID uuid = player.getUniqueId();
            String homeName = args[0];
            Location loc = player.getLocation();

            manager.addHome(uuid, homeName, loc);

            player.sendMessage(mm.deserialize("<green>Дом " + homeName + " установлен!"));
            return true;
        }
        return false;
    }
}
