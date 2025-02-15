package me.projectbw.BWTelegramNotify;

import com.velocitypowered.api.event.EventHandler;
import com.velocitypowered.api.event.Listener;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.Server;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import javax.inject.Inject;
import java.util.logging.Logger;

public class Main implements Listener {

    private final ProxyServer server;
    private final TelegramBot bot;
    private final Logger logger;

    @Inject
    public Main(ProxyServer server, TelegramBot bot, Logger logger) {
        this.server = server;
        this.bot = bot;
        this.logger = logger;
    }

    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getUsername();

        // Сообщение о входе
        String message = "Игрок " + playerName + " зашел в сервер!";
        bot.sendMessageToTelegram(message);
        
        logger.info("Игрок " + playerName + " присоединился.");
    }

    @EventHandler
    public void onPlayerQuit(DisconnectEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getUsername();

        // Сообщение о выходе
        String message = "Игрок " + playerName + " покинул сервер!";
        bot.sendMessageToTelegram(message);

        logger.info("Игрок " + playerName + " покинул сервер.");
    }

    // Можете использовать дополнительную логику для уведомлений
    // например, для изменения серверов или низкого TPS
}
