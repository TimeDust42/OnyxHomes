package com.timedust.onyxHomes.homes;

import org.bukkit.Location;

import java.util.*;

public class HomeManager {

    private final Map<UUID, Map<String, Home>> homeList = new HashMap<>();

    public void addHome(UUID uuid, String homeName, Location location) {
        Home home = new Home(homeName, location);

        homeList.computeIfAbsent(uuid, k -> new HashMap<>())
                .put(homeName, home);
    }

    public void deleteHome(UUID uuid, String homeName) {
        homeList.computeIfPresent(uuid, (id, houses) -> {
            houses.remove(homeName);
            return houses.isEmpty() ? null : houses;
        });
    }

    public Map<UUID, Map<String, Home>> getHomeList() {
        return homeList;
    }

    public List<String> getHomeList(UUID uuid, String start) {
        return homeList.getOrDefault(uuid, Map.of()).keySet()
                .stream()
                .sorted()
                .filter(name -> name.toLowerCase().startsWith(start.toLowerCase()))
                .toList();
    }

    public List<String> getHomeList(UUID uuid) {
        return homeList.getOrDefault(uuid, Map.of()).keySet()
                .stream().toList();
    }

    public Location getLocationByName(UUID uuid, String homeName) {
        if (homeList.get(uuid).containsKey(homeName)) {
            return homeList.get(uuid).get(homeName).getLocation();
        } else {
            return null;
        }
    }
}
