package net.kunmc.lab.jumpcraft;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.*;

public final class JumpCraft extends JavaPlugin {
    private static JumpCraft INSTANCE;

    private boolean isStart = false;
    private boolean isPreparing = false;
    private boolean isPause = false;
    private boolean isReverseBar = false;
    private boolean isFinish = false;
    private String winnerName = "a";
    private int winnerPoint = 0;
    private double barPosZAdder = 0;

    private HashMap<UUID,PlayerInfo> pInfo = new HashMap<>();

    public static JumpCraft getINSTANCE() {
        return INSTANCE;
    }

    public boolean isStart() {
        return isStart;
    }

    public void setStart(boolean start) {
        isStart = start;
    }

    public void setPreparing(boolean preparing) {
        isPreparing = preparing;
    }

    public boolean isPreparing() {
        return isPreparing;
    }

    public boolean isPause() {
        return isPause;
    }

    public void setPause(boolean pause) {
        isPause = pause;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void unPause() {
        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            if(!pInfo.containsKey(player.getUniqueId())) {
                return;
            }
            player.teleport(pInfo.get(player.getUniqueId()).getLoc());
        });
        isPause = false;
    }

    public void setBarPosZAdder(double barPosZAdder) {
        this.barPosZAdder = barPosZAdder;
    }

    public HashMap<UUID, PlayerInfo> getPInfo() {
        return pInfo;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        INSTANCE = this;
        new CommandListener();
        new ConfigData();
        new BoxGenerator();
        gameTask();
    }

    private void gameTask() {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                if(isPreparing) {
                    Bukkit.getServer().getOnlinePlayers().forEach(player -> {
                        if(pInfo.containsKey(player.getUniqueId())) {
                            if(player.isDead() || !player.isValid() || player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
                                return;
                            }
                           fixPlayerPosition(player);
                        }
                    });
                }
                if(!isStart) {
                    return;
                }
                setBar();
                if(ConfigData.getINSTANCE().isBattleRoyalMode() && !isFinish) {
                    checkIsFinish();
                }
                if(isFinish) {
                    Bukkit.getServer().getOnlinePlayers().forEach(player -> {
                        player.sendTitle(winnerName, "§a" + "Point:" + winnerPoint, 0, 10, 0);
                    });
                    return;
                }
                if(isPause) {
                    return;
                }
                checkPlayerState();
                setBarPositionAndAddPoint();
            }
        },0L, 2L);
    }

    private void setBar() {
        Location boxLoc = BoxGenerator.getINSTANCE().getBoxLoc();
        for(double barPosXAdder = 0; barPosXAdder < ConfigData.getINSTANCE().getWidthX(); barPosXAdder += 0.1) {
            boxLoc.add(barPosXAdder,1.5,barPosZAdder);
            boxLoc.getWorld().spawnParticle(Particle.SWEEP_ATTACK,boxLoc,1,0,0,0,0);
            boxLoc.subtract(barPosXAdder,1.5,barPosZAdder);
        }
    }

    private void checkPlayerState() {
        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            if(!pInfo.containsKey(player.getUniqueId())) {
                return;
            }
            if(player.isDead() || !player.isValid() || player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
                return;
            }
            if(pInfo.get(player.getUniqueId()).isDead()) {
                return;
            }
            player.sendActionBar(Component.text("§6" + pInfo.get(player.getUniqueId()).getPoint()));
            fixPlayerPosition(player);
            Location boxLoc = BoxGenerator.getINSTANCE().getBoxLoc();
            double barPosZ = boxLoc.getZ() + barPosZAdder;
            double barPosY = boxLoc.getY() + 1.5;
            double playerPosZ = player.getLocation().getZ();
            double playerPosY = player.getLocation().getY();
            if(barPosZ + 1 >= playerPosZ && playerPosZ >= barPosZ - 1) {
                if(barPosY >= playerPosY) {
                    player.sendMessage("§cあなたは当たりました");
                    pInfo.get(player.getUniqueId()).setDead(true);
                    if(ConfigData.getINSTANCE().isBattleRoyalMode()) {
                        for (int addZ = 0; addZ < ConfigData.getINSTANCE().getWidthZ(); addZ++) {
                            player.getWorld().getBlockAt((int) pInfo.get(player.getUniqueId()).getLoc().getX() - 1, (int) boxLoc.getY(), (int) boxLoc.getZ() + addZ).setType(Material.AIR);
                            player.teleport(pInfo.get(player.getUniqueId()).getLoc());
                        }
                    } else {
                        player.setFireTicks(1000);
                        isFinish = true;
                        winnerName = "§c" + "戦犯: " + player.getName();
                        winnerPoint = pInfo.get(player.getUniqueId()).getPoint();
                        playSound(Sound.ENTITY_BLAZE_DEATH);
                    }
                }
            }
        });
    }

    private void fixPlayerPosition(Player player) {
        int  diffX = (int) player.getLocation().getX() - (int) pInfo.get(player.getUniqueId()).getLoc().getX();
        if (diffX != 0) {
            player.setVelocity(player.getVelocity().add((new Vector(diffX * -1,0,0)).normalize().multiply(0.2)));
        }
        if(pInfo.get(player.getUniqueId()).getLoc().getY() >= player.getLocation().getY() + 5) {
            player.teleport(pInfo.get(player.getUniqueId()).getLoc());
        }
    }

    private void setBarPositionAndAddPoint() {
        if(barPosZAdder > ConfigData.getINSTANCE().getWidthZ()) {
            isReverseBar = true;
            playSound(Sound.BLOCK_NOTE_BLOCK_BELL);
            addPlayersPoint();
        }
        if(isReverseBar) {
            barPosZAdder -= ConfigData.getINSTANCE().getBarSpeed();
        } else {
            barPosZAdder += ConfigData.getINSTANCE().getBarSpeed();
        }
        if(barPosZAdder <= 0) {
            isReverseBar = false;
            playSound(Sound.BLOCK_NOTE_BLOCK_BELL);
            addPlayersPoint();
        }
    }

    private void playSound(Sound sound) {
        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            player.getWorld().playSound(player.getLocation(), sound, 1, 1);
        });
    }

    private void checkIsFinish() {
        List<Player> players = new ArrayList<>(Bukkit.getServer().getOnlinePlayers());
        int count = 0;
        for (Player player: players) {
            if(!pInfo.containsKey(player.getUniqueId())) {
                return;
            }
            if(player.isDead() || !player.isValid() || player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
                return;
            }
            if(!pInfo.get(player.getUniqueId()).isDead()) {
                winnerName = "§6" + "勝者: " + player.getName();
                winnerPoint = pInfo.get(player.getUniqueId()).getPoint();
                count++;
            }
        }
        if(count == 1) {
            isFinish = true;
            playSound(Sound.ENTITY_PLAYER_LEVELUP);
        }
    }

    private void addPlayersPoint() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if(!pInfo.containsKey(player.getUniqueId())) {
                return;
            }
            if(pInfo.get(player.getUniqueId()).isDead()) {
                return;
            }
            pInfo.get(player.getUniqueId()).addPoint();
        });
    }

    public void startGame(Location baseLoc) {
        generateBox(baseLoc);
        preparePlayer();
        delayGameStart();
    }

    private void generateBox(Location baseLoc) {
        ConfigData configData = ConfigData.getINSTANCE();
        BoxGenerator.getINSTANCE().destroyBox();
        baseLoc.setY(configData.getHeight());
        configData.setWidthX(Bukkit.getServer().getOnlinePlayers().size() + 2);
        BoxGenerator.getINSTANCE().generateBox(
                new Location(baseLoc.getWorld(), (int) baseLoc.getX(), baseLoc.getY(), (int) baseLoc.getZ()),
                configData.getWidthX(),
                configData.getWidthZ(),
                configData.getHeight(),
                configData.getBlock1(),
                configData.getBlock2(),
                configData.getBlock3()
        );
    }

    private void preparePlayer() {
        ConfigData configData = ConfigData.getINSTANCE();
        Location boxLoc = BoxGenerator.getINSTANCE().getBoxLoc();
        pInfo = new HashMap<>();
        int addZ = configData.getWidthZ() % 2 == 0 ? configData.getWidthZ() / 2 : (configData.getWidthZ() + 1) / 2;
        List<Player> players = new ArrayList<>(Bukkit.getServer().getOnlinePlayers());
        for (Player player : players) {
            if(player.isDead() || !player.isValid() || player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
                boxLoc.add(1.5, 1, addZ);
            } else {
                player.teleport(boxLoc.add(1.5, 1, addZ));
            }
            player.setFireTicks(0);
            pInfo.put(player.getUniqueId(), new PlayerInfo((int) boxLoc.getX(), (int) boxLoc.getZ(), boxLoc));
            boxLoc.subtract(1.5, 1, addZ);
            player.sendMessage("§a" + "5秒後にゲームを開始します");
        }
        isPreparing = true;
        isFinish = false;
    }

    private void delayGameStart() {
        setBarPosZAdder(ConfigData.getINSTANCE().getWidthZ());
        Bukkit.getServer().getScheduler().runTaskLater(this, new Runnable() {
            @Override
            public void run() {
                isStart = true;
                isPreparing = false;
                isPause = false;
                isReverseBar = false;
                isFinish = false;
                winnerName = "";
                winnerPoint = 0;
                Bukkit.getServer().getOnlinePlayers().forEach(player -> {
                    if(!pInfo.containsKey(player.getUniqueId())) {
                        return;
                    }
                    if(player.isDead() || !player.isValid() || player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
                        return;
                    }
                    player.sendMessage("§6ゲームを開始しました");
                    PlayerInfo pInfo = getPInfo().get(player.getUniqueId());
                    player.teleport(pInfo.getLoc());
                });
            }
        },100L);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        BoxGenerator.getINSTANCE().destroyBox();
    }
}
