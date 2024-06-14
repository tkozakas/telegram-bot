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
        return !args.isEmpty() && args.getFirst().equalsIgnoreCase("add") ?
                handleFactAdd(context) :
                handleFactRetrieve(context);
    }

    private List<Validable> handleFactAdd(UpdateContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        List<String> factArgs = context.getArgs().subList(1, context.getArgs().size());

        if (factArgs.isEmpty()) {
            return createReplyMessage(context, "Save a fact using /fact add <fact>");
        }

        String fact = factArgs.stream()
                .map(String::trim)
                .collect(Collectors.joining(" "))
                .replace("\n", " ")
                .replace("\r", " ");
        factService.addFact(chatId, fact);
        return createReplyMessage(context, "Fact: " + fact + " added");
    }

    private List<Validable> handleFactRetrieve(UpdateContext context) {
        List<Fact> facts = factService.getAllFacts();

        if (facts.isEmpty()) {
            return createReplyMessage(context, "No facts available (use /fact add <fact> to add some)");
        }

        String randomFact = facts.get(ThreadLocalRandom.current().nextInt(facts.size())).getComment();
        return getAudioMessage(context, randomFact);
    }

    private List<Validable> getAudioMessage(UpdateContext context, String response) {
        Optional<File> audioMessage = ttsService.getSpeech(response);
        if (audioMessage.isPresent()) {
            return createAudioMessage(context,
                    response,
                    audioMessage.get()
            );
        }
        log.error("Failed to generate audio message for fact: {}", response);
        return createTextMessage(context, response);
    }

    @Override
    public Command getSupportedCommand() {
        return Command.FACT;
    }
}
