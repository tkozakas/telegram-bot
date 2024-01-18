package org.churk.telegrampibot.config;

import lombok.extern.slf4j.Slf4j;
import org.churk.telegrampibot.service.DailyMessageService;
import org.churk.telegrampibot.service.FactService;
import org.churk.telegrampibot.service.StickerService;
import org.churk.telegrampibot.service.TelegramBotService;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
@Slf4j
public class BotInitializer {
    private final TelegramBotService telegramBotService;
    private final StickerService stickerPackService;
    private final DailyMessageService dailyMessageService;
    private final FactService factService;

    public BotInitializer(TelegramBotService telegramBotService, StickerService stickerPackService, DailyMessageService dailyMessageService, FactService factService) {
        this.telegramBotService = telegramBotService;
        this.stickerPackService = stickerPackService;
        this.dailyMessageService = dailyMessageService;
        this.factService = factService;
    }

    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        stickerPackService.loadStickers();
        dailyMessageService.loadMessages();
        factService.loadFacts();
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(telegramBotService);
        } catch (TelegramApiException e) {
            log.error("Error while initializing the bot", e);
        }
    }
}
