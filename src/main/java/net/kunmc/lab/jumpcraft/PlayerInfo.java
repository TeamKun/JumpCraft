package net.kunmc.lab.jumpcraft;

import org.bukkit.Location;

public class PlayerInfo {
    private boolean isDead;
    private int point;
    private final Location loc;

    PlayerInfo(Location loc) {
        isDead = false;
        point = 0;
        this.loc = new Location(loc.getWorld(),loc.getX() ,loc.getY(),loc.getZ());
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

    public int getPoint() {
        return point;
    }

    public void addPoint() {
        point++;
    }

    public Location getLoc() {
        return loc;
    }
}
