package me.projectbw;

import com.velocitypowered.api.command.SimpleCommand;

public class StatusCommand implements SimpleCommand {
    private final TelegramSender telegramSender;

    public StatusCommand(TelegramSender telegramSender) {
        this.telegramSender = telegramSender;
    }

    @Override
    public void execute(Invocation invocation) {
        String status = telegramSender.checkBotStatus();
        invocation.source().sendMessage(status);
    }
}