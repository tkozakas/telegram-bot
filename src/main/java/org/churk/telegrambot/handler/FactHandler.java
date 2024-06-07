package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.model.Fact;
import org.churk.telegrambot.service.FactService;
import org.churk.telegrambot.service.TtsService;
import org.churk.telegrambot.utility.UpdateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class FactHandler extends Handler {
    private final FactService factService;
    private final TtsService ttsService;

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

        String fact = factArgs.stream()
                .map(String::trim)
                .collect(Collectors.joining(" "))
                .replace("\n", " ")
                .replace("\r", " ");
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
        return getAudioMessage(context, randomFact, chatId, messageId);
    }

    private List<Validable> getAudioMessage(UpdateContext context, String response, Long chatId, Integer messageId) {
        Optional<File> audioMessage = ttsService.getSpeech(response);
        if (audioMessage.isPresent()) {
            return context.isReply() ?
                    getReplyAudioMessage(chatId, messageId, response, audioMessage.get()) :
                    getAudioMessage(chatId, response, audioMessage.get());
        }
        log.error("Failed to generate audio message for fact: {}", response);
        return context.isReply() ?
                getReplyMessage(chatId, messageId, response) :
                getMessage(chatId, response);
    }

    @Override
    public Command getSupportedCommand() {
        return Command.FACT;
    }
}
