package me.projectbw.BWTelegramNotify;

import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;

import javax.inject.Inject;
import java.nio.file.Path;

@Plugin(id = "bwtelegramnotify", name = "BWTelegramNotify", version = "1.0")
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private TelegramBot bot;

    @Inject
    public Main(@DataDirectory Path dataDirectory) {
        try {
            String botToken = "YOUR_BOT_TOKEN";
            String botUsername = "YOUR_BOT_USERNAME";
            String webhookUrl = "https://your-public-domain.com/webhook"; // Заменить на свой домен или ngrok

            bot = new TelegramBot(botToken, botUsername, webhookUrl);
            
            // Запуск локального веб-сервера для приёма вебхуков
            startWebhookServer();

            // Автоматическая регистрация вебхука
            bot.setWebhook();
            logger.info("Webhook successfully registered.");
        } catch (Exception e) {
            logger.error("Failed to start webhook server", e);
        }
    }

    private void startWebhookServer() {
        int port = 8080;
        Spark.port(port);
        Spark.post("/webhook", (req, res) -> {
            bot.onWebhookUpdateReceived(req.body());
            res.status(200);
            return "OK";
        });
        logger.info("Webhook server started on port " + port);
    }
}