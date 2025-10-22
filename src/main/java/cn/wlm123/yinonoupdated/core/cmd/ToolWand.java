// src/main/java/cn/wlm123/yinonoupdated/core/cmd/ToolWand.java
package cn.wlm123.yinonoupdated.core.cmd;

import cn.wlm123.yinonoupdated.Yinonoupdated;
import cn.wlm123.yinonoupdated.conf.PluginConfig;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*; // 确保有 List/Arrays/Map 等

public class ToolWand implements Listener {
    public enum WandType { WHITELIST, FILTER }

    private static final NamespacedKey KEY_WAND = new NamespacedKey(Yinonoupdated.get(), "wand-type");
    private static final Map<UUID, Long> COOLDOWN = new HashMap<>();
    private static final long CD_MS = 150;

    private final Yinonoupdated plugin;

    public ToolWand(Yinonoupdated plugin) { this.plugin = plugin; }

    /* 领取工具 */
    public ItemStack makeWand(WandType type) {
        ItemStack is = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = is.getItemMeta();
        meta.getPersistentDataContainer().set(KEY_WAND, PersistentDataType.STRING, type.name());
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addEnchant(Enchantment.LUCK_OF_THE_SEA, 1, true);

        if (type == WandType.WHITELIST) {
            meta.setDisplayName("§a白名单工具 §7(左键添加/右键删除)");
            meta.setLore(Arrays.asList("§7修改 config.yml 的 whitelist", "§7准星对准方块进行操作"));
        } else {
            meta.setDisplayName("§b过滤工具 §7(左键添加/右键删除)");
            meta.setLore(Arrays.asList("§7修改 config.yml 的 materials-filter", "§7准星对准方块进行操作"));
        }
        is.setItemMeta(meta);
        return is;
    }

    public static Optional<WandType> getWandType(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return Optional.empty();
        String val = item.getItemMeta().getPersistentDataContainer().get(KEY_WAND, PersistentDataType.STRING);
        if (val == null) return Optional.empty();
        try { return Optional.of(WandType.valueOf(val)); } catch (IllegalArgumentException e) { return Optional.empty(); }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return; // 只主手
        Player p = e.getPlayer();
        Optional<WandType> wt = getWandType(p.getInventory().getItemInMainHand());
        if (wt.isEmpty()) return;

        // 冷却，避免一次交互触发两次
        long now = System.currentTimeMillis();
        long last = COOLDOWN.getOrDefault(p.getUniqueId(), 0L);
        if (now - last < CD_MS) { e.setCancelled(true); return; }
        COOLDOWN.put(p.getUniqueId(), now);

        Action act = e.getAction();
        if (act != Action.LEFT_CLICK_AIR && act != Action.LEFT_CLICK_BLOCK
                && act != Action.RIGHT_CLICK_AIR && act != Action.RIGHT_CLICK_BLOCK) return;

        Block target = p.getTargetBlockExact(6); // 看 6 格内
        if (target == null || target.getType().isAir()) {
            p.sendMessage("§e未找到目标方块。请对准方块。");
            return;
        }

        Material m = target.getType();
        PluginConfig cfg = plugin.cfg();
        boolean changed = false;

        if (wt.get() == WandType.WHITELIST) {
            if (act == Action.LEFT_CLICK_AIR || act == Action.LEFT_CLICK_BLOCK) {
                if (cfg.addWhitelist(m)) { changed = true; p.sendMessage("§a已加入白名单：§e" + m); }
                else p.sendMessage("§7白名单已包含：§e" + m);
            } else {
                if (cfg.removeWhitelist(m)) { changed = true; p.sendMessage("§c已从白名单移除：§e" + m); }
                else p.sendMessage("§7白名单不包含：§e" + m);
            }
        } else { // FILTER
            if (act == Action.LEFT_CLICK_AIR || act == Action.LEFT_CLICK_BLOCK) {
                if (cfg.addFilter(m)) { changed = true; p.sendMessage("§a已加入过滤列表：§e" + m); }
                else p.sendMessage("§7过滤列表已包含：§e" + m);
            } else {
                if (cfg.removeFilter(m)) { changed = true; p.sendMessage("§c已从过滤列表移除：§e" + m); }
                else p.sendMessage("§7过滤列表不包含：§e" + m);
            }
        }

        if (changed) {
            cfg.saveBack(plugin);                           // 写回并保存
            p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.6f, 1.6f);
            // 热更新：不需要重启监听器；我们的逻辑直接读 cfg 集合引用
            p.sendMessage("§a配置已保存并生效。");
        }
        e.setCancelled(true);
    }
}
