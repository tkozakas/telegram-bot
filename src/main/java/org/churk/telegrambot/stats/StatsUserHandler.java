package org.churk.telegrambot.stats;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.handler.Command;
import org.churk.telegrambot.handler.Handler;
import org.churk.telegrambot.utility.HandlerContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Component
@RequiredArgsConstructor
public class StatsUserHandler extends Handler {
    private final StatsService statsService;

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

        return getReplyMessageWithMarkdown(chatId, messageId, text);
    }

    @Override
    public Command getSupportedCommand() {
        return Command.STATS_USER;
    }
}
