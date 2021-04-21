package net.kunmc.lab.jumpcraft.stage;

public class Stage {
    private int x;
    private int y;
    private int fz;
    private int lz;
    private int cZ;

    public Stage(int x, int y, int firstZ, int length) {
        this.x = x;
        this.y = y;
        this.fz = firstZ;
        this.lz = length + firstZ;
        cZ = (length / 2) + firstZ;
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

    public int getcZ() {
        return cZ;
    }
}
