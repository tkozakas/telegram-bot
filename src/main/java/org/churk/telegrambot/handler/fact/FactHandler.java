package org.churk.telegrambot.handler.fact;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.handler.Handler;
import org.churk.telegrambot.handler.HandlerContext;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.model.Fact;
import org.churk.telegrambot.service.FactService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class FactHandler extends Handler {
    private final FactService factService;

    @Override
    public List<Validable> handle(HandlerContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();
        List<Fact> facts = factService.getAllFacts();
        String randomFact = facts.get(ThreadLocalRandom.current().nextInt(facts.size())).getComment();

        return context.isReply() ?
                getReplyMessage(chatId, messageId, randomFact) :
                getMessage(chatId, randomFact);
    }

    @Override
    public Command getSupportedCommand() {
        return Command.FACT;
    }
}
