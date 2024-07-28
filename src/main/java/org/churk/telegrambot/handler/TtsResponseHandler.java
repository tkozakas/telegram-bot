package org.churk.telegrambot.handler;

import lombok.RequiredArgsConstructor;
import org.churk.telegrambot.client.MemeClient;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.model.UpdateContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TtsResponseHandler extends ResponseHandler {
    public static final int MAX_AUDIO_LENGTH = 1000;
    private final MemeClient memeClient;

    @Override
    public List<Validable> handle(UpdateContext context) {
        List<String> args = context.getArgs();

        if (args.isEmpty()) {
            context.setMarkdown(true);
            return createReplyMessage(context,
                    "Please provide a text %s <text>"
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
            context.setMarkdown(true);
            return createReplyMessage(context,
                    "Please provide a text %s <text>"
                            .formatted(Command.TTS.getPatternCleaned()));
        }
        try {
            context.setMarkdown(true);
            if (text.length() > MAX_AUDIO_LENGTH) {
                return createReplyMessage(context, text);
            }
            byte[] response = memeClient.getTts(text).getBody();
            return response == null ?
                    createReplyMessage(context, text) :
                    createAudioMessage(context, text, response);
        } catch (Exception e) {
            return createLogMessage(context, "Error generating audio", e.getMessage());
        }
    }

    @Override
    public Command getSupportedCommand() {
        return Command.TTS;
    }
}
