package org.churk.telegrambot.stats;

import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.builder.StatsListDecorator;
import org.churk.telegrambot.config.BotProperties;
import org.churk.telegrambot.handler.Handler;
import org.churk.telegrambot.handler.Command;
import org.churk.telegrambot.message.DailyMessageService;
import org.churk.telegrambot.utility.HandlerContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;

@Component
public class StatsAllHandler extends Handler {
    private final StatsService statsService;

    public StatsAllHandler(BotProperties botProperties, DailyMessageService dailyMessageService, MessageBuilderFactory messageBuilderFactory, StatsService statsService) {
        super(botProperties, dailyMessageService, messageBuilderFactory);
        this.statsService = statsService;
    }

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
