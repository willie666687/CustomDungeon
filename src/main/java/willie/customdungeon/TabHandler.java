package willie.customdungeon;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.StringUtil;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.stream.Collector;

public class TabHandler implements TabCompleter {

    private static final Map<UUID, Boolean> QUERY_BUFFER = new HashMap<>();


    @Override
    @ParametersAreNonnullByDefault
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length>=2 && Objects.equals(args[0], "edit")){
            FileConfiguration config = CustomDungeon.instance.getConfig();
            ConfigurationSection dungeons = config.getConfigurationSection("dungeons");
            List<String> keys = dungeons.getKeys(false).stream().toList();
            return keys;
        }else if (args.length>=2){
            return null;
        }
        if (args.length == 1){
            Enumeration commands;
            Vector c = new Vector();
            c.add("create");
            c.add("edit");
            c.add("exit");
            commands = c.elements();
            return Collections.list(commands);
        }
        return Collections.singletonList("");
    }
}
