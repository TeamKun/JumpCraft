package net.kunmc.lab.jumpcraft.player;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TeamData extends PlayerData {
    private final String name;
    private final List<Player> members;

    public TeamData(String name) {
        this.name = name;
        members = new ArrayList<>();
    }

    public void addMember(Player player) {
        members.add(player);
    }

    public List<Player> getMembers() {
        return members;
    }

    public String getName() {
        return name;
    }
}
