package me.projectbw.BWTelegramNotify;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class PaperMain extends JavaPlugin implements Listener {
    private TelegramBot telegramBot;
    
    private static final String BORDER = "\u001B[36m==============================\u001B[0m";
    private static final String MESSAGE = "\u001B[32m=== Плагин BWTelegramNotify активен ===\u001B[0m";

    @Override
    public void onEnable() {
        // Создаём конфиг и папку, если их нет
        File configFile = new File(getDataFolder(), "config.properties");
        if (!configFile.exists()) {
            createDefaultConfig(configFile);
        }

        // Загружаем конфиг
        Properties config = loadConfig(configFile);

        String botToken = config.getProperty("bot.token", "default_token");
        List<String> chatIds = Arrays.stream(config.getProperty("bot.chat_ids", "").split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        this.telegramBot = new TelegramBot(botToken, chatIds);

        // Регистрируем события
        Bukkit.getPluginManager().registerEvents(this, this);

        // Цветной лог в консоль
        getLogger().info(BORDER);
        getLogger().info(MESSAGE);
        getLogger().info(BORDER);

        // Лог о запуске Telegram-бота
        String botId = botToken.split(":")[0]; // ID бота из токена
        getLogger().info("\u001B[32mTelegram-бот запущен! ID бота: " + botId + "\u001B[0m");
        getLogger().info("\u001B[36mОтправка уведомлений в чаты: " + chatIds + "\u001B[0m");
    }

    @Override
    public void onDisable() {
        getLogger().info("\u001B[31mПлагин BWTelegramNotify отключен\u001B[0m");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String message = "Игрок " + event.getPlayer().getName() + " зашел на сервер.";
        getLogger().info("\u001B[33m" + message + "\u001B[0m");
        telegramBot.sendMessage(message);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String message = "Игрок " + event.getPlayer().getName() + " вышел с сервера.";
        getLogger().info("\u001B[31m" + message + "\u001B[0m");
        telegramBot.sendMessage(message);
    }

    private void createDefaultConfig(File configFile) {
        try {
            Files.createDirectories(getDataFolder().toPath());
            String defaultConfig = "bot.token=YOUR_BOT_TOKEN\nbot.chat_ids=123456789,-987654321\n";
            Files.writeString(configFile.toPath(), defaultConfig, StandardOpenOption.CREATE);
            getLogger().info("\u001B[32mКонфигурация создана: " + configFile.getPath() + "\u001B[0m");
        } catch (IOException e) {
            getLogger().severe("\u001B[31mОшибка при создании конфигурации: " + e.getMessage() + "\u001B[0m");
        }
    }

    private Properties loadConfig(File configFile) {
        Properties properties = new Properties();
        try {
            properties.load(Files.newBufferedReader(configFile.toPath()));
            getLogger().info("\u001B[32mКонфигурация загружена успешно!\u001B[0m");
        } catch (IOException e) {
            getLogger().severe("\u001B[31mОшибка при загрузке конфигурации: " + e.getMessage() + "\u001B[0m");
        }
        return properties;
    }
}