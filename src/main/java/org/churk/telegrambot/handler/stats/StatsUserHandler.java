package org.churk.telegrambot.handler.stats;

import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.config.BotProperties;
import org.churk.telegrambot.handler.Handler;
import org.churk.telegrambot.handler.HandlerContext;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.service.DailyMessageService;
import org.churk.telegrambot.service.StatsService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Component
public class StatsUserHandler extends Handler {
    private final StatsService statsService;

    public StatsUserHandler(DailyMessageService dailyMessageService, BotProperties botProperties, MessageBuilderFactory messageBuilderFactory, StatsService statsService) {
        super(dailyMessageService, botProperties, messageBuilderFactory);
        this.statsService = statsService;
    }

    @Override
    public List<Validable> handle(HandlerContext context) {
        Message message = context.getUpdate().getMessage();
        Long chatId = message.getChatId();
        Long userId = message.getFrom().getId();
        Integer messageId = message.getMessageId();
        String firstName = message.getFrom().getFirstName();
        long total = statsService.getTotalScoreByChatIdAndUserId(chatId, userId);

        String text = dailyMessageService.getKeyNameSentence("me_header")
                .formatted(firstName, botProperties.getWinnerName(), total);
        return getReplyMessage(chatId, messageId, text);
    }

    @Override
    public Command getSupportedCommand() {
        return Command.STATS_USER;
    }
}
