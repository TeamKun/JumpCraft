package net.kunmc.lab.jumpcraft;

import org.bukkit.Material;

public class ConfigData {
    private static ConfigData INSTANCE;

    public static ConfigData getINSTANCE() {
        return INSTANCE;
    }

    public ConfigData() {
        INSTANCE = this;
    }

    private int widthX = 50;
    private int widthZ = 30;
    private double height = 100;
    private Material block1 = Material.WHITE_CONCRETE;
    private Material block2 = Material.GRAY_CONCRETE;
    private Material block3 = Material.BLACK_CONCRETE;
    private double barSpeed = 1;
    private boolean isBattleRoyalMode = false;

    public int getWidthX() {
        return widthX;
    }

    public void setWidthX(int widthX) {
        this.widthX = widthX;
    }

    public int getWidthZ() {
        return widthZ;
    }

    public void setWidthZ(int widthZ) {
        this.widthZ = widthZ;
    }

    public double getHeight() {
        return height;
    }

    public Material getBlock1() {
        return block1;
    }

    public Material getBlock2() {
        return block2;
    }

    public Material getBlock3() {
        return block3;
    }

    public double getBarSpeed() {
        return barSpeed;
    }

    public void setBarSpeed(double barSpeed) {
        this.barSpeed = barSpeed;
    }

    public boolean isBattleRoyalMode() {
        return isBattleRoyalMode;
    }

    public void setBattleRoyalMode(boolean battleRoyalMode) {
        isBattleRoyalMode = battleRoyalMode;
    }
}
