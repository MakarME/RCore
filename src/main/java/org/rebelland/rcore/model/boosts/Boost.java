package org.rebelland.rcore.model.boosts;

import java.util.*;

public class Boost {
    protected final int id;
    protected final UUID ownerUuid;
    protected final Rarity rarity;
    protected final List<BoostEffect> effects;
    protected int storageSlot;

    // Если поле не null - значит буст на аукционе
    protected AuctionInfo auctionInfo;

    public Boost(int id, UUID ownerUuid, Rarity rarity, List<BoostEffect> effects, int storageSlot, AuctionInfo auctionInfo) {
        this.id = id;
        this.ownerUuid = ownerUuid;
        this.rarity = rarity;
        this.effects = effects;
        this.auctionInfo = auctionInfo;
        this.storageSlot = storageSlot;
    }

    public int getStorageSlot() { return storageSlot; }
    public void setStorageSlot(int slot) { this.storageSlot = slot; }

    public int getId() {
        return id;
    }

    public List<BoostEffect> getEffects() {
        return effects;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public double getAverageQuality() {
        if (effects.isEmpty()) return 0.0;

        double total = 0;
        int count = 0;

        for (BoostEffect effect : effects) {
            // Гарантированные (Special) эффекты можно либо считать как 100%,
            // либо не учитывать в среднем качестве.
            // Допустим, считаем только рандомные статы:
            if (!effect.isSpecial()) {
                total += effect.getQuality();
                count++;
            }
        }

        if (count == 0) return 1.0; // Если только спецэффекты, считаем идеальным
        return total / count;
    }

    public boolean isOnSale(){
        return auctionInfo != null;
    }

    // --- ВАЖНЫЕ ИЗМЕНЕНИЯ НИЖЕ ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Boost boost = (Boost) o;
        // Два буста равны, только если у них совпадает ID
        return id == boost.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Boost{id=" + id + ", rarity=" + rarity + "}";
    }

    public UUID getOwnerUuid() {
        return ownerUuid;
    }

    public boolean hasEffect(EffectType type) {
        if (this.effects == null || this.effects.isEmpty()) {
            return false;
        }

        for (BoostEffect effect : this.effects) {
            if (effect.getType() == type) {
                return true;
            }
        }
        return false;
    }

    public BoostEffect getEffectByType(EffectType type) {
        if (this.effects == null || this.effects.isEmpty()) {
            return null;
        }
        for (BoostEffect effect : this.effects) {
            if (effect.getType() == type) {
                return effect;
            }
        }
        return null;
    }
}

