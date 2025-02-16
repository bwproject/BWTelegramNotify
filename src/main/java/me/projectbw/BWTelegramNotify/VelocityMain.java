package me.projectbw.BWTelegramNotify;

import com.velocitypowered.api.event.Listener;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.connection.PreLogoutEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

@Plugin(id = "bwtelegramnotify", name = "BWTelegramNotify", version = "1.0-SNAPSHOT")
public class VelocityMain {

    private static final Logger logger = LoggerFactory.getLogger(VelocityMain.class);
    private TelegramBot telegramBot;
    private List<String> chatIds;
    private File configFile;
    private Yaml yaml;

    @Inject
    public VelocityMain() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        yaml = new Yaml(options);
    }

    @Subscribe
    public void onPostLogin(PostLoginEvent event) {
        Player player = event.getPlayer();
        String message = String.format("Игрок %s вошел в сервер!", player.getUsername());
        telegramBot.sendMessages(chatIds, message);

        logger.info("Игрок {} подключился. Отправлено сообщение в Telegram.", player.getUsername());
    }

    @Subscribe
    public void onPreLogout(PreLogoutEvent event) {
        Player player = event.getPlayer();
        String message = String.format("Игрок %s покинул сервер.", player.getUsername());
        telegramBot.sendMessages(chatIds, message);

        logger.info("Игрок {} покинул сервер. Отправлено сообщение в Telegram.", player.getUsername());
    }

    @Subscribe
    public void onServerSwitch(ServerSwitchEvent event) {
        Player player = event.getPlayer();
        Server newServer = event.getServer();
        String message = String.format("Игрок %s сменил сервер на %s.", player.getUsername(), newServer.getServerInfo().getName());
        telegramBot.sendMessages(chatIds, message);

        logger.info("Игрок {} сменил сервер. Отправлено сообщение в Telegram.", player.getUsername());
    }
}
