package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.decorator.StatsListDecorator;
import org.churk.telegrambot.model.bot.Command;
import org.churk.telegrambot.model.bot.Stat;
import org.churk.telegrambot.service.DailyMessageService;
import org.churk.telegrambot.service.StatsService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;

@Component
@AllArgsConstructor
public class StatsAllHandler implements CommandHandler {
    private final MessageBuilderFactory messageBuilderFactory;
    private final DailyMessageService dailyMessageService;
    private final StatsService statsService;

    @Override
    public List<Validable> handle(HandlerContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        List<Stat> stats = statsService.getAllStatsByChatId(chatId);

        String statsTable = dailyMessageService.getKeyNameSentence("stats_table");
        String header = dailyMessageService.getKeyNameSentence("stats_all_header");
        String footer = dailyMessageService.getKeyNameSentence("stats_footer").formatted(stats.size());
        String text = new StatsListDecorator(stats).getFormattedStats(statsTable, header, footer);

        return List.of(messageBuilderFactory
                .createTextMessageBuilder(chatId)
                .withText(text)
                .build());
    }

    @Override
    public Command getSupportedCommand() {
        return Command.STATS_ALL;
    }
}
