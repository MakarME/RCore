package org.rebelland.rcore.model.boosts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public enum EffectType {
    DROP_CONVERT(StackingStrategy.MIN_BEST, "&6$", true),
    POTION_DURATION(StackingStrategy.SUM, "&c❤", false),
    UNBREAKING(StackingStrategy.CHANCE_CAP, "&b⚓", false),
    ONESHOT(StackingStrategy.CHANCE_CAP, "&b⚓", false),
    PLUS_ANARCHITE(StackingStrategy.CHANCE_CAP, "&b⚓", false),
    CROPS_GROWTH(StackingStrategy.MIN_BEST, "&6$", true),
    HEALTH(StackingStrategy.SUM, "&c❤", false),
    AUCTION_SLOT(StackingStrategy.SUM, "&e⛃", false),

    // Стратегия CHANCE: Складываем, но не более 100%
    FISHING_DOUBLE(StackingStrategy.CHANCE_CAP, "&b⚓", false),

    // Стратегия MULTIPLIER: Перемножаем (1.2 * 1.5 = 1.8)
    EXP_MULT(StackingStrategy.SUM, "&6$", false),
    RCOIN_MULT(StackingStrategy.SUM, "&6$", false),
    CASE_CHANCE(StackingStrategy.SUM, "&6$", false),
    SOULS_RESERVOIR(StackingStrategy.SUM, "&6$", false),
    COIN_MULT(StackingStrategy.SUM, "&6$", false);


    private final StackingStrategy strategy;
    private final String icon;
    private final boolean lowerIsBetter; // <--- Новый флаг

    EffectType(StackingStrategy strategy, String icon, boolean lowerIsBetter) {
        this.strategy = strategy;
        this.icon = icon;
        this.lowerIsBetter = lowerIsBetter;
    }

    public boolean isLowerIsBetter() { return lowerIsBetter; }

    public StackingStrategy getStrategy() { return strategy; }
    public String getIcon() { return icon; }

    // Кэшируем массив значений, чтобы не создавать его заново при каждом вызове getRandom()
    private static final EffectType[] VALUES = values();

    /**
     * Возвращает случайный эффект из списка.
     */
    public static EffectType getRandom() {
        return VALUES[ThreadLocalRandom.current().nextInt(VALUES.length)];
    }

    public static EffectType fromString(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        for (EffectType status : values()) {
            if (status.name().equals(value)) {
                return status;
            }
        }

        for (EffectType status : values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }

        return null;
    }

    public static EffectType getRandomExcept(Collection<EffectType> excluded) {
        List<EffectType> available = new ArrayList<>();

        for (EffectType type : VALUES) {
            // Если список исключений пуст или не содержит текущий тип, добавляем в доступные
            if (excluded == null || !excluded.contains(type)) {
                available.add(type);
            }
        }

        // Если доступных эффектов не осталось
        if (available.isEmpty()) {
            return null;
        }

        return available.get(ThreadLocalRandom.current().nextInt(available.size()));
    }

    public static EffectType getRandomExcept(List<BoostEffect> currentEffects) {
        List<EffectType> excludedTypes = new ArrayList<>();

        if (currentEffects != null) {
            for (BoostEffect effect : currentEffects) {
                excludedTypes.add(effect.getType());
            }
        }

        // Вызываем основной метод, который мы написали ранее
        return getRandomExcept(excludedTypes);
    }
}