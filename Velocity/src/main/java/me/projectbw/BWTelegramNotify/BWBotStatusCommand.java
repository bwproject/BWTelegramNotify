package me.projectbw.BWTelegramNotify;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BWBotStatusCommand implements SimpleCommand {

    private final VelocityMain plugin;

    public BWBotStatusCommand(VelocityMain plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();

        String botName = plugin.getBotName();
        String chatId = plugin.getChatId();

        source.sendMessage(Component.text("§aПривязанный бот: §f" + botName));
        source.sendMessage(Component.text("§aПривязанный чат: §f" + chatId));
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        return CompletableFuture.completedFuture(List.of());
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("bwtelegramnotify.status");
    }
}
