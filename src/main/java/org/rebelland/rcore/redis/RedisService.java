package org.rebelland.rcore.redis;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.rebelland.rcore.RCore;
import org.rebelland.rcore.config.MainConfig;
import org.rebelland.rcore.event.NetworkMessageEvent;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class RedisService {

    private JedisPool jedisPool;
    private JedisPubSub subscriber;
    private static final String CHANNEL_NAMESPACE = "rebelland:*"; // Слушаем всё, что начинается с rebelland:

    public void connect() {
        String host = MainConfig.REDIS_HOST;
        int port = MainConfig.REDIS_PORT;
        String password = MainConfig.REDIS_PASSWORD;

        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(16);

        if (password != null && !password.isEmpty()) {
            this.jedisPool = new JedisPool(config, host, port, 2000, password);
        } else {
            this.jedisPool = new JedisPool(config, host, port, 2000);
        }

        // Запускаем прослушку в отдельном потоке
        startSubscriber();
    }

    private void startSubscriber() {
        CompletableFuture.runAsync(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                subscriber = new JedisPubSub() {
                    @Override
                    public void onPMessage(String pattern, String channel, String message) {
                        // ВАЖНО: Jedis работает в своем потоке.
                        // Ивенты Bukkit лучше вызывать синхронно или быть готовым к асинхронности.
                        // Мы используем асинхронный ивент (super(true) в NetworkMessageEvent).
                        Bukkit.getPluginManager().callEvent(new NetworkMessageEvent(channel, message));
                    }
                };
                // Подписываемся по паттерну (например, rebelland:boosts, rebelland:auction)
                jedis.psubscribe(subscriber, CHANNEL_NAMESPACE);
            } catch (Exception e) {
                RCore.getInstance().getLogger().severe("Redis subscriber crashed!");
                e.printStackTrace();
            }
        });
    }

    public String get(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void set(String key, String value, int seconds) {
        CompletableFuture.runAsync(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.setex(key, seconds, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Универсальный кулдаун для всей сети.
     *
     * @param namespace Откуда вызов (например, "social", "clans", "items")
     * @param action    Действие (например, "join_notify", "teleport")
     * @param id        Уникальный ID (UUID игрока или ID клана)
     * @param seconds   Время в секундах
     * @return true, если кулдаун установлен успешно (его не было), false если еще активен
     */
    public boolean setCooldown(String namespace, String action, String id, int seconds) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = String.format("rebelland:cooldown:%s:%s:%s", namespace, action, id);

            // Используем NX (только если нет) и EX (время жизни)
            String result = jedis.set(key, "1", redis.clients.jedis.params.SetParams.setParams().nx().ex(seconds));

            return "OK".equals(result);
        } catch (Exception e) {
            RCore.getInstance().getLogger().warning("Redis cooldown error for " + action);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Проверить, остался ли кулдаун (не обновляя его).
     * Полезно, если нужно просто узнать статус, не перезаписывая таймер.
     */
    public long getRemainingCooldown(String namespace, String action, String id) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = String.format("rebelland:cooldown:%s:%s:%s", namespace, action, id);
            return Math.max(0, jedis.ttl(key)); // TTL возвращает секунды до удаления
        } catch (Exception e) {
            return 0;
        }
    }

    public void publish(String channel, String message) {
        CompletableFuture.runAsync(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.publish(channel, message);
            }
        });
    }

    public Map<String, String> hgetAll(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.hgetAll(key);
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyMap();
        }
    }

    public void shutdown() {
        if (subscriber != null) {
            subscriber.punsubscribe();
        }
        if (jedisPool != null) {
            jedisPool.close();
        }
    }

    /**
     * Получить сервер игрока.
     * Возвращает null, если игрок оффлайн или данные устарели (TTL истек).
     */
    public String getPlayerServer(UUID uuid) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get("rebelland:player:" + uuid.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Массовое обновление (Heartbeat) через Pipeline.
     * Это очень быстро даже для 1000+ игроков.
     */
    public void sendHeartbeat(Map<UUID, String> players, int ttlSeconds) {
        if (players.isEmpty()) return;

        CompletableFuture.runAsync(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                redis.clients.jedis.Pipeline p = jedis.pipelined();

                for (Map.Entry<UUID, String> entry : players.entrySet()) {
                    String key = "rebelland:player:" + entry.getKey().toString();
                    // SETEX: Установить значение + Срок жизни (в секундах) атомно
                    p.setex(key, ttlSeconds, entry.getValue());
                }

                p.sync(); // Выполнить все команды разом
            } catch (Exception e) {
                RCore.getInstance().getLogger().severe("Failed to send heartbeat!");
                e.printStackTrace();
            }
        });
    }

    /**
     * Удаление игрока (при выходе), чтобы не ждать 5 секунд TTL.
     */
    public void removePlayerHeartbeat(UUID uuid) {
        CompletableFuture.runAsync(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.del("rebelland:player:" + uuid.toString());
            }
        });
    }

    public CompletableFuture<Void> updatePlayerHeartbeat(UUID uuid, String serverName, int ttlSeconds) {
        return CompletableFuture.runAsync(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                // Синхронная операция внутри асинхронного потока
                // Мы ждем, пока Redis реально запишет данные
                jedis.setex("rebelland:player:" + uuid.toString(), ttlSeconds, serverName);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Redis error", e);
            }
        });
    }
}
