package net.kunmc.lab.jumpcraft.player;

import net.kunmc.lab.jumpcraft.stage.StageManager;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import java.util.List;

public interface PlayerManagerIF {
    void addPoint();
    List<String> setDeadOrGetWinnerNameAndPoint(Player player, StageManager stageManager, boolean isHit);
    void setScoreBoard(Scoreboard scoreboard, boolean isNormalMode);
    List<Player> getAlivePlayers(List<Player> players);
}
