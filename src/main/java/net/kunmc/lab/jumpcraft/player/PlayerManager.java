package net.kunmc.lab.jumpcraft.player;

import net.kunmc.lab.jumpcraft.JumpCraft;
import net.kunmc.lab.jumpcraft.stage.Stage;
import net.kunmc.lab.jumpcraft.stage.StageManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {
    private Map<UUID,PlayerData> playerMap;
    private Map<String,TeamData> teamMap;
    private Scoreboard scoreboard;

    public PlayerManager() {
        playerMap = new HashMap<>();
        teamMap = new HashMap<>();
        scoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
        scoreboard.registerNewObjective("point","dummy", Component.text("Point"))
                .setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    private void initScoreBoard() {
        scoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
        scoreboard.registerNewObjective("point","dummy", Component.text("Point"))
                .setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void setPlayerMap(List<Player> players, boolean isTeamMode) {
        playerMap = new HashMap<>();
        teamMap = new HashMap<>();
        initScoreBoard();
        if(isTeamMode) {
            for(Player player : players) {
                String teamName = "無所属";
                for(Team team : Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeams()) {
                    if(team.hasEntry(player.getName())) {
                        teamName = team.getName();
                        break;
                    }
                }
                if(!teamMap.containsKey(teamName)) {
                    teamMap.put(teamName,new TeamData());
                }
                teamMap.get(teamName).addMember(player);
            }
            return;
        }
        players.forEach(player -> {
            playerMap.put(player.getUniqueId(), new PlayerData());
        });
    }

    public void addPoint(boolean isTeamMode) {
        if(isTeamMode) {
            teamMap.forEach((teamName,teamData) -> {
                teamData.getMembers().forEach(player -> {
                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL,1,1);
                });
                if(!teamData.isDead()) {
                    teamData.setPoint(teamData.getPoint() + 1);
                }
            });
            return;
        }
        playerMap.forEach((id,playerData) -> {
            if(!playerData.isDead()) {
                playerData.setPoint(playerData.getPoint() + 1);
            }
            Player player = Bukkit.getServer().getPlayer(id);
            if(player == null) {
                return;
            }
            player.getWorld().playSound(player.getLocation(),Sound.BLOCK_NOTE_BLOCK_BELL,1,1);
        });
    }

    public void checkHitBar(double y, double z, double speed, boolean isTeamMode, boolean isBattleRoyalMode) {
        if (isTeamMode) {
            for(String teamName : teamMap.keySet()) {
                if(isFinish(true,false)) {
                    JumpCraft.instance.setFinish();
                    break;
                }
                TeamData teamData = teamMap.get(teamName);
                if(teamData.isDead()) {
                    return;
                }
                boolean isHitBar = false;
                for (Player player : teamData.getMembers()) {
                    isHitBar = isHitBar(y, z, speed,player);
                    if(isHitBar) {
                        break;
                    }
                }
                if(isHitBar || teamData.getMembers().stream().anyMatch(player -> (!player.isOnline() || player.isDead() || !player.isValid()))) {
                    teamData.setDead(true);
                    teamData.getMembers().forEach(this::killPLayer);
                }
            }
            return;
        }
        for(UUID id : playerMap.keySet()) {
            if(isFinish(false,isBattleRoyalMode)) {
                JumpCraft.instance.setFinish();
                break;
            }
            PlayerData playerData = playerMap.get(id);
            if(playerData.isDead()) {
                return;
            }
            Player player = Bukkit.getServer().getPlayer(id);
            if(player == null) {
                playerData.setDead(true);
                return;
            }
            if(!player.isOnline() || player.isDead() || !player.isValid()) {
                playerData.setDead(true);
                killPLayer(player);
                return;
            }
            boolean isHitBar = isHitBar(y,z,speed,player);
            if(isHitBar) {
                playerData.setDead(true);
                killPLayer(player);
            }
        }
    }

    private boolean isHitBar(double y, double z, double speed, Player player) {
        double py = player.getLocation().getY();
        if(py > y) {
            return false;
        }
        double pz = player.getLocation().getZ();
        double s = 0;
        boolean isReverse = StageManager.instance.getBar().isReverse();
        while (speed > 0) {
            s += Math.min(speed, 0.5);
            speed -= Math.min(speed, 0.5);
            if(isReverse) {
                if(z + s + 0.5 >= pz && pz >= z + s - 0.5) {
                    return true;
                }
            } else {
                if(z - s + 0.5 >= pz && pz >= z - s - 0.5) {
                    return true;
                }
            }
        }
        return false;
    }


    private void killPLayer(Player player) {
        Stage stage = StageManager.instance.getStageMap().get(player.getUniqueId());
        if(stage == null) {
            return;
        }
        forceTp(player,stage);
        StageManager.instance.destroyStage(player.getUniqueId());
    }

    private boolean isFinish(boolean isTeamMode, boolean isBattleRoyalMode) {
        if(isBattleRoyalMode) {
            int count = playerMap.size();
            for(UUID id : playerMap.keySet()) {
                Player player = Bukkit.getServer().getPlayer(id);
                if(player == null) {
                    count--;
                    continue;
                }
                if(playerMap.get(id).isDead()) {
                    count--;
                    continue;
                }
                JumpCraft.instance.setWinMessage("§6" + "勝者" + player.getName(), "Point" + playerMap.get(id).getPoint());
            }
            return count == 1;
        }
        if(isTeamMode) {
            int count = teamMap.keySet().size();
            for(String teamName : teamMap.keySet()) {
                if(teamMap.get(teamName).isDead()) {
                    count--;
                    continue;
                }
                JumpCraft.instance.setWinMessage("§6" + "勝者" + teamName,"Point" + teamMap.get(teamName).getPoint());
            }
            return count == 1;
        }
        for(UUID id : playerMap.keySet()) {
            Player player = Bukkit.getServer().getPlayer(id);
            if(player == null) {
                continue;
            }
            if(playerMap.get(id).isDead()) {
                JumpCraft.instance.setWinMessage("§c" + "戦犯" + player.getName(), "Point" + playerMap.get(id).getPoint());
                return true;
            }
        }
        return false;
    }

    public void fixPos(boolean isTeamMode, boolean isZFix) {
        if(isTeamMode) {
            for(TeamData teamData : teamMap.values()) {
                if(teamData.isDead()) {
                    continue;
                }
                teamData.getMembers().forEach(player -> {
                    fix(player,isZFix);
                });
            }
            return;
        }
        for(UUID id : playerMap.keySet()) {
            Player player = Bukkit.getServer().getPlayer(id);
            if(player == null) {
                continue;
            }
            if(playerMap.get(id).isDead()) {
                continue;
            }
            fix(player,isZFix);
        }
    }

    private void fix(Player player, boolean isZFix) {
        Stage stage = StageManager.instance.getStageMap().get(player.getUniqueId());
        if(stage == null) {
            return;
        }
        StageManager.instance.generate(player.getUniqueId());
        if(stage.getY() > player.getLocation().getY() + 5) {
            forceTp(player,stage);
            return;
        }
        int diffX = (int) player.getLocation().getX() -  (stage.getX() + 1);
        if(diffX != 0) {
            //player.setVelocity(player.getVelocity().add(new Vector(diffX * -1,0,0).normalize().multiply(0.2)));
            forceTp(player,stage);
        }
        if(isZFix){
            int diffZ = (int) player.getLocation().getZ() - stage.getCZ();
            if(diffZ != 0) {
                //player.setVelocity(player.getVelocity().add(new Vector(0,0,diffZ * -1).normalize().multiply(0.2)));
                forceTp(player,stage);
            }
        }
    }

    private void forceTp(Player player,Stage stage) {
        Vector dir = player.getLocation().getDirection();
        player.teleport(new Location(player.getWorld(),stage.getX() + 0.5,stage.getY() + 1,stage.getCZ()).setDirection(dir));
    }

    public void displayScore(boolean isTeamMode, boolean isBattleMode) {
        setScore(isTeamMode,isBattleMode);
        Bukkit.getServer().getOnlinePlayers().forEach(player -> player.setScoreboard(scoreboard));
    }

    private void setScore(boolean isTeamMode, boolean isBattleRoyalMode) {
        if(isBattleRoyalMode) {
            playerMap.forEach((id,playerData) -> {
                Player player = Bukkit.getServer().getPlayer(id);
                if(player == null) {
                    return;
                }
                scoreboard.getObjective("point").getScore(player.getName()).setScore(playerData.getPoint());
            });
            return;
        }
        if(isTeamMode) {
            teamMap.forEach((teamName,teamData) -> {
                scoreboard.getObjective("point").getScore(teamName).setScore(teamData.getPoint());
            });
            return;
        }
        for(PlayerData playerData : playerMap.values()) {
            scoreboard.getObjective("point").getScore("total").setScore(playerData.getPoint());
            break;
        }
    }
}
