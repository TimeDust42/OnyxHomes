package com.timedust.onyxHomes.homes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HomeManager {

    private final Map<UUID, Map<String, Home>> homeList = new ConcurrentHashMap<>();

    public Map<UUID, Map<String, Home>> getSnapshot() {
        Map<UUID, Map<String, Home>> snapshot = new HashMap<>();
        homeList.forEach((uuid, homes) -> snapshot.put(uuid, new HashMap<>(homes)));
        return snapshot;
    }

    public void addHome(UUID uuid, String homeName, Location location) {
        homeList.computeIfAbsent(uuid, k -> new ConcurrentHashMap<>())
                .put(homeName, new Home(homeName, location));
    }

    public void deleteHome(UUID uuid, String homeName) {
        homeList.computeIfPresent(uuid, (id, houses) -> {
            houses.remove(homeName);
            return houses.isEmpty() ? null : houses;
        });
    }

    public boolean hasHome(UUID uuid, String name) {
        Map<String, Home> userHomes = homeList.get(uuid);
        return userHomes != null && userHomes.containsKey(name);
    }

    public Map<UUID, Map<String, Home>> getHomeList() {
        return homeList;
    }

    public List<String> getHomeList(UUID uuid) {
        return homeList.getOrDefault(uuid, Map.of()).keySet()
                .stream().toList();
    }

    public List<String> getHomeList(UUID uuid, String start) {
        return homeList.getOrDefault(uuid, Map.of()).keySet()
                .stream()
                .sorted()
                .filter(name -> name.toLowerCase().startsWith(start.toLowerCase()))
                .toList();
    }

    public synchronized Location getLocationByName(UUID uuid, String homeName) {
        if (homeList.get(uuid).containsKey(homeName)) {
            return homeList.get(uuid).get(homeName).getLocation();
        } else {
            return null;
        }
    }

    public int getPlayerHomeLimit(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        int max = 1;

        for (PermissionAttachmentInfo attachmentInfo : player.getEffectivePermissions()) {
            String permission = attachmentInfo.getPermission();

            if (permission.startsWith("onyxhomes.limit.")) {
                try {
                    String value = permission.substring(permission.lastIndexOf('.') + 1);
                    int limit = Integer.parseInt(value);

                    if (limit > max) {
                        max = limit;
                    }
                } catch (NumberFormatException e) {}
            }
        }
        return max;
    }

    public int getCountPlayersHome(UUID uuid) {
        return homeList.getOrDefault(uuid, Collections.emptyMap()).size();
    }
}
