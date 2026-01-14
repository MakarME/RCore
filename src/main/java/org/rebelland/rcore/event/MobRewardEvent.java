package org.rebelland.rcore.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NonNull;

public class MobRewardEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    // Поля final, так как убийца и жертва не меняются в процессе события
    private final Player killer;
    private final LivingEntity victim;

    // Изменяемые поля (другие плагины могут их править)
    private double baseAmount;
    private double multiplier;
    private boolean cancelled = false;

    public MobRewardEvent(Player killer, LivingEntity victim, double baseAmount, double multiplier) {
        super(true); // true = ивент асинхронный
        this.killer = killer;
        this.victim = victim;
        this.baseAmount = baseAmount;
        this.multiplier = multiplier;
    }

    // --- Геттеры и Сеттеры (вместо Lombok) ---

    public Player getKiller() {
        return killer;
    }

    public LivingEntity getVictim() {
        return victim;
    }

    public double getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(double baseAmount) {
        this.baseAmount = baseAmount;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    // --- Логика ---

    /**
     * Добавить значение к текущему множителю.
     * @param amount сколько добавить (например, 0.5)
     */
    public void addMultiplier(double amount) {
        this.multiplier += amount;
    }

    /**
     * Рассчитать итоговую сумму (База * Множитель)
     */
    public double getFinalAmount() {
        return baseAmount * multiplier;
    }

    // --- Реализация Cancellable ---

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    // --- Обязательный Boilerplate для Bukkit Event API ---

    @Override
    public @NonNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}