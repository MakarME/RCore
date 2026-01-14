package org.rebelland.rcore;

import org.bukkit.plugin.java.JavaPlugin;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.rebelland.rcore.api.BoostAPI;
import org.rebelland.rcore.api.ClansAPI;
import org.rebelland.rcore.database.DatabaseService;
import org.rebelland.rcore.database.UserRepository;
import org.rebelland.rcore.listener.GlobalPlayerManager;
import org.rebelland.rcore.listener.UserListener;
import org.rebelland.rcore.redis.RedisService;

public final class RCore extends SimplePlugin {

    private DatabaseService databaseService;
    private RedisService redisService;
    private static BoostAPI boostAPI;
    private static ClansAPI clansAPI;

    @Override
    public void onPluginStart() {
        databaseService = new DatabaseService();
        try {
            databaseService.connect();
            Common.log("MySQL connected successfully!");
            UserRepository.getInstance().initializeTable();
        } catch (Exception e) {
            Common.log("Failed to connect to MySQL! Disabling plugin...");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // 3. Инициализация Redis
        redisService = new RedisService();
        try {
            redisService.connect();
            Common.log("Redis connected successfully!");
        } catch (Exception e) {
            Common.log("Failed to connect to Redis!");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        registerEvents(new UserListener());
        registerEvents(new GlobalPlayerManager());
    }

    @Override
    public void onPluginStop() {
        if (redisService != null) redisService.shutdown();
        if (databaseService != null) databaseService.shutdown();
    }

    public static void setBoostAPI(BoostAPI api) {
        Common.log("BoostAPI инициализирован");
        boostAPI = api;
    }

    public static BoostAPI getBoostAPI() {
        return boostAPI;
    }

    public static void setClansAPI(ClansAPI api) {
        Common.log("ClansAPI инициализирован");
        clansAPI = api;
    }

    public static ClansAPI getClansAPI() {
        return clansAPI;
    }

    public static RCore getInstance() {
        return getPlugin(RCore.class);
    }
    public DatabaseService getDatabaseService() { return databaseService; }
    public RedisService getRedisService() { return redisService; }
}
