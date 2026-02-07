package org.rebelland.rcore.model;

import java.util.UUID;

public class PlayerInfo {

    private final UUID uuid;
    private final String name;
    private final long lastLogin; // Доп. данные (время последнего входа)

    public PlayerInfo(UUID uuid, String name, long lastLogin) {
        this.uuid = uuid;
        this.name = name;
        this.lastLogin = lastLogin;
    }

    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    public long getLastLogin() { return lastLogin; }

    @Override
    public String toString() {
        return "PlayerInfo{name='" + name + "', uuid=" + uuid + "}";
    }
}
