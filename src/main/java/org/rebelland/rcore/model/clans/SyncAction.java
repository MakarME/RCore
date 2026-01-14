package org.rebelland.rcore.model.clans;

public enum SyncAction {
    UPDATE,         // Общие настройки клана (pvp, slots, tag и т.д.)
    DELETE,         // Удаление клана
    INVALIDATE_ALL, // Полный сброс
    NEW_CLAN,       // Новый клан
    CLAN_EXP_CHANGE,// Изменение опыта клана
    REQUEST_ACCEPTED,
    REQUEST_DENIED,
    REQUEST_DELETE,
    REQUEST_ADD,

    // Точечные обновления
    MEMBER_UPDATE,  // Участники (вход, выход, кик, смена ранга)
    PLAYER_UPDATE,  // Глобальные данные игрока
    HOME_UPDATE,    // Дома (создание, удаление, перемещение)
    STORAGE_UPDATE; // Хранилище (изменение слотов)

    public static SyncAction fromString(String str) {
        try {
            return valueOf(str.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }
}
