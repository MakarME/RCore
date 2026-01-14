package org.rebelland.rcore.model.boosts;

public enum Rarity {
    COMMON("&7Обычный"),
    RARE("&9Редкий"),
    EPIC("&5Эпический"),
    LEGENDARY("&6Легендарный"),
    MYTHIC("&dМифический");

    private final String displayName;

    Rarity(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() { return displayName; }

    public static Rarity fromString(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        for (Rarity status : values()) {
            if (status.name().equals(value)) {
                return status;
            }
        }

        for (Rarity status : values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }

        return null;
    }
}