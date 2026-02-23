package com.timedust.onyxHomes.commands;

import com.timedust.onyxHomes.homes.HomeManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class DeleteHomeCommand implements CommandExecutor, TabCompleter {

    private final MiniMessage mm = MiniMessage.miniMessage();
    private final HomeManager manager;

    public DeleteHomeCommand(HomeManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (args.length == 0) {
            player.sendMessage(mm.deserialize("<red>Использование: /delhome <название>"));
            return true;
        }

        String homeName = args[0];
        UUID uuid = player.getUniqueId();

        if (!manager.hasHome(uuid, homeName)) {
            player.sendMessage(mm.deserialize("<red>Дом <white><home></white> не найден!",
                    Placeholder.parsed("home", homeName)));
            return true;
        }

        if (args.length == 2 && args[1].equalsIgnoreCase("confirm")) {
            manager.deleteHome(uuid, homeName);
            player.sendMessage(mm.deserialize("<green>Дом <white><home></white> успешно удален.",
                    Placeholder.parsed("home", homeName)));
            return true;
        }

        player.sendMessage(mm.deserialize(
                "<gray>⚡ Вы уверены, что хотите удалить дом <white><home></white>?</gray><newline>" +
                        "<dark_gray>» </dark_gray>" +
                        "<red><bold><click:run_command:/delhome <home> confirm>[УДАЛИТЬ]</click></bold></red> " +
                        "<gray>эта операция необратима.</gray>",
                Placeholder.parsed("home", homeName)
        ));

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 1) {
                return manager.getHomeList(player.getUniqueId(), args[0]);
            }
            if (args.length == 2) {
                return List.of("confirm");
            }
        }
        return List.of();
    }
}
