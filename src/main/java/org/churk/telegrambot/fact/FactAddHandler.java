package org.churk.telegrambot.fact;

import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.config.BotProperties;
import org.churk.telegrambot.handler.Command;
import org.churk.telegrambot.handler.Handler;
import org.churk.telegrambot.message.DailyMessageService;
import org.churk.telegrambot.utility.HandlerContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;

@Component
public class FactAddHandler extends Handler {
    private final FactService factService;

    public FactAddHandler(BotProperties botProperties, DailyMessageService dailyMessageService, MessageBuilderFactory messageBuilderFactory, FactService factService) {
        super(botProperties, dailyMessageService, messageBuilderFactory);
        this.factService = factService;
    }

    @Override
    public List<Validable> handle(HandlerContext context) {
        List<String> args = context.getArgs();
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();

        if (args.isEmpty()) {
            return getReplyMessage(chatId, messageId,
                    "Save a fact using /factadd <fact>");
        }
        String fact = args.stream()
                .reduce((a, b) -> a + " " + b)
                .orElseGet(String::new);
        factService.addFact(chatId, fact);
        return getReplyMessage(chatId, messageId,
                "Fact: " + fact + " added");
    }

    @Override
    public Command getSupportedCommand() {
        return Command.FACT_ADD;
    }
}
