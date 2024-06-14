package org.churk.telegrambot.handler;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.builder.ListHandler;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.model.Sentence;
import org.churk.telegrambot.model.Stat;
import org.churk.telegrambot.model.SubCommand;
import org.churk.telegrambot.repository.StatsRepository;
import org.churk.telegrambot.service.StatsService;
import org.churk.telegrambot.utility.UpdateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class DailyMessageHandler extends ListHandler<Stat> {
    private final StatsService statsService;
    private final StatsRepository statsRepository;

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        return true;
    }

    @Override
    public List<Validable> handle(UpdateContext context) {
        if (context.getArgs().isEmpty()) {
            return handleMessage(context);
        }

        String subCommand = context.getArgs().getFirst().toLowerCase();
        SubCommand subCommandEnum = SubCommand.getSubCommand(subCommand);

        return switch (subCommandEnum) {
            case REGISTER -> handleRegister(context);
            case STATS -> handleStats(context);
            default -> handleMessage(context);
        };
    }

    private List<Validable> handleStats(UpdateContext context) {
        if (context.getArgs().size() < 2) {
            return handleYearStats(context, "");
        }

        if (context.getArgs().size() < 3 && (isInteger(context.getArgs().getLast()))) {
            return handleYearStats(context, context.getArgs().getLast());
        }

        String subCommandArg = context.getArgs().get(1).toLowerCase();
        SubCommand subCommandEnum = SubCommand.getSubCommand(subCommandArg);

        return switch (subCommandEnum) {
            case YEAR -> handleYearStats(context, context.getArgs().getLast());
            case USER -> handleUserStats(context);
            case ALL -> handleAllStats(context);
            default -> createReplyMessage(context,
                    "Invalid command, please use %s %s".formatted(Command.DAILY_MESSAGE.getPatternCleaned(botProperties.getWinnerName()), Command.DAILY_MESSAGE.getSubCommands()));
        };
    }

    private List<Validable> handleYearStats(UpdateContext context, String yearString) {
        if (yearString.isEmpty()) {
            return handleStatsByYear(context, LocalDateTime.now().getYear());
        }
        int year;
        try {
            year = determineYear(yearString);
        } catch (NumberFormatException e) {
            return createReplyMessage(context, "Please provide a valid year /stats <year>");
        }
        return handleStatsByYear(context, year);
    }

    private List<Validable> handleStatsByYear(UpdateContext context, int year) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        List<Stat> stats = statsService.getAllStatsByChatIdAndYear(chatId, year);
        String header = dailyMessageService.getKeyNameSentence("stats_year_header").formatted(year);
        return constructStatsMessage(context, stats, header);
    }

    private List<Validable> handleAllStats(UpdateContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        List<Stat> stats = statsService.getAllStatsByChatId(chatId);

        String header = dailyMessageService.getKeyNameSentence("stats_all_header");
        return constructStatsMessage(context, stats, header);
    }

    private List<Validable> constructStatsMessage(UpdateContext context, List<Stat> stats, String header) {
        String statsTable = dailyMessageService.getKeyNameSentence("stats_table");
        String emptyMessage = dailyMessageService.getKeyNameSentence("no_stats_available");
        String footer = dailyMessageService.getKeyNameSentence("stats_footer").formatted(stats.size());
        Function<Stat, String> statFormatter = stat -> String.format(statsTable, stats.indexOf(stat) + 1, stat.getFirstName(), stat.getScore());
        context.setMarkdown(true);
        return formatListResponse(context, stats, statFormatter,
                header,
                footer, emptyMessage);
    }

    private List<Validable> handleUserStats(UpdateContext context) {
        context.setReply(true);
        Message message = context.getUpdate().getMessage();
        Long chatId = message.getChatId();

        String firstName = context.getArgs().size() == 3 ?
                context.getArgs().get(2) :
                message.getFrom().getFirstName();

        List<Stat> userIdsFromFirstName = statsService.getUserIdByChatIdAndFirstName(chatId, firstName);
        if (userIdsFromFirstName.isEmpty()) {
            return createReplyMessage(context, "No stats available for " + firstName);
        }

        long total = statsService.getTotalScoreByChatIdAndUserId(chatId, userIdsFromFirstName.getFirst().getUserId());
        String header = dailyMessageService.getKeyNameSentence("me_header").formatted(firstName, botProperties.getWinnerName(), total);
        context.setMarkdown(true);
        return createTextMessage(context, header);
    }

    private List<Validable> handleMessage(UpdateContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        int year = LocalDateTime.now().getYear();

        List<Stat> statByChatIdAndYear = statsService.getStatsByChatIdAndYear(chatId, year);

        if (statByChatIdAndYear.isEmpty()) {
            return createReplyMessage(context, dailyMessageService.getKeyNameSentence("no_stats_available"));
        }

        Optional<Stat> isWinnerStats = statByChatIdAndYear.stream()
                .filter(stats -> stats.getIsWinner() == Boolean.TRUE)
                .findFirst();

        if (isWinnerStats.isPresent()) {
            Stat isWinnerStat = isWinnerStats.get();
            String mentionedUser = "[" + isWinnerStat.getFirstName() + "](tg://user?id=" + isWinnerStat.getUserId() + ")";
            String messageText = dailyMessageService.getKeyNameSentence("winner_message")
                    .formatted(botProperties.getWinnerName(), mentionedUser);
            context.setMarkdown(true);
            return createTextMessage(context, messageText);
        }

        Stat randomWinner = statByChatIdAndYear.get(ThreadLocalRandom.current().nextInt(statByChatIdAndYear.size()));
        statsRepository.setIsWinnerByUserIdAndYear(randomWinner.getChatId(), randomWinner.getUserId(), year);

        List<Sentence> sentences = dailyMessageService.getRandomGroupSentences();
        String mentionedUser = "[" + randomWinner.getFirstName() + "](tg://user?id=" + randomWinner.getUserId() + ")";
        sentences.getLast().setText(sentences.getLast().getText() + mentionedUser);
        context.setMarkdown(true);
        return sentences
                .stream()
                .map(sent -> sent.getText()
                        .formatted(botProperties.getWinnerName())).map(text -> createTextMessage(context, text).getFirst())
                .toList();
    }

    private List<Validable> handleRegister(UpdateContext context) {
        context.setReply(true);
        Long chatId = context.getUpdate().getMessage().getChatId();
        Long userId = context.getUpdate().getMessage().getFrom().getId();
        String firstName = context.getUpdate().getMessage().getFrom().getFirstName();

        List<Stat> userStats = statsService.getStatsByChatIdAndUserId(chatId, userId);
        if (!userStats.isEmpty()) {
            return createReplyMessage(context, dailyMessageService.getKeyNameSentence("already_registered").formatted(firstName));
        }
        statsService.registerByUserIdAndChatId(userId, chatId, firstName);
        return createReplyMessage(context, dailyMessageService.getKeyNameSentence("registered_now_header").formatted(firstName));
    }

    private int determineYear(String yearString) throws NumberFormatException {
        return yearString.isEmpty() ? LocalDateTime.now().getYear() : Integer.parseInt(yearString);
    }

    @Override
    public Command getSupportedCommand() {
        return Command.DAILY_MESSAGE;
    }
}
