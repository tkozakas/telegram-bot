package org.churk.telegrampibot.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrampibot.config.BotConfig;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    private static final boolean ENABLED = true;
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
                new BotCommand("/pidorme", "Get personal stats"),
                new BotCommand("/fact", "Random fact of the day"),
                new BotCommand("/sticker", "Random sticker from a churka")
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
            List<String> commands = messageService.processMessage(update);

            if (!ENABLED) {
                return;
            }
            executeMessage(messageService.handleDailyMessage(commands));
            executeSticker(messageService.handleSticker(update));
            executeSticker(messageService.handleSticker(update));
        }
    }

    private void executeSticker(Optional<SendSticker> sendSticker) {
        if (sendSticker.isPresent()) {
            try {
                execute(sendSticker.get());
            } catch (TelegramApiException e) {
                log.error("Error while sending sticker", e);
            }
        }
    }

    private void executeMessage(List<Optional<SendMessage>> sendMessages) {
        sendMessages.forEach(sendMessage -> sendMessage.ifPresent(message -> {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error("Error while sending message", e);
            }
        }));
    }

    @SneakyThrows
    @Scheduled(cron = "0 0 11 * * ?") // 11 am
    public void sendScheduledMessage() {
        executeMessage(messageService.processDailyWinnerMessage());
    }

    @SneakyThrows
    @Scheduled(cron = "0 0 0 * * ?") // midnight
    public void resetWinner() {
        messageService.resetWinner();
    }
}
