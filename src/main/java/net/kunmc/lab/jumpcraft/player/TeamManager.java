package net.kunmc.lab.jumpcraft.player;

import net.kunmc.lab.jumpcraft.stage.StageManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TeamManager implements PlayerManagerIF {
    private final List<TeamData> teams;

    public TeamManager(List<Player> players) {
        teams = new ArrayList<>();
        for (Player player : players) {
            String name = "無所属";
            for (Team team : Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeams()) {
                if (team.hasEntry(player.getName())) {
                    name = team.getName();
                    break;
                }
            }
            String finalName = name;
            if (teams.stream().noneMatch(data -> data.getName().equals(finalName))) {
                teams.add(new TeamData(name));
            }
            teams.stream().filter(data -> data.getName().equals(finalName))
                    .findFirst().ifPresent(teamData -> teamData.addMember(player));
        }
    }

    public void addPoint() {
        teams.stream().filter(data -> !data.isDead())
                .forEach(data -> data.setPoint(data.getPoint() + 1));
    }

    public List<String> setDeadOrGetWinnerNameAndPoint(Player player, StageManager stageManager, boolean isHit) {
        TeamData teamData = teams.stream().filter(data -> data.getMembers().contains(player))
                .findFirst().orElse(null);
        if (teamData == null) {
            return null;
        }
        if (teams.stream().anyMatch(data -> !data.getName().equals(teamData.getName()) && !data.isDead())) {
            if(isHit) {
                player.sendMessage("§c" + "当たりました");
                teamData.setDead(true);
                teamData.getMembers().forEach(member -> {
                    stageManager.destroyStage(member.getUniqueId());
                    if (!member.equals(player)) {
                        member.sendMessage("§c" + "メンバーが当たりました");
                    }
                });
            }
            return null;
        }
        return Stream.of("§6" + "勝者" + teamData.getName(), "Point:" + teamData.getPoint()).collect(Collectors.toList());
    }

    public List<Player> getAlivePlayers(List<Player> players) {
        return players.stream().filter(player -> !teams.stream()
                .filter(data -> data.getMembers().contains(player))
                .findFirst().get().isDead())
                .collect(Collectors.toList());
    }

    public void setScoreBoard(Scoreboard scoreboard, boolean isNormalMode) {
        teams.forEach(data -> scoreboard.getObjective("point").getScore(data.getName())
        .setScore(data.getPoint()));
    }
}
