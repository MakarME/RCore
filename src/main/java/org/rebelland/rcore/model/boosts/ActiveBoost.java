package org.rebelland.rcore.model.boosts;

public class ActiveBoost {
    private final Boost boost;
    private final int slot;

    public ActiveBoost(Boost boost, int slot) {
        this.boost = boost;
        this.slot = slot;
    }

    public Boost getBoost() {
        return boost;
    }

    public int getSlot() {
        return slot;
    }
}
