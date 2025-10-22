package cn.wlm123.yinonoupdated.core.cmd;

import cn.wlm123.yinonoupdated.Yinonoupdated;
import cn.wlm123.yinonoupdated.conf.PluginConfig;
import cn.wlm123.yinonoupdated.core.cmd.ToolWand.WandType;
import org.bukkit.Material;
import org.bukkit.command.*;

public class YinonoupdatedCommand implements CommandExecutor {
    private final Yinonoupdated plugin;

    public YinonoupdatedCommand(Yinonoupdated plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (!s.hasPermission("yinonoupdated.admin")) { s.sendMessage("§c你没有权限。"); return true; }
        if (args.length == 0) { help(s); return true; }

        PluginConfig cfg = plugin.cfg();
        switch (args[0].toLowerCase()) {
            case "reload" -> {
                plugin.reloadAll();
                s.sendMessage("§a配置已重载。当前启用世界：§e" + String.join(", ", cfg.enabledWorlds));
                return true;
            }
            case "resim" -> {
                if (args.length < 2) { s.sendMessage("§e用法: /" + label + " resim <all|MATERIAL>"); return true; }
                Material m = "all".equalsIgnoreCase(args[1]) ? null : Material.matchMaterial(args[1]);
                if (m == null && !"all".equalsIgnoreCase(args[1])) { s.sendMessage("§c材料名无效: " + args[1]); return true; }
                plugin.dirty().resimulate(plugin, m);
                s.sendMessage("§a开始重算 " + (m==null ? "§eALL§a" : "§e"+m+"§a") + "。");
                return true;
            }
            case "toggle" -> {
                if (args.length < 2) { s.sendMessage("§e用法: /" + label + " toggle <world>"); return true; }
                boolean on = cfg.toggleWorld(args[1]);
                cfg.saveBack(plugin);
                s.sendMessage("§a世界 §e" + args[1] + " §a已" + (on ? "启用" : "停用") + "拦截。");
                return true;
            }
            case "tool" -> {
                if (!(s instanceof org.bukkit.entity.Player p)) { s.sendMessage("§c该命令只能由玩家执行。"); return true; }
                if (args.length < 2) { s.sendMessage("§e用法: /" + label + " tool <whitelist|filter>"); return true; }
                WandType type = switch (args[1].toLowerCase()) {
                    case "whitelist" -> WandType.WHITELIST;
                    case "filter" -> WandType.FILTER;
                    default -> null;
                };
                if (type == null) { s.sendMessage("§e用法: /" + label + " tool <whitelist|filter>"); return true; }
                p.getInventory().addItem(plugin.getToolWand().makeWand(type));
                p.sendMessage("§a已发放工具：" + (type==WandType.WHITELIST ? "§a白名单工具" : "§b过滤工具") + " §7(左键添加/右键删除)");
                return true;
            }
            case "addwl" -> {
                if (args.length < 2) { s.sendMessage("§e用法: /" + label + " addwl <MATERIAL>"); return true; }
                Material m = Material.matchMaterial(args[1]);
                if (m == null) { s.sendMessage("§c材料名无效。"); return true; }
                if (cfg.addWhitelist(m)) { cfg.saveBack(plugin); s.sendMessage("§a已加入白名单：§e"+m); }
                else s.sendMessage("§7白名单已包含：§e"+m);
                return true;
            }
            case "delwl" -> {
                if (args.length < 2) { s.sendMessage("§e用法: /" + label + " delwl <MATERIAL>"); return true; }
                Material m = Material.matchMaterial(args[1]);
                if (m == null) { s.sendMessage("§c材料名无效。"); return true; }
                if (cfg.removeWhitelist(m)) { cfg.saveBack(plugin); s.sendMessage("§c已从白名单移除：§e"+m); }
                else s.sendMessage("§7白名单不包含：§e"+m);
                return true;
            }
            case "addfilter" -> {
                if (args.length < 2) { s.sendMessage("§e用法: /" + label + " addfilter <MATERIAL>"); return true; }
                Material m = Material.matchMaterial(args[1]);
                if (m == null) { s.sendMessage("§c材料名无效。"); return true; }
                if (cfg.addFilter(m)) { cfg.saveBack(plugin); s.sendMessage("§a已加入过滤列表：§e"+m); }
                else s.sendMessage("§7过滤列表已包含：§e"+m);
                return true;
            }
            case "delfilter" -> {
                if (args.length < 2) { s.sendMessage("§e用法: /" + label + " delfilter <MATERIAL>"); return true; }
                Material m = Material.matchMaterial(args[1]);
                if (m == null) { s.sendMessage("§c材料名无效。"); return true; }
                if (cfg.removeFilter(m)) { cfg.saveBack(plugin); s.sendMessage("§c已从过滤列表移除：§e"+m); }
                else s.sendMessage("§7过滤列表不包含：§e"+m);
                return true;
            }
            default -> { help(s); return true; }
        }
    }

    private void help(CommandSender s) {
        s.sendMessage("""
            §e/yinonoupdated reload §7- 重载配置
            §e/yinonoupdated resim <all|MATERIAL> §7- 补算被压制的更新
            §e/yinonoupdated toggle <world> §7- 启用/停用某世界
            §e/yinonoupdated tool <whitelist|filter> §7- 领取工具（左加右删）
            §e/yinonoupdated addwl <MATERIAL> / delwl <MATERIAL>
            §e/yinonoupdated addfilter <MATERIAL> / delfilter <MATERIAL>
            """);
    }
}
