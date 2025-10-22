package cn.wlm123.yinonoupdated.core;

import cn.wlm123.yinonoupdated.conf.PluginConfig;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.world.StructureGrowEvent;

public class UpdateBlockerListener implements Listener {
    private final PluginConfig cfg;
    private final DirtyIndex dirty;

    public UpdateBlockerListener(PluginConfig cfg, DirtyIndex dirty) {
        this.cfg = cfg; this.dirty = dirty;
    }

    /** 是否在插件控制范围内（世界正确、材料命中过滤，且方块本体不是白名单） */
    private boolean inScope(Block b) {
        World w = b.getWorld();
        if (!cfg.enabledWorlds.contains(w.getName())) return false;

        Material type = b.getType();
        // 白名单方块本体：一律放行（不进入拦截流程）
        if (cfg.whitelist.contains(type)) return false;

        // 如果设置了 materials-filter，就只拦其中列出的材料；否则按类型总开关
        if (!cfg.materialsFilter.isEmpty() && !cfg.materialsFilter.contains(type)) return false;

        return true;
    }

    /** 仅放行“白名单方块的底部方块”的更新：即 base 上方一格是白名单 */
    private boolean isSupportingWhitelisted(Block base) {
        Block above = base.getRelative(BlockFace.UP);
        return cfg.whitelist.contains(above.getType());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPhysics(BlockPhysicsEvent e) {
        if (!cfg.blockPhysics) return;
        Block b = e.getBlock();
        if (!inScope(b)) return;

        // ★ 只放行“白名单方块正下方”的方块更新
        if (isSupportingWhitelisted(b)) return;

        e.setCancelled(true);
        dirty.mark(b.getWorld(), b, b.getType());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onFromTo(BlockFromToEvent e) {
        if (!cfg.blockFluids) return;
        Block b = e.getBlock();
        if (!inScope(b)) return;

        // ★ 水/岩浆从该块流出时，如果它正上方是白名单方块（例如甘蔗底部方块），放行
        if (isSupportingWhitelisted(b)) return;

        e.setCancelled(true);
        dirty.mark(b.getWorld(), b, b.getType());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onGrow(BlockGrowEvent e) {
        if (!cfg.blockGrowth) return;
        Block b = e.getBlock();
        // 白名单方块本体（如 SUGAR_CANE）允许生长
        if (cfg.whitelist.contains(b.getType())) return;

        if (!inScope(b)) return;

        // 不是白名单本体，但若它正上方是白名单（即作为“白名单底座”），也放行
        if (isSupportingWhitelisted(b)) return;

        e.setCancelled(true);
        dirty.mark(b.getWorld(), b, b.getType());
    }

    // 可选：蔓延（苔藓/草/菌丝等），通常与“底座”逻辑无关，按开关拦
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onSpread(BlockSpreadEvent e) {
        if (!cfg.blockGrowth) return;
        Block b = e.getBlock();
        if (!inScope(b)) return;

        // 仅当它正上方是白名单方块，才放行（极少见；保持一致性）
        if (isSupportingWhitelisted(b)) return;

        e.setCancelled(true);
        dirty.mark(b.getWorld(), b, b.getType());
    }

    // 大型结构生成（树等），直接跟随总开关
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onStructure(StructureGrowEvent e) {
        if (!cfg.blockGrowth) return;
        if (!cfg.enabledWorlds.contains(e.getWorld().getName())) return;

        e.setCancelled(true);
        // 结构生成涉及大量方块，不逐个 mark；需要时管理员用 /noupdate resim all
    }

    // 实体改动方块（落沙、末影人等）
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityChange(EntityChangeBlockEvent e) {
        if (!cfg.blockPhysics) return;
        Block b = e.getBlock();
        if (!cfg.enabledWorlds.contains(b.getWorld().getName())) return;

        // 白名单本体或“白名单底座”放行
        if (cfg.whitelist.contains(b.getType()) || isSupportingWhitelisted(b)) return;

        // materials-filter 逻辑与 inScope 一致，这里复核一次
        if (!cfg.materialsFilter.isEmpty() && !cfg.materialsFilter.contains(b.getType())) return;

        e.setCancelled(true);
        dirty.mark(b.getWorld(), b, (e.getTo() == null ? b.getType() : e.getTo()));
    }
}
