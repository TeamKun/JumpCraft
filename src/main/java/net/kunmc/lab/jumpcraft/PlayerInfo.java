package net.kunmc.lab.jumpcraft;

import org.bukkit.Location;

public class PlayerInfo {
    private boolean isDead;
    private int point;
    private final Location loc;
    private String teamName;

    PlayerInfo(Location loc, String teamName) {
        isDead = false;
        point = 0;
        this.loc = new Location(loc.getWorld(),loc.getX() ,loc.getY(),loc.getZ());
        this.teamName = teamName;
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

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
}
