package org.churk.telegrambot.handler.fact;

import lombok.AllArgsConstructor;
import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.handler.CommandHandler;
import org.churk.telegrambot.handler.HandlerContext;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.service.FactService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.util.List;

@Component
@AllArgsConstructor
public class FactAddHandler implements CommandHandler {
    private final FactService factService;
    private final MessageBuilderFactory messageBuilderFactory;

    @Override
    public List<Validable> handle(HandlerContext context) {
        List<String> args = context.getArgs();
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();

        if (args.isEmpty()) {
            return getTextMessageWithReply(chatId, messageId,
                    "Please provide a valid name /factadd <fact>");
        }
        factService.addFact(chatId, args.stream().reduce((a, b) -> a + " " + b).get());
        return getTextMessageWithReply(chatId, messageId,
                "Fact: " + args.getFirst() + " added");
    }

    private List<Validable> getTextMessageWithReply(Long chatId, Integer messageId, String s) {
        return List.of(messageBuilderFactory
                .createTextMessageBuilder(chatId)
                .withReplyToMessageId(messageId)
                .withText(s)
                .enableMarkdown(false)
                .build());
    }

    @Override
    public Command getSupportedCommand() {
        return Command.FACT_ADD;
    }
}
