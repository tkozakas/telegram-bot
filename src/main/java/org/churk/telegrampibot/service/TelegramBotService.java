package org.churk.telegrampibot.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrampibot.config.BotProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

import static java.lang.Thread.sleep;

@Slf4j
@Component
public class TelegramBotService extends TelegramLongPollingBot {
    private static final boolean ENABLED = true;
    private final BotProperties botConfig;
    private final MessageService messageService;

    public TelegramBotService(BotProperties botConfig, MessageService messageService) {
        this.botConfig = botConfig;
        this.messageService = messageService;

        List<BotCommand> botCommandList = List.of(
                new BotCommand("/pidorreg", "Register yourself as a " + botConfig.getWinnerName()),
                new BotCommand("/pidor", "Get today's " + botConfig.getWinnerName()),
                new BotCommand("/pidorstats", "Get stats (use /pidorstats [year] for specific year)"),
                new BotCommand("/pidorall", "Get all-time stats"),
                new BotCommand("/pidorme", "Get personal stats"),
                new BotCommand("/fact", "Random fact of the day"),
                new BotCommand("/sticker", "Random sticker from a churka"),
                new BotCommand("/meme", "Random meme (use /meme [year] for specific subreddit)")
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
            String messageText = update.getMessage().getText();
            String firstName = update.getMessage().getFrom().getFirstName();
            log.info("{}: {}", firstName, messageText);

            if (!ENABLED) {
                return;
            }
            executeMessages(messageService.handleCommand(update));
        }
    }

    private void executeMessages(List<Validable> sendMessages) throws InterruptedException {
        for (Validable sendMessage : sendMessages) {
            try {
                if (sendMessage == null) {
                    continue;
                }
                if (sendMessage instanceof SendMessage sendmessage) {
                    execute(sendmessage);
                } else if (sendMessage instanceof SendSticker sendsticker) {
                    execute(sendsticker);
                } else if (sendMessage instanceof SendPhoto sendphoto) {
                    execute(sendphoto);
                }
                sleep(1000);
            } catch (TelegramApiException e) {
                log.error("Error while sending message", e);
            }
        }
    }

    @Scheduled(cron = "${meme.schedule}") // 11 am
    public void sendScheduledMessage() throws InterruptedException {
        executeMessages(messageService.processDailyWinnerMessage());
    }

    @Scheduled(cron = "${meme.schedule}") // hourly memes
    public void sendScheduledSticker() throws InterruptedException {
        executeMessages(messageService.processScheduledRandomMeme());
    }

    @Scheduled(cron = "${bot.reset-schedule}")  // midnight
    public void resetWinner() {
        messageService.resetWinner();
    }
}
