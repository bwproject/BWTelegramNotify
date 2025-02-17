package me.projectbw.BWTelegramNotify;

import fi.iki.elonen.NanoHTTPD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

public class WebhookServer extends NanoHTTPD {
    private static final Logger logger = LoggerFactory.getLogger(WebhookServer.class);
    private final TelegramBot bot;

    public WebhookServer(int port, TelegramBot bot) throws IOException {
        super(port);
        this.bot = bot;
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        logger.info("Webhook server started on port " + port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        if (!"POST".equalsIgnoreCase(session.getMethod().name())) {
            return newFixedLengthResponse(Response.Status.METHOD_NOT_ALLOWED, "text/plain", "Only POST requests allowed");
        }

        try {
            // Читаем JSON из запроса
            Map<String, String> body = session.getParms();
            String json = body.get("postData");

            if (json == null) {
                return newFixedLengthResponse(Response.Status.BAD_REQUEST, "text/plain", "Invalid request");
            }

            // Парсим JSON в объект Update
            ObjectMapper objectMapper = new ObjectMapper();
            Update update = objectMapper.readValue(json, Update.class);

            // Передаём обновление боту
            bot.onUpdateReceived(update);

            return newFixedLengthResponse(Response.Status.OK, "application/json", "{\"status\": \"ok\"}");
        } catch (Exception e) {
            logger.error("Error processing update: ", e);
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", "Error processing update");
        }
    }
}
