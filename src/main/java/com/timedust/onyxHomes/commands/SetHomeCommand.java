package com.timedust.onyxHomes.commands;

import com.timedust.onyxHomes.homes.HomeManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
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

        UUID uuid = player.getUniqueId();
        Location loc = player.getLocation();

        if (args.length == 0) {
            if (manager.hasHome(uuid, "home")) {
                player.sendMessage(mm.deserialize(
                        "<gray>⚡ <yellow>Точка <white><home></white> уже установлена.</gray><newline>" +
                                "<dark_gray>» </dark_gray>" +
                                "<green><bold><click:run_command:/sethome <home> confirm>[ПЕРЕЗАПИСАТЬ]</click></bold></green> " +
                                "<gray>или</gray> " +
                                "<red><bold><click:run_command:/cancel>[ОТМЕНА]</click></bold></red>",
                        Placeholder.parsed("home", "home")
                ));
                return true;
            }

            if (manager.getCountPlayersHome(uuid) < manager.getPlayerHomeLimit(uuid)) {
                manager.addHome(uuid, "home", loc);
                player.sendMessage(mm.deserialize("<green>Дом установлен!"));
                return true;
            } else {
                player.sendMessage(mm.deserialize("<red>Вы достигли лимита количества домов!"));
            }

        }

        if (args.length == 1) {
            String homeName = args[0];

            if (manager.hasHome(uuid, homeName)) {
                player.sendMessage(mm.deserialize(
                        "<gray>⚡ <yellow>Точка <white><home></white> уже установлена.</gray><newline>" +
                                "<dark_gray>» </dark_gray>" +
                                "<green><bold><click:run_command:/sethome <home> confirm>[ПЕРЕЗАПИСАТЬ]</click></bold></green> " +
                                "<gray>или</gray> " +
                                "<red><bold><click:run_command:/cancel>[ОТМЕНА]</click></bold></red>",
                        Placeholder.parsed("home", homeName)
                ));
                return true;
            }

            if (manager.getCountPlayersHome(uuid) < manager.getPlayerHomeLimit(uuid)) {
                manager.addHome(uuid, homeName, loc);
                player.sendMessage(mm.deserialize("<green>Дом <aqua><home></aqua> установлен!", Placeholder.parsed("home", homeName)));
                return true;
            } else {
                player.sendMessage(mm.deserialize("<red>Вы достигли лимита количества домов!"));
            }
        }

        if (args.length == 2 && args[1].equalsIgnoreCase("confirm")) {
            String homeName = args[0];

            manager.addHome(uuid, homeName, loc);

            player.sendMessage(mm.deserialize("<green>Дом <aqua>home</aqua> установлен!", Placeholder.parsed("home", homeName)));
            return true;
        }
        return false;
    }
}
