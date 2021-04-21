package net.kunmc.lab.jumpcraft.stage;

public class Bar {
    private double fx;
    private double y;
    private double z;
    private double lx;
    private double fz;
    private double lz;
    private boolean isReverse;

    public Bar(double fx, double y, double z, double lx, int length) {
        this.fx = fx;
        this.y = y;
        this.z = length + z;
        this.lx = lx;
        fz = z;
        this.lz = length + z;
        isReverse = true;
    }

    public double getFx() {
        return fx;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getLx() {
        return lx;
    }

    public double getFz() {
        return fz;
    }

    public double getLz() {
        return lz;
    }

    public boolean isReverse() {
        return isReverse;
    }

    public void setReverse(boolean reverse) {
        isReverse = reverse;
    }
}
