package com.timedust.onyxHomes.teleport;

import com.timedust.onyxHomes.data.PluginConfig;
import com.timedust.onyxHomes.homes.Home;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
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
    private final PluginConfig config;

    private final MiniMessage mm = MiniMessage.miniMessage();

    private final Map<UUID, BukkitTask> teleportTasks = new HashMap<>();

    public TeleportManager(JavaPlugin plugin, PluginConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    public void teleportWithDelay(Player player, Location location) {
        UUID uuid = player.getUniqueId();
        if (teleportTasks.containsKey(uuid)) teleportTasks.get(uuid).cancel();

        Location startLoc = player.getLocation();

        location.getChunk().load(true);

        final int[] ticksLeft = {60};

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!startLoc.getWorld().equals(player.getWorld()) || startLoc.distance(player.getLocation()) > 0.1) {
                player.showTitle(Title.title(config.getTeleportCancelOne(), config.getTeleportCancelTwo()));
                stopTask(uuid);
                return;
            }

            if (ticksLeft[0] % 20 == 0) {
                int secondsVisible = ticksLeft[0] / 20;

                if (secondsVisible > 0) {
                    player.showTitle(Title.title(config.getTeleportProcessOne(secondsVisible), config.getTeleportProcessTwo()));
                }
            }

            if (ticksLeft[0] <= 0) {
                player.teleportAsync(location).thenAccept(success -> {
                    if (success) {
                        player.showTitle(Title.title(config.getTeleportSuccessOne(), config.getTeleportSuccessTwo()));
                        location.getWorld().spawnParticle(Particle.END_ROD, location.clone().add(0, 1, 0), 30, 0.5, 0.5, 0.5, 0.1);
                    }
                });
                stopTask(uuid);
                return;
            }

            ticksLeft[0] -= 2;
        }, 0L, 2L);

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
