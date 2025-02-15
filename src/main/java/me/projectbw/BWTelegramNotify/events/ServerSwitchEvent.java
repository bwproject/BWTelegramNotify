package java.me.projectbw.BWTelegramNotify.events;

import com.velocitypowered.api.event.Listener;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerSwitchEvent;
import java.me.projectbw.BWTelegramNotify.Notifier;

public class ServerSwitchEvent {

    private final Notifier notifier;

    public ServerSwitchEvent(Notifier notifier) {
        this.notifier = notifier;
    }

    @Subscribe
    public void onServerSwitch(ServerSwitchEvent event) {
        String playerName = event.getPlayer().getUsername();
        String serverName = event.getPlayer().getCurrentServer().map(s -> s.getServerInfo().getName()).orElse("unknown");
        notifier.sendServerSwitchNotification(playerName, serverName);
    }
}
