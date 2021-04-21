package net.kunmc.lab.jumpcraft.player;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TeamData extends PlayerData{
    private final List<Player> members;

    public TeamData() {
        members = new ArrayList<>();
    }

    public void addMember(Player player) {
        members.add(player);
    }

    public List<Player> getMembers() {
        return members;
    }
}
