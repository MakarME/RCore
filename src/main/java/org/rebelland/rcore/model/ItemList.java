package org.rebelland.rcore.model;

import org.bukkit.Material;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ItemList implements Iterable<ItemList.Entry> {

    public static class Entry {
        private final Material material;
        private double amount;

        public Entry(Material material) {
            this.material = material;
            this.amount = 0.0;
        }

        public Material getMaterial() { return material; }
        public double getAmount() { return amount; }
        public void addAmount(double value) { this.amount += value; }

        /**
         * Уменьшает и возвращает true, если после вычитания amount == 0 (требуется удалить).
         * Не удаляет сам entry — это ответственность ItemList.
         */
        public boolean subtractAmount(int value) {
            if (amount >= value) {
                amount -= value;
                return amount == 0;
            }
            return false; // не хватило
        }
    }

    // Синхронизированный список + concurrent map для быстрого доступа по материалу
    private final List<Entry> entries = Collections.synchronizedList(new ArrayList<>());
    private final Map<Material, Entry> entryMap = new ConcurrentHashMap<>();

    /**
     * Добавляет amount для материала. Синхронизация нужна, чтобы не ломать entries при параллельных операциях.
     */
    public void addAmount(Material material, double amount) {
        // синхронизируемся на entries, чтобы совместно с сортировкой и итерацией были атомарные изменения списка
        synchronized (entries) {
            Entry entry = entryMap.get(material);
            if (entry == null) {
                entry = new Entry(material);
                entryMap.put(material, entry);
                entries.add(entry);
            }
            entry.addAmount(amount);
        }
    }

    public boolean subtractAmount(Material material, int amountToSubtract) {
        synchronized (entries) {
            Entry entry = entryMap.get(material);
            if (entry == null) return false;

            boolean becameZero = entry.subtractAmount(amountToSubtract);
            if (becameZero) {
                entryMap.remove(material);
                entries.remove(entry);
            }
            return true; // сюда мы попали только если было достаточно (entry.subtractAmount возвращал false/true),
            // но нам важно отличать "не хватило" — тогда entry.subtractAmount вернул false и мы возвращаем false выше.
        }
    }

    // если нужен, добавь метод, который возвращает точное булево значение успеха:
    public boolean trySubtractAmount(Material material, int amountToSubtract) {
        synchronized (entries) {
            Entry entry = entryMap.get(material);
            if (entry == null) return false;
            boolean hadEnough = entry.getAmount() >= amountToSubtract;
            if (!hadEnough) return false;
            boolean becameZero = entry.subtractAmount(amountToSubtract);
            if (becameZero) {
                entryMap.remove(material);
                entries.remove(entry);
            }
            return true;
        }
    }

    /**
     * Пересортировка по убыванию amount.
     * ВАЖНО: синхронизируемся на entries, чтобы никто не менял список в процессе сортировки.
     */
    public void sortDescending() {
        synchronized (entries) {
            entries.sort(Comparator.comparingDouble(Entry::getAmount).reversed());
        }
    }

    /**
     * Итератор — возвращаем snapshot (копию) чтобы внешний код мог безопасно итерировать без синхронизации.
     */
    @Override
    public Iterator<Entry> iterator() {
        // делаем копию под синхронизацией
        synchronized (entries) {
            return new ArrayList<>(entries).iterator();
        }
    }

    // Нельзя отдавать сам entries напрямую, отдаём unmodifiable копию
    public List<Entry> getEntries() {
        synchronized (entries) {
            return Collections.unmodifiableList(new ArrayList<>(entries));
        }
    }
}