package net.kunmc.lab.jumpcraft.stage;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StageManager {
    public static StageManager instance;
    private Map<UUID,Stage> stageMap;
    private Bar bar;
    private World world;
    public StageManager() {
        instance = this;
        stageMap = new HashMap<>();
    }

    public void generateStage(List<Player> players, int x, int y, int fz, int length, World world) {
        this.world = world;
        int fx = x;
        destroyAllStage();
        setBlock(UUID.randomUUID(),x,y,fz,length);
        x++;
        for(Player player : players) {
            setBlock(player.getUniqueId(),x,y,fz,length);
            x++;
        }
        setBlock(UUID.randomUUID(),x,y,fz,length);
        bar = new Bar(fx + 0.5,y + 1.5, fz, x + 0.5, length);
    }

    private void setBlock(UUID id, int x, int y, int fz, int length) {
        stageMap.put(id,new Stage(x,y,fz,length));
        Material block = x % 2 == 0 ? Material.GRAY_CONCRETE : Material.WHITE_CONCRETE;
        for(int z = fz; z < length + fz; z++) {
            world.getBlockAt(x,y,z).setType(block);
        }
    }

    public Map<UUID, Stage> getStageMap() {
        return stageMap;
    }

    public Bar getBar() {
        return bar;
    }

    public boolean moveBar(double speed) {
        spawnBar();
        if(bar.getFz() > bar.getZ()) {
            bar.setReverse(false);
            bar.setZ(bar.getFz());
            return true;
        }
        if(bar.getZ() > bar.getLz()) {
            bar.setReverse(true);
            bar.setZ(bar.getLz());
            return true;
        }
        if(bar.isReverse()) {
            bar.setZ(bar.getZ() - speed);
            return false;
        }
        bar.setZ(bar.getZ() + speed);
        return false;
    }

    private void spawnBar() {
        if(world == null){return;}
        for(double x = bar.getFx(); x <= bar.getLx(); x+=0.1) {
            world.spawnParticle(Particle.SWEEP_ATTACK, x, bar.getY(), bar.getZ(), 1,0,0,0,0);
        }
    }

    public void destroyStage(UUID id) {
        if(world == null) {return;}
        if(!stageMap.containsKey(id)) {
            return;
        }
        Stage stage = stageMap.get(id);
        for(int z = stage.getFz(); z < stage.getLz(); z++) {
            world.getBlockAt(stage.getX(),stage.getY(),z)
                    .setType(Material.AIR);
        }

    }

    public void destroyAllStage() {
        if(world == null ) { return;}
        stageMap.forEach((id,stage) -> {
            for(int z = stage.getFz(); z < stage.getLz(); z++) {
                world.getBlockAt(stage.getX(),stage.getY(),z)
                        .setType(Material.AIR);
            }
        });
        stageMap = new HashMap<>();
    }
}
