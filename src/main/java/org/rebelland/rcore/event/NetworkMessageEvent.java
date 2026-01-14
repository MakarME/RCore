package org.rebelland.rcore.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Вызывается АСИНХРОННО, когда приходит сообщение из Redis.
 */
public class NetworkMessageEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final String channel;
    private final String message;

    public NetworkMessageEvent(String channel, String message) {
        super(true); // true = async
        this.channel = channel;
        this.message = message;
    }

    public String getChannel() { return channel; }
    public String getMessage() { return message; }

    @Override
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
