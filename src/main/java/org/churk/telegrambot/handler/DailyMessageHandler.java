package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.config.BotProperties;
import org.churk.telegrambot.model.Sentence;
import org.churk.telegrambot.model.Stats;
import org.churk.telegrambot.service.DailyMessageService;
import org.churk.telegrambot.service.StatsService;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
public class DailyMessageHandler implements CommandHandler {
    private final BotProperties botProperties;
    private final MessageBuilderFactory messageBuilderFactory;
    private final DailyMessageService dailyMessageService;
    private final StatsService statsService;

    @Override
    public List<Validable> handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        int year = LocalDateTime.now().getYear();

        List<Stats> statsByChatIdAndYear = statsService.getStatsByChatIdAndYear(chatId, year);

        if (statsByChatIdAndYear.isEmpty()) {
            return getNoStatsAvailableMessage(chatId);
        }

        Optional<Stats> isWinnerStats = statsByChatIdAndYear.stream()
                .filter(stats -> stats.getIsWinner() == Boolean.TRUE)
                .findFirst();

        if (isWinnerStats.isPresent()) {
            return getMessage(isWinnerStats.get(), chatId);
        }

        Stats randomWinner = statsByChatIdAndYear.get(ThreadLocalRandom.current().nextInt(statsByChatIdAndYear.size()));
        statsService.updateStats(randomWinner);

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

    private List<Validable> getMessage(Stats isWinnerStats, Long chatId) {
        log.info("Winner exists for chatId: {}", chatId);
        return getMessage(dailyMessageService.getKeyNameSentence("winner_message")
                .formatted(botProperties.getWinnerName(), isWinnerStats.getFirstName()), chatId);
    }
}
