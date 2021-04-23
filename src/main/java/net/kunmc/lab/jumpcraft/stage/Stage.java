package net.kunmc.lab.jumpcraft.stage;

import org.bukkit.Material;

public class Stage {
    private int x;
    private int y;
    private int fz;
    private int lz;
    private int cZ;
    Material block;

    public Stage(int x, int y, int firstZ, int length, Material block) {
        this.x = x;
        this.y = y;
        this.fz = firstZ;
        this.lz = length + firstZ;
        cZ = (length / 2) + firstZ;
        this.block = block;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getFz() {
        return fz;
    }

    public int getLz() {
        return lz;
    }

    public int getCZ() {
        return cZ;
    }

    public Material getBlock() {
        return block;
    }
}
