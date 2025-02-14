package me.projectbw.BWTelegramNotify;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public class Config {

    private final JavaPlugin plugin;
    private FileConfiguration config;

    // Конструктор, который принимает плагин для получения конфигурации
    public Config(JavaPlugin plugin) {
        this.plugin = plugin;
        this.loadConfig();
    }

    // Загружаем конфигурационный файл или создаем новый, если его нет
    private void loadConfig() {
        plugin.saveDefaultConfig();  // Сохраняет config.yml в случае его отсутствия
        this.config = plugin.getConfig();  // Загружает конфигурацию
    }

    // Получаем токен для бота
    public String getBotToken() {
        return config.getString("telegram.token");
    }

    // Получаем список чатов для отправки уведомлений
    public List<String> getChatIds() {
        return config.getStringList("telegram.chats");
    }

    // Получаем параметры уведомлений для Paper
    public boolean isServerStatusEnabled() {
        return config.getBoolean("notifications.server_status", true);
    }

    public boolean isPlayerLoginEnabled() {
        return config.getBoolean("notifications.player_login", true);
    }

    public boolean isPlayerLogoutEnabled() {
        return config.getBoolean("notifications.player_logout", true);
    }

    public boolean isLowTpsEnabled() {
        return config.getBoolean("notifications.low_tps", true);
    }

    // Получаем параметры уведомлений для Velocity
    public boolean isServerChangeEnabled() {
        return config.getBoolean("notifications.server_change", true);
    }

    // Методы для изменения значений в конфигурации (при необходимости)
    public void setBotToken(String token) {
        config.set("telegram.token", token);
        plugin.saveConfig();
    }

    public void setChatIds(List<String> chatIds) {
        config.set("telegram.chats", chatIds);
        plugin.saveConfig();
    }

    // Выводим конфигурацию для отладки
    public void printConfig() {
        plugin.getLogger().info("Telegram Bot Token: " + getBotToken());
        plugin.getLogger().info("Telegram Chat IDs: " + getChatIds());
        plugin.getLogger().info("Server Status Notifications: " + isServerStatusEnabled());
        plugin.getLogger().info("Player Login Notifications: " + isPlayerLoginEnabled());
        plugin.getLogger().info("Player Logout Notifications: " + isPlayerLogoutEnabled());
        plugin.getLogger().info("Low TPS Notifications: " + isLowTpsEnabled());
        plugin.getLogger().info("Server Change Notifications (Velocity): " + isServerChangeEnabled());
    }
}
