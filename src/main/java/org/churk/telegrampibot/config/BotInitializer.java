package org.churk.telegrampibot.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrampibot.service.TelegramBotService;
import org.churk.telegrampibot.utility.DailyMessageLoader;
import org.churk.telegrampibot.utility.FactLoader;
import org.churk.telegrampibot.utility.StickerLoader;
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
    private final TelegramBotService telegramBotService;
    private final StickerLoader stickerLoader;
    private final DailyMessageLoader dailyMessageLoader;
    private final FactLoader factLoader;

    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        stickerLoader.loadStickers();
        dailyMessageLoader.loadMessages();
        factLoader.loadFacts();
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(telegramBotService);
        } catch (TelegramApiException e) {
            log.error("Error while initializing the bot", e);
        }
    }
}
