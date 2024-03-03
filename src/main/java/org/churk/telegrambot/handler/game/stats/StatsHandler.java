package org.churk.telegrambot.handler.game.stats;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.builder.StatsListDecorator;
import org.churk.telegrambot.handler.Command;
import org.churk.telegrambot.handler.Handler;
import org.churk.telegrambot.utility.HandlerContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StatsHandler extends Handler {
    private final StatsService statsService;

    @Override
    public List<Validable> handle(HandlerContext context) {
        List<String> args = context.getArgs();
        String subCommand = args.isEmpty() ? "all" : args.getFirst().toLowerCase();

        return switch (subCommand) {
            case "year" -> handleYearStats(context);
            case "user" -> handleUserStats(context);
            default -> handleAllStats(context);
        };
    }

    private List<Validable> handleYearStats(HandlerContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();
        List<String> args = context.getArgs().subList(1, context.getArgs().size());

        int year;
        try {
            year = determineYear(args);
        } catch (NumberFormatException e) {
            return getReplyMessage(chatId, messageId, "Please provide a valid year /stats <year>");
        }

        List<Stat> stats = statsService.getAllStatsByChatIdAndYear(chatId, year);
        String header = dailyMessageService.getKeyNameSentence("stats_year_header").formatted(year);
        return constructStatsMessage(chatId, messageId, stats, header);
    }

    private int determineYear(List<String> args) throws NumberFormatException {
        return args.isEmpty() ? LocalDateTime.now().getYear() : Integer.parseInt(args.getFirst());
    }

    private List<Validable> handleUserStats(HandlerContext context) {
        Message message = context.getUpdate().getMessage();
        Long chatId = message.getChatId();
        Long userId = message.getFrom().getId();
        Integer messageId = message.getMessageId();
        String firstName = message.getFrom().getFirstName();
        long total = statsService.getTotalScoreByChatIdAndUserId(chatId, userId);

        String header = dailyMessageService.getKeyNameSentence("me_header").formatted(firstName, botProperties.getWinnerName(), total);
        return getReplyMessageWithMarkdown(chatId, messageId, header);
    }

    private List<Validable> handleAllStats(HandlerContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();
        List<Stat> stats = statsService.getAllStatsByChatId(chatId);

        String header = dailyMessageService.getKeyNameSentence("stats_all_header");
        return constructStatsMessage(chatId, messageId, stats, header);
    }

    private List<Validable> constructStatsMessage(Long chatId, Integer messageId, List<Stat> stats, String header) {
        if (stats.isEmpty()) {
            return getReplyMessage(chatId, messageId, "No stats available");
        }

        String statsTable = dailyMessageService.getKeyNameSentence("stats_table");
        String footer = dailyMessageService.getKeyNameSentence("stats_footer").formatted(stats.size());
        String text = new StatsListDecorator(stats).getFormattedStats(statsTable, header, footer, 10);

        return getMessageWithMarkdown(chatId, text);
    }

    @Override
    public Command getSupportedCommand() {
        return Command.STATS;
    }
}
