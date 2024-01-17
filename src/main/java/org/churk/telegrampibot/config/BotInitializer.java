package org.churk.telegrampibot.config;

import lombok.extern.slf4j.Slf4j;
import org.churk.telegrampibot.utility.StickerPackLoader;
import org.churk.telegrampibot.service.TelegramBot;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
@Slf4j
public class BotInitializer {
    private final TelegramBot telegramBot;
    private final StickerPackLoader stickerPackLoader;

    public BotInitializer(TelegramBot telegramBot, StickerPackLoader stickerPackLoader) {
        this.telegramBot = telegramBot;
        this.stickerPackLoader = stickerPackLoader;
    }

    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        stickerPackLoader.loadStickerPacks();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(telegramBot);
        } catch (TelegramApiException e) {
            log.error("Error while initializing bot", e);
        }
    }
}
