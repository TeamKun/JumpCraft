package net.kunmc.lab.jumpcraft.stage;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;

public class StageManager {
    private final Map<UUID,Stage> stageMap;
    private final Bar bar;

    public StageManager(int x, int y, int firstZ, int centerZ, int lastZ, World world, List<Player> players) {
        stageMap = new HashMap<>();
        bar = new Bar(x + 0.5, x + players.size() + 1.5, y + 1.5, firstZ, lastZ, lastZ, true, world);
        UUID id = UUID.randomUUID();
        setBlock(id,x,y,firstZ,centerZ,lastZ,world);
        x++;
        for (Player player : players) {
            id = player.getUniqueId();
            setBlock(id,x,y,firstZ,centerZ,lastZ,world);
            x++;
        }
        id = UUID.randomUUID();
        setBlock(id,x,y,firstZ,centerZ,lastZ,world);
    }

    private void setBlock(UUID id, int x, int y, int firstZ, int centerZ, int lastZ, World world) {
        Material block = x % 2 == 0 ? Material.GRAY_CONCRETE : Material.WHITE_CONCRETE;
        stageMap.put(id, new Stage(x,y,firstZ,centerZ,lastZ,block));
        Stage stage = stageMap.get(id);
        for(int z = firstZ; z  <= lastZ; z++) {
            Block b = world.getBlockAt(x,y,z);
            b.setType(block);
            stage.getBlocks().add(b);
        }
    }

    public void destroyStage(UUID id) {
        Stage stage = stageMap.get(id);
        stage.getBlocks().forEach(block -> block.setType(Material.AIR));
    }

    public void fixStage(UUID id) {
        Stage stage = stageMap.get(id);
        stage.getBlocks().stream().filter(block -> block.getType() == Material.AIR)
                .forEach(block -> block.setType(stage.getBlock()));
    }

    public boolean spawnBarParticle(double speed, boolean isPause) {
        for(double x = bar.getFirstX(); x <= bar.getLastX(); x += 0.1) {
            bar.getWorld().spawnParticle(Particle.SWEEP_ATTACK, x, bar.getY(), bar.getNowZ(), 1,0,0,0,0);
        }
        if(isPause) {
            return false;
        }
        if(bar.getFirstZ() > bar.getNowZ()) {
            bar.setReverse(false);
            bar.setNowZ(bar.getFirstZ());
            return true;
        }
        if(bar.getNowZ() > bar.getLastZ()) {
            bar.setReverse(true);
            bar.setNowZ(bar.getLastZ());
            return true;
        }
        if(bar.isReverse()) {
            bar.setNowZ(bar.getNowZ() - speed);
            return false;
        }
        bar.setNowZ(bar.getNowZ() + speed);
        return false;
    }

    public void destroyAllStage() {
        stageMap.values().forEach(stage ->
            stage.getBlocks().forEach(block -> block.setType(Material.AIR))
        );
    }

    public Map<UUID, Stage> getStageMap() {
        return stageMap;
    }

    public Bar getBar() {
        return bar;
    }
}
