package org.churk.telegrambot.handler.stats;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.decorator.StatsListDecorator;
import org.churk.telegrambot.handler.Handler;
import org.churk.telegrambot.handler.HandlerContext;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.model.Stat;
import org.churk.telegrambot.service.DailyMessageService;
import org.churk.telegrambot.service.StatsService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StatsAllHandler extends Handler {
    private final DailyMessageService dailyMessageService;
    private final StatsService statsService;

    @Override
    public List<Validable> handle(HandlerContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        List<Stat> stats = statsService.getAllStatsByChatId(chatId);

        String statsTable = dailyMessageService.getKeyNameSentence("stats_table");
        String header = dailyMessageService.getKeyNameSentence("stats_all_header");
        String footer = dailyMessageService.getKeyNameSentence("stats_footer").formatted(stats.size());
        String text = new StatsListDecorator(stats).getFormattedStats(statsTable, header, footer, 10);

        return getMessage(chatId, text);
    }

    @Override
    public Command getSupportedCommand() {
        return Command.STATS_ALL;
    }
}
