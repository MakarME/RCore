package org.rebelland.rcore.database;

import org.mineacademy.fo.Common;
import org.rebelland.rcore.RCore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class UserRepository {

    private static UserRepository instance;

    public static UserRepository getInstance() {
        if (instance == null) instance = new UserRepository();
        return instance;
    }

    /**
     * Создает таблицу core_users.
     */
    public void initializeTable() {
        // Берем соединение у своего же DatabaseService
        try (Connection conn = RCore.getInstance().getDatabaseService().getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "CREATE TABLE IF NOT EXISTS core_users (" +
                    "  uuid VARCHAR(36) PRIMARY KEY," +
                    "  name VARCHAR(16) NOT NULL," +
                    "  last_login TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "  ip_address VARCHAR(45)" +
                    ") DEFAULT CHARSET=utf8mb4";

            stmt.execute(sql);

        } catch (SQLException e) {
            Common.log("Could not create core_users table!");
            e.printStackTrace();
        }
    }

    /**
     * Обновляет данные игрока при входе.
     * Если игрока нет - создает. Если есть - обновляет ник, IP и время входа.
     */
    public void upsertUser(UUID uuid, String name, String ip) {
        // Выполняем асинхронно, чтобы не фризить вход
        RCore.getInstance().getDatabaseService().executeAsync(() -> {

            String sql = "INSERT INTO core_users (uuid, name, ip_address, last_login) VALUES (?, ?, ?, NOW()) " +
                    "ON DUPLICATE KEY UPDATE name = VALUES(name), ip_address = VALUES(ip_address), last_login = NOW()";

            try (Connection conn = RCore.getInstance().getDatabaseService().getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, uuid.toString());
                ps.setString(2, name);
                ps.setString(3, ip);

                ps.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
