package com.timedust.onyxHomes.homes;

import org.bukkit.Location;

public class Home {

    private String homeName;
    private Location location;

    public Home(String homeName, Location location) {
        this.homeName = homeName;
        this.location = location;
    }

    public String getHomeName() {
        return homeName;
    }

    public Location getLocation() {
        return location;
    }

    public void setHomeName(String homeName) {
        this.homeName = homeName;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
