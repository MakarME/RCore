package org.rebelland.rcore.model.socials;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SocialCache {
    private final UUID uuid;
    private final String name;
    private PlayerSettings settings;
    private final List<Friend> friends;
    private final List<BlockedPlayer> blockedPlayers;
    private final List<UUID> incomingRequests;
    private long reputation;
    private boolean pingsEnabled; // НОВОЕ ПОЛЕ

    public SocialCache(UUID uuid, String name, PlayerSettings settings, List<Friend> friends,
                       List<BlockedPlayer> blocked, List<UUID> requests, long reputation, boolean pingsEnabled) {
        this.uuid = uuid;
        this.name = name;
        this.settings = settings;
        this.friends = friends;
        this.blockedPlayers = blocked;
        this.incomingRequests = requests;
        this.reputation = reputation;
        this.pingsEnabled = pingsEnabled;
    }

    public boolean isPingsEnabled() { return pingsEnabled; }
    public void setPingsEnabled(boolean pingsEnabled) { this.pingsEnabled = pingsEnabled; }

    public String getName() { return name; }
    public UUID getUuid() { return uuid; }

    public PlayerSettings getSettings() { return settings; }
    public List<Friend> getFriends() { return friends; }
    public List<BlockedPlayer> getBlockedPlayers() { return blockedPlayers; }

    public List<UUID> getIncomingRequests() {
        return incomingRequests != null ? incomingRequests : new ArrayList<>();
    }

    public long getReputation() { return reputation; }

    public void setSettings(PlayerSettings settings) { this.settings = settings; }
    public void setReputation(long reputation) { this.reputation = reputation; }
}