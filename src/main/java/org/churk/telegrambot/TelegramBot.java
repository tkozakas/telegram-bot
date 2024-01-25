package org.churk.telegrambot;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.config.BotProperties;
import org.churk.telegrambot.handler.CommandProcessor;
import org.churk.telegrambot.model.Command;
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

import static java.lang.Thread.sleep;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private static final boolean ENABLED = true;
    private final BotProperties botProperties;
    private final CommandProcessor commandProcessor;

    @PostConstruct
    private void registerBotCommands() throws TelegramApiException {
        List<Command> commands = List.of(Command.values());
        List<BotCommand> botCommandList = commands.stream()
                .map(command -> {
                    String com = command.getPattern().formatted(botProperties.getWinnerName())
                            .replace(".*/", "/").replace("\\b.*", "");;
                    return new BotCommand(com, command.getDescription().formatted(botProperties.getWinnerName()));
                }).toList();
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
            if (!ENABLED) {
                return;
            }
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
