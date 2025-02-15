package me.projectbw.BWTelegramNotify.events;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerSwitchEvent;
import me.projectbw.BWTelegramNotify.Notifier;

public class ServerSwitchEvent {

    private final Notifier notifier;

    public ServerSwitchEvent(Notifier notifier) {
        this.notifier = notifier;
    }

    @Subscribe
    public void onServerSwitch(ServerSwitchEvent event) {
        String playerName = event.getPlayer().getUsername();
        // Логика отправки уведомления
        notifier.sendServerSwitchNotification(playerName, event.getPreviousServer().getName());
    }
}
