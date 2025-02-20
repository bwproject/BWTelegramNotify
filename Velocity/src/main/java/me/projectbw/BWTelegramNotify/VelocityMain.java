package me.projectbw.BWTelegramNotify;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Plugin(id = "bwtelegramnotify", name = "BWTelegramNotify", version = "1.1")
public class VelocityMain {
    private final ProxyServer server;
    private final Logger logger;
    private final TelegramBot telegramBot;
    
    private static final String BORDER = "\u001B[36m==============================\u001B[0m";
    private static final String MESSAGE = "\u001B[32m=== Плагин BWTelegramNotify активен ===\u001B[0m";

    @Inject
    public VelocityMain(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;

        // Создаём конфиг и папку, если их нет
        File configFile = new File("plugins/BWTelegramNotify/config.properties");
        if (!configFile.exists()) {
            createDefaultConfig(configFile);
        }

        // Загружаем конфиг
        Properties config = loadConfig(configFile);

        String botToken = config.getProperty("bot.token", "default_token");

        // Загружаем список чатов
        List<String> chatIds = Arrays.stream(config.getProperty("bot.chat_ids", "").split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        this.telegramBot = new TelegramBot(botToken, chatIds);

        // Цветной лог в консоль
        logger.info(BORDER);
        logger.info(MESSAGE);
        logger.info(BORDER);
    }

    @Subscribe
    public void onPlayerJoin(LoginEvent event) {
        String message = "Игрок " + event.getPlayer().getUsername() + " зашел на сервер.";
        logger.info("\u001B[33m" + message + "\u001B[0m");
        telegramBot.sendMessage(message);
    }

    @Subscribe
    public void onPlayerQuit(DisconnectEvent event) {
        String message = "Игрок " + event.getPlayer().getUsername() + " вышел с сервера.";
        logger.info("\u001B[31m" + message + "\u001B[0m");
        telegramBot.sendMessage(message);
    }

    @Subscribe
    public void onServerSwitch(ServerConnectedEvent event) {
        String message = "Игрок " + event.getPlayer().getUsername() + " сменил сервер на " + event.getServer().getServerInfo().getName();
        logger.info("\u001B[34m" + message + "\u001B[0m");
        telegramBot.sendMessage(message);
    }

    private void createDefaultConfig(File configFile) {
        try {
            Files.createDirectories(Path.of("plugins/BWTelegramNotify"));
            String defaultConfig = "bot.token=YOUR_BOT_TOKEN\nbot.chat_ids=123456789,-987654321\n";
            Files.writeString(configFile.toPath(), defaultConfig, StandardOpenOption.CREATE);
            logger.info("\u001B[32mКонфигурация создана: plugins/BWTelegramNotify/config.properties\u001B[0m");
        } catch (IOException e) {
            logger.error("\u001B[31mОшибка при создании конфигурации: " + e.getMessage() + "\u001B[0m");
        }
    }

    private Properties loadConfig(File configFile) {
        Properties properties = new Properties();
        try {
            properties.load(Files.newBufferedReader(configFile.toPath()));
            logger.info("\u001B[32mКонфигурация загружена успешно!\u001B[0m");
        } catch (IOException e) {
            logger.error("\u001B[31mОшибка при загрузке конфигурации: " + e.getMessage() + "\u001B[0m");
        }
        return properties;
    }
}