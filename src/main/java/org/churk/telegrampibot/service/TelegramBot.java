package org.churk.telegrampibot.service;

import lombok.SneakyThrows;
import org.churk.telegrampibot.config.BotConfig;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;

import java.util.List;
import java.util.Optional;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private final MessageService messageService;

    public TelegramBot(BotConfig botConfig, MessageService messageService) {
        this.botConfig = botConfig;
        this.messageService = messageService;

        List<BotCommand> botCommandList = List.of(
                new BotCommand("/pidoreg", "Register yourself as a pidor"),
                new BotCommand("/pidor", "Get today's pidor"),
                new BotCommand("/pidorstats", "Get stats (use /pidorstats [year] for specific year)"),
                new BotCommand("/pidorall", "Get all-time stats"),
                new BotCommand("/pidorme", "Get personal stats")
        );
        registerBotCommands(botCommandList);
    }

    @SneakyThrows
    private void registerBotCommands(List<BotCommand> botCommandList) {
        this.execute(new SetMyCommands(botCommandList, new BotCommandScopeDefault(), null));
    }

    @Override
    public String getBotUsername() {
        return botConfig.getUsername();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Optional<SendMessage> sendMessage = messageService.processMessage(update);
            if (sendMessage.isPresent()) {
                execute(sendMessage.get());
            }
        }
    }

    @SneakyThrows
    @Scheduled(cron = "0 0 11 * * ?") // 11 am
    public void sendScheduledMessage() {
        Optional<SendMessage> sendMessage = messageService.processScheduledMessage();
        if (sendMessage.isPresent()) {
            execute(sendMessage.get());
        }
    }

    @SneakyThrows
    @Scheduled(cron = "0 0 0 * * ?") // midnight
    public void resetWinner() {
        messageService.resetWinner();
    }
}
