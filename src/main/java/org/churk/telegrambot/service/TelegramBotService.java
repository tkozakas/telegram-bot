package org.churk.telegrambot.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.churk.telegrambot.config.BotProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

@Slf4j
@Component
public class TelegramBotService extends TelegramLongPollingBot {
    private static final boolean ENABLED = true;
    private final BotProperties botProperties;
    private final MessageService messageService;

    private final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());


    public TelegramBotService(BotProperties botProperties, MessageService messageService) throws TelegramApiException {
        this.botProperties = botProperties;
        this.messageService = messageService;

        List<Pair<String, String>> commands = List.of(
                Pair.of("/%sreg".formatted(botProperties.getWinnerName()), "Register yourself as a " + botProperties.getWinnerName()),
                Pair.of("/%s".formatted(botProperties.getWinnerName()), "Get today's " + botProperties.getWinnerName()),
                Pair.of("/%sstats".formatted(botProperties.getWinnerName()), "Get stats (use %sstats [year] for specific year)".formatted(botProperties.getWinnerName())),
                Pair.of("/%sall".formatted(botProperties.getWinnerName()), "Get all-time stats"),
                Pair.of("/%sme".formatted(botProperties.getWinnerName()), "Get personal stats"),
                Pair.of("/fact", "Random fact of the day"),
                Pair.of("/sticker", "Random sticker from a " + botProperties.getWinnerName() + " sticker set"),
                Pair.of("/meme", "Random meme (use /meme [year] for specific subreddit)")
        );
        List<BotCommand> botCommandList = commands.stream()
                .map(command -> new BotCommand(command.getLeft(), command.getRight()))
                .toList();
        registerBotCommands(botCommandList);
    }

    private void registerBotCommands(List<BotCommand> botCommandList) throws TelegramApiException {
        this.execute(new SetMyCommands(botCommandList, new BotCommandScopeDefault(), null));
    }

    @Override
    public String getBotUsername() {
        return botProperties.getUsername();
    }

    @Override
    public String getBotToken() {
        return botProperties.getToken();
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

    private void executeMessages(List<Validable> sendMessages) {
        for (Validable sendMessage : sendMessages) {
            executorService.submit(() -> {
                try {
                    if (sendMessage == null) {
                        return;
                    }
                    if (sendMessage instanceof SendMessage sendmessage) {
                        execute(sendmessage);
                    } else if (sendMessage instanceof SendSticker sendsticker) {
                        execute(sendsticker);
                    } else if (sendMessage instanceof SendPhoto sendphoto) {
                        execute(sendphoto);
                    } else if (sendMessage instanceof SendAnimation sendanimation) {
                        execute(sendanimation);
                    }
                    sleep(1000);
                } catch (TelegramApiException | InterruptedException e) {
                    log.error("Error while sending message", e);
                }
            });
        }
    }


    @Scheduled(cron = "${bot.schedule}") // 12 am
    public void sendScheduledMessage() {
        executeMessages(messageService.processDailyWinnerMessage());
    }

    @Scheduled(cron = "${meme.schedule}") // hourly memes
    public void sendScheduledRandomMeme() {
        executeMessages(messageService.processScheduledRandomMeme());
    }

    @Scheduled(cron = "${bot.reset-schedule}")  // midnight
    public void resetWinner() {
        messageService.resetWinner();
    }
}
