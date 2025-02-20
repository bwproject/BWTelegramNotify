package me.projectbw.BWTelegramNotify;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TelegramBot {
    private final String botToken;
    private final List<String> chatIds;
    private final Logger logger = LoggerFactory.getLogger(TelegramBot.class);

    private String botName = "Unknown Bot";
    private String botUsername = "unknown";
    private String serverStatus = "⏳ Обновление данных...";

    public TelegramBot(String botToken, List<String> chatIds) {
        this.botToken = botToken;
        this.chatIds = chatIds;

        fetchBotInfo();
        scheduleServerStatusUpdates();
    }

    public void sendMessage(String message) {
        for (String chatId : chatIds) {
            sendRequest("https://api.telegram.org/bot" + botToken + "/sendMessage?chat_id=" + chatId + "&text=" + message);
        }
    }

    public void checkForCommands() {
        try {
            String response = sendRequest("https://api.telegram.org/bot" + botToken + "/getUpdates");
            JSONObject jsonResponse = new JSONObject(response);

            jsonResponse.getJSONArray("result").forEach(item -> {
                JSONObject update = (JSONObject) item;
                if (update.has("message")) {
                    JSONObject message = update.getJSONObject("message");
                    String text = message.getString("text");
                    String chatId = message.getJSONObject("chat").getString("id");

                    if (text.equals("/bwstatusserver")) {
                        sendMessage("📊 Статус серверов:\n" + serverStatus);
                    }
                }
            });

        } catch (Exception e) {
            logger.error("Ошибка при обработке команд: " + e.getMessage());
        }
    }

    private void fetchBotInfo() {
        try {
            String response = sendRequest("https://api.telegram.org/bot" + botToken + "/getMe");
            JSONObject jsonResponse = new JSONObject(response);
            JSONObject result = jsonResponse.getJSONObject("result");

            botName = result.getString("first_name");
            botUsername = result.getString("username");

        } catch (Exception e) {
            logger.error("Ошибка получения информации о боте: " + e.getMessage());
        }
    }

    private void scheduleServerStatusUpdates() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            serverStatus = "✅ Paper-сервер: " + (PaperMain.isRunning() ? "Работает" : "Отключен") +
                    "\n✅ Velocity-сервер: " + (VelocityMain.isRunning() ? "Работает" : "Отключен");
        }, 0, 1, TimeUnit.MINUTES);
    }

    private String sendRequest(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = reader.readLine();
            reader.close();
            return response;

        } catch (Exception e) {
            logger.error("Ошибка при отправке запроса: " + e.getMessage());
            return "";
        }
    }

    public String getBotName() {
        return botName;
    }

    public String getBotUsername() {
        return botUsername;
    }
}