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
    private final TelegramBot telegramBot;
    private String botToken;
    private String chatId;

    @Inject
    public VelocityMain(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;
        this.telegramBot = new TelegramBot();
        loadConfig(); // Загружаем конфигурацию при старте плагина
        logger.info("BWTelegramNotify for Velocity has been enabled!");
    }

    // Обработчик для события входа игрока
    @Subscribe
    public void onPlayerJoin(LoginEvent event) {
        String message = "Player " + event.getPlayer().getUsername() + " has joined the server.";
        logger.info(message);
        telegramBot.sendMessage(message);
    }

    // Обработчик для события выхода игрока
    @Subscribe
    public void onPlayerLeave(DisconnectEvent event) {
        String message = "Player " + event.getPlayer().getUsername() + " has left the server.";
        logger.info(message);
        telegramBot.sendMessage(message);
    }

    // Обработчик для события смены сервера игроком
    @Subscribe
    public void onServerSwitch(ServerConnectedEvent event) {
        String message = "Player " + event.getPlayer().getUsername() + " switched servers.";
        logger.info(message);
        telegramBot.sendMessage(message);
    }

    // Чтение конфигурации для бота (например, получение пути для конфигурации)
    public void loadConfig() {
        Path path = Paths.get("plugins/BWTelegramNotify/config.json"); // Пример пути к конфигу
        logger.info("Config path: " + path);

        // Обрабатываем конфигурацию (парсинг JSON)
        try {
            if (Files.exists(path)) {
                // Используем Jackson для парсинга JSON
                ObjectMapper objectMapper = new ObjectMapper();
                Config config = objectMapper.readValue(path.toFile(), Config.class);  // Чтение конфигурации в объект
                this.botToken = config.getBotToken();
                this.chatId = config.getChatId();
                logger.info("Bot Token: " + botToken);
                logger.info("Chat ID: " + chatId);
            } else {
                logger.warn("Configuration file not found: " + path);
            }
        } catch (IOException e) {
            logger.error("Error loading configuration", e);
        }
    }

    // Класс для представления конфигурации
    public static class Config {
        private String botToken;
        private String chatId;

        public String getBotToken() {
            return botToken;
        }

        public void setBotToken(String botToken) {
            this.botToken = botToken;
        }

        public String getChatId() {
            return chatId;
        }

        public void setChatId(String chatId) {
            this.chatId = chatId;
        }
    }
}