// src/main/java/cn/wlm123/yinonoupdated/conf/PluginConfig.java
package cn.wlm123.yinonoupdated.conf;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.stream.Collectors;

public class PluginConfig {
    public final Set<String> enabledWorlds;

    public final boolean blockPhysics;
    public final boolean blockFluids;
    public final boolean blockGrowth;

    public final Set<Material> whitelist;
    public final Set<Material> materialsFilter;

    public final boolean dirtyEnabled;
    public final int maxPerWorld;
    public final long entryTtlMs;
    public final int resimBatch;

    private PluginConfig(Set<String> worlds, boolean phys, boolean fluids, boolean growth,
                         Set<Material> whitelist, Set<Material> filter,
                         boolean dirtyEnabled, int maxPerWorld, long entryTtlMs, int resimBatch) {
        this.enabledWorlds = worlds;
        this.blockPhysics = phys;
        this.blockFluids = fluids;
        this.blockGrowth = growth;
        this.whitelist = whitelist;
        this.materialsFilter = filter;
        this.dirtyEnabled = dirtyEnabled;
        this.maxPerWorld = maxPerWorld;
        this.entryTtlMs = entryTtlMs;
        this.resimBatch = resimBatch;
    }

    public static PluginConfig load(FileConfiguration c) {
        Set<String> worlds = new HashSet<>(c.getStringList("enabled-worlds"));

        boolean phys = c.getBoolean("block.physics", true);
        boolean flu  = c.getBoolean("block.fluids",  true);
        boolean gro  = c.getBoolean("block.growth",  true);

        Set<Material> wl = toMatSet(c.getStringList("whitelist"));
        Set<Material> mf = toMatSet(c.getStringList("materials-filter"));

        boolean dEn = c.getBoolean("dirty-index.enabled", true);
        int max = c.getInt("dirty-index.max-per-world", 5000);
        long ttl = c.getInt("dirty-index.entry-ttl-seconds", 600) * 1000L;
        int batch = c.getInt("dirty-index.resim-batch", 200);

        return new PluginConfig(worlds, phys, flu, gro, wl, mf, dEn, max, ttl, batch);
    }

    private static Set<Material> toMatSet(List<String> names) {
        Set<Material> s = new HashSet<>();
        for (String n : names) {
            Material m = Material.matchMaterial(n);
            if (m != null) s.add(m);
        }
        return s;
    }

    public boolean toggleWorld(String w) {
        if (enabledWorlds.contains(w)) { enabledWorlds.remove(w); return false; }
        enabledWorlds.add(w); return true;
    }

    /* ---------------- 写回到 config.yml 的工具方法 ---------------- */

    public void saveBack(Plugin plugin) {
        FileConfiguration c = plugin.getConfig();
        c.set("enabled-worlds", new ArrayList<>(enabledWorlds));
        c.set("block.physics", blockPhysics);
        c.set("block.fluids",  blockFluids);
        c.set("block.growth",  blockGrowth);
        c.set("whitelist", whitelist.stream().map(Enum::name).collect(Collectors.toList()));
        c.set("materials-filter", materialsFilter.stream().map(Enum::name).collect(Collectors.toList()));
        c.set("dirty-index.enabled", dirtyEnabled);
        c.set("dirty-index.max-per-world", maxPerWorld);
        c.set("dirty-index.entry-ttl-seconds", (int)(entryTtlMs / 1000L));
        c.set("dirty-index.resim-batch", resimBatch);
        plugin.saveConfig();
    }

    public boolean addWhitelist(Material m) { return whitelist.add(m); }
    public boolean removeWhitelist(Material m) { return whitelist.remove(m); }
    public boolean addFilter(Material m) { return materialsFilter.add(m); }
    public boolean removeFilter(Material m) { return materialsFilter.remove(m); }
}
