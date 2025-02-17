package me.projectbw.BWTelegramNotify;

import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;

@Plugin(id = "bwtelegramnotify", name = "BWTelegramNotify", version = "1.0")
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private TelegramBot bot;
    private WebhookServer webhookServer;

    @Inject
    public Main(@DataDirectory Path dataDirectory) {
        try {
            String botToken = "YOUR_BOT_TOKEN";
            String botUsername = "YOUR_BOT_USERNAME";
            String webhookUrl = "https://your-public-domain.com/webhook"; // Можно заменить на ngrok

            bot = new TelegramBot(botToken, botUsername, webhookUrl);

            int port = 8080;
            webhookServer = new