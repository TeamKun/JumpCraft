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
    private boolean isNoTeam = false;
    private String winnerName = "";
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
        new ScoreBoard();
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
                            displayScoreboard();
                        }
                    });
                }
                if(!isStart) {
                    return;
                }
                setBarAndCheckPlayerStates();
                displayScoreboard();
                if((ConfigData.getINSTANCE().isBattleRoyalMode() || !isNoTeam) && !isFinish) {
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
                setBarPositionAndAddPoint();
            }
        },0L, 2L);
    }

    private void displayScoreboard() {
        if(ConfigData.getINSTANCE().isBattleRoyalMode()) {
            ScoreBoard.getINSTANCE().disPlayScoreBoard(pInfo,3);
        } else if(isNoTeam) {
            ScoreBoard.getINSTANCE().disPlayScoreBoard(pInfo,1);
        } else {
            ScoreBoard.getINSTANCE().disPlayScoreBoard(pInfo,2);
        }
    }

    private void setBarAndCheckPlayerStates() {
        Location boxLoc = BoxGenerator.getINSTANCE().getBoxLoc();
        if(ConfigData.getINSTANCE().getBarSpeed() >= 1.5) {
            int count = 0;
            double adder = ConfigData.getINSTANCE().getBarSpeed();
            double temp = barPosZAdder;
            while (adder >= 0.5) {
                adder -= 0.5;
                count++;
            }
            for (int i = 0; i < count; i++) {
                if(isReverseBar) {
                    barPosZAdder += 0.5;
                } else {
                    barPosZAdder -= 0.5;
                }

                checkIsFinishAndPlayerStates();
            }
            barPosZAdder = temp;
            addBarPosZ(boxLoc);
            checkIsFinishAndPlayerStates();
            return;
        }
        addBarPosZ(boxLoc);
        checkIsFinishAndPlayerStates();
    }

    private void addBarPosZ(Location boxLoc) {
        for(double barPosXAdder = 0; barPosXAdder < ConfigData.getINSTANCE().getWidthX(); barPosXAdder += 0.1) {
            boxLoc.add(barPosXAdder,1.5,barPosZAdder);
            boxLoc.getWorld().spawnParticle(Particle.SWEEP_ATTACK, boxLoc, 1, 0, 0, 0, 0);
            boxLoc.subtract(barPosXAdder,1.5, barPosZAdder);
        }
    }

    private void checkIsFinishAndPlayerStates() {
        if(ConfigData.getINSTANCE().isBattleRoyalMode() && !isFinish) {
            checkIsFinish();
        }
        if(!isFinish && !isPause) {
            checkPlayerState();
        }
    }

    private void checkPlayerState() {
        Bukkit.getServer().getOnlinePlayers().forEach(player -> {
            if(isNotPlayerValid(player)) {
                return;
            }
            int point = pInfo.get(player.getUniqueId()).getPoint();
            if (point != 0 && point % 10 == 0) {
                player.sendActionBar(Component.text("§6" + "Excellent!"));
            }
            fixPlayerPosition(player);
            Location boxLoc = BoxGenerator.getINSTANCE().getBoxLoc();
            double barPosZ = boxLoc.getZ() + barPosZAdder;
            double barPosY = boxLoc.getY() + 2;
            double playerPosZ = player.getLocation().getZ();
            double playerPosY = player.getLocation().getY();
            if(barPosZ + 0.5 >= playerPosZ && playerPosZ >= barPosZ - 0.5) {
                if(barPosY >= playerPosY) {
                    player.sendMessage("§cあなたは当たりました");
                    if(ConfigData.getINSTANCE().isBattleRoyalMode()) {
                        pInfo.get(player.getUniqueId()).setDead(true);
                        destroyPlayersFloor(player, boxLoc);
                    } else {
                        if(isNoTeam) {
                            player.setFireTicks(1000);
                            isFinish = true;
                            winnerName = "§c" + "戦犯: " + player.getName();
                            winnerPoint = pInfo.get(player.getUniqueId()).getPoint();
                            playSound(Sound.ENTITY_BLAZE_DEATH);
                            return;
                        }
                        String teamName = pInfo.get(player.getUniqueId()).getTeamName();
                        Bukkit.getServer().getOnlinePlayers().forEach(p -> {
                            if(isNotPlayerValid(p)) {
                                return;
                            }
                            if(pInfo.get(p.getUniqueId()).getTeamName().equals(teamName)) {
                                pInfo.get(player.getUniqueId()).setDead(true);
                                destroyPlayersFloor(player, boxLoc);
                            }
                        });
                        checkIsFinish();
                    }
                }
            }
        });
    }

    private boolean isNotPlayerValid(Player player) {
        if(!pInfo.containsKey(player.getUniqueId())) {
            return true;
        }
        if(player.isDead() || !player.isValid() || player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
            return true;
        }
        return pInfo.get(player.getUniqueId()).isDead();
    }

    private void destroyPlayersFloor(Player player, Location boxLoc) {
        for (int addZ = 0; addZ < ConfigData.getINSTANCE().getWidthZ(); addZ++) {
            player.getWorld().getBlockAt((int) pInfo.get(player.getUniqueId()).getLoc().getX() - 1, (int) boxLoc.getY(), (int) boxLoc.getZ() + addZ).setType(Material.AIR);
            player.teleport(pInfo.get(player.getUniqueId()).getLoc());
        }
    }

    private void fixPlayerPosition(Player player) {
        int diffX = (int) player.getLocation().getX() - (int) pInfo.get(player.getUniqueId()).getLoc().getX();
        if (diffX != 0) {
            player.setVelocity(player.getVelocity().add((new Vector(diffX * -1, 0, 0)).normalize().multiply(0.2)));
        }
        if(ConfigData.getINSTANCE().isZFixMode()) {
            int diffZ = (int) player.getLocation().getZ() - (int) pInfo.get(player.getUniqueId()).getLoc().getZ();
            if (diffZ != 0) {
                player.setVelocity(player.getVelocity().add((new Vector(0, 0, diffZ * -1)).normalize().multiply(0.2)));
            }
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
        String winTeam = "empty";
        for (Player player: players) {
            if(!pInfo.containsKey(player.getUniqueId())) {
                return;
            }
            if(player.isDead() || !player.isValid() || player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
                return;
            }
            if(!pInfo.get(player.getUniqueId()).isDead()) {
                if(isNoTeam) {
                    winnerName = "§6" + "勝者: " + player.getName();
                    winnerPoint = pInfo.get(player.getUniqueId()).getPoint();
                    count++;
                } else {
                    if(winTeam.equals("empty")) {
                        winTeam = pInfo.get(player.getUniqueId()).getTeamName();
                        winnerName = "§6" + "勝者: " + winTeam;
                        winnerPoint = pInfo.get(player.getUniqueId()).getPoint();
                        count = 1;
                    } else if (!winTeam.equals(pInfo.get(player.getUniqueId()).getTeamName())) {
                        count = 0;
                    }
                }
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
        isNoTeam = false;
        pInfo = new HashMap<>();
        int addZ = configData.getWidthZ() % 2 == 0 ? configData.getWidthZ() / 2 : (configData.getWidthZ() + 1) / 2;
        List<Player> players = new ArrayList<>(Bukkit.getServer().getOnlinePlayers());
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            if(player.isDead() || !player.isValid() || player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) {
                boxLoc.add(i + 1.5, 1, addZ);
            } else {
                player.teleport(boxLoc.add(i + 1.5, 1, addZ));
            }
            player.setFireTicks(0);
            String teamName = ScoreBoard.getINSTANCE().getPlayersTeamName(player.getName());
            pInfo.put(player.getUniqueId(), new PlayerInfo(boxLoc, teamName));
            if(teamName.equals("empty")) {
                isNoTeam = true;
            }
            boxLoc.subtract(i + 1.5, 1, addZ);
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
