package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.config.BotProperties;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.model.Sentence;
import org.churk.telegrambot.model.Stat;
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
        Integer messageId = context.getUpdate().getMessage().getMessageId();
        Long chatId = context.getUpdate().getMessage().getChatId();
        int year = LocalDateTime.now().getYear();

        List<Stat> statByChatIdAndYear = statsService.getStatsByChatIdAndYear(chatId, year);

        if (statByChatIdAndYear.isEmpty()) {
            return getErrorMessage(chatId, messageId, dailyMessageService.getKeyNameSentence("no_stats_available"));
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

    private List<Validable> getErrorMessage(Long chatId, Integer messageId, String text) {
        return List.of(messageBuilderFactory
                .createTextMessageBuilder(chatId)
                .withReplyToMessageId(messageId)
                .withText(text)
                .build());
    }

    private List<Validable> getMessage(Stat isWinnerStat, Long chatId) {
        log.info("Winner exists for chatId: {}", chatId);
        String mentionedUser = "[" + isWinnerStat.getFirstName() + "](tg://user?id=" + isWinnerStat.getUserId() + ")";
        String messageText = dailyMessageService.getKeyNameSentence("winner_message")
                .formatted(botProperties.getWinnerName(), mentionedUser);
        return List.of(messageBuilderFactory
                .createTextMessageBuilder(chatId)
                .withText(messageText)
                .build());
    }

    @Override
    public Command getSupportedCommand() {
        return Command.DAILY_MESSAGE;
    }
}
