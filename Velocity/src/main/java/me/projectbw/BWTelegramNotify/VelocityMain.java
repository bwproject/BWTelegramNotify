package me.projectbw.BWTelegramNotify;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import javax.inject.Inject;
import java.util.List;
import java.util.logging.Logger;

@Plugin(id = "bwtelegramnotify", name = "BWTelegramNotify", version = "1.0.0", description = "Плагин для уведомлений в Telegram", authors = {"The_Mr_Mes109"})
public class VelocityMain {

    private final ProxyServer server;
    private final Logger logger;
    private TelegramBot telegramBot;

    @Inject
    public VelocityMain(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("==================================");
        logger.info("=== BWTelegramNotify загружается ===");
        logger.info("==================================");

        loadConfig();

        if (telegramBot != null) {
            String message = "🔵 **Прокси-сервер запущен!**";
            telegramBot.sendMessage(message);
        }

        logger.info("BWTelegramNotify успешно загружен!");
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        logger.info("BWTelegramNotify: Остановка плагина...");
        if (telegramBot != null) {
            String message = "🔴 **Прокси-сервер выключен!**";
            telegramBot.sendMessage(message);
        }
        logger.info("BWTelegramNotify успешно отключен.");
    }

    @Subscribe
    public void onPlayerLogin(LoginEvent event) {
        String playerName = event.getPlayer().getUsername();
        logger.info("Игрок зашел: " + playerName);
        if (telegramBot != null) {
            String message = "✅ **Игрок зашел**: " + playerName;
            telegramBot.sendMessage(message);
        }
    }

    @Subscribe
    public void onPlayerDisconnect(DisconnectEvent event) {
        String playerName = event.getPlayer().getUsername();
        logger.info("Игрок вышел: " + playerName);
        if (telegramBot != null) {
            String message = "❌ **Игрок вышел**: " + playerName;
            telegramBot.sendMessage(message);
        }
    }

    @Command(aliases = "velocity_send", description = "Sends a message to the Telegram bot.", usage = "/velocity_send <action> <message>")
    public void velocitySend(CommandSource source, String action, String message) {
        // Обработка полученной команды и отправка в Telegram
        switch (action) {
            case "server_started":
                telegramBot.sendMessage(message);
                break;
            case "server_stopped":
                telegramBot.sendMessage(message);
                break;
            case "player_join":
                telegramBot.sendMessage(message);
                break;
            case "player_quit":
                telegramBot.sendMessage(message);
                break;
            case "low_tps":
                telegramBot.sendMessage(message);
                break;
            default:
                source.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("❌ Неизвестная команда: " + action));
                break;
        }
    }

    private void loadConfig() {
        String botToken = "ВАШ_ТОКЕН";
        List<String> chatIds = List.of("ВАШ_ЧАТ_ID");
        telegramBot = new TelegramBot(botToken, chatIds);
    }
}