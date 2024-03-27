package org.churk.telegrambot.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.TelegramBot;
import org.churk.telegrambot.utility.StickerLoader;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
@Slf4j
@RequiredArgsConstructor
public class BotInitializer {
    private final TelegramBot telegramBot;
    private final StickerLoader stickerLoader;

    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        stickerLoader.loadStickers();
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(telegramBot);
        } catch (TelegramApiException e) {
            log.error("Error while initializing the bot", e);
        }
    }
}
