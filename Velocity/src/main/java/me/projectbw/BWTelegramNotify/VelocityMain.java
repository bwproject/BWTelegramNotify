package me.projectbw.BWTelegramNotify;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Plugin(id = "bwtelegramnotify", name = "BWTelegramNotify", version = "1.0")
public class VelocityMain {
    private final ProxyServer server;
    private final Logger logger;
    private TelegramBot telegramBot;
    private WebhookServer webhookServer;
    private String botToken;
    private String botUsername;
    private String webhookUrl;
    private String chatId;

    @Inject
    public VelocityMain(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
        loadConfig(); // Загружаем конфиг
        this.telegramBot = new TelegramBot(botToken, botUsername, webhookUrl);
        startWebhookServer(); // Запускаем Webhook
        logger.info("BWTelegramNotify for Velocity has been enabled!");
    }

    // Обработчик входа игрока
    @Subscribe
    public void onPlayerJoin(LoginEvent event) {
        String message = "Player " + event.getPlayer().getUsername() + " has joined the server.";
        logger.info(message);
        telegramBot.sendMessage(chatId, message);
    }

    // Обработчик выхода игрока
    @Subscribe
    public void onPlayerLeave(DisconnectEvent event) {
        String message = "Player " + event.getPlayer().getUsername() + " has left the server.";
        logger.info(message);
        telegramBot.sendMessage(chatId, message);
    }

    // Обработчик смены сервера игроком
    @Subscribe
    public void onServerSwitch(ServerConnectedEvent event) {
        String message = "Player " + event.getPlayer().getUsername() + " switched servers.";
        logger.info(message);
        telegramBot.sendMessage(chatId, message);
    }

    // Чтение конфигурации
    public void loadConfig() {
        Path path = Paths.get("plugins/BWTelegramNotify/config.json");

        try {
            if (Files.exists(path)) {
                ObjectMapper objectMapper = new ObjectMapper();
                Config config = objectMapper.readValue(path.toFile(), Config.class);
                this.botToken = config.getBotToken();
                this.botUsername = config.getBotUsername();
                this.webhookUrl = config.getWebhookUrl();
                this.chatId = config.getChatId();
                logger.info("Loaded bot config successfully.");
            } else {
                logger.warn("Configuration file not found: " + path);
            }
        } catch (IOException e) {
            logger.error("Error loading configuration", e);
        }
    }

    // Запуск Webhook-сервера
    private void startWebhookServer() {
        int port = 8080;
        try {
            webhookServer = new WebhookServer(port, telegramBot);
            webhookServer.start();
            logger.info("Webhook server started on port " + port);
        } catch (IOException e) {
            logger.error("Failed to start Webhook server", e);
        }
    }

    // Класс для парсинга конфигурации
    public static class Config {
        private String botToken;
        private String botUsername;
        private String webhookUrl;
        private String chatId;

        public String getBotToken() {
            return botToken;
        }

        public String getBotUsername() {
            return botUsername;
        }

        public String getWebhookUrl() {
            return webhookUrl;
        }

        public String getChatId() {
            return chatId;
        }
    }
}