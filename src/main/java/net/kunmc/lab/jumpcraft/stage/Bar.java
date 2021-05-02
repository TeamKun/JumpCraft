package net.kunmc.lab.jumpcraft.stage;

import org.bukkit.World;

public class Bar {
    private final double firstX;
    private final double lastX;
    private final double y;
    private final double firstZ;
    private final double lastZ;
    private double nowZ;
    private boolean isReverse;
    private final World world;

    public Bar(double firstX, double lastX, double y, double firstZ, double lastZ, double nowZ, boolean isReverse, World world) {
        this.firstX = firstX;
        this.lastX = lastX;
        this.y = y;
        this.firstZ = firstZ;
        this.lastZ = lastZ;
        this.nowZ = nowZ;
        this.isReverse = isReverse;
        this.world = world;
    }

    public double getFirstX() {
        return firstX;
    }

    public double getLastX() {
        return lastX;
    }

    public double getY() {
        return y;
    }

    public double getFirstZ() {
        return firstZ;
    }

    public double getLastZ() {
        return lastZ;
    }

    public double getNowZ() {
        return nowZ;
    }

    public boolean isReverse() {
        return isReverse;
    }

    public World getWorld() {
        return world;
    }

    public void setNowZ(double nowZ) {
        this.nowZ = nowZ;
    }

    public void setReverse(boolean reverse) {
        isReverse = reverse;
    }
}
