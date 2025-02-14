package me.projectbw.BWTelegramNotify;

import com.velocitypowered.api.event.Listener;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.event.proxy.PlayerJoinEvent;
import com.velocitypowered.api.event.proxy.PlayerLeaveEvent;
import com.velocitypowered.api.event.proxy.ServerPreConnectEvent;
import com.velocitypowered.api.event.handler.EventHandler;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

@Plugin(id = "BWTelegramNotify", name = "BWTelegramNotify", version = "1.0-SNAPSHOT")
public class BWTelegramNotifyVelocity {
    private final ProxyServer server;
    private final Logger logger = LoggerFactory.getLogger(BWTelegramNotifyVelocity.class);
    private TelegramBot telegramBot;

    @Inject
    public BWTelegramNotifyVelocity(ProxyServer server) {
        this.server = server;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (Config.get().notifications.player_login) {
            telegramBot.sendMessage("Player " + event.getPlayer().getUsername() + " joined.");
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerLeaveEvent event) {
        if (Config.get().notifications.player_logout) {
            telegramBot.sendMessage("Player " + event.getPlayer().getUsername() + " left.");
        }
    }

    @EventHandler
    public void onServerChange(ServerPreConnectEvent event) {
        if (Config.get().notifications.server_change) {
            telegramBot.sendMessage("Player " + event.getPlayer().getUsername() + " is switching to server " + event.getTarget().getName());
        }
    }

    @EventHandler
    public void onServerPing(ProxyPingEvent event) {
        if (Config.get().notifications.low_tps && server.getConfiguration().getTPS() < 15) {
            telegramBot.sendMessage("Low TPS detected: " + server.getConfiguration().getTPS() + " TPS");
        }
    }

    @EventHandler
    public void onServerStart() {
        telegramBot.sendMessage("Server started!");
    }

    @EventHandler
    public void onServerStop() {
        telegramBot.sendMessage("Server stopped!");
    }

    public void onEnable() {
        telegramBot = new TelegramBot(Config.get().telegram.token);
        telegramBot.start();
        logger.info("BWTelegramNotify plugin enabled.");
    }

    public void onDisable() {
        telegramBot.stop();
        logger.info("BWTelegramNotify plugin disabled.");
    }
}
