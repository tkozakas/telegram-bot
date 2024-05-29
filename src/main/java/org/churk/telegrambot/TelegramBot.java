package org.churk.telegrambot;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.config.BotProperties;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.utility.CommandProcessor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.stream.Stream;

import static java.lang.Thread.sleep;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private final BotProperties botProperties;
    private final CommandProcessor commandProcessor;

    @Override
    public String getBotUsername() {
        return botProperties.getUsername();
    }

    @Override
    public String getBotToken() {
        return botProperties.getToken();
    }


    @PostConstruct
    private void registerBotCommands() throws TelegramApiException {
        List<BotCommand> botCommandList = Stream.of(Command.values())
                .filter(command -> command != Command.NONE)
                .map(command -> new BotCommand(command.getPatterns().stream()
                        .findFirst()
                        .orElse("")
                        .formatted(botProperties.getWinnerName())
                        .replace(".*/", "/")
                        .replace("\\b.*", ""),
                        command.getDescription().replace("%s", botProperties.getWinnerName())))
                .toList();
        this.execute(new SetMyCommands(botCommandList, new BotCommandScopeDefault(), null));
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            executeMessages(commandProcessor.handleCommand(update));
        }
    }

    private void executeMessages(List<Validable> sendMessages) {
        for (Validable sendMessage : sendMessages) {
            try {
                switch (sendMessage) {
                    case SendMessage sendmessage -> execute(sendmessage);
                    case SendSticker sendsticker -> execute(sendsticker);
                    case SendPhoto sendphoto -> execute(sendphoto);
                    case SendAnimation sendanimation -> execute(sendanimation);
                    case SendVideo sendvideo -> execute(sendvideo);
                    case SendMediaGroup sendmediagroup -> execute(sendmediagroup);
                    default -> {
                    }
                }
                sleep(1000);
            } catch (TelegramApiException | InterruptedException e) {
                log.error("Error while sending message", e);
            }
        }
    }

    @Scheduled(cron = "${schedule.daily-message}") // 12 am
    public void sendScheduledMessage() {
        executeMessages(commandProcessor.handleScheduledCommand(Command.DAILY_MESSAGE));
    }

    @Scheduled(cron = "${schedule.meme-post}") // hourly reddit memes
    public void sendScheduledRedditMeme() {
        executeMessages(commandProcessor.handleScheduledCommand(Command.REDDIT));
    }

    @Scheduled(cron = "${schedule.winner-reset}")  // midnight
    public void resetWinner() {
        commandProcessor.handleReset();
    }
}
