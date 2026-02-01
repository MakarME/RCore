package org.rebelland.rcore.model.socials;

public enum SocialAction {
    FRIEND_ADD,
    FRIEND_REMOVE,
    FRIEND_INVITE,
    FRIEND_JOIN,
    FRIEND_DECLINE,
    FRIEND_CHANGE_SERVER,
    FRIEND_SETTINGS_CHANGE,
    FRIEND_QUIT,
    BLACKLIST_ADD,
    BLACKLIST_REMOVE,
    BLACKLIST_SETTINGS_CHANGE,
    REP_ADD,
    REP_REMOVE,
    GLOBAL_SETTINGS_CHANGE;

    public static SocialAction fromString(String str) {
        try {
            return valueOf(str.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }
}
