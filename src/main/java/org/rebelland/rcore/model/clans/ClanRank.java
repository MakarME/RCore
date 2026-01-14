package org.rebelland.rcore.model.clans;

import org.mineacademy.fo.Common;

public enum ClanRank {
    LEADER(100, "&4"),      // Лидер
    WARLORD(30, "&b"),      // Воевода
    CAPTAIN(20, "&f&l"),    // Страж/Капитан
    SOLDIER(10, "&e"),      // Боец
    RECRUIT(0, "&7");       // Новобранец

    private final int level;
    private final String colorCode;

    ClanRank(int level, String colorCode) {
        this.level = level;
        this.colorCode = colorCode;
    }

    public int getLevel() {
        return level;
    }

    public String getColorCode() {
        return colorCode;
    }

    public String getColoredName() {
        return Common.colorize(colorCode + name());
    }

    public static ClanRank fromLevel(int level) {
        for (ClanRank type : values()) {
            if (type.level == level)
                return type;
        }
        // Возвращаем рекрута как дефолт или кидаем ошибку
        return RECRUIT;
    }
}
