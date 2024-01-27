package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.config.BotProperties;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.service.DailyMessageService;
import org.churk.telegrambot.service.StatsService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;

@Component
@AllArgsConstructor
public class StatsUserHandler implements CommandHandler {
    private final BotProperties botProperties;
    private final MessageBuilderFactory messageBuilderFactory;
    private final DailyMessageService dailyMessageService;
    private final StatsService statsService;

    @Override
    public List<Validable> handle(HandlerContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        Long userId = context.getUpdate().getMessage().getFrom().getId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();
        String firstName = context.getUpdate().getMessage().getFrom().getFirstName();
        long total = statsService.getTotalScoreByChatIdAndUserId(chatId, userId);

        String text = dailyMessageService.getKeyNameSentence("me_header")
                .formatted(firstName, botProperties.getWinnerName(), total);
        return List.of(messageBuilderFactory
                .createTextMessageBuilder(chatId)
                .withText(text)
                .withReplyToMessageId(messageId)
                .build());
    }

    @Override
    public Command getSupportedCommand() {
        return Command.STATS_USER;
    }
}
