package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.model.Stats;
import org.churk.telegrambot.service.DailyMessageService;
import org.churk.telegrambot.service.StatsService;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class DailyMessageHandler implements CommandHandler {
    private final MessageBuilderFactory messageBuilderFactory;
    private final DailyMessageService dailyMessageService;
    private final StatsService statsService;

    @Override
    public List<Validable> handle(Update update) {
        List<Validable> result = new ArrayList<>();
        Long chatId = update.getMessage().getChatId();
        int year = LocalDateTime.now().getYear();

        List<Stats> statsByChatIdAndYear = statsService.getStatsByChatIdAndYear(chatId, year);

        if (statsByChatIdAndYear.isEmpty()) {
            log.info("No stats available for chatId: {}", chatId);
            String messageText = dailyMessageService.getKeyNameSentence("no_stats_available");

            SendMessage message = messageBuilderFactory
                    .createTextMessageBuilder(chatId)
                    .withText(messageText)
                    .build();

            result.add(message);
        }

        return result;
    }
}
