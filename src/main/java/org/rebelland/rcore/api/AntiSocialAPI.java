package org.rebelland.rcore.api;

import org.rebelland.rcore.model.socials.Friend;

import java.util.List;
import java.util.UUID;

public interface AntiSocialAPI {
    long getReputation(UUID uuid);

    boolean arePingsEnabled(UUID uuid);

    boolean isBlocked(UUID owner, UUID target);

    public List<Friend> getFriends(UUID uuid);
}
