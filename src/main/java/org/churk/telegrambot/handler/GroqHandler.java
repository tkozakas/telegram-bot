package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.service.GroqService;
import org.churk.telegrambot.service.TtsService;
import org.churk.telegrambot.utility.UpdateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@AllArgsConstructor
public class GroqHandler extends Handler {
    private final GroqService groqService;
    private final TtsService ttsService;

    @Override
    public List<Validable> handle(UpdateContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();
        List<String> args = context.getArgs();

        if (args.isEmpty()) {
            return getReplyMessage(chatId, messageId, "Save a fact using /gpt <prompt>");
        }

        String prompt = args.stream()
                .map(String::trim)
                .collect(Collectors.joining(" "))
                .replace("\n", " ")
                .replace("\r", " ");
        String response = groqService.getChatCompletion(prompt).getChoices().getFirst().getMessage().getContent();

        return getAudioMessage(context, response, chatId, messageId);
    }

    private List<Validable> getAudioMessage(UpdateContext context, String response, Long chatId, Integer messageId) {
        Optional<File> audioMessage = ttsService.getSpeech(response);
        if (audioMessage.isPresent()) {
            return context.isReply() ?
                    getReplyAudioMessage(chatId, messageId, response, audioMessage.get()) :
                    getAudioMessage(chatId, response, audioMessage.get());
        }
        log.error("Failed to generate audio message for: {}", response);
        return context.isReply() ?
                getReplyMessage(chatId, messageId, response) :
                getMessage(chatId, response);
    }

    @Override
    public Command getSupportedCommand() {
        return Command.GPT;
    }
}
