package org.churk.telegrambot.handler;

import lombok.AllArgsConstructor;
import org.churk.telegrambot.builder.MessageBuilderFactory;
import org.churk.telegrambot.model.Command;
import org.churk.telegrambot.service.InstagramService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.interfaces.Validable;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class InstagramHandler implements CommandHandler {
    private final MessageBuilderFactory messageBuilderFactory;
    private final InstagramService instagramService;

    @Override
    public List<Validable> handle(HandlerContext context) {
        Long chatId = context.getUpdate().getMessage().getChatId();
        Integer messageId = context.getUpdate().getMessage().getMessageId();

        String postCode = context.getArgs().size() == 2 ? context.getArgs().get(1) : null;
        if (postCode == null) {
            return List.of(messageBuilderFactory
                    .createTextMessageBuilder(chatId)
                    .withText("Please provide a url using /reels <url>")
                    .withReplyToMessageId(messageId)
                    .build());
        }

        String[] parts = postCode.split("/");
        String identifier = parts[parts.length - 1];

        Optional<File> file = instagramService.getInstagramMedia(identifier);
        if (file.isPresent()) {
            File existingFile = file.get();
            return List.of(messageBuilderFactory
                    .createVideoMessage(chatId)
                    .withVideo(existingFile)
                    .build());
        }
        return List.of();
    }

    @Override
    public Command getSupportedCommand() {
        return Command.REELS;
    }
}
