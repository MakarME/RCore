package org.rebelland.rcore.model.socials;

public class PlayerSettings {
    private boolean allowFriendRequests;
    private boolean globalNotifyOnJoin;
    private boolean globalReceiveJoinNotifs;
    private boolean globalReceivePMs;

    // Новое поле
    private PingMode pingMode;

    private BlockMessageMode globalBlockMessageMode; // Что я вижу от заблокированных по умолчанию?
    private boolean globalBlockAllowTeleport;        // Разрешаю ли я тп от заблокированных по умолчанию?

    public PlayerSettings(boolean requests, boolean notify, boolean receive, boolean pms, PingMode pingMode, BlockMessageMode globalBlockMessageMode, boolean globalBlockAllowTeleport) {
        this.allowFriendRequests = requests;
        this.globalNotifyOnJoin = notify;
        this.globalReceiveJoinNotifs = receive;
        this.globalReceivePMs = pms;
        this.pingMode = pingMode;
    }

    public BlockMessageMode getGlobalBlockMessageMode() { return globalBlockMessageMode; }
    public void setGlobalBlockMessageMode(BlockMessageMode mode) { this.globalBlockMessageMode = mode; }

    public boolean isGlobalBlockAllowTeleport() { return globalBlockAllowTeleport; }
    public void setGlobalBlockAllowTeleport(boolean allow) { this.globalBlockAllowTeleport = allow; }

    // Геттеры и сеттеры
    public PingMode getPingMode() { return pingMode; }
    public void setPingMode(PingMode pingMode) { this.pingMode = pingMode; }

    public boolean isAllowFriendRequests() { return allowFriendRequests; }
    public void setAllowFriendRequests(boolean allow) { this.allowFriendRequests = allow; }

    public boolean isGlobalNotifyOnJoin() { return globalNotifyOnJoin; }
    public void setGlobalNotifyOnJoin(boolean notify) { this.globalNotifyOnJoin = notify; }

    public boolean isGlobalReceiveJoinNotifs() { return globalReceiveJoinNotifs; }
    public void setGlobalReceiveJoinNotifs(boolean receive) { this.globalReceiveJoinNotifs = receive; }

    public boolean isGlobalReceivePMs() { return globalReceivePMs; }
    public void setGlobalReceivePMs(boolean pms) { this.globalReceivePMs = pms; }
}
