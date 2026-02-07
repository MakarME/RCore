package org.rebelland.rcore.listener;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.persistence.PersistentDataType;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.plugin.SimplePlugin;

@AutoRegister
public final class ItemListener implements Listener {

    private final NamespacedKey key = new NamespacedKey(SimplePlugin.getInstance(), "rcore_floating_item");

    /**
     * 1. Запрещаем деспавн (исчезновение через 5 минут)
     */
    @EventHandler
    public void onDespawn(ItemDespawnEvent e) {
        if (isFloatingItem(e.getEntity())) {
            e.setCancelled(true);
        }
    }

    /**
     * 2. Запрещаем объединение (стаканье) предметов
     * Иначе два декоративных меча сольются в один стак x2
     */
    @EventHandler
    public void onMerge(ItemMergeEvent e) {
        if (isFloatingItem(e.getEntity()) || isFloatingItem(e.getTarget())) {
            e.setCancelled(true);
        }
    }

    /**
     * 3. Запрещаем воронкам засасывать эти предметы
     */
    @EventHandler
    public void onHopperPickup(InventoryPickupItemEvent e) {
        if (isFloatingItem(e.getItem())) {
            e.setCancelled(true);
        }
    }

    // Вспомогательный метод проверки метки
    private boolean isFloatingItem(org.bukkit.entity.Item item) {
        return item.getPersistentDataContainer().has(key, PersistentDataType.BYTE);
    }
}
