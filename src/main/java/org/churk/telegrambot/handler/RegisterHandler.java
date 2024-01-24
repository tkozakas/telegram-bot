package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.model.Stat;
import org.churk.telegrambot.service.DailyMessageService;
import org.churk.telegrambot.service.StatsService;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
public class RegisterHandler implements CommandHandler {
    private final MessageBuilderFactory messageBuilderFactory;
    private final DailyMessageService dailyMessageService;
    private final StatsService statsService;
    @Override
    public List<Validable> handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        Long userId = update.getMessage().getFrom().getId();
        Integer messageId = update.getMessage().getMessageId();
        String firstName = update.getMessage().getFrom().getFirstName();

        Optional<Stat> userStats = statsService.getStatsByChatIdAndUserId(chatId, userId);
        if (userStats.isPresent()) {
            String text = dailyMessageService.getKeyNameSentence("registered_header").formatted(firstName);
            return getMessage(text, chatId, messageId);
        }
        statsService.registerByUserIdAndChatId(userId, chatId, firstName);
        String text = dailyMessageService.getKeyNameSentence("registered_now_header").formatted(firstName);
        return getMessage(text, chatId, messageId);
    }

    private List<Validable> getMessage(String text, Long chatId, Integer messageId) {
        return List.of(messageBuilderFactory
                .createTextMessageBuilder(chatId)
                .withText(text)
                .withReplyToMessageId(messageId)
                .build());
    }
}
