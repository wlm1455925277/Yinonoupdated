package cn.wlm123.yinonoupdated.core;

import cn.wlm123.yinonoupdated.conf.PluginConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockVector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DirtyIndex {
    record Entry(Material mat, long expireAt) {}
    private final Map<UUID, LinkedHashMap<BlockVector, Entry>> data = new ConcurrentHashMap<>();
    private final PluginConfig cfg;

    public DirtyIndex(PluginConfig cfg) { this.cfg = cfg; }

    public void mark(World w, Block b, Material m) {
        if (!cfg.dirtyEnabled) return;
        var worldMap = data.computeIfAbsent(w.getUID(), k -> new LinkedHashMap<>());

        // 先清理过期项
        if (!worldMap.isEmpty()) {
            Iterator<Map.Entry<BlockVector, Entry>> it = worldMap.entrySet().iterator();
            long now = System.currentTimeMillis();
            while (it.hasNext()) {
                if (it.next().getValue().expireAt() < now) it.remove();
            }
        }

        // 超量裁剪（FIFO）：必须先 next() 再 remove()，否则会抛 IllegalStateException
        int max = Math.max(1, cfg.maxPerWorld);
        if (worldMap.size() >= max) {
            int toRemove = Math.max(1, max / 10);
            Iterator<BlockVector> it = worldMap.keySet().iterator();
            for (int i = 0; i < toRemove && it.hasNext(); i++) {
                it.next();   // 先移动到下一个元素
                it.remove(); // 再删除当前元素
            }
        }

        long ttl = System.currentTimeMillis() + cfg.entryTtlMs;
        worldMap.put(new BlockVector(b.getX(), b.getY(), b.getZ()), new Entry(m, ttl));
    }

    public void resimulate(Plugin plugin, Material only) {
        new BukkitRunnable() {
            @Override public void run() {
                int budget = cfg.resimBatch;
                for (var eWorld : data.entrySet()) {
                    World w = Bukkit.getWorld(eWorld.getKey());
                    if (w == null) continue;

                    Iterator<Map.Entry<BlockVector, Entry>> it = eWorld.getValue().entrySet().iterator();
                    while (it.hasNext() && budget-- > 0) {
                        var e = it.next();
                        var pos = e.getKey();
                        var entry = e.getValue();
                        if (entry.expireAt() < System.currentTimeMillis()) { it.remove(); continue; }
                        if (only != null && entry.mat() != only) continue;

                        Block b = w.getBlockAt(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
                        // 触发一次温和的物理检查（force=true, applyPhysics=true）
                        b.getState().update(true, true);
                        it.remove();
                    }
                }
                if (isEmpty()) cancel();
            }
        }.runTaskTimer(plugin, 1L, 1L);
    }

    public boolean isEmpty() {
        return data.values().stream().allMatch(Map::isEmpty);
    }
}
