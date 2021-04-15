package net.kunmc.lab.jumpcraft;

import org.bukkit.Location;
import org.bukkit.Material;

public class BoxGenerator {
    private static BoxGenerator INSTANCE;
    private Location boxLoc;
    private int beforeWidthX;
    private int beforeWidthZ;

    public BoxGenerator() {
        INSTANCE = this;
    }

    public static BoxGenerator getINSTANCE() {
        return INSTANCE;
    }

    public Location getBoxLoc() {
        return boxLoc;
    }

    public void generateBox(Location loc, int widthX, int widthZ, double height, Material block1, Material block2, Material block3) {
        loc.setX((int) loc.getX());
        loc.setY(height);
        loc.setZ((int) loc.getZ());
        boxLoc = new Location(loc.getWorld(),(int)loc.getX(),loc.getY(),(int)loc.getZ());
        beforeWidthX = widthX;
        beforeWidthZ = widthZ;
        setBlock(loc,widthX,widthZ,block1,block2,block3);
    }

    public void destroyBox() {
        if(boxLoc == null) { return;}
        setBlock(boxLoc,beforeWidthX,beforeWidthZ,Material.AIR, Material.AIR, Material.AIR);
    }

    public void setBlock(Location loc, int widthX, int widthZ,Material block1, Material block2, Material block3) {
        for(int i = 0; i < widthX; i++) {
            for(int j = 0; j < widthZ; j++) {
                loc.add(i,0,j);
                if(block1 != Material.AIR && (j == 0 || j == widthZ - 1 )) {
                    loc.getBlock().setType(block3);
                } else {
                    if(i % 2 == 0) {
                        loc.getBlock().setType(block1);
                    } else {
                        loc.getBlock().setType(block2);
                    }
                }
                loc.subtract(i,0,j);
            }
        }
    }
}
