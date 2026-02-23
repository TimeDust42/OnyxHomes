package com.timedust.onyxHomes.teleport;

import com.timedust.onyxHomes.homes.Home;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportManager implements Listener {

    private final JavaPlugin plugin;

    private final MiniMessage mm = MiniMessage.miniMessage();

    private final Map<UUID, BukkitTask> teleportTasks = new HashMap<>();

    public TeleportManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void teleportWithDelay(Player player, Location location) {
        UUID uuid = player.getUniqueId();

        if (teleportTasks.containsKey(uuid)) {
            teleportTasks.get(uuid).cancel();
        }

        Location startLoc = player.getLocation();
        final int[] seconds = {3};

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!startLoc.getWorld().equals(player.getWorld()) || startLoc.distance(player.getLocation()) > 0.1) {
                player.showTitle(Title.title(mm.deserialize("<red>Отменено"), mm.deserialize("<gray>Вы сдвинулись")));
                stopTask(uuid);
                return;
            }

            if (seconds[0] <= 0) {
                player.teleport(location);
                player.showTitle(Title.title(mm.deserialize("<green>Успех"), Component.empty()));
                stopTask(uuid);
                return;
            }

            player.showTitle(Title.title(mm.deserialize("<yellow>Ждите..."), mm.deserialize("<gray>" + seconds[0])));
            seconds[0]--;
        }, 0L, 20L);

        teleportTasks.put(uuid, task);
    }

    private void stopTask(UUID uuid) {
        BukkitTask task = teleportTasks.remove(uuid);
        if (task != null) task.cancel();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        stopTask(event.getPlayer().getUniqueId());
    }
}
