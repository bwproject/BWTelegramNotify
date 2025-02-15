package me.projectbw.BWTelegramNotify.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerPingEvent;
import org.bukkit.event.server.ServerShutdownEvent;
import me.projectbw.BWTelegramNotify.Notifier;

public class ServerEvents implements Listener {

    private final Notifier notifier;

    public ServerEvents(Notifier notifier) {
        this.notifier = notifier;
    }

    @EventHandler
    public void onServerPing(ServerPingEvent event) {
        // Логика обработки пинга сервера
        notifier.sendServerStatusNotification("Server is being pinged!");
    }

    @EventHandler
    public void onServerShutdown(ServerShutdownEvent event) {
        // Логика обработки остановки сервера
        notifier.sendServerStatusNotification("Server is shutting down!");
    }
}
