package org.rebelland.rcore.model.boosts;

import java.util.UUID;
import java.util.Map;

public class BoostPlayer {
    private final UUID uuid;
    private final String name;
    // ИЗМЕНЕНО: Теперь это карта редкостей, а не одно число
    private final Map<Rarity, Integer> shards;
    private int maxSlots;

    public BoostPlayer(UUID uuid, String name, Map<Rarity, Integer> shards, int maxSlots) {
        this.uuid = uuid;
        this.name = name;
        this.shards = shards;
        this.maxSlots = maxSlots;
    }

    public UUID getUuid() { return uuid; }
    public String getName() { return name; }

    // Получить все осколки
    public Map<Rarity, Integer> getAllShards() { return shards; }

    // Получить конкретное количество осколков
    public int getShardAmount(Rarity rarity) {
        return shards.getOrDefault(rarity, 0);
    }

    public int getMaxSlots() { return maxSlots; }

    // Сеттеры для обновления в памяти
    public void setShardAmount(Rarity rarity, int amount) {
        this.shards.put(rarity, amount);
    }

    public void addShardAmount(Rarity rarity, int amount) {
        this.shards.merge(rarity, amount, Integer::sum);
    }

    public void setMaxSlots(int maxSlots) { this.maxSlots = maxSlots; }
}