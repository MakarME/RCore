package org.rebelland.rcore.manager;

import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.collection.SerializedMap;
import org.rebelland.rcore.config.HologramConfig;
import org.rebelland.rcore.config.ItemConfig;
import org.rebelland.rcore.model.FastPersistentHologram;
import org.rebelland.rcore.model.FloatingItem;

import java.util.*;

public class HologramManager {

    private static final HologramManager instance = new HologramManager();

    // Хранилища
    private final Map<String, FastPersistentHologram> holograms = new HashMap<>();
    private final Map<String, FloatingItem> items = new HashMap<>();

    private BukkitTask particleTask;

    private HologramManager() {}

    public static HologramManager getInstance() { return instance; }

    // --- Lifecycle ---

    public void load() {
        shutdown();
        loadHolograms();
        loadItems();
        startTicker();
    }

    public void shutdown() {
        if (particleTask != null) {
            particleTask.cancel();
            particleTask = null;
        }

        holograms.values().forEach(FastPersistentHologram::despawn);
        holograms.clear();

        items.values().forEach(FloatingItem::remove);
        items.clear();
    }

    // --- HOLOGRAM API ---

    public FastPersistentHologram getHologram(String id) {
        return holograms.get(id);
    }

    public boolean isHologramExists(String id) {
        return holograms.containsKey(id);
    }

    public FastPersistentHologram getOrCreateHologram(String id, Location defaultLocation) {
        if (holograms.containsKey(id)) {
            FastPersistentHologram existing = holograms.get(id);
            if (!existing.isSpawned()) existing.spawn();
            return existing;
        }

        FastPersistentHologram newHolo = new FastPersistentHologram(id, defaultLocation);
        newHolo.spawn();
        holograms.put(id, newHolo);
        return newHolo;
    }

    public void deleteHologram(String id) {
        FastPersistentHologram holo = holograms.remove(id);
        if (holo != null) holo.remove();
    }

    public void spawnTemporaryHologram(Location location, int ticks, String... lines) {
        String tempId = "temp_" + UUID.randomUUID();
        FastPersistentHologram holo = new FastPersistentHologram(tempId, location);
        holo.setTemporary(true);
        holo.loadStateInternal("default", Arrays.asList(lines));
        holo.spawn();
        holograms.put(tempId, holo);
        Common.runLater(ticks, () -> {
            if (holograms.containsKey(tempId)) {
                FastPersistentHologram existing = holograms.remove(tempId);
                if (existing != null) existing.despawn();
            }
        });
    }

    // --- ITEM API ---

    public FloatingItem getItem(String id) {
        return items.get(id);
    }

    public boolean isItemExists(String id) {
        return items.containsKey(id);
    }

    public FloatingItem getOrCreateItem(String id, Location defaultLocation, ItemStack defaultStack) {
        if (items.containsKey(id)) {
            FloatingItem existing = items.get(id);
            // Если предмет почему-то исчез, спавним его снова
            if (!existing.isSpawned()) existing.spawn();
            return existing;
        }

        return createStaticItem(id, defaultLocation, defaultStack);
    }

    /**
     * Создает новый статичный предмет и сохраняет его в конфиг.
     */
    public FloatingItem createStaticItem(String id, Location loc, ItemStack stack) {
        FloatingItem item = new FloatingItem(id, loc, stack);
        item.spawn();
        item.save();
        items.put(id, item);
        return item;
    }

    /**
     * Удаляет предмет из мира и из конфига.
     */
    public void deleteItem(String id) {
        FloatingItem item = items.remove(id);
        if (item != null) item.remove();
    }

    /**
     * Создает временную "обертку" для выпавшего предмета (для частиц/имени).
     */
    public void decorateDroppedItem(Item droppedItem, String name, Particle particle) {
        FloatingItem item = new FloatingItem(droppedItem);
        if (name != null) item.setCustomName(name);
        if (particle != null) item.setParticle(particle);
        items.put(item.getId(), item);
    }

    // --- Internal Logic ---

    private void startTicker() {
        this.particleTask = Common.runTimer(5, () -> {
            Iterator<Map.Entry<String, FloatingItem>> it = items.entrySet().iterator();
            while (it.hasNext()) {
                FloatingItem item = it.next().getValue();

                // Очистка мертвых временных предметов
                if (item.isTemporary() && !item.isValid()) {
                    it.remove();
                    continue;
                }

                item.onTick();
            }
        });
    }

    private void loadHolograms() {
        HologramConfig config = HologramConfig.getInstance();
        Set<String> keys = config.getKeys(false);

        int count = 0;
        for (String id : keys) {
            if (!config.isSet(id + ".Location.World")) continue;

            String worldName = config.getString(id + ".Location.World");
            double x = config.getDouble(id + ".Location.X");
            double y = config.getDouble(id + ".Location.Y");
            double z = config.getDouble(id + ".Location.Z");
            float yaw = config.getDouble(id + ".Location.Yaw").floatValue();
            float pitch = config.getDouble(id + ".Location.Pitch").floatValue();

            World world = Bukkit.getWorld(worldName);
            if (world == null) continue;

            Location loc = new Location(world, x, y, z, yaw, pitch);
            FastPersistentHologram holo = new FastPersistentHologram(id, loc);

            // States
            if (config.isSet(id + ".States")) {
                SerializedMap statesMap = config.getMap(id + ".States");
                for (String stateName : statesMap.keySet()) {
                    List<String> stateLines = config.getStringList(id + ".States." + stateName);
                    holo.loadStateInternal(stateName, stateLines);
                }
            }

            // Placeholders
            if (config.isSet(id + ".Placeholders")) {
                SerializedMap plMap = config.getMap(id + ".Placeholders");
                for (Map.Entry<String, Object> entry : plMap.asMap().entrySet()) {
                    holo.setStaticPlaceholder(entry.getKey(), entry.getValue());
                }
            }

            holo.setState(config.getString(id + ".ActiveState", "default"));
            holo.spawn();
            holograms.put(id, holo);
            count++;
        }
        Common.log("&2[Holograms] &7Loaded " + count + " holograms.");
    }

    private void loadItems() {
        ItemConfig config = ItemConfig.getInstance();
        Set<String> keys = config.getItemKeys();
        if (keys == null) return;

        int count = 0;
        for (String id : keys) {
            String path = "Items." + id;
            if (!config.isSet(path + ".Location.World")) continue;

            String worldName = config.getString(path + ".Location.World");
            double x = config.getDouble(path + ".Location.X");
            double y = config.getDouble(path + ".Location.Y");
            double z = config.getDouble(path + ".Location.Z");

            World world = Bukkit.getWorld(worldName);
            if (world == null) continue;

            Location loc = new Location(world, x, y, z);

            Material mat = Material.getMaterial(config.getString(path + ".Material", "STONE"));
            FloatingItem item = new FloatingItem(id, loc, new ItemStack(mat));

            String name = config.getString(path + ".Name");
            if (name != null) item.setCustomName(name);

            String pName = config.getString(path + ".Particle");
            if (pName != null) {
                try { item.setParticle(Particle.valueOf(pName)); } catch (Exception ignored) {}
            }

            item.spawn();
            items.put(id, item);
            count++;
        }
        Common.log("&2[Holograms] &7Loaded " + count + " items.");
    }
}