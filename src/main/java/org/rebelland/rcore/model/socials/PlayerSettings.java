package org.rebelland.rcore.model.socials;

public class PlayerSettings {
    private boolean allowFriendRequests;
    private boolean globalNotifyOnJoin;
    private boolean globalReceiveJoinNotifs;
    private boolean globalReceivePMs;

    public PlayerSettings(boolean requests, boolean notify, boolean receive, boolean pms) {
        this.allowFriendRequests = requests;
        this.globalNotifyOnJoin = notify;
        this.globalReceiveJoinNotifs = receive;
        this.globalReceivePMs = pms;
    }

    public boolean isAllowFriendRequests() { return allowFriendRequests; }
    public void setAllowFriendRequests(boolean allow) { this.allowFriendRequests = allow; }

    public boolean isGlobalNotifyOnJoin() { return globalNotifyOnJoin; }
    public void setGlobalNotifyOnJoin(boolean notify) { this.globalNotifyOnJoin = notify; }

    public boolean isGlobalReceiveJoinNotifs() { return globalReceiveJoinNotifs; }
    public void setGlobalReceiveJoinNotifs(boolean receive) { this.globalReceiveJoinNotifs = receive; }

    public boolean isGlobalReceivePMs() { return globalReceivePMs; }
    public void setGlobalReceivePMs(boolean pms) { this.globalReceivePMs = pms; }
}
