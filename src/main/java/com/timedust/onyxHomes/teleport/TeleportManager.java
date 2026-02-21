package com.timedust.onyxHomes.teleport;

import com.timedust.onyxHomes.homes.Home;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportManager {

    private final JavaPlugin plugin;

    private final Map<UUID, BukkitTask> teleportTasks = new HashMap<>();

    private BukkitTask teleportTask;

    public TeleportManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void teleportWithDelay(Player player, Home home) {

        Location playerLocation = player.getLocation();
        final int[] seconds = {3};

        teleportTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (playerLocation.distance(player.getLocation()) > 0.1) {
                teleportTask.cancel();
            }

            if (seconds[0] > 0) {
                player.showTitle(Title.title(Component.text("Не двигайтесь!"), Component.text("Телепортация через " + seconds[0])));
            }

            if (seconds[0] == 0) {
                player.showTitle(Title.title(Component.text("Внимание"), Component.text("Телепортация успешна!")));
                player.teleport(home.getLocation());
                teleportTask.cancel();
            }

            seconds[0]--;

        }, 0L, 20L);
    }

}
