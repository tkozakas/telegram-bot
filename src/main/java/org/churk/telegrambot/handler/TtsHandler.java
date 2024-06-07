package org.churk.telegrambot.handler;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.service.TtsService;
import org.churk.telegrambot.utility.UpdateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TtsHandler extends Handler {
    private final TtsService ttsService;

    @Override
    public List<Validable> handle(UpdateContext context) {
        Integer messageId = context.getUpdate().getMessage().getMessageId();
        Long chatId = context.getUpdate().getMessage().getChatId();
        List<String> args = context.getArgs();

        if (args.isEmpty()) {
            return getReplyMessage(chatId, messageId, "Please provide a text %s <text>"
                    .formatted(Command.TTS.getPatternCleaned()));
        }

        String text;
        Command command = Command.getTextCommand(args.getFirst(), botProperties.getWinnerName());
        if (command != Command.NONE) {
            HandlerFactory handlerFactory = context.getHandlerFactory();
            CommandHandler handler = handlerFactory.getHandler(command);
            List<Validable> result = handler.handle(UpdateContext.builder()
                    .handlerFactory(handlerFactory)
                    .update(context.getUpdate())
                    .args(args.subList(1, args.size()))
                    .build());
            text = getAudioMessage(result).formatted(botProperties.getWinnerName());
        } else {
            text = args.stream()
                    .map(String::trim)
                    .collect(Collectors.joining(" "))
                    .replace("\n", " ")
                    .replace("\r", " ");
        }
        if (text.isBlank()) {
            return getTextReplyMessage(chatId, messageId, "Please provide a text %s <text>"
                    .formatted(Command.TTS.getPatternCleaned()));
        }

        Optional<File> speechFile = ttsService.getSpeech(text);
        if (speechFile.isEmpty()) {
            return getTextReplyMessage(chatId, messageId, "Error generating speech");
        }
        return getAudioMessage(chatId, text, speechFile.get());
    }

    @Override
    public Command getSupportedCommand() {
        return Command.TTS;
    }
}
