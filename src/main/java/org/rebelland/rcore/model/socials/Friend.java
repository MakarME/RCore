package org.rebelland.rcore.model.socials;

import java.util.UUID;

public class Friend {
    private final UUID uuid;
    private final String name;
    private final boolean isFavorite;
    private final long becameFriendAt;

    // Эффективные настройки (то, что влияет на логику уведомлений)
    private final boolean notifyOnJoin;
    private final boolean receiveJoinNotifs;

    // --- НОВЫЕ ПОЛЯ (Сырые настройки для меню) ---
    private final boolean useCustom;         // Включен ли режим "Особые настройки"?
    private final boolean customNotifyJoin;  // Галочка "Уведомлять о входе" (в БД)
    private final boolean customReceiveJoin; // Галочка "Видеть вход" (в БД)

    public Friend(UUID uuid, String name, boolean isFavorite, long becameFriendAt,
                  boolean notifyOnJoin, boolean receiveJoinNotifs,
                  // Добавляем в конструктор
                  boolean useCustom, boolean customNotifyJoin, boolean customReceiveJoin) {
        this.uuid = uuid;
        this.name = name;
        this.isFavorite = isFavorite;
        this.becameFriendAt = becameFriendAt;
        this.notifyOnJoin = notifyOnJoin;
        this.receiveJoinNotifs = receiveJoinNotifs;

        this.useCustom = useCustom;
        this.customNotifyJoin = customNotifyJoin;
        this.customReceiveJoin = customReceiveJoin;
    }

    // Старые геттеры...
    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    public boolean isFavorite() { return isFavorite; }
    public boolean isNotifyOnJoin() { return notifyOnJoin; }
    public boolean isReceiveJoinNotifs() { return receiveJoinNotifs; }
    public long getBecameFriendAt() { return becameFriendAt; }

    // Новые геттеры
    public boolean isUseCustom() { return useCustom; }
    public boolean isCustomNotifyJoin() { return customNotifyJoin; }
    public boolean isCustomReceiveJoin() { return customReceiveJoin; }
}