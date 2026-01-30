package org.rebelland.rcore.model.socials;

import java.util.UUID;

public class BlockedPlayer {
    private final UUID uuid;
    private final String name;
    private final long blockedAt;

    public BlockedPlayer(UUID uuid, String name, long blockedAt) {
        this.uuid = uuid;
        this.name = name;
        this.blockedAt = blockedAt;
    }

    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    public long getBlockedAt() { return blockedAt; }
}
