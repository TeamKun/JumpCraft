package net.kunmc.lab.jumpcraft;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class ConfigManager {
    public static ConfigManager instance;
    private int length = 30;
    private double speed = 1;
    private boolean isBattleRoyalMode = false;
    private boolean isTeamMode = false;
    private boolean isXFix = true;
    private boolean isZFix = true;
    private Set<String> blackList = new HashSet<>();

    public ConfigManager() {
        instance = this;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public boolean isBattleRoyalMode() {
        return isBattleRoyalMode;
    }

    public void setBattleRoyalMode(boolean battleRoyalMode) {
        isBattleRoyalMode = battleRoyalMode;
    }

    public boolean isTeamMode() {
        return isTeamMode;
    }

    public void setTeamMode(boolean teamMode) {
        isTeamMode = teamMode;
    }

    public boolean isXFix() {
        return isXFix;
    }

    public void setXFix(boolean XFix) {
        isXFix = XFix;
    }

    public boolean isZFix() {
        return isZFix;
    }

    public void setZFix(boolean ZFix) {
        isZFix = ZFix;
    }

    public Set<String> getBlackList() {
        return blackList;
    }

    public void setBlackList(Set<String> blackList) {
        this.blackList = blackList;
    }

    public void show(CommandSender sender) {
        sender.sendMessage("§a" + "barSpeed: " + getSpeed());
        sender.sendMessage("§a" + "stageLength: " + getLength());
        sender.sendMessage("§a" + "shouldXFix" + isXFix());
        sender.sendMessage("§a" + "shouldZFix" + isZFix());
        sender.sendMessage("§a" + "teamMode: " + isTeamMode());
        sender.sendMessage("§a" + "battleRoyalMode: " + isBattleRoyalMode());
        String names = new ArrayList<>(getBlackList()).toString();
        sender.sendMessage("§a" + "blackList: " + names.substring(1,names.length() - 1));
    }

    public boolean set(String item, String content) {
        boolean result = true;
        switch (item) {
            case "barSpeed":
                double speed;
                try {
                    speed = Double.parseDouble(content);
                } catch (NumberFormatException e) {
                    speed = 0.0;
                }
                if(speed == 0.0) {
                    result = false;
                } else {
                    setSpeed(speed);
                }
                break;
            case "stageLength":
                int length;
                try {
                    length = (int) Double.parseDouble(content);
                } catch (NumberFormatException e) {
                    length = 0;
                }
                if(5 > length) {
                    result = false;
                } else {
                    setLength(length);
                }
                break;
            case "shouldZFix":
                if(content.equals("true") || content.equals("false")) {
                    setZFix(Boolean.parseBoolean(content));
                } else {
                    result = false;
                }
                break;
            case "shouldXFix":
                if(content.equals("true") || content.equals("false")) {
                    setXFix(Boolean.parseBoolean(content));
                } else {
                    result = false;
                }
                break;
            case "teamMode":
                if(content.equals("true") || content.equals("false")) {
                    setTeamMode(Boolean.parseBoolean(content));
                } else {
                    result = false;
                }
                break;
            case "battleRoyalMode":
                if(content.equals("true") || content.equals("false")) {
                    setBattleRoyalMode(Boolean.parseBoolean(content));
                } else {
                    result = false;
                }
                break;
            default:
                result = false;
                break;
        }
        return result;
    }
}