package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.model.bot.Command;
import org.churk.telegrambot.model.bot.Fact;
import org.churk.telegrambot.service.FactService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
@AllArgsConstructor
public class FactHandler implements CommandHandler {
    private final MessageBuilderFactory messageBuilderFactory;
    private final FactService factService;

    @Override
    public List<Validable> handle(HandlerContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();
        List<Fact> facts = factService.getAllFacts();
        Fact randomFact = facts.get(ThreadLocalRandom.current().nextInt(facts.size()));

        return context.isReply() ?
                getReplyMessage(chatId, messageId, randomFact) :
                getMessage(chatId, randomFact);
    }

    private List<Validable> getMessage(Long chatId, Fact randomFact) {
        return List.of(messageBuilderFactory
                .createTextMessageBuilder(chatId)
                .withText(randomFact.getComment())
                .build());
    }

    private List<Validable> getReplyMessage(Long chatId, Integer messageId, Fact randomFact) {
        return List.of(messageBuilderFactory
                .createTextMessageBuilder(chatId)
                .withText(randomFact.getComment())
                .withReplyToMessageId(messageId)
                .build());
    }

    @Override
    public Command getSupportedCommand() {
        return Command.FACT;
    }
}
