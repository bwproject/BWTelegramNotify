package me.projectbw;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class TelegramNotifyPaper extends JavaPlugin implements Listener {
    private static final Path CONFIG_PATH = Path.of("plugins/TelegramNotify/config.yml");
    private String telegramToken;
    private List<String> chatIds;
    private String startMessage;
    private String stopMessage;
    private String restartMessage;
    private String lowTpsMessage;
    private String playerJoinMessage;
    private String playerQuitMessage;
    private String playerChangeServerMessage;
    private double lowTpsThreshold;
    private int tpsCheckInterval;

    @Override
    public void onEnable() {
        loadConfig();
        Bukkit.getPluginManager().registerEvents(this, this);
        sendTelegramMessage(startMessage);
        startTpsMonitor();
    }

    @Override
    public void onDisable() {
        sendTelegramMessage(stopMessage);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String message = playerJoinMessage.replace("{player}", event.getPlayer().getName())
                                          .replace("{ip}", event.getPlayer().getAddress().getAddress().getHostAddress());
        sendTelegramMessage(message);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String message = playerQuitMessage.replace("{player}", event.getPlayer().getName())
                                         .replace("{ip}", event.getPlayer().getAddress().getAddress().getHostAddress());
        sendTelegramMessage(message);
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        String fromServer = event.getFrom().getName();
        String toServer = event.getPlayer().getWorld().getName();
        String ip = event.getPlayer().getAddress().getAddress().getHostAddress();
        String message = playerChangeServerMessage.replace("{player}", event.getPlayer().getName())
                                                  .replace("{from_server}", fromServer)
                                                  .replace("{to_server}", toServer)
                                                  .replace("{ip}", ip);
        sendTelegramMessage(message);
    }

    private void startTpsMonitor() {
        new BukkitRunnable() {
            @Override
            public void run() {
                double tps = Bukkit.getTPS()[0]; // Получаем TPS за последнюю минуту
                if (tps < lowTpsThreshold) {
                    sendTelegramMessage(lowTpsMessage.replace("{tps}", String.format("%.2f", tps)));
                }
            }
        }.runTaskTimerAsynchronously(this, 0, tpsCheckInterval * 20L); // Интервал в тиках
    }

    private void loadConfig() {
        if (!Files.exists(CONFIG_PATH)) {
            saveDefaultConfig();
        }
        try (InputStream input = Files.newInputStream(CONFIG_PATH)) {
            Yaml yaml = new Yaml();
            Map<String, Object> config = yaml.load(input);
            Map<String, Object> telegramConfig = (Map<String, Object>) config.get("telegram");

            this.telegramToken = (String) telegramConfig.get("token");
            this.chatIds = (List<String>) telegramConfig.get("chat_ids");

            Map<String, String> messages = (Map<String, String>) telegramConfig.get("messages");
            this.startMessage = messages.getOrDefault("start", "Сервер запущен!");
            this.stopMessage = messages.getOrDefault("stop", "Сервер выключен!");
            this.restartMessage = messages.getOrDefault("restart", "Сервер перезагружен!");
            this.lowTpsMessage = messages.getOrDefault("low_tps", "⚠ Внимание! TPS сервера опустился ниже {tps}!");
            this.playerJoinMessage = messages.getOrDefault("player_join", "Игрок {player} зашел с IP {ip}");
            this.playerQuitMessage = messages.getOrDefault("player_quit", "Игрок {player} покинул сервер с IP {ip}");
            this.playerChangeServerMessage = messages.getOrDefault("player_change_server", "Игрок {player} сменил сервер с {from_server} на {to_server} с IP {ip}");

            this.lowTpsThreshold = telegramConfig.containsKey("low_tps_threshold") ? 
                                   ((Number) telegramConfig.get("low_tps_threshold")).doubleValue() : 15.0;
            this.tpsCheckInterval = telegramConfig.containsKey("tps_check_interval") ? 
                                   ((Number) telegramConfig.get("tps_check_interval")).intValue() : 60;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveDefaultConfig() {
        String defaultConfig = """
                telegram:
                  token: "YOUR_BOT_TOKEN"
                  chat_ids:
                    - "123456789"
                    - "-1009876543210"
                    - "@your_channel"
                  messages:
                    start: "Сервер запущен!"
                    stop: "Сервер выключен!"
                    restart: "Сервер перезагружен!"
                    low_tps: "⚠ Внимание! TPS сервера опустился ниже {tps}!"
                    player_join: "Игрок {player} зашел с IP {ip}"
                    player_quit: "Игрок {player} покинул сервер с IP {ip}"
                    player_change_server: "Игрок {player} сменил сервер с {from_server} на {to_server} с IP {ip}"
                  low_tps_threshold: 15.0
                  tps_check_interval: 60
                """;
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, defaultConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendTelegramMessage(String message) {
        if (telegramToken == null || chatIds == null || message == null) return;

        for (String chatId : chatIds) {
            String url = "https://api.telegram.org/bot" + telegramToken + "/sendMessage";
            String jsonPayload = "{\"chat_id\":\"" + chatId + "\",\"text\":\"" + message + "\"}";

            // Здесь можно использовать HTTP-клиент для отправки запроса
        }
    }
}
