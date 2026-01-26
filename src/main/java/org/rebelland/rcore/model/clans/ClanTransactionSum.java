package org.rebelland.rcore.model.clans;

import java.util.UUID;

public class ClanTransactionSum {
    private final UUID playerUuid;
    private final long totalAmount;

    public ClanTransactionSum(UUID playerUuid, long totalAmount) {
        this.playerUuid = playerUuid;
        this.totalAmount = totalAmount;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public long getTotalAmount() {
        return totalAmount;
    }
}
