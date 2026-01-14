package org.rebelland.rcore.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.mineacademy.fo.Common;
import org.rebelland.rcore.RCore;
import org.rebelland.rcore.config.MainConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GlobalPlayerManager implements Listener {

    private static final int HEARTBEAT_INTERVAL_TICKS = 60; // 3 сек
    private static final int REDIS_TTL_SECONDS = 5;         // 5 сек

    private final String currentServerName;

    public GlobalPlayerManager() {
        this.currentServerName = MainConfig.SERVER;

        startHeartbeatTask();
    }

    private void startHeartbeatTask() {
        Common.runTimerAsync(20, HEARTBEAT_INTERVAL_TICKS, () ->{
            Map<UUID, String> onlineMap = new HashMap<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                onlineMap.put(player.getUniqueId(), currentServerName);
            }
            if (!onlineMap.isEmpty()) {
                RCore.getInstance().getRedisService().sendHeartbeat(onlineMap, REDIS_TTL_SECONDS);
            }
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        RCore.getInstance().getRedisService().updatePlayerHeartbeat(
                event.getPlayer().getUniqueId(),
                currentServerName,
                REDIS_TTL_SECONDS
        );
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        RCore.getInstance().getRedisService().removePlayerHeartbeat(event.getPlayer().getUniqueId());
    }

    public String getGlobalPlayerServer(UUID targetUuid) {
        Player localPlayer = Bukkit.getPlayer(targetUuid);
        if (localPlayer != null) {
            return currentServerName;
        }
        return RCore.getInstance().getRedisService().getPlayerServer(targetUuid);
    }

    public boolean isGlobalOnline(UUID targetUuid) {
        return getGlobalPlayerServer(targetUuid) != null;
    }
}
