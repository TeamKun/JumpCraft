package net.kunmc.lab.jumpcraft;


import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class JumpCraft extends JavaPlugin {
    static JumpCraft instance;
    private GameManager gameManager;
    public void onEnable() {
        instance = this;
        new ConfigManager();
        new CommandListener();
        task();
    }

    public void task() {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                if(gameManager == null) {
                    return;
                }
                if(!gameManager.isStart()) {
                    return;
                }
                gameManager.displayScore();
                if(gameManager.isFinish()) {
                    gameManager.sendFinMessage();
                    return;
                }
                gameManager.fixPosAndStage();
                if(gameManager.isPause()) {
                    gameManager.moveBarAndAddPoint(true);
                    return;
                }
                gameManager.checkIsFinish();
                if(!gameManager.isFinish()) {
                    gameManager.moveBarAndAddPoint(false);
                }
            }
        },0,2);
    }

    public void startGame(Player sender) {
        gameManager = new GameManager(
                (int) sender.getLocation().getX(),
                100,
                (int) sender.getLocation().getZ(),
                (int) (sender.getLocation().getZ() + (ConfigManager.instance.getLength() / 2)),
                (int) (sender.getLocation().getZ() + ConfigManager.instance.getLength()),
                sender.getWorld(),
                ConfigManager.instance.isBattleRoyalMode() ? 2 : ConfigManager.instance.isTeamMode() ? 3 : 1
        );
    }

    public boolean pauseGame() {
        if(isNotStart()) {
            return false;
        }
        gameManager.pauseGame(true);
        return true;
    }

    public boolean unpauseGame() {
        if(isNotStart()) {
            return false;
        }
        gameManager.pauseGame(false);
        return true;
    }

    public boolean finishGame() {
        if(gameManager != null) {
            gameManager.finishGame();
        }
        return true;
    }

    public boolean isNotStart() {
        return gameManager == null || !gameManager.isStart() || gameManager.isFinish();
    }

    @Override
    public void onDisable() {
        if(gameManager != null) {
            gameManager.finishGame();
        }
    }
}
