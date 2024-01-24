package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.model.Fact;
import org.churk.telegrambot.service.FactService;
import org.telegram.telegrambots.meta.api.interfaces.Validable;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@AllArgsConstructor
public class FactHandler implements CommandHandler {
    private final MessageBuilderFactory messageBuilderFactory;
    private final FactService factService;

    @Override
    public List<Validable> handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        return getFact(chatId);
    }

    @Override
    public List<Validable> handleByChatId(Long chatId) {
        return getFact(chatId);
    }

    private List<Validable> getFact(Long chatId) {
        List<Fact> facts = factService.getAllFacts();
        Fact randomFact = facts.get(ThreadLocalRandom.current().nextInt(facts.size()));
        return List.of(messageBuilderFactory
                .createTextMessageBuilder(chatId)
                .withText(randomFact.getComment())
                .build());
    }
}
