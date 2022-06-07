package willie.customdungeon;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class CustomDungeon extends JavaPlugin {
    public static JavaPlugin instance;


    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        if (Bukkit.getPluginCommand("customdungeon") != null) {
            Bukkit.getPluginCommand("customdungeon").setExecutor(new Commander());
        }
        Objects.requireNonNull(Bukkit.getPluginCommand("customdungeon")).setTabCompleter(new TabHandler());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        for(Player p : Bukkit.getOnlinePlayers()){
            if(Commander.back_location.get(p)!=null){
                p.teleport(Commander.back_location.get(p));
            }
            if(Commander.back_spawn.get(p)!=null){
                p.setBedSpawnLocation(Commander.back_spawn.get(p),true);
            }
        }

    }
}
