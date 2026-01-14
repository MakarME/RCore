package org.rebelland.rcore.model.boosts;

import java.util.Collection;

public class BoostEffect {
    private final EffectType type;
    private double value;
    private final double quality; // 0.85 = 85%
    private final boolean isSpecial;

    public BoostEffect(EffectType type, double value, double quality, boolean isSpecial) {
        this.type = type;
        this.value = value;
        this.quality = quality;
        this.isSpecial = isSpecial;
    }

    public double getQuality() { return quality; }
    public EffectType getType() { return type; }
    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; } // Для реролла/полировки
    public boolean isSpecial() { return isSpecial; }
}
