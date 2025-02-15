package java.me.projectbw.BWTelegramNotify.events;

import com.velocitypowered.api.event.Listener;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.server.ServerPingEvent;
import com.velocitypowered.api.event.server.ServerShutdownEvent;
import java.me.projectbw.BWTelegramNotify.Notifier;

public class ServerEvents {

    private final Notifier notifier;

    public ServerEvents(Notifier notifier) {
        this.notifier = notifier;
    }

    @Subscribe
    public void onServerPing(ServerPingEvent event) {
        notifier.sendServerStatusNotification("Server is up and running!");
    }

    @Subscribe
    public void onServerShutdown(ServerShutdownEvent event) {
        notifier.sendServerStatusNotification("Server is shutting down.");
    }
}
