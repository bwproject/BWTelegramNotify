package me.projectbw.BWTelegramNotify.events;

import org.bukkit.event.server.ServerPingEvent;
import org.bukkit.event.server.ServerShutdownEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ServerEvents implements Listener {

    @EventHandler
    public void onServerPing(ServerPingEvent event) {
        // Ваш код для обработки события пинга сервера
    }

    @EventHandler
    public void onServerShutdown(ServerShutdownEvent event) {
        // Ваш код для обработки события выключения сервера
    }
}
