package org.rebelland.rcore.api;

import org.mineacademy.fo.Common;
import org.rebelland.rcore.RCore;
import org.rebelland.rcore.model.boosts.ActiveBoost;
import org.rebelland.rcore.model.boosts.Boost;
import org.rebelland.rcore.model.boosts.PlayerInfo;
import org.rebelland.rcore.model.boosts.Rarity;

import java.sql.SQLException;
import java.util.*;

public interface BoostAPI {
    List<Boost> getBoosts(UUID uuid);

    List<ActiveBoost> getActiveBoosts(UUID uuid);

    ActiveBoost getActiveBoost(UUID uuid, int slot);

    PlayerInfo getInfo(UUID uuid);

    Map<Rarity, Integer> getPlayerShards(UUID uuid);

    boolean hasSpaceInInventory(UUID uuid);

    Boost findBoostBySlot(UUID p, int slot);

    Boost findBoostById(UUID p, int id);

    void addBoostToCache(UUID uuid, Boost boost);

    void removeBoost(UUID p, Boost boost, Runnable onSuccess);

    void equipBoostToCache(UUID uuid, ActiveBoost activeBoost);

    void unequipBoostFromCache(UUID uuid, int slot);
}
