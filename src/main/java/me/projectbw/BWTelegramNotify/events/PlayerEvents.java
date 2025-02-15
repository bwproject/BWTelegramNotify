package java.me.projectbw.BWTelegramNotify.events;

import com.velocitypowered.api.event.Listener;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerJoinEvent;
import com.velocitypowered.api.event.player.PlayerQuitEvent;
import me.projectbw.BWTelegramNotify.Notifier;

public class PlayerEvents {

    private final Notifier notifier;

    public PlayerEvents(Notifier notifier) {
        this.notifier = notifier;
    }

    @Subscribe
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getUsername();
        notifier.sendPlayerNotification(playerName, "joined the server");
    }

    @Subscribe
    public void onPlayerQuit(PlayerQuitEvent event) {
        String playerName = event.getPlayer().getUsername();
        notifier.sendPlayerNotification(playerName, "left the server");
    }
}

