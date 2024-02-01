package org.churk.telegrambot.handler.stats;

import lombok.AllArgsConstructor;
import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.decorator.StatsListDecorator;
import org.churk.telegrambot.handler.CommandHandler;
import org.churk.telegrambot.handler.HandlerContext;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.model.Stat;
import org.churk.telegrambot.service.DailyMessageService;
import org.churk.telegrambot.service.StatsService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.time.LocalDateTime;
import java.util.List;

@Component
@AllArgsConstructor
public class StatsYearHandler implements CommandHandler {
    private final MessageBuilderFactory messageBuilderFactory;
    private final DailyMessageService dailyMessageService;
    private final StatsService statsService;

    @Override
    public List<Validable> handle(HandlerContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();
        List<String> args = context.getArgs();

        int year;
        if (!args.isEmpty()) {
            try {
                year = Integer.parseInt(args.getFirst());
            } catch (NumberFormatException e) {
                return getErrorMessage(chatId, messageId,
                        "Please provide a valid year (/stats <year>)");
            }
        } else {
            year = LocalDateTime.now().getYear();
        }

        List<Stat> stats = statsService.getAllStatsByChatIdAndYear(chatId, year);
        String statsTable = dailyMessageService.getKeyNameSentence("stats_table");
        String header = dailyMessageService.getKeyNameSentence("stats_year_header").formatted(year);
        String footer = dailyMessageService.getKeyNameSentence("stats_footer").formatted(stats.size());
        String text = new StatsListDecorator(stats).getFormattedStats(statsTable, header, footer, 10);

        return List.of(messageBuilderFactory
                .createTextMessageBuilder(chatId)
                .withText(text)
                .build());
    }

    private List<Validable> getErrorMessage(Long chatId, Integer messageId, String text) {
        return List.of(messageBuilderFactory
                .createTextMessageBuilder(chatId)
                .withReplyToMessageId(messageId)
                .withText(text)
                .build());
    }

    @Override
    public Command getSupportedCommand() {
        return Command.STATS;
    }
}
