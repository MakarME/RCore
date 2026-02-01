package org.rebelland.rcore.service;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.mineacademy.fo.Common;
import org.mineacademy.fo.plugin.SimplePlugin;
import org.rebelland.rcore.RCore;
import org.rebelland.rcore.database.UserRepository;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlaytimeService implements Listener {

    private static PlaytimeService instance;

    // Кэш "базового" времени из БД (чтобы быстро показывать в меню)
    private final Map<UUID, Long> cachedTotalTime = new ConcurrentHashMap<>();

    // Время начала текущей сессии (или последнего чекпоинта сохранения)
    private final Map<UUID, Long> sessionStart = new ConcurrentHashMap<>();

    // Очередь на сохранение: UUID -> Секунды, которые нужно добавить в БД
    // Сюда попадают данные вышедших игроков и периодические сбросы онлайна
    private final Map<UUID, Long> commitQueue = new ConcurrentHashMap<>();

    private static final int AUTOSAVE_TICKS = 6000; // 5 минут

    private PlaytimeService() {
        Bukkit.getPluginManager().registerEvents(this, SimplePlugin.getInstance());
        startAutosaveTask();
    }

    public static PlaytimeService getInstance() {
        if (instance == null) instance = new PlaytimeService();
        return instance;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();

        // 1. Загружаем текущее значение из БД для кэша
        RCore.getInstance().getDatabaseService().executeAsync(() -> {
            long dbTime = UserRepository.getInstance().getPlaytime(uuid);
            cachedTotalTime.put(uuid, dbTime);
        });

        // 2. Старт сессии
        sessionStart.put(uuid, System.currentTimeMillis());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        // При выходе сбрасываем текущую сессию в очередь на сохранение
        flushSessionToQueue(event.getPlayer().getUniqueId());

        // Удаляем из активных сессий и кэша отображения (чтобы не занимать память)
        sessionStart.remove(event.getPlayer().getUniqueId());
        cachedTotalTime.remove(event.getPlayer().getUniqueId());
    }

    /**
     * Переносит накопленное время из "онлайн-сессии" в "очередь на сохранение".
     * Обновляет локальный кэш totalTime, чтобы цифры не прыгали назад.
     */
    private void flushSessionToQueue(UUID uuid) {
        Long start = sessionStart.get(uuid);
        if (start == null) return;

        long now = System.currentTimeMillis();
        long secondsPlayed = (now - start) / 1000;

        if (secondsPlayed > 0) {
            // Добавляем в очередь на отправку в БД
            commitQueue.merge(uuid, secondsPlayed, Long::sum);

            // Обновляем локальный кэш (чтобы getLivePlaytime был точным после сброса start)
            cachedTotalTime.merge(uuid, secondsPlayed, Long::sum);

            // Сбрасываем "начало" сессии на текущий момент
            sessionStart.put(uuid, now);
        }
    }

    /**
     * ГЛАВНЫЙ МЕТОД ДЛЯ МЕНЮ
     * Возвращает общее время игры в секундах прямо на текущий момент.
     * Учитывает: Значение в БД + Накопленное в очереди + Текущую сессию.
     */
    public long getLivePlaytime(UUID uuid) {
        // 1. Базовое значение (из БД + то, что уже в очереди на сохранение)
        long base = cachedTotalTime.getOrDefault(uuid, 0L);

        // 2. Если игрок онлайн, добавляем "живые" секунды
        if (sessionStart.containsKey(uuid)) {
            long currentSessionSeconds = (System.currentTimeMillis() - sessionStart.get(uuid)) / 1000;
            return base + currentSessionSeconds;
        }

        // Если игрок оффлайн (или только зашел и кэш не прогрузился), возвращаем что есть
        return base;
    }

    /**
     * Задача автосохранения
     */
    private void startAutosaveTask() {
        Common.runTimerAsync(AUTOSAVE_TICKS, AUTOSAVE_TICKS, () -> {

            // 1. Проходимся по ВСЕМ ОНЛАЙН игрокам и сбрасываем их прогресс в очередь
            // Это нужно, чтобы если сервер упадет через минуту, мы не потеряли прогресс
            // Также это обновляет cachedTotalTime
            for (UUID onlineUuid : sessionStart.keySet()) {
                flushSessionToQueue(onlineUuid);
            }

            // 2. Если очередь пуста, ничего не делаем
            if (commitQueue.isEmpty()) return;

            // 3. Создаем копию очереди для отправки и очищаем оригинал
            Map<UUID, Long> batch = new ConcurrentHashMap<>(commitQueue);
            commitQueue.clear();

            // 4. Отправляем батч запрос
            UserRepository.getInstance().updatePlaytimeBatch(batch);
        });
    }

    public void shutdown() {
        // 1. Сбрасываем сессии всех онлайн игроков
        for (UUID uuid : sessionStart.keySet()) {
            flushSessionToQueue(uuid);
        }

        // 2. Отправляем остатки
        if (!commitQueue.isEmpty()) {
            UserRepository.getInstance().updatePlaytimeBatch(commitQueue);
        }
    }
}
