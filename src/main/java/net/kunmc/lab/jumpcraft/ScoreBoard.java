package net.kunmc.lab.jumpcraft;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

public class ScoreBoard {
    private static ScoreBoard INSTANCE;

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

    public ScoreBoard() {
        INSTANCE = this;
    }
}
