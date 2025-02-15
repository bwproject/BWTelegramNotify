package me.projectbw.BWTelegramNotify;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import javax.inject.Inject;
import java.util.logging.Logger;

public class Main {

    private final ProxyServer server;
    private final TelegramBot bot;
    private final Logger logger;

    @Inject
    public Main(ProxyServer server, TelegramBot bot, Logger logger) {
        this.server = server;
        this.bot = bot;
        this.logger = logger;
    }

    @Subscribe
    public void onPlayerJoin(PostLoginEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getUsername();

        // Сообщение о входе
        String message = "Игрок " + playerName + " зашел в сервер!";
        bot.sendMessageToTelegram(message);

        logger.info("Игрок " + playerName + " присоединился.");
    }

    @Subscribe
    public void onPlayerQuit(DisconnectEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getUsername();

        // Сообщение о выходе игрока
        String message = "Игрок " + playerName + " покинул сервер!";
        bot.sendMessageToTelegram(message);

        logger.info("Игрок " + playerName + " покинул сервер.");
    }
}
