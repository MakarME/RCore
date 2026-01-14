package org.rebelland.rcore.model.clans;

import java.util.Objects;
import java.util.UUID;

public class ClanRequest {
    private final int clanId;
    private final UUID playerUuid;
    private final long requestTime;

    public ClanRequest(int clanId, UUID playerUuid, long requestTime) {
        this.clanId = clanId;
        this.playerUuid = playerUuid;
        this.requestTime = requestTime;
    }

    public int getClanId() { return clanId; }
    public UUID getPlayerUuid() { return playerUuid; }
    public long getRequestTime() { return requestTime; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClanRequest that = (ClanRequest) o;
        return clanId == that.clanId &&
                Objects.equals(playerUuid, that.playerUuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clanId, playerUuid);
    }
}
