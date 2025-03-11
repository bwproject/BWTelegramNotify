package me.projectbw.BWTelegramNotify;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import java.nio.charset.StandardCharsets;

@Plugin(
    id = "bwtelegramnotify",
    name = "BWTelegramNotify",
    version = "1.1.0",
    description = "Плагин для уведомлений в Telegram",
    authors = {"The_Mr_Mes109"}
)
public class VelocityListener {

    private final ProxyServer server;
    private final VelocityMain plugin;
    private static final ChannelIdentifier CHANNEL = MinecraftChannelIdentifier.from("bwtelegram:notify");

    public VelocityListener(ProxyServer server, VelocityMain plugin) {
        this.server = server;
        this.plugin = plugin;
    }

    private String applyTemplate(String template, String playerName, String previousServer, String newServer) {
        return template
                .replace("%player%", playerName)
                .replace("%previous_server%", previousServer)
                .replace("%new_server%", newServer);
    }

    @Subscribe
    public void onPlayerLogin(LoginEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getUsername();
        plugin.getLogger().info("Игрок зашел: " + playerName);
        
        // Получаем шаблон из конфигурации
        String messageTemplate = plugin.getConfig().getString("messages.player_logged_in", "✅ **Игрок зашел**: %player%");
        
        // Применяем шаблон и отправляем сообщение в Telegram
        String message = applyTemplate(messageTemplate, playerName, null, null);
        plugin.getTelegramBot().sendMessage(message);
    }

    @Subscribe
    public void onPlayerDisconnect(DisconnectEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getUsername();
        plugin.getLogger().info("Игрок вышел: " + playerName);
        
        // Получаем шаблон из конфигурации
        String messageTemplate = plugin.getConfig().getString("messages.player_logged_out", "❌ **Игрок вышел**: %player%");
        
        // Применяем шаблон и отправляем сообщение в Telegram
        String message = applyTemplate(messageTemplate, playerName, null, null);
        plugin.getTelegramBot().sendMessage(message);
    }

    @Subscribe
    public void onPlayerSwitchServer(ServerConnectedEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getUsername();
        String previousServer = event.getPreviousServer().map(s -> s.getServerInfo().getName()).orElse("неизвестен");
        String newServer = event.getServer().getServerInfo().getName();

        plugin.getLogger().info("Игрок " + playerName + " сменил сервер: " + previousServer + " -> " + newServer);
        
        // Получаем шаблон из конфигурации
        String messageTemplate = plugin.getConfig().getString("messages.player_switched_server", "🔄 **Игрок сменил сервер**: %player%\n➡ **%previous_server%** → **%new_server%**");
        
        // Применяем шаблон и отправляем сообщение в Telegram
        String message = applyTemplate(messageTemplate, playerName, previousServer, newServer);
        plugin.getTelegramBot().sendMessage(message);
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        plugin.getLogger().info("BWTelegramNotify: Остановка плагина...");
        
        // Получаем шаблон из конфигурации
        String messageTemplate = plugin.getConfig().getString("messages.server_stopped", "🔴 **Прокси-сервер выключен!**");
        
        // Применяем шаблон и отправляем сообщение в Telegram
        String message = applyTemplate(messageTemplate, null, null, null);
        plugin.getTelegramBot().sendMessage(message);

        plugin.getLogger().info("BWTelegramNotify успешно отключен.");
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        plugin.getLogger().info("BWTelegramNotify: Инициализация плагина...");
        
        // Получаем шаблон из конфигурации
        String messageTemplate = plugin.getConfig().getString("messages.server_started", "🔵 **Прокси-сервер запущен!**");
        
        // Применяем шаблон и отправляем сообщение в Telegram
        String message = applyTemplate(messageTemplate, null, null, null);
        plugin.getTelegramBot().sendMessage(message);
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        // Проверяем, что сообщение пришло по нужному каналу
        if (!event.getIdentifier().equals(CHANNEL)) return;

        String message = new String(event.getData(), StandardCharsets.UTF_8);
        plugin.getLogger().info("📩 Получено сообщение от Paper: " + message);

        // Получаем шаблон из конфигурации
        String messageTemplate = plugin.getConfig().getString("messages.from_paper", "📩 Получено сообщение от Paper: %message%");

        // Применяем шаблон и отправляем сообщение в Telegram
        String formattedMessage = applyTemplate(messageTemplate, message, null, null);
        plugin.getTelegramBot().sendMessage(formattedMessage);
    }
}
