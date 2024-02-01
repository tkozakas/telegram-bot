package org.churk.telegrambot.fact;

import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.config.BotProperties;
import org.churk.telegrambot.handler.Handler;
import org.churk.telegrambot.utility.HandlerContext;
import org.churk.telegrambot.handler.Command;
import org.churk.telegrambot.message.DailyMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class FactHandler extends Handler {
    private final FactService factService;

    public FactHandler(BotProperties botProperties, DailyMessageService dailyMessageService, MessageBuilderFactory messageBuilderFactory, FactService factService) {
        super(botProperties, dailyMessageService, messageBuilderFactory);
        this.factService = factService;
    }

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
