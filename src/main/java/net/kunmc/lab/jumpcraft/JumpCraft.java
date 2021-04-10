package net.kunmc.lab.jumpcraft;

import org.bukkit.plugin.java.JavaPlugin;

public final class JumpCraft extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("起動しました");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
