package org.churk.telegrampibot.service;

import lombok.SneakyThrows;
import org.churk.telegrampibot.config.BotConfig;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private final MessageService messageService;

    public TelegramBot(BotConfig botConfig, MessageService messageService) {
        this.botConfig = botConfig;
        this.messageService = messageService;
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
    @Scheduled(cron = "0 0 12 * * ?")
    public void sendScheduledMessage() {
        Optional<SendMessage> sendMessage = messageService.processScheduledMessage();
        if (sendMessage.isPresent()) {
            execute(sendMessage.get());
        }
    }
}
