package org.rebelland.rcore.model.socials;

public enum BlockMessageMode {
    ONLY_CHAT("&eТолько Чат"),
    ONLY_PM("&eТолько ЛС"),
    RECEIVE_ALL("&aВсе сообщения"),
    RECEIVE_NONE("&cНичего (Полный игнор)");

    private final String label;

    BlockMessageMode(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static BlockMessageMode fromName(String name) {
        try {
            return valueOf(name);
        } catch (Exception e) {
            return ONLY_CHAT; // Дефолт - полный игнор
        }
    }

    // Хелпер для цикличного переключения в меню
    public BlockMessageMode next() {
        int nextOrdinal = (this.ordinal() + 1) % values().length;
        return values()[nextOrdinal];
    }
}
