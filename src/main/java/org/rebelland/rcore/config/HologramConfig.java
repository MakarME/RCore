package org.rebelland.rcore.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.mineacademy.fo.settings.YamlConfig;

import java.io.File;
import java.util.List;
import java.util.Map;

public class HologramConfig extends YamlConfig {

    private static final HologramConfig instance = new HologramConfig();

    private HologramConfig() {
        Plugin rCore = Bukkit.getPluginManager().getPlugin("RCore");

        if (rCore != null) {
            File file = new File(rCore.getDataFolder(), "holograms.yml");

            // ВАЖНО: Проверяем, существует ли файл. Если нет — распаковываем.
            if (!file.exists()) {
                // saveResource берет файл из JAR плагина RCore и кладет в папку RCore
                rCore.saveResource("holograms.yml", false);
            }

            this.load(file);
        } else {
            // Если RCore не найден, фоллбэк на стандартную загрузку (в папку текущего плагина)
            this.loadConfiguration("holograms.yml");
        }
    }

    public static HologramConfig getInstance() {
        return instance;
    }

    /**
     * Сохраняет ВСЕ данные голограммы: позицию, текущие строки, состояние и переменные.
     */
    public void saveFullHologram(String id, Location location, List<String> currentLines,
                                 String currentState, Map<String, Object> staticPlaceholders) {

        // 1. Сохраняем локацию вручную, чтобы точно сохранить дроби (0.5)
        this.set(id + ".Location.World", location.getWorld().getName());
        this.set(id + ".Location.X", location.getX());
        this.set(id + ".Location.Y", location.getY());
        this.set(id + ".Location.Z", location.getZ());
        this.set(id + ".Location.Yaw", location.getYaw());
        this.set(id + ".Location.Pitch", location.getPitch());

        // 2. Сохраняем визуальные строки (на всякий случай)
        this.set(id + ".CurrentLines", currentLines);

        // 3. Сохраняем логическое состояние
        this.set(id + ".ActiveState", currentState);

        // 4. Сохраняем статические плейсхолдеры (монеты, спавнеры и т.д.)
        this.set(id + ".Placeholders", staticPlaceholders);

        this.save();
    }

    public void saveStateTemplate(String id, String stateName, List<String> lines) {
        this.set(id + ".States." + stateName, lines);
        this.save();
    }

    public void removeHologram(String id) {
        this.set(id, null);
        this.save();
    }
}