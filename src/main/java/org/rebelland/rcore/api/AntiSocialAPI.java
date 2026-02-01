package org.rebelland.rcore.api;

import org.rebelland.rcore.model.socials.*;

import java.util.List;
import java.util.UUID;

public interface AntiSocialAPI {
    long getReputation(UUID uuid);

    PingMode getPingMode(UUID uuid);

    boolean canPing(UUID sender, UUID target);

    boolean isBlocked(UUID owner, UUID target);

    List<Friend> getFriends(UUID uuid);

    public BlockedPlayer getBlockEntry(UUID sender, UUID target);

    /**
     * Можно ли Sender'у отправить запрос на телепортацию (TPA) к Target'у?
     */
    public boolean canTpa(UUID sender, UUID target);

    /**
     * Можно ли Sender'у написать в ЛС Target'у?
     */
    public boolean canSendPm(UUID sender, UUID target);

    /**
     * Видит ли Target сообщения Sender'а в глобальном чате?
     * (Используйте это в ивенте чата: event.getRecipients().removeIf...)
     */
    boolean canSeeChat(UUID sender, UUID target);
}
