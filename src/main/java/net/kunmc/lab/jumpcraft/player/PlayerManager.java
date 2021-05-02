package net.kunmc.lab.jumpcraft.player;

import net.kunmc.lab.jumpcraft.ConfigManager;
import net.kunmc.lab.jumpcraft.stage.StageManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerManager implements PlayerManagerIF{
    private final Map<UUID,PlayerData> playerMap;

    public PlayerManager(List<Player> players) {
        playerMap = new HashMap<>();
        players.forEach(player -> playerMap.put(player.getUniqueId(),new PlayerData()));
    }

    public void addPoint() {
        playerMap.values().stream().filter(data -> !data.isDead())
                .forEach(data -> data.setPoint(data.getPoint() + 1));
    }

    public List<String> setDeadOrGetWinnerNameAndPoint(Player player, StageManager stageManager, boolean isHit) {
        UUID id = player.getUniqueId();
        if(!playerMap.containsKey(id)) {
            return null;
        }
        if(!ConfigManager.instance.isBattleRoyalMode() && !ConfigManager.instance.isTeamMode()) {
            if(isHit) {
                stageManager.destroyStage(player.getUniqueId());
                player.sendMessage("§c" + "当たりました");
                return Stream.of("§c" + "戦犯" + player.getName(), "Point:" + playerMap.get(id).getPoint()).collect(Collectors.toList());
            }
            return null;
        }
        if(playerMap.entrySet().stream().anyMatch(kv -> !kv.getKey().equals(id) && !kv.getValue().isDead())) {
            if(isHit) {
                playerMap.get(id).setDead(true);
                stageManager.destroyStage(player.getUniqueId());
            }
            return null;
        }
        return Stream.of("§6" + "勝者" + player.getName(),String.valueOf(playerMap.get(id).getPoint())).collect(Collectors.toList());
    }

    public List<Player> getAlivePlayers(List<Player> players) {
        return players.stream().filter(player -> !playerMap.get(player.getUniqueId()).isDead())
                .collect(Collectors.toList());
    }

    public void setScoreBoard(Scoreboard scoreboard, boolean isNormalMode) {
        if(isNormalMode) {
            scoreboard.getObjective("point").getScore("total")
                    .setScore(playerMap.values().stream().findFirst().get().getPoint());
            return;
        }
        playerMap.forEach((id,data) -> {
            Player player = Bukkit.getServer().getPlayer(id);
            if(player == null) {
                return;
            }
            scoreboard.getObjective("point").getScore(player.getName())
                    .setScore(data.getPoint());
        });
    }
}
