package org.churk.telegrambot.stats;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.builder.StatsListDecorator;
import org.churk.telegrambot.handler.Command;
import org.churk.telegrambot.handler.Handler;
import org.churk.telegrambot.utility.HandlerContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StatsYearHandler extends Handler {
    private final StatsService statsService;

    @Override
    public List<Validable> handle(HandlerContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();
        List<String> args = context.getArgs();

        int year;
        try {
            year = determineYear(args);
        } catch (NumberFormatException e) {
            return getReplyMessage(chatId, messageId,
                    "Please provide a valid year /stats <year>");
        }

        List<Stat> stats = statsService.getAllStatsByChatIdAndYear(chatId, year);

        if (stats.isEmpty()) {
            return getReplyMessage(chatId, messageId,
                    "No stats found for " + year);
        }

        String statsTable = dailyMessageService.getKeyNameSentence("stats_table");
        String header = dailyMessageService.getKeyNameSentence("stats_year_header").formatted(year);
        String footer = dailyMessageService.getKeyNameSentence("stats_footer").formatted(stats.size());
        String text = new StatsListDecorator(stats).getFormattedStats(statsTable, header, footer, 10);

        return getMessageWithMarkdown(chatId, text);
    }

    private int determineYear(List<String> args) throws NumberFormatException {
        return args.isEmpty() ?
                LocalDateTime.now().getYear() :
                Integer.parseInt(args.getFirst());
    }

    @Override
    public Command getSupportedCommand() {
        return Command.STATS;
    }
}
