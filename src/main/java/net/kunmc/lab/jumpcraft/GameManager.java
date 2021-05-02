package net.kunmc.lab.jumpcraft;

import net.kunmc.lab.jumpcraft.player.PlayerManager;
import net.kunmc.lab.jumpcraft.player.PlayerManagerIF;
import net.kunmc.lab.jumpcraft.player.TeamManager;
import net.kunmc.lab.jumpcraft.stage.Bar;
import net.kunmc.lab.jumpcraft.stage.Stage;
import net.kunmc.lab.jumpcraft.stage.StageManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class GameManager {
    private boolean isStart;
    private boolean isFinish;
    private boolean isPause;
    private String[] winMassage;
    private int gameMode;
    private List<Player> players;
    private StageManager stageManager;
    private PlayerManagerIF playerManagerIF;
    private Scoreboard scoreboard;

    public boolean isStart() {
        return isStart;
    }

    public boolean isPause() {
        return isPause;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public GameManager(int x, int y, int firstZ, int centerZ, int lastZ, World world, int gameMode) {
        startGame(x, y, firstZ, centerZ, lastZ, world, gameMode);
    }

    private void init(int x, int y, int firstZ, int centerZ, int lastZ, World world, int gameMode) {
        isStart = true;
        isFinish = false;
        isPause = true;
        winMassage = new String[2];
        this.gameMode = gameMode;
        players = new ArrayList<>(Bukkit.getServer().getOnlinePlayers());
        stageManager = new StageManager(x, y, firstZ, centerZ, lastZ, world, players);
        playerManagerIF = gameMode == 3 ? new TeamManager(players) : new PlayerManager(players);
        scoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
        scoreboard.registerNewObjective("point","dummy", Component.text("Point"))
                .setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void startGame(int x, int y, int firstZ, int centerZ, int lastZ, World world, int gameMode) {
        if(stageManager != null) {
            stageManager.destroyAllStage();
        }
        init(x, y, firstZ, centerZ, lastZ, world, gameMode);
        players.forEach(player -> {
            Stage stage = stageManager.getStageMap().get(player.getUniqueId());
            player.teleport(player.getLocation().set(stage.getX() + 0.5,stage.getY() + 1, stage.getCenterZ()).setDirection(
                    new Vector(0,0,lastZ).normalize()
            ));
            player.sendMessage("§a" + "5秒後にゲームを開始します");
        });
        Bukkit.getServer().getScheduler().runTaskLater(JumpCraft.instance, bukkitTask -> {
            if(!isFinish) {
                isPause = false;
            }
            players.forEach(player -> player.sendMessage("§6" + "ゲームを開始しました"));
            bukkitTask.cancel();
        },100);
        countStartTitle();
    }

    public void pauseGame(boolean isPause) {
        this.isPause = isPause;
    }

    private void countStartTitle() {
        AtomicInteger count = new AtomicInteger(5);
        Bukkit.getServer().getScheduler().runTaskTimer(JumpCraft.instance, bukkitTask -> {
            if(isFinish) {
                bukkitTask.cancel();
                return;
            }
            if(count.get() <= 0) {
                players.forEach(player -> {
                    player.sendTitle("§a" + "start!!", "", 0, 5, 1);
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL,1,1);
                });
                bukkitTask.cancel();
                return;
            }
            players.forEach(player -> {
                player.sendTitle(String.valueOf(count.get()),"",0,20,1);
                player.playSound(player.getLocation(),Sound.BLOCK_NOTE_BLOCK_BELL,1,1);
            });
            count.getAndDecrement();
        },0,20);
    }

    public void finishGame() {
        if(stageManager != null) {
            stageManager.destroyAllStage();
        }
        isFinish = true;
        isStart = false;
        players.forEach(player -> player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard()));
    }

    public void displayScore() {
        playerManagerIF.setScoreBoard(scoreboard, gameMode == 1);
        players.forEach(player -> player.setScoreboard(scoreboard));
    }

    public void checkIsFinish() {
        List<Player> players = playerManagerIF.getAlivePlayers(this.players);
        for (Player player : players) {
            List<String> messages = playerManagerIF.setDeadOrGetWinnerNameAndPoint(player, stageManager, isHitBar(player));
            if(messages != null) {
                this.players.forEach(p -> {
                    if(ConfigManager.instance.isBattleRoyalMode() || ConfigManager.instance.isTeamMode()) {
                        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                    } else {
                        p.getWorld().playSound(p.getLocation(),Sound.ENTITY_BLAZE_DEATH,1,1);
                    }
                });
                winMassage[0] = messages.get(0);
                winMassage[1] = messages.get(1);
                isFinish = true;
                return;
            }
        }
    }

    private boolean isHitBar(Player player) {
        if(player.isDead() || !player.isValid() || !player.isOnline()) {
            return true;
        }
        Bar bar = stageManager.getBar();
        double y = player.getLocation().getY();
        if(y > bar.getY()) {
            return false;
        }
        double z = player.getLocation().getZ();
        double speed = ConfigManager.instance.getSpeed();
        double s = 0;
        while (speed > 0) {
            s += Math.min(speed, 0.5);
            speed -= Math.min(speed, 0.5);
            if(bar.isReverse()) {
                if(bar.getNowZ() + s + 0.3 >= z && z >= bar.getNowZ() + s - 0.3) {
                    return true;
                }
            } else {
                if(bar.getNowZ() - s + 0.3 >= z && z >= bar.getNowZ() - s - 0.3) {
                    return true;
                }
            }
        }
        return false;
    }

    public void moveBarAndAddPoint(boolean isPause) {
        boolean shouldAddPoint = stageManager.spawnBarParticle(ConfigManager.instance.getSpeed(), isPause);
        if(shouldAddPoint) {
            playerManagerIF.addPoint();
            players.forEach(player -> player.playSound(player.getLocation(),Sound.BLOCK_NOTE_BLOCK_BELL,1,1));
        }
    }

    public void fixPosAndStage() {
        List<Player> players = playerManagerIF.getAlivePlayers(this.players);
        players.forEach(player -> {
            stageManager.fixStage(player.getUniqueId());
            Stage stage = stageManager.getStageMap().get(player.getUniqueId());
            int diffX = (int) player.getLocation().getX() - (stage.getX() + 1);
            int diffZ = (int) player.getLocation().getZ() - stage.getCenterZ();
            if(diffX != 0 || (ConfigManager.instance.isZFix()) && diffZ != 0) {
                player.teleport(player.getLocation().set(stage.getX() + 0.5, stage.getY() + 1, stage.getCenterZ())
                .setDirection(player.getLocation().getDirection()));
            }
        });
    }

    public void sendFinMessage() {
        players.forEach(player -> player.sendTitle(winMassage[0],winMassage[1],0,10,0));
    }

}
