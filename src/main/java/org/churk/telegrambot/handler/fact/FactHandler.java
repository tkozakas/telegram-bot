package org.churk.telegrambot.handler.fact;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.handler.Command;
import org.churk.telegrambot.handler.Handler;
import org.churk.telegrambot.utility.HandlerContext;
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
        if (facts.isEmpty()) {
            return getReplyMessage(chatId, messageId,
                    "No facts available (use /factadd <fact> to add some)");
        }
        String randomFact = facts.get(ThreadLocalRandom.current().nextInt(facts.size())).getComment();

        return context.isReply() ?
                getReplyMessage(chatId, messageId, randomFact) :
                getMessageWithMarkdown(chatId, randomFact);
    }

    @Override
    public Command getSupportedCommand() {
        return Command.FACT;
    }
}
