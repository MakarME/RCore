package org.rebelland.rcore.service;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.rebelland.rcore.RCore;
import org.rebelland.rcore.database.UserRepository;
import org.rebelland.rcore.model.PlayerInfo;
import org.rebelland.rcore.redis.RedisService;
import redis.clients.jedis.Jedis;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerIdentityService implements Listener {

    private static PlayerIdentityService instance;

    // L1 Cache: Храним готовые объекты PlayerInfo
    // Основной кеш по UUID
    private final Map<UUID, PlayerInfo> uuidCache = new ConcurrentHashMap<>();
    // Вспомогательный кеш для быстрого поиска по нику (ссылается на тот же UUID)
    private final Map<String, UUID> nameIndex = new ConcurrentHashMap<>();

    public static PlayerIdentityService getInstance() {
        if (instance == null) instance = new PlayerIdentityService();
        return instance;
    }

    private PlayerIdentityService() {
        Bukkit.getPluginManager().registerEvents(this, SimplePlugin.getInstance());
    }

    // ==========================================
    // PUBLIC API
    // ==========================================

    /**
     * Поиск по НИКУ.
     */
    public CompletableFuture<PlayerInfo> get(String name) {
        String lowerName = name.toLowerCase();

        // 1. Быстрая проверка в памяти (через индекс)
        if (nameIndex.containsKey(lowerName)) {
            UUID uuid = nameIndex.get(lowerName);
            PlayerInfo info = uuidCache.get(uuid);
            if (info != null) return CompletableFuture.completedFuture(info);
        }

        // 2. Если игрок онлайн (Bukkit API) - создаем и кешируем мгновенно
        Player online = Bukkit.getPlayerExact(name);
        if (online != null) {
            PlayerInfo info = new PlayerInfo(online.getUniqueId(), online.getName(), System.currentTimeMillis());
            cache(info);
            return CompletableFuture.completedFuture(info);
        }

        // 3. Ищем в Redis / DB
        return CompletableFuture.supplyAsync(() -> fetchByName(name));
    }

    /**
     * Поиск по UUID.
     */
    public CompletableFuture<PlayerInfo> get(UUID uuid) {
        // 1. Память
        if (uuidCache.containsKey(uuid)) {
            return CompletableFuture.completedFuture(uuidCache.get(uuid));
        }

        // 2. Онлайн
        Player online = Bukkit.getPlayer(uuid);
        if (online != null) {
            PlayerInfo info = new PlayerInfo(uuid, online.getName(), System.currentTimeMillis());
            cache(info);
            return CompletableFuture.completedFuture(info);
        }

        // 3. Redis / DB
        return CompletableFuture.supplyAsync(() -> fetchByUuid(uuid));
    }

    // ==========================================
    // ВНУТРЕННЯЯ ЛОГИКА
    // ==========================================

    private void cache(PlayerInfo info) {
        if (info == null) return;
        uuidCache.put(info.getUuid(), info);
        nameIndex.put(info.getName().toLowerCase(), info.getUuid());
    }

    // --- FETCHING ---

    private PlayerInfo fetchByName(String name) {
        RedisService redis = RCore.getInstance().getRedisService();
        String key = "rcore:identity:name:" + name.toLowerCase();

        // А. Redis (сначала ищем UUID по нику)
        String cachedUuid = redis.get(key);
        if (cachedUuid != null) {
            try {
                UUID uuid = UUID.fromString(cachedUuid);
                // Если нашли UUID, пробуем достать полную инфу
                return fetchByUuid(uuid);
            } catch (Exception ignored) {}
        }

        // Б. MySQL (Source of Truth)
        UUID dbUuid = UserRepository.getInstance().getUuidByName(name);
        if (dbUuid != null) {
            // Чтобы получить LastLogin, нам все равно придется дернуть fetchByUuid или сделать Join запрос
            // Для простоты вызовем fetchByUuid, он сходит в базу за деталями
            return fetchByUuid(dbUuid);
        }

        return null; // Не найден
    }

    private PlayerInfo fetchByUuid(UUID uuid) {
        RedisService redis = RCore.getInstance().getRedisService();
        String key = "rcore:identity:uuid:" + uuid.toString();

        // А. Redis (пробуем получить Ник)
        // В идеале в Redis можно хранить JSON с полным PlayerInfo, но пока у нас там только Ник
        String cachedName = redis.get(key);

        if (cachedName != null) {
            // Если в редисе есть имя, но нет lastLogin - можем вернуть с 0 или сходить в базу
            // Для скорости вернем то, что есть.
            // (Если критично время входа, нужно хранить в Redis JSON)
            PlayerInfo info = new PlayerInfo(uuid, cachedName, 0);
            cache(info);
            return info;
        }

        // Б. MySQL (Загружаем полную инфу)
        // Нам нужно добавить в UserRepository метод получения имени + lastLogin одним запросом
        // Пока используем то, что есть:
        String dbName = UserRepository.getInstance().getPlayerName(uuid);
        long lastLogin = UserRepository.getInstance().getLastLogin(uuid);

        if (dbName != null && !dbName.equals("Unknown")) {
            PlayerInfo info = new PlayerInfo(uuid, dbName, lastLogin);
            cache(info);
            updateRedis(info);
            return info;
        }

        return null;
    }

    private void updateRedis(PlayerInfo info) {
        RedisService redis = RCore.getInstance().getRedisService();
        // Кешируем перекрестные ссылки
        redis.set("rcore:identity:name:" + info.getName().toLowerCase(), info.getUuid().toString(), 86400);
        redis.set("rcore:identity:uuid:" + info.getUuid().toString(), info.getName(), 86400);
    }

    // --- LISTENERS ---

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        PlayerInfo info = new PlayerInfo(e.getPlayer().getUniqueId(), e.getPlayer().getName(), System.currentTimeMillis());
        cache(info);
        updateRedis(info);
    }
}
