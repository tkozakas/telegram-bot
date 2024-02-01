package org.churk.telegrambot.handler;

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

@Component
public class DailyMessageHandler extends Handler {
    private static final boolean ENABLED = true;
    private final StatsService statsService;

    public DailyMessageHandler(BotProperties botProperties, DailyMessageService dailyMessageService, MessageBuilderFactory messageBuilderFactory, StatsService statsService) {
        super(botProperties, dailyMessageService, messageBuilderFactory);
        this.statsService = statsService;
    }

    @Override
    public List<Validable> handle(HandlerContext context) {
        Integer messageId = context.getUpdate().getMessage().getMessageId();
        Long chatId = context.getUpdate().getMessage().getChatId();
        int year = LocalDateTime.now().getYear();

        List<Stat> statByChatIdAndYear = statsService.getStatsByChatIdAndYear(chatId, year);

        if (statByChatIdAndYear.isEmpty()) {
            return getReplyMessage(chatId, messageId, dailyMessageService.getKeyNameSentence("no_stats_available"));
        }

        Optional<Stat> isWinnerStats = statByChatIdAndYear.stream()
                .filter(stats -> stats.getIsWinner() == Boolean.TRUE)
                .findFirst();

        if (isWinnerStats.isPresent()) {
            Stat isWinnerStat = isWinnerStats.get();
            String mentionedUser = "[" + isWinnerStat.getFirstName() + "](tg://user?id=" + isWinnerStat.getUserId() + ")";
            String messageText = dailyMessageService.getKeyNameSentence("winner_message")
                    .formatted(botProperties.getWinnerName(), mentionedUser);
            return getMessage(chatId, messageText);
        }

        Stat randomWinner = statByChatIdAndYear.get(ThreadLocalRandom.current().nextInt(statByChatIdAndYear.size()));
        if (ENABLED) {
            statsService.updateStats(randomWinner);
        }

        List<Sentence> sentences = dailyMessageService.getRandomGroupSentences();
        sentences.getLast().setText(sentences.getLast().getText() + randomWinner);
        return sentences.stream()
                .map(sentence -> messageBuilderFactory
                        .createTextMessageBuilder(chatId)
                        .withText(sentence.getText().formatted(botProperties.getWinnerName()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public Command getSupportedCommand() {
        return Command.DAILY_MESSAGE;
    }
}
