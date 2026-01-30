package org.rebelland.rcore.api;

import org.rebelland.rcore.model.clans.*;

import java.util.*;

public interface ClansAPI {
    Clan getClan(int id);

    List<ClanMember> getMembers(int clanId);

    List<ClanMember> getMembersSorted(int clanId);

    List<ClanHome> getHomes(int clanId);

    List<ClanRequest> getRequests(int clanId);

    ClanMember getMember(UUID uuid);

    List<ClanMember> getAllLeaders();

    ClanMember getClanLeader(int clanId);

    Clan getClanByPlayer(UUID uuid);

    Clan getClanByName(String name);

    Clan getClanByTag(String tag);

    List<ClanMember> getClanLevelTopSorted();

    List<ClanMember> getClanBalanceTopSorted();

    ClanPlayerGlobal getGlobalPlayer(UUID uuid);

    ClanPlayerGlobal getGlobalPlayer(String name);

    List<ClanPlayerGlobal> getGlobalPlayers();

    ClanRequest getRequestForClan(int clanId, UUID uuid);

    List<Clan> getClans();

    List<ClanTransactionSum> getTransactionSums(int clanId);

    List<ClanHome> getHomesForServer(int clanId, String server);

    ClanHome getHomeByName(int clanId, String homeName);

    boolean isClanChatOn(UUID uuid);
}