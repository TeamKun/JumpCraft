package net.kunmc.lab.jumpcraft.stage;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class Stage {
    private final int x;
    private final int y;
    private final int firstZ;
    private final int centerZ;
    private final int lastZ;
    private final Material block;
    private final List<Block> blocks;
    private final int pPosX;

    public Stage(int x, int y, int firstZ, int centerZ, int lastZ, Material block, int pPosX) {
        this.x = x;
        this.y = y;
        this.firstZ = firstZ;
        this.centerZ = centerZ;
        this.lastZ = lastZ;
        this.block = block;
        blocks = new ArrayList<>();
        this.pPosX = pPosX;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getFirstZ() {
        return firstZ;
    }

    public int getCenterZ() {
        return centerZ;
    }

    public int getLastZ() {
        return lastZ;
    }

    public Material getBlock() {
        return block;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public int getpPosX() {
        return pPosX;
    }
}
