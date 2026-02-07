package org.rebelland.rcore.api;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.inventory.ItemStack;
import org.rebelland.rcore.model.boosts.ActiveBoost;
import org.rebelland.rcore.model.boosts.Boost;
import org.rebelland.rcore.model.boosts.BoostPlayer;
import org.rebelland.rcore.model.boosts.Rarity;

import java.util.*;

public interface BoostAPI {
    List<Boost> getBoosts(UUID uuid);

    List<ActiveBoost> getActiveBoosts(UUID uuid);

    ActiveBoost getActiveBoost(UUID uuid, int slot);

    BoostPlayer getInfo(UUID uuid);

    Map<Rarity, Integer> getPlayerShards(UUID uuid);

    boolean hasSpaceInInventory(UUID uuid);

    Boost findBoostBySlot(UUID p, int slot);

    Boost findBoostById(UUID p, int id);

    void addBoostToCache(UUID uuid, Boost boost);

    void removeBoost(UUID p, Boost boost, Runnable onSuccess);

    void equipBoostToCache(UUID uuid, ActiveBoost activeBoost);

    void unequipBoostFromCache(UUID uuid, int slot);

    String generateBoostTitle(Boost boost);

    List<String> generateBoostLore(Boost boost);

    ItemStack getBoostItem(Boost boost);

    TextComponent getBoostStar(Boost boost);
}
