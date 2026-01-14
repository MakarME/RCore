package org.rebelland.rcore.model.clans;

import java.util.UUID;

public class ClanPlayerGlobal {
    private final UUID uuid;
    private final String name; // Добавлено поле
    private int personalStacks;

    public ClanPlayerGlobal(UUID uuid, String name, int personalStacks) {
        this.uuid = uuid;
        this.name = name;
        this.personalStacks = personalStacks;
    }

    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    public int getPersonalStacks() { return personalStacks; }

    public void setPersonalStacks(int personalStacks) { this.personalStacks = personalStacks; }
}
