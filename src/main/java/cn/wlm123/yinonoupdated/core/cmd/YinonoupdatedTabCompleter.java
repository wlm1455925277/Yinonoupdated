package cn.wlm123.yinonoupdated.core.cmd;

import cn.wlm123.yinonoupdated.Yinonoupdated;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.*;
import java.util.stream.Collectors;

public class YinonoupdatedTabCompleter implements TabCompleter {
    private final Yinonoupdated plugin;
    private final List<String> SUBS = List.of("reload","resim","toggle","tool","addwl","delwl","addfilter","delfilter");
    private final List<String> RESIM_HEAD = List.of("all");
    private final List<String> TOOL_ARGS  = List.of("whitelist","filter");

    public YinonoupdatedTabCompleter(Yinonoupdated plugin) { this.plugin = plugin; }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("yinonoupdated.admin")) return Collections.emptyList();

        if (args.length == 1) return prefixFilter(SUBS, args[0]);

        if (args.length == 2) {
            String sub = args[0].toLowerCase(Locale.ROOT);
            switch (sub) {
                case "resim" -> {
                    List<String> allMats = new ArrayList<>(RESIM_HEAD);
                    for (Material m : Material.values()) allMats.add(m.name());
                    return prefixFilter(allMats, args[1]);
                }
                case "toggle" -> {
                    List<String> worlds = Bukkit.getWorlds().stream().map(w -> w.getName()).collect(Collectors.toList());
                    return prefixFilter(worlds, args[1]);
                }
                case "tool" -> { return prefixFilter(TOOL_ARGS, args[1]); }
                case "addwl", "delwl", "addfilter", "delfilter" -> {
                    List<String> mats = Arrays.stream(Material.values()).map(Enum::name).toList();
                    return prefixFilter(mats, args[1]);
                }
            }
        }
        return Collections.emptyList();
    }

    private List<String> prefixFilter(Collection<String> source, String prefixRaw) {
        String prefix = prefixRaw == null ? "" : prefixRaw.toUpperCase(Locale.ROOT);
        return source.stream()
                .filter(s -> s != null && s.toUpperCase(Locale.ROOT).startsWith(prefix))
                .sorted()
                .limit(100)
                .collect(Collectors.toList());
    }
}
