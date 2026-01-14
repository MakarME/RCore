package org.rebelland.rcore.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.rebelland.rcore.database.UserRepository;

import java.util.UUID;

public class UserListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) return;
        try {
            UserRepository.getInstance().upsertUser(
                    event.getUniqueId(),
                    event.getName(),
                    event.getAddress().getHostAddress()
            );
        } catch (Exception e) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§cОшибка базы данных. Попробуйте позже.");
            e.printStackTrace();
        }
    }
}
