package org.rebelland.rcore.model.socials;

import java.util.UUID;

public class Friend {
    private final UUID uuid;
    private final String name;
    private final boolean isFavorite;
    private final long becameFriendAt;

    // Эффективные настройки (уже вычисленные с учетом глобальных/кастомных)
    private final boolean notifyOnJoin;
    private final boolean receiveJoinNotifs;

    public Friend(UUID uuid, String name, boolean isFavorite, long becameFriendAt,
                  boolean notifyOnJoin, boolean receiveJoinNotifs) {
        this.uuid = uuid;
        this.name = name;
        this.isFavorite = isFavorite;
        this.becameFriendAt = becameFriendAt;
        this.notifyOnJoin = notifyOnJoin;
        this.receiveJoinNotifs = receiveJoinNotifs;
    }

    // Getters...
    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    public boolean isFavorite() { return isFavorite; }

    public boolean isNotifyOnJoin() {
        return notifyOnJoin;
    }

    public boolean isReceiveJoinNotifs() {
        return receiveJoinNotifs;
    }

    public long getBecameFriendAt() {
        return becameFriendAt;
    }
}