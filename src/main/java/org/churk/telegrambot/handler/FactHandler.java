package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.model.Fact;
import org.churk.telegrambot.service.FactService;
import org.churk.telegrambot.service.VoiceOverService;
import org.churk.telegrambot.utility.UpdateContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Component
@AllArgsConstructor
public class FactHandler extends Handler {
    private static final Logger log = LoggerFactory.getLogger(FactHandler.class);
    private final FactService factService;
    private final VoiceOverService ttsService;

    @Override
    public List<Validable> handle(UpdateContext context) {
        List<String> args = context.getArgs();
        if (!args.isEmpty() && args.getFirst().equalsIgnoreCase("add")) {
            return handleFactAdd(context);
        }
        return handleFactRetrieve(context);
    }

    private List<Validable> handleFactAdd(UpdateContext context) {
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

    private List<Validable> handleFactRetrieve(UpdateContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();
        List<Fact> facts = factService.getAllFacts();

        if (facts.isEmpty()) {
            return getReplyMessage(chatId, messageId, "No facts available (use /fact add <fact> to add some)");
        }

        String randomFact = facts.get(ThreadLocalRandom.current().nextInt(facts.size())).getComment();
        Optional<File> audioMessage = ttsService.getSpeech(randomFact);
        if (audioMessage.isPresent()) {
            return context.isReply() ?
                    getReplyAudioMessage(chatId, messageId, randomFact, audioMessage.get()) :
                    getAudioMessage(chatId, randomFact, audioMessage.get());
        }
        log.error("Failed to generate audio message for fact: {}", randomFact);
        return context.isReply() ?
                getReplyMessage(chatId, messageId, randomFact) :
                getMessage(chatId, randomFact);
    }

    @Override
    public Command getSupportedCommand() {
        return Command.FACT;
    }
}
