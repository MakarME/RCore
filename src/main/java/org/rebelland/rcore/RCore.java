package org.rebelland.rcore;

import org.bukkit.plugin.java.JavaPlugin;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.rebelland.rcore.api.AntiSocialAPI;
import org.rebelland.rcore.api.BoostAPI;
import org.rebelland.rcore.api.CheaterStatusAPI;
import org.rebelland.rcore.api.ClansAPI;
import org.rebelland.rcore.database.DatabaseService;
import org.rebelland.rcore.database.UserRepository;
import org.rebelland.rcore.listener.GlobalPlayerManager;
import org.rebelland.rcore.listener.UserListener;
import org.rebelland.rcore.manager.HologramManager;
import org.rebelland.rcore.redis.RedisService;
import org.rebelland.rcore.service.PlayerIdentityService;
import org.rebelland.rcore.service.PlaytimeService;

public final class RCore extends SimplePlugin {

    private DatabaseService databaseService;
    private RedisService redisService;
    private static BoostAPI boostAPI;
    private static ClansAPI clansAPI;
    private static CheaterStatusAPI cheaterStatusAPI;
    private static AntiSocialAPI antiSocialAPI;

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
        PlaytimeService.getInstance();
        PlayerIdentityService.getInstance();
        HologramManager.getInstance().load();
    }

    @Override
    public void onPluginStop() {
        PlaytimeService.getInstance().shutdown();
        if (redisService != null) redisService.shutdown();
        if (databaseService != null) databaseService.shutdown();
        HologramManager.getInstance().shutdown();
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

    public static void setCheaterStatusAPI(CheaterStatusAPI api) {
        Common.log("CheaterStatusAPI инициализирован");
        cheaterStatusAPI = api;
    }

    public static CheaterStatusAPI getCheaterStatusAPI() {
        return cheaterStatusAPI;
    }

    public static void setAntiSocialAPI(AntiSocialAPI api) {
        Common.log("AntiSocialAPI инициализирован");
        antiSocialAPI = api;
    }

    public static AntiSocialAPI getAntiSocialAPI() {
        return antiSocialAPI;
    }

    public static RCore getInstance() {
        return getPlugin(RCore.class);
    }
    public DatabaseService getDatabaseService() { return databaseService; }
    public RedisService getRedisService() { return redisService; }
}
