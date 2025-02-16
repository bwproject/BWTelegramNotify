package me.projectbw.BWTelegramNotify;

import com.velocitypowered.api.event.Listener;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.connection.ServerSwitchEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.event.Subscribe;
import javax.inject.Inject;
import java.util.List;

@Plugin(id = "bwtelegramnotify", name = "BWTelegramNotify", version = "1.0", description = "Telegram Notification Bot for Velocity")
public class VelocityMain {

    private final ProxyServer server;
    private TelegramBot telegramBot;
    private List<String> chatIds;

    @Inject
    public VelocityMain(ProxyServer server) {
        this.server = server;
    }

    @Subscribe
    public void onPlayerJoin(PostLoginEvent event) {
        String playerName = event.getPlayer().getUsername();
        String joinMessage = "Игрок " + playerName + " присоединился.";
        telegramBot.sendMessages(chatIds, joinMessage);
    }

    @Subscribe
    public void onPlayerQuit(PreLoginEvent event) {
        String playerName = event.getPlayer().getUsername();
        String quitMessage = "Игрок " + playerName + " покинул сервер.";
        telegramBot.sendMessages(chatIds, quitMessage);
    }

    @Subscribe
    public void onServerSwitch(ServerSwitchEvent event) {
        String playerName = event.getPlayer().getUsername();
        String serverName = event.getPreviousServer().get().getServerInfo().getName();
        String switchMessage = "Игрок " + playerName + " сменил сервер с " + serverName + " на " + event.getServer().getServerInfo().getName();
        telegramBot.sendMessages(chatIds, switchMessage);
    }
}
