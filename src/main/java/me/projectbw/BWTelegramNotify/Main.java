package me.projectbw.BWTelegramNotify;

import com.velocitypowered.api.event.EventHandler;
import com.velocitypowered.api.event.Listener;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import me.projectbw.BWTelegramNotify.events.PlayerEvents;
import me.projectbw.BWTelegramNotify.events.ServerEvents;
import me.projectbw.BWTelegramNotify.events.ServerSwitchEvent;
import me.projectbw.BWTelegramNotify.events.TPSMonitor;

import javax.inject.Inject;

@Plugin(id = "bwtelegramnotify", name = "BWTelegramNotify", version = "1.0")
public class Main {

    private final ProxyServer server;
    private final Notifier notifier;

    @Inject
    public Main(ProxyServer server, Notifier notifier) {
        this.server = server;
        this.notifier = notifier;
    }

    @EventHandler
    public void onEnable() {
        server.getEventManager().register(this, new PlayerEvents(notifier));
        server.getEventManager().register(this, new ServerEvents(notifier));
        server.getEventManager().register(this, new ServerSwitchEvent(notifier));
        server.getEventManager().register(this, new TPSMonitor(notifier));
    }
}
