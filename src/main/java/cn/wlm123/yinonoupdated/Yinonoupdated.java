// src/main/java/cn/wlm123/yinonoupdated/Yinonoupdated.java
package cn.wlm123.yinonoupdated;

import cn.wlm123.yinonoupdated.conf.PluginConfig;
import cn.wlm123.yinonoupdated.core.DirtyIndex;
import cn.wlm123.yinonoupdated.core.UpdateBlockerListener;
import cn.wlm123.yinonoupdated.core.cmd.YinonoupdatedCommand;
import cn.wlm123.yinonoupdated.core.cmd.YinonoupdatedTabCompleter;
import cn.wlm123.yinonoupdated.core.cmd.ToolWand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Yinonoupdated extends JavaPlugin {
    private static Yinonoupdated inst;
    private PluginConfig cfg;
    private DirtyIndex dirty;
    private ToolWand toolWand;

    public static Yinonoupdated get() { return inst; }
    public PluginConfig cfg() { return cfg; }
    public DirtyIndex dirty() { return dirty; }
    public ToolWand getToolWand() { return toolWand; }

    @Override
    public void onEnable() {
        inst = this;
        saveDefaultConfig();
        reloadAll();

        Bukkit.getPluginManager().registerEvents(new UpdateBlockerListener(cfg, dirty), this);
        this.toolWand = new ToolWand(this);
        Bukkit.getPluginManager().registerEvents(toolWand, this);

        getCommand("yinonoupdated").setExecutor(new YinonoupdatedCommand(this));
        getCommand("yinonoupdated").setTabCompleter(new YinonoupdatedTabCompleter(this));

        getLogger().info("Yinonoupdated 已启用。");
    }

    public void reloadAll() {
        reloadConfig();
        this.cfg = PluginConfig.load(getConfig());
        this.dirty = new DirtyIndex(cfg);
    }
}
