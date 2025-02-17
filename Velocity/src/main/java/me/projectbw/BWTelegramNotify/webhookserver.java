package me.projectbw.BWTelegramNotify;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import org.telegram.telegrambots.meta.api.objects.Update;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WebhookServer {
    private static final Logger logger = LoggerFactory.getLogger(WebhookServer.class);
    private final int port;
    private final TelegramBot bot;
    private HttpServer server;

    public WebhookServer(int port, TelegramBot bot) {
        this.port = port;
        this.bot = bot;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/webhook", new WebhookHandler(bot));
        server.setExecutor(null);
        server.start();
        logger.info("Webhook server is listening on port " + port);
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            logger.info("Webhook server stopped.");
        }
    }

    private static class WebhookHandler implements HttpHandler {
        private final TelegramBot bot;

        public WebhookHandler(TelegramBot bot) {
            this.bot = bot;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                ObjectMapper objectMapper = new ObjectMapper();
                Update update = objectMapper.readValue(exchange.getRequestBody(), Update.class);

                bot.onUpdateReceived(update);

                String response = "OK";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            }
        }
    }
}