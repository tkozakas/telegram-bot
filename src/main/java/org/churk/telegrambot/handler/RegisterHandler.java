package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.model.bot.Command;
import org.churk.telegrambot.model.bot.Stat;
import org.churk.telegrambot.service.DailyMessageService;
import org.churk.telegrambot.service.StatsService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@AllArgsConstructor
public class RegisterHandler implements CommandHandler {
    private final MessageBuilderFactory messageBuilderFactory;
    private final DailyMessageService dailyMessageService;
    private final StatsService statsService;

    @Override
    public List<Validable> handle(HandlerContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        Long userId = context.getUpdate().getMessage().getFrom().getId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();
        String firstName = context.getUpdate().getMessage().getFrom().getFirstName();

        return getRegister(chatId, userId, firstName, messageId);
    }

    @Override
    public Command getSupportedCommand() {
        return Command.REGISTER;
    }

    private List<Validable> getRegister(Long chatId, Long userId, String firstName, Integer messageId) {
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
