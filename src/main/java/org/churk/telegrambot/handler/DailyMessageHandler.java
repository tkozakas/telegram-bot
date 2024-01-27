package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.config.BotProperties;
import org.churk.telegrambot.model.bot.Command;
import org.churk.telegrambot.model.bot.Sentence;
import org.churk.telegrambot.model.bot.Stat;
import org.churk.telegrambot.service.DailyMessageService;
import org.churk.telegrambot.service.StatsService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class DailyMessageHandler implements CommandHandler {
    private final boolean ENABLED = true;
    private final BotProperties botProperties;
    private final MessageBuilderFactory messageBuilderFactory;
    private final DailyMessageService dailyMessageService;
    private final StatsService statsService;

    @Override
    public List<Validable> handle(HandlerContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        int year = LocalDateTime.now().getYear();

        List<Stat> statByChatIdAndYear = statsService.getStatsByChatIdAndYear(chatId, year);

        if (statByChatIdAndYear.isEmpty()) {
            return getNoStatsAvailableMessage(chatId);
        }

        Optional<Stat> isWinnerStats = statByChatIdAndYear.stream()
                .filter(stats -> stats.getIsWinner() == Boolean.TRUE)
                .findFirst();

        if (isWinnerStats.isPresent()) {
            return getMessage(isWinnerStats.get(), chatId);
        }

        Stat randomWinner = statByChatIdAndYear.get(ThreadLocalRandom.current().nextInt(statByChatIdAndYear.size()));
        if (ENABLED) {
            statsService.updateStats(randomWinner);
        }

        return getNewWinnerMessage(randomWinner.getFirstName(), chatId);
    }

    private List<Validable> getNewWinnerMessage(String randomWinner, Long chatId) {
        List<Sentence> sentences = dailyMessageService.getRandomGroupSentences();
        sentences.getLast().setText(sentences.getLast().getText() + randomWinner);
        return sentences.stream()
                .map(sentence -> messageBuilderFactory
                        .createTextMessageBuilder(chatId)
                        .withText(sentence.getText().formatted(botProperties.getWinnerName()))
                        .build())
                .collect(Collectors.toList());
    }

    private List<Validable> getMessage(String text, Long chatId) {
        return List.of(messageBuilderFactory
                .createTextMessageBuilder(chatId)
                .withText(text)
                .build());
    }

    private List<Validable> getNoStatsAvailableMessage(Long chatId) {
        log.info("No stats available for chatId: {}", chatId);
        return getMessage(dailyMessageService.getKeyNameSentence("no_stats_available"), chatId);
    }

    private List<Validable> getMessage(Stat isWinnerStat, Long chatId) {
        log.info("Winner exists for chatId: {}", chatId);
        String mentionedUser = "[" + isWinnerStat.getFirstName() + "](tg://user?id=" + isWinnerStat.getUserId() + ")";
        String messageText = dailyMessageService.getKeyNameSentence("winner_message")
                .formatted(botProperties.getWinnerName(), mentionedUser);
        return getMessage(messageText, chatId);
    }

    @Override
    public Command getSupportedCommand() {
        return Command.DAILY_MESSAGE;
    }
}
