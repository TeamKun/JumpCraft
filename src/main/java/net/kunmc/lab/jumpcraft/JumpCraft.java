package net.kunmc.lab.jumpcraft;

import net.kunmc.lab.jumpcraft.data.ConfigManager;
import net.kunmc.lab.jumpcraft.command.CommandListener;
import net.kunmc.lab.jumpcraft.player.PlayerManager;
import net.kunmc.lab.jumpcraft.stage.StageManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class JumpCraft extends JavaPlugin {
    public static JumpCraft instance;
    GameManager gameManager;
    StageManager stageManager;
    ConfigManager configManager;
    PlayerManager playerManager;
    @Override
    public void onEnable() {
        instance = this;
        gameManager = new GameManager();
        stageManager = new StageManager();
        configManager = new ConfigManager();
        playerManager = new PlayerManager();
        new CommandListener();
        task();
    }

    public void startGame(Player sender) {
        List<Player> players = new ArrayList<>(Bukkit.getServer().getOnlinePlayers());
        stageManager.generateStage(
                players,
                (int) sender.getLocation().getX(),
                100,
                (int) sender.getLocation().getZ(),
                configManager.getLength(),
                sender.getWorld());
        playerManager.setPlayerMap(players,configManager.isTeamMode() && !configManager.isBattleRoyalMode());
        gameManager.start(stageManager.getStageMap(),players);
    }

    public boolean finishGame() {
        boolean isStart = gameManager.isStart();
        stageManager.destroyAllStage();
        gameManager.finish();
        Bukkit.getServer().getOnlinePlayers().forEach(player -> player.setScoreboard(
                Bukkit.getServer().getScoreboardManager().getNewScoreboard()
        ));
        return isStart;
    }

    public boolean pauseGame() {
        if(gameManager.isStart()){
            gameManager.setPause(true);
            return true;
        }
        return false;
    }

    public boolean unpauseGame() {
        if(gameManager.isPause()) {
            gameManager.setPause(false);
            return true;
        }
        return false;
    }

    public void setWinMessage(String main, String sub) {
        gameManager.setWinMassage(main,sub);
    }

    public void setFinish() {
        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            if(configManager.isBattleRoyalMode() || configManager.isTeamMode()) {
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            } else {
                player.getWorld().playSound(player.getLocation(),Sound.ENTITY_BLAZE_DEATH,1,1);
            }
        });
        gameManager.setFinish(true);
    }

    public void showConfig(CommandSender sender) {
        configManager.show(sender);
    }

    public boolean setConfig(String item, String content) {
        return configManager.set(item,content);
    }

    public boolean isStart() {
        return gameManager.isStart() && !gameManager.isFinish();
    }

    private void task() {
        Bukkit.getServer().getScheduler().runTaskTimer(this, bukkitTask -> {
            if(gameManager.isStart()) {
                playerManager.displayScore(configManager.isTeamMode(),configManager.isBattleRoyalMode());
                if(gameManager.isFinish()) {
                    gameManager.sendFinishMessage();
                    return;
                }
                playerManager.fixPos(configManager.isTeamMode() && !configManager.isBattleRoyalMode(),configManager.isZFix());
                if(gameManager.isPause()) {
                    stageManager.moveBar(0);
                    return;
                }
                playerManager.checkHitBar(
                        stageManager.getBar().getY(),
                        stageManager.getBar().getZ(),
                        configManager.getSpeed(),
                        configManager.isTeamMode() && !configManager.isBattleRoyalMode(),
                        configManager.isBattleRoyalMode());
                boolean shouldAddPoint = stageManager.moveBar(configManager.getSpeed());
                if(shouldAddPoint) {
                    playerManager.addPoint(configManager.isTeamMode() && !configManager.isBattleRoyalMode());
                }
            }
        },0L,2L);
    }


    @Override
    public void onDisable() {
        finishGame();
    }
}
