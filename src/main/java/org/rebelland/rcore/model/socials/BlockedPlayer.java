package org.rebelland.rcore.model.socials;

import java.util.UUID;

public class BlockedPlayer {
    private final UUID uuid;
    private final String name;
    private final long blockedAt;

    // Эффективные настройки (для логики проверок)
    private final BlockMessageMode effMessageMode;
    private final boolean effAllowTeleport;

    // Сырые настройки (для меню)
    private final boolean useCustom;
    private final BlockMessageMode customMessageMode;
    private final boolean customAllowTeleport;

    public BlockedPlayer(UUID uuid, String name, long blockedAt,
                         BlockMessageMode effMessageMode, boolean effAllowTeleport,
                         boolean useCustom, BlockMessageMode customMessageMode, boolean customAllowTeleport) {
        this.uuid = uuid;
        this.name = name;
        this.blockedAt = blockedAt;

        this.effMessageMode = effMessageMode;
        this.effAllowTeleport = effAllowTeleport;

        this.useCustom = useCustom;
        this.customMessageMode = customMessageMode;
        this.customAllowTeleport = customAllowTeleport;
    }

    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    public long getBlockedAt() { return blockedAt; }

    // Геттеры для логики
    public BlockMessageMode getMessageMode() { return effMessageMode; }
    public boolean isAllowTeleport() { return effAllowTeleport; }

    // Геттеры для меню
    public boolean isUseCustom() { return useCustom; }
    public BlockMessageMode getCustomMessageMode() { return customMessageMode; }
    public boolean isCustomAllowTeleport() { return customAllowTeleport; }
}