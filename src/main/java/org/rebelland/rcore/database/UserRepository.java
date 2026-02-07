package org.rebelland.rcore.database;

import org.mineacademy.fo.Common;
import org.rebelland.rcore.RCore;

import java.sql.*;
import java.util.Map;
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
                    "  ip_address VARCHAR(45)," +
                    "  INDEX idx_name (name)" +
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

    /**
     * Получить наигранное время в секундах.
     */
    public long getPlaytime(UUID uuid) {
        String sql = "SELECT total_playtime FROM core_users WHERE uuid = ?";
        try (Connection conn = RCore.getInstance().getDatabaseService().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong("total_playtime");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public void updatePlaytimeBatch(Map<UUID, Long> updates) {
        if (updates.isEmpty()) return;

        RCore.getInstance().getDatabaseService().executeAsync(() -> {
            String sql = "UPDATE core_users SET total_playtime = total_playtime + ? WHERE uuid = ?";

            try (Connection conn = RCore.getInstance().getDatabaseService().getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                // Отключаем авто-коммит для скорости транзакции
                conn.setAutoCommit(false);

                for (Map.Entry<UUID, Long> entry : updates.entrySet()) {
                    ps.setLong(1, entry.getValue());
                    ps.setString(2, entry.getKey().toString());
                    ps.addBatch();
                }

                ps.executeBatch();
                conn.commit(); // Коммитим все изменения разом

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public String getPlayerName(UUID uuid) {
        String sql = "SELECT name FROM core_users WHERE uuid = ?";
        try (Connection conn = RCore.getInstance().getDatabaseService().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("name");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "Unknown";
    }

    public UUID getUuidByName(String name) {
        String sql = "SELECT uuid FROM core_users WHERE name = ?";
        try (Connection conn = RCore.getInstance().getDatabaseService().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return UUID.fromString(rs.getString("uuid"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public long getLastLogin(UUID uuid) {
        String sql = "SELECT last_login FROM core_users WHERE uuid = ?";
        try (Connection conn = RCore.getInstance().getDatabaseService().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getTimestamp("last_login").getTime();
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
}
