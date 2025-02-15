package me.projectbw.BWTelegramNotify.events;

import com.velocitypowered.api.event.Event;
import com.velocitypowered.api.event.player.ServerSwitchEvent;
import com.velocitypowered.api.event.player.Player;

public class ServerSwitchEvent {
    private final Player player;
    private final String previousServer;

    public ServerSwitchEvent(Player player, String previousServer) {
        this.player = player;
        this.previousServer = previousServer;
    }

    public Player getPlayer() {
        return player;
    }

    public String getPreviousServer() {
        return previousServer;
    }
}
