package org.rebelland.rcore.model.socials;

public enum SocialAction {
    FRIEND_ADD,
    FRIEND_REMOVE,
    FRIEND_INVITE,
    FRIEND_JOIN,
    FRIEND_DECLINE,
    FRIEND_CHANGE_SERVER,
    FRIEND_QUIT,
    BLACKLIST_ADD,
    BLACKLIST_REMOVE,
    REP_ADD,
    REP_REMOVE;

    public static SocialAction fromString(String str) {
        try {
            return valueOf(str.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }
}
