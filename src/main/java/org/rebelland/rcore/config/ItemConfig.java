package org.rebelland.rcore.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.mineacademy.fo.settings.YamlConfig;

import java.io.File;
import java.util.Collections;
import java.util.Set;

public class ItemConfig extends YamlConfig {

    private static final ItemConfig instance = new ItemConfig();

    private ItemConfig() {
        Plugin rCore = Bukkit.getPluginManager().getPlugin("RCore");

        if (rCore != null) {
            File file = new File(rCore.getDataFolder(), "items.yml");

            // ВАЖНО: Проверяем, существует ли файл. Если нет — распаковываем.
            if (!file.exists()) {
                // saveResource берет файл из JAR плагина RCore и кладет в папку RCore
                rCore.saveResource("items.yml", false);
            }

            this.load(file);
        } else {
            // Если RCore не найден, фоллбэк на стандартную загрузку (в папку текущего плагина)
            this.loadConfiguration("items.yml");
        }
    }

    public static ItemConfig getInstance() {
        return instance;
    }

    public void saveItem(String id, Location location, ItemStack itemStack, String name, Particle particle) {
        String path = "Items." + id;

        this.set(path + ".Location.World", location.getWorld().getName());
        this.set(path + ".Location.X", location.getX());
        this.set(path + ".Location.Y", location.getY());
        this.set(path + ".Location.Z", location.getZ());

        this.set(path + ".Material", itemStack.getType().toString());
        this.set(path + ".Name", name); // Может быть null
        if (particle != null) {
            this.set(path + ".Particle", particle.toString());
        } else {
            this.set(path + ".Particle", null);
        }

        this.save();
    }

    public void removeItem(String id) {
        this.set("Items." + id, null);
        this.save();
    }

    // Метод для получения списка ID всех сохраненных предметов
    public Set<String> getItemKeys() {
        if (isSet("Items")) {
            return getMap("Items").keySet();
        }
        return Collections.emptySet();
    }
}