package net.kunmc.lab.jumpcraft.player;

public class PlayerData {
    private int point;
    private boolean isDead;

    public PlayerData() {
        point = 0;
        isDead = false;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }
}
