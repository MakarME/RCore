package org.rebelland.rcore.model.clans;

public enum SyncAction {
    DELETE,         // Удаление клана
    INVALIDATE_ALL, // Полный сброс
    NEW_CLAN,       // Новый клан
    CLAN_EXP_CHANGE,// Изменение опыта клана
    CLAN_BALANCE_CHANGE,
    REQUEST_ACCEPTED,
    REQUEST_DENIED,
    REQUEST_DELETE,
    REQUEST_ADD,
    POTION_STACKS_UPDATE,
    CHAT,
    PLAYER_SETTINGS_UPDATE,
    INVITE_SEND,
    INVITE_RESULT_ACCEPT,
    INVITE_RESULT_DENY,
    MEMBER_KICK,
    MEMBER_LEAVE,
    CLAN_SETTINGS_UPDATE,

    // Точечные обновления
    MEMBER_UPDATE,  // Участники (вход, выход, кик, смена ранга)
    PLAYER_UPDATE,  // Глобальные данные игрока
    HOME_UPDATE,    // Дома (создание, удаление, перемещение)
    HOME_DELETE,
    HOME_NEW,
    STORAGE_UPDATE; // Хранилище (изменение слотов)

    public static SyncAction fromString(String str) {
        try {
            return valueOf(str.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }
}
