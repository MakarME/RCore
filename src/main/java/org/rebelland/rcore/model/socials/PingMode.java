package org.rebelland.rcore.model.socials;

public enum PingMode {
    ALLOW_ALL("Для всех"),      // Включено для всех
    FRIENDS_ONLY("Только для друхей"),   // Только для друзей
    DISABLED("Ни для кого");       // Выключено

    private final String displayName;

    PingMode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() { return displayName; }

    public static PingMode fromName(String name) {
        try {
            switch (name){
                case "on":
                    return ALLOW_ALL;
                case "off":
                    return DISABLED;
                case "friends":
                    return FRIENDS_ONLY;
                default:
                    return valueOf(name);
            }
        } catch (Exception e) {
            return ALLOW_ALL;
        }
    }
}
