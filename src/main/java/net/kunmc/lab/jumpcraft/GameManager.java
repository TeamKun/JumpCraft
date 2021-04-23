package net.kunmc.lab.jumpcraft;

import net.kunmc.lab.jumpcraft.stage.Stage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GameManager {
    private boolean isStart;
    private boolean isFinish;
    private boolean isPause;
    private final String[] winMassage;

    public void setWinMassage(String main, String sub) {
        winMassage[0] = main;
        winMassage[1] = sub;
    }

    public GameManager() {
        isStart = false;
        isFinish = false;
        isPause = false;
        winMassage = new String[2];
    }

    public void start(Map<UUID, Stage> stageMap, List<Player> players) {
        isStart = true;
        isPause = true;
        isFinish = false;
        players.forEach(player -> {
            Stage stage = stageMap.get(player.getUniqueId());
            player.teleport(new Location(player.getWorld(),stage.getX() + 0.5,stage.getY() + 1,stage.getCZ()));
            player.sendMessage("§a" + "5秒後にゲームを開始します");
        });
        Bukkit.getServer().getScheduler().runTaskLater(JumpCraft.instance, new Runnable() {
            @Override
            public void run() {
                if(isFinish){return;}
                isPause = false;
                players.forEach(player -> player.sendMessage("§6" + "ゲームを開始しました"));
            }
        },100L);
    }

    public void finish() {
        isStart = false;
        isFinish = true;
        isPause = false;
    }

    public boolean isStart() {
        return isStart;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }

    public boolean isPause() {
        return isPause;
    }

    public void setPause(boolean pause) {
        isPause = pause;
    }

    public void sendFinishMessage() {
        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            player.sendTitle(winMassage[0],winMassage[1], 0,5,0);
        });
    }
}
