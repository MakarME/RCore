package org.rebelland.rcore.model.clans;

import java.util.UUID;

public class ClanTransaction {
    private final int id;
    private final int clanId;
    private final UUID playerUuid;
    private final long amount;
    private final String reason;
    private final long timestamp;

    public ClanTransaction(int id, int clanId, UUID playerUuid, long amount, String reason, long timestamp) {
        this.id = id;
        this.clanId = clanId;
        this.playerUuid = playerUuid;
        this.amount = amount;
        this.reason = reason;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public int getClanId() {
        return clanId;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public long getAmount() {
        return amount;
    }

    public String getReason() {
        return reason;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
