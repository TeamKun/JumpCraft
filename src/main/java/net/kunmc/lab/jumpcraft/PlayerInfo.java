package net.kunmc.lab.jumpcraft;

import org.bukkit.Location;

public class PlayerInfo {
    private boolean isDead = false;
    private int point = 0;
    private int jumpCount = 0;
    private double posX;
    private double posZ;
    private Location loc;

    PlayerInfo(double posX,double posZ, Location loc) {
        isDead = false;
        point = 0;
        jumpCount = 0;
        this.posX = posX;
        this.posZ = posZ;
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

    public int getJumpCount() {
        return jumpCount;
    }

    public void setJumpCount(int jumpCount) {
        this.jumpCount = jumpCount;
    }

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosZ() {
        return posZ;
    }

    public void setPosZ(double posZ) {
        this.posZ = posZ;
    }

    public Location getLoc() {
        return loc;
    }

    public void setLoc(Location loc) {
        this.loc.set(loc.getX(),loc.getY(),loc.getZ());
    }
}
