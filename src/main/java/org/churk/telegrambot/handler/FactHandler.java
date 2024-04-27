package org.churk.telegrambot.handler;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.model.Fact;
import org.churk.telegrambot.service.FactService;
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
        List<String> args = context.getArgs();
        if (!args.isEmpty() && args.getFirst().equalsIgnoreCase("add")) {
            return handleFactAdd(context);
        }
        return handleFactRetrieve(context);
    }

    private List<Validable> handleFactAdd(HandlerContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();
        List<String> factArgs = context.getArgs().subList(1, context.getArgs().size());

        if (factArgs.isEmpty()) {
            return getReplyMessage(chatId, messageId, "Save a fact using /fact add <fact>");
        }

        String fact = String.join(" ", factArgs);
        factService.addFact(chatId, fact);
        return getReplyMessage(chatId, messageId, "Fact: " + fact + " added");
    }

    private List<Validable> handleFactRetrieve(HandlerContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();
        List<Fact> facts = factService.getAllFacts();

        if (facts.isEmpty()) {
            return getReplyMessage(chatId, messageId, "No facts available (use /fact add <fact> to add some)");
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
