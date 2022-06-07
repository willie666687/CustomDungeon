package willie.customdungeon;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

import static org.bukkit.Bukkit.getWorld;
import static org.bukkit.GameRule.*;

public class Commander implements CommandExecutor {
    public static FileConfiguration config = CustomDungeon.instance.getConfig();
    public static Map<Player,Location> back_location = new HashMap<Player, Location>();
    public static Map<Player,Location> back_spawn = new HashMap<>();
    @Override
    @ParametersAreNonnullByDefault
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (Objects.equals(args[0], "create")) {
            String dungeon_name = args[1].toLowerCase();
            CustomDungeon.instance.saveConfig();
            FileConfiguration config = CustomDungeon.instance.getConfig();
            Player player = (Player) sender;

            if (config.contains("dungeons." + dungeon_name)){
                sender.sendMessage(ChatColor.WHITE+"[CustomDungeon]"+ChatColor.RED + "Already a dungeon named '"+dungeon_name+"'");
            }else if (!config.contains("dungeons." + player.getWorld().getName())){
                config.set("dungeons."+dungeon_name+".active",true);
                player.sendMessage(ChatColor.WHITE+"[CustomDungeon]"+ChatColor.GREEN + "Creating dungeon '"+dungeon_name+"'");
                CustomDungeon.instance.saveConfig();
                WorldCreator wc = new WorldCreator(dungeon_name);

                wc.environment(World.Environment.NORMAL);
                wc.type(WorldType.FLAT);
                wc.generateStructures(false);

                wc.createWorld();
                Location l = new Location(Bukkit.getWorld(dungeon_name),0,-60,0);
                Bukkit.getWorld(dungeon_name).setGameRule(DO_MOB_SPAWNING,false);
                Bukkit.getWorld(dungeon_name).setGameRule(MOB_GRIEFING,false);
                Bukkit.getWorld(dungeon_name).setGameRule(KEEP_INVENTORY,true);
                Bukkit.getWorld(dungeon_name).setSpawnLocation(0,-60,0);
                back_location.put(player,player.getLocation());
                back_spawn.put(player,player.getBedSpawnLocation());
                player.teleport(l);
                player.setBedSpawnLocation(new Location(Bukkit.getWorld(dungeon_name),0,-60,0),true);
                sender.sendMessage(ChatColor.WHITE+"[CustomDungeon]"+ChatColor.GREEN + "created '" + dungeon_name+"'");
            }else{
                player.sendMessage(ChatColor.WHITE+"[CustomDungeon]"+ChatColor.RED + "Already in a dungeon");
            }
            return true;
        }
        if(Objects.equals(args[0],"edit")){
            Player player = (Player) sender;
            String dungeon_name = args[1].toLowerCase();
            CustomDungeon.instance.saveConfig();
            FileConfiguration config = CustomDungeon.instance.getConfig();
            if(config.contains("dungeons." + dungeon_name)&&!config.contains("dungeons." + player.getWorld().getName())){
                back_location.put(player,player.getLocation());
                if(player.getBedSpawnLocation()!=null){
                    back_spawn.put(player,player.getBedSpawnLocation());
                }else{
                    back_spawn.put(player,player.getWorld().getSpawnLocation());
                }
                World world = new WorldCreator(dungeon_name).createWorld();
                player.teleportAsync(new Location(Bukkit.getWorld(dungeon_name),0,-60,0));
                player.setBedSpawnLocation(new Location(Bukkit.getWorld(dungeon_name),0,-60,0),true);
                getWorld(dungeon_name).setSpawnLocation(0,-60,0);
                player.sendMessage(ChatColor.WHITE+"[CustomDungeon]"+ChatColor.RED + "Teleported to '"+dungeon_name+"'");
            }else if (!config.contains("dungeons." + dungeon_name)){
                player.sendMessage(ChatColor.WHITE+"[CustomDungeon]"+ChatColor.RED + "No dungeon named '"+dungeon_name+"'");
            }else if (config.contains("dungeons." + player.getWorld().getName())){
                player.sendMessage(ChatColor.WHITE+"[CustomDungeon]"+ChatColor.RED + "Already in a dungeon");
            }
            return true;
        }
        if(Objects.equals(args[0],"exit")){
            Player player = (Player) sender;
            if(back_location.get(player)!=null){
                player.teleportAsync(back_location.get(player));
                player.setBedSpawnLocation(back_spawn.get(player),true);
                back_location.remove(player);
                back_spawn.remove(player);
            }else{
                player.sendMessage(ChatColor.WHITE+"[CustomDungeon]"+ChatColor.RED + "You are not in a dungeon");
            }

        }
        if(args[0].equals("test")){
            FileConfiguration config = CustomDungeon.instance.getConfig();
            ConfigurationSection dungeons = config.getConfigurationSection("dungeons");
            List<String> keys = dungeons.getKeys(false).stream().toList();
            sender.sendMessage(keys.toString());
        }
        return true;
    }
}
