package org.churk.telegrambot.handler;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.model.Stat;
import org.churk.telegrambot.service.StatsService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RegisterHandler extends Handler {
    private final StatsService statsService;

    @Override
    public List<Validable> handle(HandlerContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        Long userId = context.getUpdate().getMessage().getFrom().getId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();
        String firstName = context.getUpdate().getMessage().getFrom().getFirstName();

        return getRegister(chatId, userId, firstName, messageId);
    }

    private List<Validable> getRegister(Long chatId, Long userId, String firstName, Integer messageId) {
        Optional<Stat> userStats = statsService.getStatsByChatIdAndUserId(chatId, userId);
        if (userStats.isPresent()) {
            String text = dailyMessageService.getKeyNameSentence("registered_header").formatted(firstName);
            return getReplyMessage(chatId, messageId, text);
        }
        statsService.registerByUserIdAndChatId(userId, chatId, firstName);
        String text = dailyMessageService.getKeyNameSentence("registered_now_header").formatted(firstName);
        return getReplyMessage(chatId, messageId, text);
    }

    @Override
    public Command getSupportedCommand() {
        return Command.REGISTER;
    }
}
