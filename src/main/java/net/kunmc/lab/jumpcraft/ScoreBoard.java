package net.kunmc.lab.jumpcraft;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ScoreBoard {
    private static ScoreBoard INSTANCE;
    private Scoreboard scoreboard;

    public static ScoreBoard getINSTANCE() {
        return INSTANCE;
    }

    public String getPlayersTeamName(String playerName) {
        List<Team> teamList = new ArrayList<>(Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeams());
        String teamName = "empty";
        for (Team team : teamList) {
            if (team.hasEntry(playerName)) {
                teamName = team.getName();
                break;
            }
        }
        return teamName;
    }

    public void disPlayScoreBoard(HashMap<UUID,PlayerInfo> pInfo, int mode) {
        scoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
        scoreboard.registerNewObjective("point","dummy", Component.text("POINT"))
                .setDisplaySlot(DisplaySlot.SIDEBAR);
        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            if(!pInfo.containsKey(player.getUniqueId())) {
                return;
            }
            if(mode == 1) {
                scoreboard.getObjective("point")
                        .getScore("total")
                        .setScore(pInfo.get(player.getUniqueId()).getPoint());
            }
            if(mode == 2) {
                scoreboard.getObjective("point")
                        .getScore(pInfo.get(player.getUniqueId()).getTeamName())
                        .setScore(pInfo.get(player.getUniqueId()).getPoint());
            }
            if(mode == 3) {
                scoreboard.getObjective("point")
                        .getScore(player.getName())
                        .setScore(pInfo.get(player.getUniqueId()).getPoint());
            }
        });
        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            if (!pInfo.containsKey(player.getUniqueId())) {
                    return;
            }
            player.setScoreboard(scoreboard);
        });
    }

    public ScoreBoard() {
        INSTANCE = this;
        scoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
        scoreboard.registerNewObjective("point","dummy", Component.text("POINT"))
                .setDisplaySlot(DisplaySlot.SIDEBAR);
    }
}
