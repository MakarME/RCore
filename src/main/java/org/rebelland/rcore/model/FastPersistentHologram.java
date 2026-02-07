package org.rebelland.rcore.model;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.persistence.PersistentDataType;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.model.Replacer;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.rebelland.rcore.config.HologramConfig;

import java.util.*;

public class FastPersistentHologram {

    private final String id;
    private Location location;

    private final List<ArmorStand> entities = new ArrayList<>();
    private final Map<String, List<String>> stateTemplates = new HashMap<>();
    private final Map<String, Object> staticPlaceholders = new HashMap<>();
    private Map<String, Object> dynamicPlaceholders = new HashMap<>();
    private final Map<String, String> dynamicAppends = new LinkedHashMap<>();

    private String currentState = "default";
    private boolean isTemporary = false;
    private static final double LINE_SPACING = 0.25;

    // Метка, чтобы отличать НАШИ голограммы от чужих
    private final NamespacedKey idKey = new NamespacedKey(SimplePlugin.getInstance(), "hologram_id");

    public FastPersistentHologram(String id, Location location) {
        this.id = id;
        this.location = location;
    }

    public void spawn() {
        if (isSpawned()) return;

        // ШАГ 1: Убиваем дубликаты перед спавном
        //cleanupOrphans();

        // ШАГ 2: Спавним новые
        refresh();
    }

    /**
     * Агрессивно удаляет старые голограммы в этой точке.
     */
    private void cleanupOrphans() {
        if (location == null || location.getWorld() == null) return;

        // Ищем энтити в радиусе 1 блока (очень близко)
        Collection<Entity> nearby = location.getWorld().getNearbyEntities(location, 1, 3, 1);

        for (Entity entity : nearby) {
            if (entity.getType() == EntityType.ARMOR_STAND) {
                ArmorStand stand = (ArmorStand) entity;

                // Проверяем: это наша голограмма?
                // 1. По ключу PDC (если это новая версия)
                String storedId = stand.getPersistentDataContainer().get(idKey, PersistentDataType.STRING);

                // 2. Или если это "мусор" (невидимый маркер с именем) - удаляем, чтобы не было дублей
                boolean looksLikeHologram = !stand.isVisible() && stand.isMarker() && stand.isCustomNameVisible();

                if (this.id.equals(storedId) || looksLikeHologram) {
                    stand.remove();
                }
            }
        }
    }

    public void refresh() {
        List<String> finalLines = buildLines();

        if (finalLines.isEmpty()) {
            despawn();
            return;
        }

        Location currentLoc = location.clone();
        // Рассчитываем высоту, учитывая даже пустые строки
        double yOffset = (finalLines.size() - 1) * LINE_SPACING;
        currentLoc.add(0, yOffset, 0);

        // Синхронизируем размер списка энтити с количеством строк
        while (entities.size() < finalLines.size()) {
            entities.add(null);
        }
        while (entities.size() > finalLines.size()) {
            ArmorStand stand = entities.remove(entities.size() - 1);
            if (stand != null) stand.remove();
        }

        for (int i = 0; i < finalLines.size(); i++) {
            String rawText = finalLines.get(i);

            // Проверяем, является ли строка "пустой" (пробелы или пустота)
            boolean isEmptyLine = rawText == null || rawText.trim().isEmpty();

            ArmorStand stand = entities.get(i);

            if (isEmptyLine) {
                // Если строка пустая, но стенд есть — удаляем его и ставим null в список
                if (stand != null) {
                    stand.remove();
                    entities.set(i, null);
                }
                // Стенд не спавним, но координаты ниже сдвигаем
            } else {
                String text = Common.colorize(rawText);

                if (stand == null || stand.isDead() || !stand.isValid()) {
                    // Если стенда нет или он умер — спавним новый
                    stand = spawnArmorStand(currentLoc, text);
                    entities.set(i, stand);
                } else {
                    // Если стенд жив — обновляем
                    if (!Objects.equals(stand.getCustomName(), text)) {
                        stand.setCustomName(text);
                    }
                    // Телепортируем, если сместился (например, изменилось кол-во строк выше)
                    if (stand.getLocation().distanceSquared(currentLoc) > 0.005) {
                        stand.teleport(currentLoc);
                    }
                }
            }

            // Сдвигаем позицию вниз для следующей строки (даже если текущая была пустой)
            currentLoc.subtract(0, LINE_SPACING, 0);
        }
    }

    private ArmorStand spawnArmorStand(Location loc, String text) {
        // Проверка чанка (из предыдущих фиксов)
        //if (!loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)) return null;

        // ИСПОЛЬЗУЕМ CONSUMER для настройки ДО спавна
        return loc.getWorld().spawn(loc, ArmorStand.class, stand -> {
            stand.setGravity(false);
            stand.setBasePlate(false);
            stand.setSmall(true);
            stand.setMarker(true);

            // Самое важное: делаем невидимым ДО появления в мире
            stand.setVisible(false);

            stand.setCustomName(text);
            stand.setCustomNameVisible(true);

            // Настройки персистентности
            stand.setPersistent(false);
            stand.getPersistentDataContainer().set(idKey, PersistentDataType.STRING, this.id);
        });
    }

    public void remove() {
        despawn();
        if (!isTemporary) {
            HologramConfig.getInstance().removeHologram(this.id);
        }
    }

    public void despawn() {
        for (ArmorStand stand : entities) {
            if (stand != null) {
                stand.remove();
            }
        }
        entities.clear();
    }

    public void save() {
        if (isTemporary) return;
        HologramConfig.getInstance().saveFullHologram(this.id, this.location, buildLines(), this.currentState, this.staticPlaceholders);
    }

    // --- Boilerplate (Геттеры, Сеттеры, без изменений логики) ---

    public String getId() { return id; }
    public Location getLocation() { return location.clone(); }
    public String getCurrentState() { return currentState; }
    public boolean isTemporary() { return isTemporary; }
    public boolean isSpawned() { return !entities.isEmpty() && !entities.get(0).isDead(); }

    public FastPersistentHologram setTemporary(boolean temporary) {
        this.isTemporary = temporary;
        return this;
    }

    public void setLocation(Location location) {
        this.location = location;
        refresh();
        save();
    }

    public void defineState(String state, List<String> lines) {
        this.stateTemplates.put(state, lines);
        if (!isTemporary) {
            HologramConfig.getInstance().saveStateTemplate(this.id, state, lines);
        }
    }

    public void setDynamicLine(String key, String text) {
        if (text == null) dynamicAppends.remove(key);
        else dynamicAppends.put(key, text);
        refresh();
    }

    public void setStaticPlaceholder(String key, Object value) {
        this.staticPlaceholders.put(key, value);
        refresh();
    }

    public void updateTick(String state, Map<String, Object> tickParams) {
        this.currentState = state;
        this.dynamicPlaceholders = tickParams != null ? tickParams : new HashMap<>();
        refresh();
    }

    public void setState(String stateName) { setState(stateName, null); }

    public void setState(String stateName, Map<String, Object> params) {
        this.currentState = stateName;
        if (params != null) this.dynamicPlaceholders = params;
        refresh();
    }

    public void loadStateInternal(String state, List<String> lines) {
        this.stateTemplates.put(state, lines);
    }

    private List<String> buildLines() {
        List<String> rawTemplate = stateTemplates.getOrDefault(currentState, new ArrayList<>());
        List<String> result = new ArrayList<>();
        Map<String, Object> effectiveParams = new HashMap<>(staticPlaceholders);
        effectiveParams.putAll(dynamicPlaceholders);

        for (String line : rawTemplate) {
            boolean shouldSkip = false;
            for (Map.Entry<String, Object> entry : effectiveParams.entrySet()) {
                String key = "{" + entry.getKey() + "}";
                if (line.contains(key)) {
                    Object val = entry.getValue();
                    if (val == null || String.valueOf(val).isEmpty()) {
                        shouldSkip = true;
                        break;
                    }
                }
            }
            if (!shouldSkip) result.add(Replacer.replaceArray(line, effectiveParams));
        }
        result.addAll(dynamicAppends.values());
        return result;
    }
}