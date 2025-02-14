package me.projectbw;

import com.velocitypowered.api.event.EventHandler;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.connection.PreLogoutEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Plugin(id = "bwtelegramnotify", name = "BWTelegramNotify", version = "1.0-SNAPSHOT", description = "Notify server status and players")
public class BWTelegramNotifyVelocity implements SimpleCommand {

    private static final Logger logger = LoggerFactory.getLogger(BWTelegramNotifyVelocity.class);
    private final TelegramSender telegramSender;

    public BWTelegramNotifyVelocity() {
        this.telegramSender = new TelegramSender(
            "your_bot_token_here",
            "your_chat_id_here"
        );
    }

    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        Player player = event.getPlayer();
        telegramSender.sendMessage("Игрок " + player.getUsername() + " присоединился к серверу");
    }

    @EventHandler
    public void onPlayerQuit(PreLogoutEvent event) {
        Player player = event.getPlayer();
        telegramSender.sendMessage("Игрок " + player.getUsername() + " покинул сервер");
    }

    @Override
    public void execute(Invocation invocation) {
        String status = telegramSender.checkBotStatus();
        invocation.source().sendMessage(Component.text(status));
    }
}