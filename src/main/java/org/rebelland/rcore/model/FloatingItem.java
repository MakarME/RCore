package org.rebelland.rcore.model;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.rebelland.rcore.config.ItemConfig;

import java.util.UUID;

public class FloatingItem {

    private final String id;
    private Location location;
    private ItemStack itemStack;
    private String customName;
    private Particle particle;

    private Item itemEntity;
    private boolean isTemporary; // Если true - не сохраняется в конфиг
    private boolean isStatic;    // Если true - висит в воздухе и нельзя подобрать

    // Конструктор для СТАТИЧНЫХ предметов (из конфига или команды создания декора)
    public FloatingItem(String id, Location location, ItemStack itemStack) {
        this.id = id;
        this.location = location;
        this.itemStack = itemStack;
        this.isTemporary = false;
        this.isStatic = true;
    }

    // Конструктор для ВРЕМЕННЫХ предметов (обертка над существующим дропом)
    public FloatingItem(Item existingItem) {
        this.id = "temp_item_" + UUID.randomUUID();
        this.location = existingItem.getLocation();
        this.itemStack = existingItem.getItemStack();
        this.itemEntity = existingItem;
        this.isTemporary = true;
        this.isStatic = false; // Это обычный предмет, его можно подобрать
    }

    public void spawn() {
        if (isSpawned()) return;
        if (location == null || location.getWorld() == null) return;

        if (isStatic) {
            cleanupOrphans();

            this.itemEntity = location.getWorld().dropItem(location, itemStack);
            this.itemEntity.setPickupDelay(Integer.MAX_VALUE);
            this.itemEntity.setGravity(false);
            this.itemEntity.setVelocity(new Vector(0, 0, 0));
            this.itemEntity.setInvulnerable(true);
            this.itemEntity.setPersistent(false);

            // --- НОВОЕ: Ставим метку "rcore_floating_item" ---
            NamespacedKey key = new NamespacedKey(SimplePlugin.getInstance(), "rcore_floating_item");
            this.itemEntity.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
            // ------------------------------------------------
        }

        updateVisuals();
    }

    public boolean isSpawned() {
        return itemEntity != null && itemEntity.isValid();
    }

    public void updateVisuals() {
        if (itemEntity == null || !itemEntity.isValid()) return;

        if (customName != null && !customName.isEmpty()) {
            itemEntity.setCustomName(Common.colorize(customName));
            itemEntity.setCustomNameVisible(true);
        } else {
            itemEntity.setCustomNameVisible(false);
        }

        if (isStatic) {
            // Телепорт на всякий случай, если сдвинули
            if (itemEntity.getLocation().distanceSquared(location) > 0.01) {
                itemEntity.teleport(location);
                itemEntity.setVelocity(new Vector(0, 0, 0));
            }
        }
    }

    public void onTick() {
        // Проверка валидности предмета
        if (itemEntity == null || !itemEntity.isValid()) {
            if (isStatic && location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
                // Если статический предмет пропал (например, /kill @e), восстанавливаем
                if (!isSpawned()) spawn();
            }
            return;
        }

        // Оптимизация: если чанк не прогружен или рядом нет игроков - не тратим ресурсы
        if (!itemEntity.getLocation().isWorldLoaded()) return;
        if (!hasPlayersNearby(itemEntity.getLocation(), 20)) return;

        // СПАВН ЧАСТИЦ
        if (particle != null) {
            Location loc = itemEntity.getLocation().add(0, 0.5, 0); // Центр предмета

            // Исправление отображения:
            // count: 3 (количество частиц за раз)
            // offset X/Y/Z: 0.3 (разброс вокруг предмета, создает эффект ауры)
            // extra: 0.02 (скорость анимации, чтобы они плавно разлетались или висели)
            loc.getWorld().spawnParticle(particle, loc, 3, 0.3, 0.3, 0.3, 0.02, null, false);
        }
    }
    public Particle getParticle() {
        return particle;
    }

    // Легковесная проверка наличия игроков
    private boolean hasPlayersNearby(Location loc, double radius) {
        double radiusSq = radius * radius;
        for (org.bukkit.entity.Player p : loc.getWorld().getPlayers()) {
            if (p.getLocation().distanceSquared(loc) <= radiusSq) {
                return true;
            }
        }
        return false;
    }

    private void cleanupOrphans() {
        // Простая очистка предметов в той же точке с тем же типом, чтобы не было кучи
        for (Entity e : location.getWorld().getNearbyEntities(location, 0.5, 0.5, 0.5)) {
            if (e instanceof Item it) {
                if (it.getItemStack().getType() == itemStack.getType() && !it.canMobPickup()) {
                    it.remove();
                }
            }
        }
    }

    public void remove() {
        if (itemEntity != null) {
            itemEntity.remove();
            itemEntity = null;
        }
        if (!isTemporary) {
            ItemConfig.getInstance().removeItem(this.id);
        }
    }

    public void save() {
        if (isTemporary) return;
        ItemConfig.getInstance().saveItem(this.id, this.location, this.itemStack, this.customName, this.particle);
    }

    public boolean isValid() {
        return itemEntity != null && itemEntity.isValid();
    }

    // Setters
    public FloatingItem setCustomName(String name) {
        this.customName = Common.colorize(name);
        updateVisuals();
        return this;
    }

    public FloatingItem setParticle(Particle particle) {
        this.particle = particle;
        return this;
    }

    public boolean isTemporary() { return isTemporary; }
    public String getId() { return id; }
}