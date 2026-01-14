package org.rebelland.rcore.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.java.JavaPlugin;
import org.rebelland.rcore.config.MainConfig;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class DatabaseService {

    private HikariDataSource dataSource;

    public void connect() {
        HikariConfig config = new HikariConfig();

        // Читаем настройки из config.yml ядра
        String host = MainConfig.MYSQL_HOST;
        String port = MainConfig.MYSQL_PORT;
        String database = MainConfig.MYSQL_DATABASE;
        String username = MainConfig.MYSQL_USERNAME;
        String password = MainConfig.MYSQL_PASSWORD;

        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&characterEncoding=utf8");
        config.setUsername(username);
        config.setPassword(password);

        // Оптимальные настройки пула
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000); // 30 сек
        config.setIdleTimeout(600000); // 10 мин
        config.setMaxLifetime(1800000); // 30 мин

        this.dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null) throw new SQLException("Database not initialized");
        return dataSource.getConnection();
    }

    public void shutdown() {
        if (dataSource != null) dataSource.close();
    }

    // Утилита для асинхронного выполнения
    public void executeAsync(Runnable task) {
        CompletableFuture.runAsync(task);
    }
}
