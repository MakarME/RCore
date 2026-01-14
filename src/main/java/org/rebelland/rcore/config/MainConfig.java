package org.rebelland.rcore.config;

import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.settings.YamlStaticConfig;

@AutoRegister
public class MainConfig extends YamlStaticConfig {
    @Override
    protected void onLoad() throws Exception {
        this.loadConfiguration("MainConfig.yml");
    }

    public static String SERVER;
    public static String MYSQL_HOST;
    public static String MYSQL_PORT;
    public static String MYSQL_DATABASE;
    public static String MYSQL_USERNAME;
    public static String MYSQL_PASSWORD;
    public static String REDIS_HOST;
    public static Integer REDIS_PORT;
    public static String REDIS_PASSWORD;

    private static void init() {
        SERVER = getString("server");
        MYSQL_HOST = getString("mysql.host");
        MYSQL_PORT = getString("mysql.port");
        MYSQL_DATABASE = getString("mysql.database");
        MYSQL_USERNAME = getString("mysql.username");
        MYSQL_PASSWORD = getString("mysql.password");
        REDIS_HOST = getString("redis.host");
        REDIS_PORT = getInteger("redis.port");
        REDIS_PASSWORD = getString("redis.password");
    }
}
