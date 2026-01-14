package org.rebelland.rcore.model.clans;

import java.util.Objects;
import java.util.UUID;

public class ClanMember {
    private final UUID uuid;
    private final String name; // Добавлено поле
    private final int clanId;
    private final long joinedAt;
    private boolean showClanArmor;
    private boolean clanChatEnabled;
    private ClanRank rank;

    public ClanMember(UUID uuid, String name, int clanId, long joinedAt, boolean showClanArmor, boolean clanChatEnabled, ClanRank rank) {
        this.uuid = uuid;
        this.name = name;
        this.clanId = clanId;
        this.joinedAt = joinedAt;
        this.showClanArmor = showClanArmor;
        this.clanChatEnabled = clanChatEnabled;
        this.rank = rank;
    }

    public UUID getUuid() { return uuid; }
    public String getName() { return name; } // Геттер
    public int getClanId() { return clanId; }
    public long getJoinedAt() { return joinedAt; }
    public boolean isShowClanArmor() { return showClanArmor; }
    public boolean isClanChatEnabled() { return clanChatEnabled; }
    public ClanRank getRank() { return rank; }

    public void setRank(ClanRank rank) { this.rank = rank; }
    public void setShowClanArmor(boolean showClanArmor) { this.showClanArmor = showClanArmor; }
    public void setClanChatEnabled(boolean clanChatEnabled) { this.clanChatEnabled = clanChatEnabled; }

    public boolean isLeader(){
        return rank == ClanRank.LEADER;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClanMember that = (ClanMember) o;
        return clanId == that.clanId &&
                Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clanId, uuid);
    }
}
