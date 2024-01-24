package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.decorator.StatsListDecorator;
import org.churk.telegrambot.model.Stat;
import org.churk.telegrambot.service.DailyMessageService;
import org.churk.telegrambot.service.StatsService;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@AllArgsConstructor
public class StatsAllHandler implements CommandHandler {
    private final MessageBuilderFactory messageBuilderFactory;
    private final DailyMessageService dailyMessageService;
    private final StatsService statsService;
    @Override
    public List<Validable> handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        List<Stat> stats = statsService.getAllStatsByChatId(chatId);

        String statsTable = dailyMessageService.getKeyNameSentence("stats_table");
        String header = dailyMessageService.getKeyNameSentence("stats_all_header");
        String footer = dailyMessageService.getKeyNameSentence("stats_footer").formatted(stats.size());
        String text = new StatsListDecorator(stats).getFormattedStats(statsTable, header, footer);

        return List.of(messageBuilderFactory
                .createTextMessageBuilder(update.getMessage().getChatId())
                .withText(text)
                .build());
    }

    @Override
    public List<Validable> handleByChatId(Long chatId) {
        return List.of();
    }
}
